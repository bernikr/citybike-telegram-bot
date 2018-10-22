import json

import citybikeAPI
from db import DB
from telegram.ext import Updater, CommandHandler
import logging

logging.basicConfig(format='%(asctime)s - %(name)s - %(levelname)s - %(message)s', level=logging.INFO)
logger = logging.getLogger(__name__)


def error(bot, update, error):
    logger.warning('Update "%s" caused error "%s"', update, error)
    update.message.reply_text('There was an error. Please try again')


def start(bot, update):
    if update.message.chat.type != 'private':
        update.message.reply_text('this bot only works in private chats')
    else:
        update.message.reply_text('Use \'/login username password\' to log into your Citybike Account')
        update.message.reply_text('⚠️WARNING⚠️\nYou should never submit any login data to a third party. '
                                  'Only use this bot if you know me personally and trust me with your login.')


def login(bot, update, args):
    if update.message.chat.type != 'private':
        update.message.reply_text('this bot only works in private chats')
        return

    user_id = update.message.chat.id

    with DB() as db:
        user = db.get_user(user_id)
    if user is not None:
        update.message.reply_text('There is already a CityBike Account Linked with this Telegram Account.\n'
                                  'Please use /deleteUserData to delete existing user data.')
        return

    if len(args) < 2:
        update.message.reply_text('Bot must be started with \'/login username password\'')
        update.message.reply_text('⚠️WARNING⚠️\nYou should never submit any login data to a third party. '
                                  'Only use this bot if you know me personally and trust me with your login.')
        return

    username = args[0]
    password = ' '.join(args[1:])

    try:
        my_acc = citybikeAPI.CitybikeAccount(username, password)
    except citybikeAPI.LoginError:
        update.message.reply_text('There was an error logging in. Check your username and Password')
        return

    update.message.reply_text('Logged in as %s!\nDownloading rides (this may take a while)' % username)

    with DB() as db:
        db.new_user(user_id, username, password)
        db.store_cookie_dump(user_id, my_acc.get_cookies_dump())
        rides = update_rides(db, user_id)
        update.message.reply_text('%d rides found.' % len(rides))


def delete_user_data(bot, update):
    user_id = update.message.chat.id
    with DB() as db:
        db.delete_user(user_id)
        update.message.reply_text('Removed all User Data')


def update(bot, update):
    update.message.reply_text('Getting new rides')
    user_id = update.message.chat.id
    with DB() as db:
        if db.get_user(user_id) is None:
            update.message.reply_text('You must be logged in to do this. Use /login')
            return
        rides = update_rides(db, user_id)
        update.message.reply_text('%d new rides downloaded rides found.' % len(rides))


def main():
    with open('config.json') as f:
        config = json.load(f)

    with DB() as db:
        db.init_tables()
        db.update_stations(citybikeAPI.get_stations())

    updater = Updater(config['bot_token'])
    dp = updater.dispatcher
    dp.add_handler(CommandHandler('start', start))
    dp.add_handler(CommandHandler('login', login, pass_args=True))
    dp.add_handler(CommandHandler('update', update))
    dp.add_handler(CommandHandler(['stop', 'deleteUserData'], delete_user_data))
    dp.add_error_handler(error)
    updater.start_polling()
    updater.idle()


def update_rides(db, user_id):
    u = db.get_user(user_id)
    my_acc = citybikeAPI.CitybikeAccount(u['username'], u['password'], u['cookie_dump'])
    rides = my_acc.get_rides(since=db.get_newest_end_time(user_id))
    db.store_cookie_dump(user_id, my_acc.get_cookies_dump())
    for r in rides:
        db.insert_ride(r, user_id)
    return rides


if __name__ == "__main__":
    main()

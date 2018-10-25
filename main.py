import json

from sqlalchemy import create_engine, func, exists
from sqlalchemy.orm import sessionmaker, scoped_session

import citybikeAPI
from telegram.ext import Updater, CommandHandler
import logging

from DBtypes import create_all, Ride, User

logging.basicConfig(format='%(asctime)s - %(name)s - %(levelname)s - %(message)s', level=logging.INFO)
logger = logging.getLogger(__name__)

db_engine = create_engine('sqlite:///citybikes.db')
DB = scoped_session(sessionmaker(bind=db_engine))


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
    session = DB()

    if session.query(exists().where(User.id == user_id)).scalar():
        update.message.reply_text('There is already a CityBike Account Linked with this Telegram Account.\n'
                                  'Please use /deleteUserData to delete existing user data.')
        return

    if len(args) < 2:
        update.message.reply_text('Bot must be started with \'/login username password\'')
        update.message.reply_text('⚠️WARNING⚠️\nYou should never submit any login data to a third party. '
                                  'Only use this bot if you know me personally and trust me with your login.')
        return

    user = User(id=user_id, username=args[0], password=' '.join(args[1:]))
    try:
        my_acc = citybikeAPI.CitybikeAccount(user)
    except citybikeAPI.LoginError:
        update.message.reply_text('There was an error logging in. Check your username and Password')
        return

    update.message.reply_text('Logged in as %s!\nDownloading rides (this may take a while)' % user.username)

    session.add(user)
    session.commit()
    update_rides(bot, session, user)
    DB.remove()


def delete_user_data(bot, update):
    user_id = update.message.chat.id
    # TODO
    update.message.reply_text('Removed all User Data')


def update(bot, update):
    user_id = update.message.chat.id
    session = DB()
    user = session.query(User).get(user_id)
    if user is None:
        update.message.reply_text('You must be logged in to do this. Use /login')
        return
    update_rides(bot, session, user)
    session.commit()
    DB.remove()


def main():
    with open('config.json') as f:
        config = json.load(f)

    create_all(db_engine)
    session = DB()

    [session.merge(s) for s in citybikeAPI.get_stations()]
    session.commit()

    updater = Updater(config['bot_token'])
    dp = updater.dispatcher
    dp.add_handler(CommandHandler('start', start))
    dp.add_handler(CommandHandler('login', login, pass_args=True))
    dp.add_handler(CommandHandler('update', update))
    dp.add_handler(CommandHandler(['stop', 'deleteUserData'], delete_user_data))
    dp.add_error_handler(error)
    updater.start_polling()
    updater.idle()


def update_rides(bot, session, user, silent=False):
    callback = None
    msg = None
    if not silent:
        msg = bot.send_message(user.id, 'Loading rides...')
        callback = update_rides_callback

    max_time = session.query(func.max(Ride.end_time)).filter(Ride.user == user).scalar()

    acc = citybikeAPI.CitybikeAccount(user)
    rides = acc.get_rides(since=max_time, callback=callback, callbackArgs=(bot, msg))

    [r.link_stations(session) for r in rides]
    session.add_all(rides)


def update_rides_callback(current=0, count=0, finished=False, callbackArgs=()):
    bot, msg = callbackArgs
    if finished:
        bot.edit_message_text('%d new rides loaded✅' % current, chat_id=msg.chat.id, message_id=msg.message_id)
    else:
        bot.edit_message_text('Loading rides: %d/%d' % (current, count), chat_id=msg.chat.id, message_id=msg.message_id)


if __name__ == "__main__":
    main()

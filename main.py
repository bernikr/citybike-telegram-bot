import json

import citybikeAPI
from db import DB
from telegram.ext import Updater, CommandHandler
import logging

logging.basicConfig(format='%(asctime)s - %(name)s - %(levelname)s - %(message)s', level=logging.INFO)
logger = logging.getLogger(__name__)


def error(bot, update, error):
    logger.warning('Update "%s" caused error "%s"', update, error)


def start(bot, update, args):
    if update.message.chat.type == 'private':
        user_id = update.message.chat.id

        if len(args) < 2:
            update.message.reply_text('Bot must be started with \'/start username password\'')
        else:
            username = args[0]
            password = ' '.join(args[1:])
            with DB() as db:
                db.new_user(user_id, username, password)
            rides = update_rides(user_id)
            update.message.reply_text('Logged in as %s!\n%d rides found.' % (username, len(rides)))
    else:
        update.message.reply_text('this bot only works in private chats')


def delete_user_data(bot, update):
    user_id = update.message.chat.id
    with DB() as db:
        db.delete_user(user_id)
        update.message.reply_text('Removed all User Data')


def main():
    logger.info('Load Config File')
    with open('config.json') as f:
        config = json.load(f)

    logger.info('Update Station Table')
    with DB() as db:
        db.init_tables()
        db.update_stations(citybikeAPI.get_stations())

    logger.info('Initialize Bot')
    updater = Updater(config['bot_token'])
    dp = updater.dispatcher
    dp.add_handler(CommandHandler("start", start, pass_args=True))
    dp.add_handler(CommandHandler(["stop", "deleteUserData"], delete_user_data))
    dp.add_error_handler(error)
    updater.start_polling()
    logger.info('Starting Polling...')
    updater.idle()


def update_rides(user_id):
    with DB() as db:
        u = db.get_user(user_id)
        my_acc = citybikeAPI.CitybikeAccount(u['username'], u['password'])
        rides = my_acc.get_rides(since=db.get_newest_end_time(user_id))
        for r in rides:
            db.insert_ride(r, user_id)
    return rides


if __name__ == "__main__":
    main()

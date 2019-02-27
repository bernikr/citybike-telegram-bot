import json
import logging

from telegram.ext import *

from get_nearby_stations import get_nearby_stations


def main():
    logging.basicConfig(format='%(asctime)s - %(name)s - %(levelname)s - %(message)s', level=logging.INFO)

    with open('config.json') as f:
        config = json.load(f)

    updater = Updater(config['bot_token'], use_context=True)
    dp = updater.dispatcher
    dp.add_handler(MessageHandler(Filters.location, get_nearby_stations))
    updater.start_polling()
    updater.idle()


if __name__ == "__main__":
    main()

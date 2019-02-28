import json
import logging

from telegram.ext import *

from location_info import location_info_handler


def main():
    logging.basicConfig(format='%(asctime)s - %(name)s - %(levelname)s - %(message)s', level=logging.INFO)

    with open('config.json') as f:
        config = json.load(f)

    per = PicklePersistence(filename='data')
    updater = Updater(config['bot_token'], use_context=True, persistence=per)
    dp = updater.dispatcher
    dp.add_handler(location_info_handler)
    updater.start_polling()
    updater.idle()


if __name__ == "__main__":
    main()

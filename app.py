import json
import logging

from telegram.ext import Updater

from helpBot import help_handler, start_handler
from stationBot import location_info_handler


def main():
    logging.basicConfig(format='%(asctime)s - %(name)s - %(levelname)s - %(message)s', level=logging.INFO)

    with open('config.json') as f:
        config = json.load(f)

    updater = Updater(config['bot_token'], use_context=True)
    dp = updater.dispatcher
    dp.add_handler(location_info_handler)
    dp.add_handler(start_handler)
    dp.add_handler(help_handler)
    updater.start_polling()
    updater.idle()


if __name__ == "__main__":
    main()

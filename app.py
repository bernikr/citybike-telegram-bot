import json
import logging
import os
from logging.handlers import TimedRotatingFileHandler

from telegram.ext import Updater

from helpBot import help_handler, start_handler
from stationBot import location_info_handler

logger = logging.getLogger(__name__)


def main():
    with open('config.json') as f:
        config = json.load(f)

    directory = os.path.dirname('logs/')
    if not os.path.exists(directory):
        os.makedirs(directory)
    handler = TimedRotatingFileHandler("logs/bot.log", when="midnight", interval=1)
    handler.setLevel(logging.INFO)
    handler.suffix = "%Y-%m-%d"
    logging.basicConfig(format="%(asctime)s - %(name)s - %(levelname)s - %(message)s",
                        level=logging.INFO, handlers=[handler, logging.StreamHandler()])

    updater = Updater(config['bot_token'], use_context=True)
    dp = updater.dispatcher
    dp.add_handler(location_info_handler)
    dp.add_handler(start_handler)
    dp.add_handler(help_handler)
    updater.start_polling()
    logger.info("Bot started")
    updater.idle()


if __name__ == "__main__":
    main()

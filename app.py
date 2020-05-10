import json
import logging
import os
import sys
from logging.handlers import TimedRotatingFileHandler
from os import path

from telegram.ext import Updater

from helpBot import help_handler, start_handler
from stationBot import location_info_handler

logging.basicConfig(format="%(asctime)s - %(name)s - %(levelname)s - %(message)s",
                    level=logging.INFO, handlers=[logging.StreamHandler(sys.stdout)])
logger = logging.getLogger(__name__)


def attach_handlers(dp):
    dp.add_handler(location_info_handler)
    dp.add_handler(start_handler)
    dp.add_handler(help_handler)


if __name__ == "__main__":
    if path.exists('config.json'):
        with open('config.json') as f:
            config = json.load(f)

            updater = Updater(config['bot_token'], use_context=True)
            attach_handlers(updater.dispatcher)
            updater.start_polling()
            logger.info("Bot started")
            updater.idle()
    else:
        TOKEN = os.environ.get('TOKEN')
        BASE_URL = os.environ.get('BASE_URL')
        PORT = os.environ.get('PORT')
        logger = logging.getLogger(__name__)

        # Set up the Updater
        updater = Updater(TOKEN, use_context=True)
        attach_handlers(updater.dispatcher)

        # Start the webhook
        updater.start_webhook(listen="0.0.0.0",
                              port=int(PORT),
                              url_path=TOKEN)
        updater.bot.setWebhook(BASE_URL + TOKEN)
        logger.info("Bot started")
        updater.idle()

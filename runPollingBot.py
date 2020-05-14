import json
import logging
import sys
from os import path

from telegram.ext import Updater

from app.bot.modules import attach_handlers

if __name__ == "__main__":
    logging.basicConfig(format="%(asctime)s - %(name)s - %(levelname)s - %(message)s",
                        level=logging.INFO, handlers=[logging.StreamHandler(sys.stdout)])
    logger = logging.getLogger(__name__)

    if path.exists('config.json'):
        with open('config.json') as f:
            config = json.load(f)

            updater = Updater(config['bot_token'], use_context=True)
            attach_handlers(updater.dispatcher)
            updater.start_polling()
            logger.info("Bot started")
            updater.idle()

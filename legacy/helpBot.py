import logging

from telegram.ext import CommandHandler

from utils import reply_function

logger = logging.getLogger(__name__)


def start_message(update, context):
    logger.info("%s: /start" % update.message.chat_id)
    logger.info(update.message.chat)
    reply_function(update, context)("To use this Bot just send a location to get the nearest Citybike stations!")


start_handler = CommandHandler("start", start_message)
help_handler = CommandHandler("help", start_message)

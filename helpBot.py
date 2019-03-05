from telegram.ext import CommandHandler

from utils import reply_function


def start_message(update, context):
    reply_function(update, context)("To use this Bot just send a location to get the nearest Citybike stations!")


start_handler = CommandHandler("start", start_message)
help_handler = CommandHandler("help", start_message)

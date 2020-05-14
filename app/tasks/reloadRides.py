import os

from telegram import Bot


def reload_rides(data):
    bot = Bot(os.environ.get('TOKEN'))
    bot.send_message(int(data), "hello")

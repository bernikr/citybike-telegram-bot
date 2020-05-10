import json
import logging
import os
import sys
from queue import Queue
from threading import Thread

from flask import Flask, request
from telegram import Bot, Update
from telegram.ext import Dispatcher

from helpBot import help_handler, start_handler
from stationBot import location_info_handler

app = Flask(__name__)

TOKEN = os.environ.get('TOKEN')
BASE_URL = os.environ.get('BASE_URL')
PORT = os.environ.get('PORT')

update_queue = Queue()


@app.route('/{}'.format(TOKEN), methods=['POST'])
def respond():
    # retrieve the message in JSON and then transform it to Telegram object
    update = Update.de_json(request.get_json(force=True), bot)
    update_queue.put(update)
    return 'ok'


@app.route('/')
def index():
    return 'hello world'


def attach_handlers(dp):
    dp.add_handler(location_info_handler)
    dp.add_handler(start_handler)
    dp.add_handler(help_handler)


if __name__ == "__main__":
    logging.basicConfig(format="%(asctime)s - %(name)s - %(levelname)s - %(message)s",
                        level=logging.INFO, handlers=[logging.StreamHandler(sys.stdout)])
    logger = logging.getLogger(__name__)

    # Create bot, update queue and dispatcher instances
    bot = Bot(TOKEN)
    dispatcher = Dispatcher(bot, update_queue, use_context=True)

    attach_handlers(dispatcher)

    # Start the thread
    thread = Thread(target=dispatcher.start, name='dispatcher')
    thread.start()

    bot.set_webhook(BASE_URL + TOKEN)

    app.run(host="0.0.0.0", port=PORT)

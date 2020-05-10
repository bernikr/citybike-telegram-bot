import logging
import os
import sys
from queue import Queue
from threading import Thread
from time import sleep

from flask import Flask, request
from telegram import Bot, Update
from telegram.error import RetryAfter
from telegram.ext import Dispatcher

from app.modules import register_blueprints, attach_handlers

logging.basicConfig(format="%(asctime)s - %(name)s - %(levelname)s - %(message)s",
                    level=logging.INFO, handlers=[logging.StreamHandler(sys.stdout)])
logger = logging.getLogger(__name__)

TOKEN = os.environ.get('TOKEN')
BASE_URL = os.environ.get('BASE_URL')
PORT = os.environ.get('PORT')

app = Flask(__name__)
register_blueprints(app)

update_queue = Queue()

# Create bot, update queue and dispatcher instances
bot = Bot(TOKEN)
dispatcher = Dispatcher(bot, update_queue, use_context=True)

attach_handlers(dispatcher)

# Start the thread
thread = Thread(target=dispatcher.start, name='dispatcher')
thread.start()

while True:
    try:
        bot.set_webhook(BASE_URL + TOKEN)
        break
    except RetryAfter as e:
        sleep(e.retry_after)


@app.route('/{}'.format(TOKEN), methods=['POST'])
def respond():
    # retrieve the message in JSON and then transform it to Telegram object
    update = Update.de_json(request.get_json(force=True), bot)
    update_queue.put(update)
    return 'ok'


@app.route('/')
def index():
    return 'hello world'


if __name__ == '__main__':
    app.run(host="0.0.0.0", port=PORT)
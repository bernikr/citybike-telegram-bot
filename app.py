import logging
import os
import sys
from queue import Queue
from threading import Thread

from flask import Flask, request
from telegram import Bot, Update
from telegram.ext import Dispatcher

from bot import attach_handlers

logging.basicConfig(format="%(asctime)s - %(name)s - %(levelname)s - %(message)s",
                    level=logging.INFO, handlers=[logging.StreamHandler(sys.stdout)])
logger = logging.getLogger(__name__)

TOKEN = os.environ.get('TOKEN')
BASE_URL = os.environ.get('BASE_URL')
PORT = os.environ.get('PORT')

app = Flask(__name__)

update_queue = Queue()

# Create bot, update queue and dispatcher instances
bot = Bot(TOKEN)
dispatcher = Dispatcher(bot, update_queue, use_context=True)

attach_handlers(dispatcher)

# Start the thread
thread = Thread(target=dispatcher.start, name='dispatcher')
thread.start()

bot.set_webhook(BASE_URL + TOKEN)


@app.route('/{}'.format(TOKEN), methods=['POST'])
def respond():
    # retrieve the message in JSON and then transform it to Telegram object
    update = Update.de_json(request.get_json(force=True), bot)
    update_queue.put(update)
    return 'ok'


@app.route('/')
def index():
    return 'hello world'

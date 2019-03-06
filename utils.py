# -*- coding: utf-8 -*-
import logging

from telegram import ReplyKeyboardRemove

logger = logging.getLogger(__name__)


def reply_function(update, context):
    def reply(text):
        context.bot.send_message(chat_id=update.message.chat_id, text=text, parse_mode="Markdown",
                                 disable_web_page_preview=True, reply_markup=ReplyKeyboardRemove())
    return reply

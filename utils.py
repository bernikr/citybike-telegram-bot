# -*- coding: utf-8 -*-
import logging

from telegram import ReplyKeyboardRemove

logger = logging.getLogger(__name__)


def normalize_umlauts(text):
    chars = {u'ö': 'oe', u'ä': 'ae', u'ü': 'ue', u'ß': 'ss'}
    for char in chars:
        text = text.replace(char, chars[char])
    return text


def reply_function(update, context):
    def reply(text):
        context.bot.send_message(chat_id=update.message.chat_id, text=text, parse_mode="Markdown",
                                 disable_web_page_preview=True, reply_markup=ReplyKeyboardRemove())
    return reply

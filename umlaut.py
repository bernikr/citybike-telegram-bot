# -*- coding: utf-8 -*-


def normalize(text):
    chars = {u'ö': 'oe', u'ä': 'ae', u'ü': 'ue', u'ß': 'ss'}
    for char in chars:
        text = text.replace(char, chars[char])
    return text

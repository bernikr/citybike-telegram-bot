# -*- coding: utf-8 -*-


def normalize_umlauts(text):
    chars = {u'ö': 'oe', u'ä': 'ae', u'ü': 'ue', u'ß': 'ss'}
    for char in chars:
        text = text.replace(char, chars[char])
    return text


class Location:
    def __init__(self, lat, lon):
        self.lat = float(lat)
        self.lon = float(lon)

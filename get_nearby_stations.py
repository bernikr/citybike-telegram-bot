from operator import itemgetter

import requests
from geopy import distance
from telegram import *


def emoji_number(i):
    if i == 0:
        return "🚫"
    if i <= 5:
        return "⚠️ %d" % i
    return "✅ %d" % i

def get_nearby_stations(update, context):
    if update.message:
        loc = update.message.location

        r = requests.get('https://api.citybik.es/v2/networks/citybike-wien')
        stations = r.json()['network']['stations']
        for s in stations:
            s['distance'] = distance.distance((loc.latitude, loc.longitude), (s['latitude'], s['longitude'])).meters
            s['free_bikes_text'] = emoji_number(s['free_bikes'])
            s['empty_slots_text'] = emoji_number(s['empty_slots'])
        near_stations = sorted(stations, key=itemgetter('distance'))[:3]

        station_text = "{distance:.0f}m - [{name}](http://maps.google.com/maps?q={latitude},{longitude})\n" \
                       "🚲: {free_bikes_text}\n" \
                       "🚩: {empty_slots_text}"
        station_texts = [station_text.format_map(s) for s in near_stations]

        msg_text = "\n\n".join(station_texts)
        context.bot.send_message(chat_id=update.message.chat_id,
                                 text=msg_text, parse_mode="Markdown",
                                 disable_web_page_preview=True,
                                 reply_markup=ReplyKeyboardMarkup([[KeyboardButton(text="📍 Find Stations next to me", request_location=True)]])
                                 )
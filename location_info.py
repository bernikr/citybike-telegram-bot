from operator import itemgetter

import requests
import xmltodict
from geopy import distance
from telegram import ReplyKeyboardRemove
from telegram.ext import MessageHandler, Filters, ConversationHandler, CommandHandler

from persistance import get_setting, set_setting


def count_to_emoji(i):
    i = int(i)
    if i == 0:
        return "❌"
    if i <= 5:
        return "⚠️"
    else:
        return "✅"


def format_meters(m):
    if m < 1000:
        return "%dm" % m
    else:
        return "%0.1fkm" % (m / 1000)


def format_station_text(s):
    station_text = "[{name}](http://maps.google.com/maps?q={latitude},{longitude}) ({distance_text})\n" \
                   "{free_bikes_emoji} Bikes (*{free_bikes}*)\n" \
                   "{free_boxes_emoji} Slots (*{free_boxes}*)"
    return station_text.format(
        **s,
        free_bikes_emoji=count_to_emoji(s['free_bikes']),
        free_boxes_emoji=count_to_emoji(s['free_boxes']),
        distance_text=format_meters(s['distance'])
    )


def load_stations():
    r = requests.get('http://dynamisch.citybikewien.at/citybike_xml.php')
    return xmltodict.parse(r.content)['stations']['station']


def load_stations_with_distance(lat, lon):
    stations = load_stations()
    for s in stations:
        s['distance'] = distance.distance((lat, lon), (s['latitude'], s['longitude'])).meters
    return stations


def get_nearby_stations(update, context):
    if update.message:
        loc = update.message.location
        chat_id = update.message.chat_id

        r = requests.get('http://dynamisch.citybikewien.at/citybike_xml.php')
        stations = load_stations_with_distance(loc.latitude, loc.longitude)
        near_stations = sorted(stations, key=itemgetter('distance'))[:3]

        station_texts = [format_station_text(s) for s in near_stations]

        msg_text = "\n\n".join(station_texts)
        context.bot.send_message(chat_id=chat_id, text=msg_text, parse_mode="Markdown",
                                 disable_web_page_preview=True, reply_markup=ReplyKeyboardRemove())
        if get_setting(chat_id, 'home_station') is None:
            context.bot.send_message(chat_id=chat_id,
                                     text="No home station set.\nUse /sethome to set it")
        else:
            home_station = [s for s in stations if s['id'] == get_setting(chat_id, 'home_station')][0]
            msg_text = "*Your Home Station:*\n" + format_station_text(home_station)
            context.bot.send_message(chat_id=chat_id, text=msg_text, parse_mode="Markdown",
                                     disable_web_page_preview=True)

        return -1


def set_home(update, context):
    context.bot.send_message(chat_id=update.message.chat_id,
                             text="Send a location to set its nearest station as home.\nUse /cancel to cancel, or /delete to remove it")
    return "SETHOME"


def set_home_location(update, context):
    if not update.message:
        return invald_message(update, context)

    loc = update.message.location
    home_station = sorted(load_stations_with_distance(loc.latitude, loc.longitude), key=itemgetter('distance'))[0]
    set_setting(update.message.chat_id, 'home_station', home_station['id'])

    context.bot.send_message(chat_id=update.message.chat_id,
                             text="Home Station set to:\n\n" + format_station_text(home_station), parse_mode="Markdown",
                             disable_web_page_preview=True)

    return -1


def invald_message(update, context):
    context.bot.send_message(chat_id=update.message.chat_id, text="Please send a location next to your Home Station")


def delete_home(update, context):
    set_setting(update.message.chat_id, 'home_station', None)
    context.bot.send_message(chat_id=update.message.chat_id,
                             text="Your Home Station was deleted!\nUse /sethome to set it again")
    return -1


location_info_handler = ConversationHandler(entry_points=[MessageHandler(Filters.location, get_nearby_stations),
                                                          CommandHandler("sethome", set_home)],
                                            states={"SETHOME": [CommandHandler("cancel", lambda x, y: -1),
                                                                CommandHandler("delete", delete_home),
                                                                MessageHandler(Filters.location, set_home_location)]},
                                            fallbacks=[MessageHandler(None, invald_message)])

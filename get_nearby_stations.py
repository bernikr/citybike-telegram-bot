from operator import itemgetter

import requests
import xmltodict
from geopy import distance


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
        return "%0.1fkm" % (m/1000)

def get_nearby_stations(update, context):
    if update.message:
        loc = update.message.location

        r = requests.get('http://dynamisch.citybikewien.at/citybike_xml.php')
        stations = xmltodict.parse(r.content)['stations']['station']
        for s in stations:
            s['distance'] = distance.distance((loc.latitude, loc.longitude), (s['latitude'], s['longitude'])).meters
        near_stations = sorted(stations, key=itemgetter('distance'))[:3]

        station_text = "[{name}](http://maps.google.com/maps?q={latitude},{longitude}) ({distance_text})\n" \
                       "{free_bikes_emoji} Bikes (*{free_bikes}*)\n" \
                       "{free_boxes_emoji} Slots (*{free_boxes}*)"
        station_texts = [station_text.format(**s,
                                             free_bikes_emoji=count_to_emoji(s['free_bikes']),
                                             free_boxes_emoji=count_to_emoji(s['free_boxes']),
                                             distance_text=format_meters(s['distance'])
                                             ) for s in near_stations]

        msg_text = "\n\n".join(station_texts)
        context.bot.send_message(chat_id=update.message.chat_id,
                                 text=msg_text, parse_mode="Markdown",
                                 disable_web_page_preview=True
                                 )
from operator import attrgetter

import requests
import xmltodict
from geopy import distance

from utils import Location


def get_all_stations():
    r = requests.get('http://dynamisch.citybikewien.at/citybike_xml.php')
    return [Station(s) for s in xmltodict.parse(r.content)['stations']['station']]


def get_nearest_stations(loc, n):
    stations = get_all_stations()
    [s.calculate_distance(loc) for s in stations]
    return sorted(stations, key=attrgetter('distance'))[:n]


def get_station_by_id(id):
    station = [s for s in get_all_stations() if s.id == id]
    if len(station) == 1:
        return station[0]
    else:
        return None


class Station:
    def __init__(self, s):
        self.id = int(s['id'])
        self.name = s['name']
        self.free_boxes = int(s['free_boxes'])
        self.free_bikes = int(s['free_bikes'])
        self.loc = Location(s['latitude'], s['longitude'])
        self.distance = None

    def calculate_distance(self, loc):
        self.distance = distance.distance((loc.lat, loc.lon), (self.loc.lat, self.loc.lon)).meters

    def formatted_string(self):
        station_text = "[{s.name}](http://maps.google.com/maps?q={s.loc.lat},{s.loc.lon}){distance_text}\n" \
                       "{free_bikes_emoji} Bikes (*{s.free_bikes}*)\n" \
                       "{free_boxes_emoji} Slots (*{s.free_boxes}*)"
        return station_text.format(
            s=self,
            free_bikes_emoji=self.count_to_emoji(self.free_bikes),
            free_boxes_emoji=self.count_to_emoji(self.free_boxes),
            distance_text=self.distance_text()
        )

    @staticmethod
    def count_to_emoji(i):
        if i == 0:
            return "❌"
        if i <= 5:
            return "⚠️"
        else:
            return "✅"

    def distance_text(self):
        if self.distance is None:
            return ""
        elif self.distance < 1000:
            return " (%dm)" % self.distance
        else:
            return " (%0.1fkm)" % (self.distance / 1000)

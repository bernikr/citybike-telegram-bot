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

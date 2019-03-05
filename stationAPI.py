import datetime
import logging

import requests
import xmltodict

from utils import Location

logger = logging.getLogger(__name__)

station_cache = []
last_cached = datetime.datetime.min
cache_time = datetime.timedelta(seconds=10)


def get_all_stations():
    logger.info("get_all_stations")
    global station_cache, last_cached, cache_time
    if datetime.datetime.now() > last_cached + cache_time:
        r = requests.get('http://dynamisch.citybikewien.at/citybike_xml.php')
        logger.info("Request Station Data from API")
        station_cache = [Station(s) for s in xmltodict.parse(r.content)['stations']['station']]
        last_cached = datetime.datetime.now()
    return station_cache


def get_station_by_id(id):
    logger.info("get_station_by_id")
    station = [s for s in get_all_stations() if s.id == id]
    if len(station) == 1:
        return station[0]
    else:
        return None


def get_nearest_stations(loc, n):
    logger.info("get_nearest_stations")
    stations = get_all_stations()
    station_distance_pairs = [(s, s.loc.distance(loc)) for s in stations]
    return sorted(station_distance_pairs, key=lambda x: x[1])[:n]


class Station:
    def __init__(self, s):
        self.id = int(s['id'])
        self.name = s['name']
        self.free_boxes = int(s['free_boxes'])
        self.free_bikes = int(s['free_bikes'])
        self.loc = Location(s['latitude'], s['longitude'])

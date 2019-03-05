import logging

import persistance
import stationAPI

logger = logging.getLogger(__name__)


def get_nearby_station_info(loc):
    logger.info("get_nearby_station_info")
    return stationAPI.get_nearest_stations(loc, 3)


def get_home_station_info(user_id, loc):
    logger.info("get_home_station_info")
    home_station_id = persistance.get_setting(user_id, 'home_station')
    if home_station_id is not None:
        home_station = stationAPI.get_station_by_id(home_station_id)
        return home_station, home_station.loc.distance(loc)
    return None, None


def set_home(user_id, loc):
    logger.info("set_home")
    home_station, distance = stationAPI.get_nearest_stations(loc, 1)[0]
    persistance.set_setting(user_id, 'home_station', home_station.id)
    return home_station, distance


def delete_home(user_id):
    logger.info("delete_home")
    persistance.set_setting(user_id, 'home_station', None)

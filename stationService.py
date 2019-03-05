import persistance
import stationAPI


def get_nearby_station_info(loc):
    return stationAPI.get_nearest_stations(loc, 3)


def get_home_station_info(user_id, loc):
    home_station = None
    if persistance.get_setting(user_id, 'home_station') is not None:
        home_station = stationAPI.get_station_by_id(persistance.get_setting(user_id, 'home_station'))
        home_station.calculate_distance(loc)
    return home_station


def set_home(user_id, loc):
    home_station = stationAPI.get_nearest_stations(loc, 1)[0]
    persistance.set_setting(user_id, 'home_station', home_station.id)
    return home_station


def delete_home(user_id):
    persistance.set_setting(user_id, 'home_station', None)

import persistance
import stationAPI


def get_nearby_stations_message(user_id, reply, loc):
    near_stations = stationAPI.get_nearest_stations(loc, 3)
    station_texts = [s.formatted_string() for s in near_stations]

    reply("\n\n".join(station_texts))

    if persistance.get_setting(user_id, 'home_station') is None:
        reply("No home station set.\nUse /sethome to set it")
    else:
        home_station = stationAPI.get_station_by_id(persistance.get_setting(user_id, 'home_station'))
        home_station.calculate_distance(loc)
        reply("*Your Home Station:*\n" + home_station.formatted_string())


def set_home(user_id, reply, loc):
    home_station = stationAPI.get_nearest_stations(loc, 1)[0]
    persistance.set_setting(user_id, 'home_station', home_station.id)
    reply("*Home set to:*\n" + home_station.formatted_string())


def delete_home(user_id, reply):
    persistance.set_setting(user_id, 'home_station', None)
    reply("Your Home Station was deleted!\nUse /sethome to set it again")

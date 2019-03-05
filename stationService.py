import persistance
import stationAPI


def get_nearby_stations_message(loc, user_id):
    replies = []

    near_stations = stationAPI.get_nearest_stations(loc, 3)
    station_texts = [s.formatted_string() for s in near_stations]

    replies.append("\n\n".join(station_texts))

    if persistance.get_setting(user_id, 'home_station') is None:
        replies.append("No home station set.\nUse /sethome to set it")
    else:
        home_station = stationAPI.get_station_by_id(persistance.get_setting(user_id, 'home_station'))
        home_station.calculate_distance(loc)
        replies.append("*Your Home Station:*\n" + home_station.formatted_string())

    return replies


def set_home(loc, user_id):
    home_station = stationAPI.get_nearest_stations(loc, 1)[0]
    persistance.set_setting(user_id, 'home_station', home_station.id)
    return "*Home set to:*\n" + home_station.formatted_string()


def delete_home(user_id):
    persistance.set_setting(user_id, 'home_station', None)
    return "Your Home Station was deleted!\nUse /sethome to set it again"

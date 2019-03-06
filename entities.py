from geopy import distance


class Location:
    def __init__(self, lat, lon):
        self.lat = float(lat)
        self.lon = float(lon)

    def distance(self, other_loc):
        return distance.distance((self.lat, self.lon), (other_loc.lat, other_loc.lon)).meters


class Station:
    def __init__(self, s):
        self.id = int(s['id'])
        self.name = s['name']
        self.free_boxes = int(s['free_boxes'])
        self.free_bikes = int(s['free_bikes'])
        self.loc = Location(s['latitude'], s['longitude'])

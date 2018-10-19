import json

import requests

import umlaut as uml
from db import DB


def update_stations_in_db():
    db = DB()

    db.update_stations(get_stations())


def get_stations():
    data = requests.get('https://data.wien.gv.at/daten/geo'
                        '?service=WFS&request=GetFeature&version=1.1.0'
                        '&typeName=ogdwien:CITYBIKEOGD&srsName=EPSG:4326&outputFormat=json')
    json_data = json.loads(data.content)
    raw_data = json_data['features']

    formated_data = [{'id': s['properties']['SE_SDO_ROWID'],
                      'bezirk': s['properties']['BEZIRK'],
                      'name': uml.normalize(s['properties']['STATION']),
                      'lat': s['geometry']['coordinates'][1],
                      'lon': s['geometry']['coordinates'][0]
                      } for s in raw_data]
    return formated_data


if __name__ == "__main__":
    update_stations_in_db()

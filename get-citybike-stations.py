import requests
import json
import csv
import umlaut as uml
from db import DB


def main():
    db = DB()

    print('get data from data.wien.gv.at')
    data = requests.get('https://data.wien.gv.at/daten/geo?service=WFS&request=GetFeature&version=1.1.0&typeName=ogdwien:CITYBIKEOGD&srsName=EPSG:4326&outputFormat=json')
    data = json.loads(data.content)

    output = []

    print('convert data')
    for s in data['features']:
        station = {
            'id': s['properties']['SE_SDO_ROWID'],
            'bezirk': s['properties']['BEZIRK'],
            'name': uml.normalize(s['properties']['STATION']),
            'lat': s['geometry']['coordinates'][1],
            'lon': s['geometry']['coordinates'][0]
        }
        db.update_station(station)

if __name__ == "__main__":
    main()

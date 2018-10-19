import requests
import json
import csv
import umlaut as uml

print('get data from data.wien.gv.at')
data = requests.get('https://data.wien.gv.at/daten/geo?service=WFS&request=GetFeature&version=1.1.0&typeName=ogdwien:CITYBIKEOGD&srsName=EPSG:4326&outputFormat=json')
data = json.loads(data.content)

output = []

print('convert data')
for station in data['features']:
    output_row = [
        uml.normalize(station['properties']['STATION']),
        station['geometry']['coordinates'][1],
        station['geometry']['coordinates'][0]
    ]

    output.append(output_row)

print('writing csv')
with open('stations.csv', 'wb') as f:
    writer = csv.writer(f)
    writer.writerow(['station', 'lat', 'lon'])
    writer.writerows(output)

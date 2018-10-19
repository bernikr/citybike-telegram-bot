import json
import math
from datetime import datetime
import requests
from bs4 import BeautifulSoup
import utils


class CitybikeAccount:
    def __init__(self, username, password):
        login_data = {"username": username, "password": password}

        # start a request session to store the login cookie
        self.s = requests.Session()

        # get the hidden login fields needed to login
        frontpage = self.s.get("https://www.citybikewien.at/de")
        fp = BeautifulSoup(frontpage.content, 'html.parser')
        login = fp.find('form', id='mloginfrm')
        hiddeninputs = login.find_all('input', type='hidden')
        for i in hiddeninputs:
            login_data[i['name']] = i['value']

        # login to the site and save the cookie to the session

        login_url = "https://www.citybikewien.at/de/component/users/?task=user.login&Itemid=101"
        logedin = self.s.post(login_url, data=login_data)
        soup = BeautifulSoup(logedin.content, 'html.parser')
        user_name = soup.select(".user-name-data")
        if len(user_name) < 1:
            print("invalid login")
            exit()
        self.username = user_name[1].get_text()[:-1]

    def get_page_count(self):
        # get the number of existing rows from the website
        page = self.s.get("https://www.citybikewien.at/en/my-rides")
        soup = BeautifulSoup(page.content, 'html.parser')
        tab = soup.select('#content div + p')[0]
        line_num = int(tab.get_text().split(' ')[0])
        return math.ceil(line_num / 5)

    def load_page(self, i):
        data_url = "https://www.citybikewien.at/de/meine-fahrten?start=" + str((i - 1) * 5)
        page = self.s.get(data_url)
        soup = BeautifulSoup(page.content, 'html.parser')
        table = soup.select('#content table tbody')[0]

        rows = []
        for row in table.find_all('tr'):
            r = []

            # go through every cell in a row
            for cell in row.find_all('td'):
                # check if if it is a 'normal' cell with only one data field
                children = cell.findChildren()
                if len(children) <= 1:
                    r.append(cell.get_text())
                else:
                    # if it contains a location and a date split it into two
                    r.append(children[0].get_text())
                    r.append(children[1].get_text() + ' ' + children[2].get_text())

            # Cutoff the Euro-sign from the price and the 'm' from the elevation
            r[5] = r[5][2:]
            r[6] = r[6][:-2]

            # remove newlines and replace umlaute
            r = [utils.normalize_umlauts(t.replace('\n', ' ').strip()) for t in r]

            output_row_obj = {'date': datetime.strptime(r[0], '%d.%m.%Y').date(),
                              'start_station': r[1],
                              'start_time': datetime.strptime(r[2], '%d.%m.%Y %H:%M'),
                              'end_station': r[3],
                              'end_time': datetime.strptime(r[4], '%d.%m.%Y %H:%M'),
                              'price': float(r[5].replace(',', '.')),
                              'elevation': r[6]
                              }
            rows.append(output_row_obj)
        return rows

    def get_rides(self, since=datetime.min):
        pages = self.get_page_count()
        print(str(pages) + " pages found")

        output = []
        newdata = True  # helper for aborting the double loop
        # load all pages and add them to the outputs
        for i in range(1, pages + 1):
            if not newdata:  # check if the inner loop was aborted
                break

            # load the current table
            print("Loading page " + str(i) + "/" + str(pages))

            # read the rows
            for output_row in self.load_page(i):
                # check if the row is newer then the requested timestamp
                if output_row['end_time'] > since:
                    output.append(output_row)
                else:
                    # stop the datacollection if the ride already exists
                    print("All new data loaded. Abort data collection")
                    newdata = False
                    break
        return output


def get_stations():
    data = requests.get('https://data.wien.gv.at/daten/geo'
                        '?service=WFS&request=GetFeature&version=1.1.0'
                        '&typeName=ogdwien:CITYBIKEOGD&srsName=EPSG:4326&outputFormat=json')
    json_data = json.loads(data.content)
    raw_data = json_data['features']

    formatted_data = [{'id': s['properties']['SE_SDO_ROWID'],
                       'bezirk': s['properties']['BEZIRK'],
                       'name': utils.normalize_umlauts(s['properties']['STATION']),
                       'lat': s['geometry']['coordinates'][1],
                       'lon': s['geometry']['coordinates'][0]
                       } for s in raw_data]
    return formatted_data

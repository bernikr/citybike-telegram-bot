import json
import pickle
from datetime import datetime
import requests
from bs4 import BeautifulSoup
import utils
from DBtypes import Station, Ride


class LoginError(IOError):
    pass


class CitybikeAccount:
    def __init__(self, user):
        # start a request session to store the login cookie
        self.user = user
        self.s = requests.Session()
        if user.cookie_dump is None:
            self.login()
            user.cookie_dump = pickle.dumps(self.s.cookies)
        else:
            self.s.cookies.update(pickle.loads(user.cookie_dump))

    def login(self):
        login_data = {"username": self.user.username, "password": self.user.password}
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
            raise LoginError()

    def get_ride_count(self):
        # get the number of existing rows from the website
        page = self.s.get("https://www.citybikewien.at/de/meine-fahrten")
        soup = BeautifulSoup(page.content, 'html.parser')
        tab = soup.select('#content div + p')[0]
        return int(tab.get_text().split(' ')[2])

    def load_page(self, starting_id, since=datetime.min):
        data_url = "https://www.citybikewien.at/de/meine-fahrten?start=" + str(starting_id)
        page = self.s.get(data_url)
        soup = BeautifulSoup(page.content, 'html.parser')
        table = soup.select('#content table tbody')[0]

        rides = []
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

            end_time = datetime.strptime(r[4], '%d.%m.%Y %H:%M')

            if end_time > since:
                ride = Ride(user=self.user,
                            date=datetime.strptime(r[0], '%d.%m.%Y').date(),
                            start_station_name=r[1],
                            start_time=datetime.strptime(r[2], '%d.%m.%Y %H:%M'),
                            end_station_name=r[3],
                            end_time=end_time,
                            price=float(r[5].replace(',', '.')),
                            elevation=r[6]
                            )
                rides.append(ride)
            else:
                break
        return rides

    def get_rides(self, since=None, callback=None, callbackArgs=None):
        if since is None:
            since = datetime.min

        try:
            ride_count = self.get_ride_count()
        except:
            self.login()
            ride_count = self.get_ride_count()

        output = []
        newdata = True  # helper for aborting the double loop
        # load all pages and add them to the outputs
        for i in range(0, ride_count, 5):
            if not newdata:  # check if the inner loop was aborted
                break
            # read the rows
            for ride in self.load_page(i, since=since):
                # check if the row is newer then the requested timestamp
                if ride.end_time > since:
                    output.append(ride)
                    i += 1
                    if callback is not None:
                        callback(current=i, count=ride_count, finished=False, callbackArgs=callbackArgs)
                else:
                    # stop the datacollection if the ride already exists
                    newdata = False
                    break
        if callback is not None:
            callback(current=len(output), finished=True, callbackArgs=callbackArgs)
        return output


def get_stations():
    data = requests.get('https://data.wien.gv.at/daten/geo'
                        '?service=WFS&request=GetFeature&version=1.1.0'
                        '&typeName=ogdwien:CITYBIKEOGD&srsName=EPSG:4326&outputFormat=json')
    json_data = json.loads(data.content)
    raw_data = json_data['features']

    stations = [Station(id=s['properties']['SE_SDO_ROWID'],
                        bezirk=s['properties']['BEZIRK'],
                        name=utils.normalize_umlauts(s['properties']['STATION']),
                        #name=s['properties']['STATION'],
                        lat=s['geometry']['coordinates'][1],
                        lon=s['geometry']['coordinates'][0]
                        ) for s in raw_data]
    return stations

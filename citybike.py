import requests
from bs4 import BeautifulSoup
import umlaut

class CitybikeAccount:
    def __init__(self, username, password):
        login_data = {}
        login_data["username"] = username
        login_data["password"] = password

        # start a request session to store the login cookie
        self.s = requests.Session()

        # get the hidden login fields needed to login
        frontpage = self.s.get("https://www.citybikewien.at/de")
        fp = BeautifulSoup(frontpage.content, 'html.parser')
        login = fp.find('form', id='mloginfrm')
        hiddeninputs = login.find_all('input', type='hidden')
        for i in hiddeninputs:
            login_data[i['name']]=i['value']
            
        # login to the site and save the cookie to the session

        login_url = "https://www.citybikewien.at/de/component/users/?task=user.login&Itemid=101"
        logedin = self.s.post(login_url, data=login_data)
        soup = BeautifulSoup(logedin.content, 'html.parser')
        user_name = soup.select(".user-name-data")
        if(len(user_name) < 1):
            print("invalid login")
            exit()
        self.username = user_name[1].get_text()[:-1]

    def get_page_count(self):
        # get the number of existing rows from the website
        page = self.s.get("https://www.citybikewien.at/en/my-rides")
        soup = BeautifulSoup(page.content, 'html.parser')
        tab = soup.select('#content div + p')[0]
        line_num = int(tab.get_text().split(' ')[0])
        return(line_num/5+1)

    def load_page(self,i):
        data_url = "https://www.citybikewien.at/de/meine-fahrten?start=" + str((i-1)*5)
        page = self.s.get(data_url)
        soup = BeautifulSoup(page.content, 'html.parser')
        table = soup.select('#content table tbody')[0]

        rows = []
        for row in table.find_all('tr'):
            output_row = []
            
            # go through every cell in a row
            for cell in row.find_all('td'):
                # check if if it is a 'normal' cell with only one data field
                children = cell.findChildren()
                if len(children) <= 1:
                    output_row.append(cell.get_text())
                else:
                    # if it contains a location and a date split it into two
                    output_row.append(children[0].get_text())
                    output_row.append(children[1].get_text() + ' ' + children[2].get_text())

            # Cutoff the Euro-sign from the price and the 'm' from the elevation
            output_row[5] = output_row[5][2:]
            output_row[6] = output_row[6][:-2]
                
            # remove newlines and replace umlaute
            output_row = [umlaut.normalize(t.replace('\n', ' ').strip()) for t in output_row]

            rows.append(output_row)
            
        return rows
            

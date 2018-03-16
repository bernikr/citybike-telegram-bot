# -*- coding: utf-8 -*-

import requests
from bs4 import BeautifulSoup
import csv
import getpass

# get the login data from the user
login_data = {}
login_data["username"] = raw_input("Username: ")
login_data["password"] = getpass.getpass("Password: ")

# start a request session to store the login cookie
s = requests.Session()

# get the hidden login fields needed to login
frontpage = s.get("https://www.citybikewien.at/de")
fp = BeautifulSoup(frontpage.content, 'html.parser')
login = fp.find('form', id='mloginfrm')
hiddeninputs = login.find_all('input', type='hidden')
for i in hiddeninputs:
    login_data[i['name']]=i['value']
    
# login to the site and save the cookie to the session
print("logging in")
login_url = "https://www.citybikewien.at/de/component/users/?task=user.login&Itemid=101"
logedin = s.post(login_url, data=login_data)
soup = BeautifulSoup(logedin.content, 'html.parser')
user_name = soup.select(".user-name-data")
if(len(user_name) < 1):
    print("invalid login")
    exit()
print("loged in as: " + user_name[1].get_text()[:-1])

# append the output rows to this array
output = []

# get the number of existing rows from the website
page = s.get("https://www.citybikewien.at/en/my-rides")
soup = BeautifulSoup(page.content, 'html.parser')
tab = soup.select('#content div + p')[0]
line_num = int(tab.get_text().split(' ')[0])
print(str(line_num) + " rides found")


# load all pages and add them to the outputs
for i in range(0, line_num, 5):
    # load the current table
    print("Loading page " + str(i/5+1) + "/" + str(line_num/5+1))
    data_url = "https://www.citybikewien.at/de/meine-fahrten?start=" + str(i)
    page = s.get(data_url)
    soup = BeautifulSoup(page.content, 'html.parser')
    table = soup.select('#content table tbody')[0]

    # read the rows
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

        # remove newlines and utf-8 encode everything
        output_row = [t.replace('\n', ' ').strip().encode('utf-8') for t in output_row]

        # Cutoff the Euro-sign from the price
        output_row[6] = output_row[6][4:]

        # add the row to the output array
        output.append(output_row)

# write the output array to the csv and add headers
print("writing csv")
with open('export.csv', 'wb') as f:
    writer = csv.writer(f)
    writer.writerow(['Datum', 'Fahrtnummer', 'Entlehnstation', 'Entlehnzeit', 'Rückgabestation', 'Rückgabezeit', 'Preis'])
    writer.writerows(output)

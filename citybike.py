# -*- coding: utf-8 -*-

import requests
from bs4 import BeautifulSoup
import csv
import getpass

login_data = {}
login_data["username"] = raw_input("Username: ")
login_data["password"] = getpass.getpass("Password: ")

s = requests.Session()

frontpage = s.get("https://www.citybikewien.at/de")
fp = BeautifulSoup(frontpage.content, 'html.parser')
login = fp.find('form', id='mloginfrm')
hiddeninputs = login.find_all('input', type='hidden')
for i in hiddeninputs:
    login_data[i['name']]=i['value']

print("logging in")
# login to the site and save the cookie to the session
login_url = "https://www.citybikewien.at/de/component/users/?task=user.login&Itemid=101"
logedin = s.post(login_url, data=login_data)
soup = BeautifulSoup(logedin.content, 'html.parser')
user_name = soup.select(".user-name-data")
if(len(user_name) < 1):
    print("invalid login")
    exit()
print("loged in as: " + user_name[1].get_text()[:-1])


output = []

data_url = "https://www.citybikewien.at/en/my-rides"

page = s.get(data_url)
soup = BeautifulSoup(page.content, 'html.parser')
tab = soup.select('#content div + p')[0]
line_num = int(tab.get_text().split(' ')[0])
print(str(line_num) + " rides found")

for i in range(0, line_num, 5):
    print("Loading page " + str(len(output)/5 + 1))
    data_url = "https://www.citybikewien.at/de/meine-fahrten?start=" + str(len(output))
    page = s.get(data_url)
    soup = BeautifulSoup(page.content, 'html.parser')
    tab = soup.select('#content table tbody')[0]

    rows = tab.find_all('tr')
        
    for row in rows:
        output_row = []

        cell = row.find_all('td')
        output_row.append(cell[0].get_text().strip('\t\r\n').encode('utf-8'))
        output_row.append(cell[1].get_text().strip('\t\r\n').encode('utf-8'))
        abfahrt = cell[2].find_all('span')
        output_row.append(abfahrt[0].get_text().strip('\t\r\n').encode('utf-8'))
        output_row.append(abfahrt[1].get_text().strip('\t\r\n').encode('utf-8'))
        output_row.append(abfahrt[2].get_text().strip('\t\r\n').encode('utf-8'))
        ankunft = cell[3].find_all('span')
        output_row.append(ankunft[0].get_text().strip('\t\r\n').encode('utf-8'))
        output_row.append(ankunft[1].get_text().strip('\t\r\n').encode('utf-8'))
        output_row.append(ankunft[2].get_text().strip('\t\r\n').encode('utf-8'))
        output_row.append(cell[4].get_text().strip('\t\r\n').encode('utf-8'))
        
        output.append(output_row)

print("writing csv")
myFile = open('export.csv', 'wb')
with myFile:
    writer = csv.writer(myFile)
    writer.writerow(['Datum', 'Fahrtnummer', 'Entlehnstation', 'Entlehndatum', 'Entlehnzeit', 'Rückgabestation', 'Rückgabedatum', 'Rückgabezeit', 'Preis'])
    writer.writerows(output)

import requests
from bs4 import BeautifulSoup

s = requests.Session() 

login_data = {"username":"", "password":""}

frontpage = s.get("https://www.citybikewien.at/de")
fp = BeautifulSoup(frontpage.content, 'html.parser')
login = fp.find('form', id='mloginfrm')
hiddeninputs = login.find_all('input', type='hidden')
for i in hiddeninputs:
    login_data[i['name']]=i['value']

# login to the site and save the cookie to the session
page = s.post("https://www.citybikewien.at/de/component/users/?task=user.login&Itemid=101", data=login_data)

output = []

for i in range(1,10):
    
    page = s.get("https://www.citybikewien.at/de/meine-fahrten?start=" + str(len(output)))
    soup = BeautifulSoup(page.content, 'html.parser')
    tab = soup.select('#content table tbody')[0]
    print("got page " + str(i))


    for row in tab.find_all('tr'):
        output_row = []

        cell = row.find_all('td')
        output_row.append(cell[0].get_text().strip('\t\r\n'))
        output_row.append(cell[1].get_text().strip('\t\r\n'))
        abfahrt = cell[2].find_all('span')
        output_row.append(abfahrt[0].get_text().strip('\t\r\n'))
        output_row.append(abfahrt[1].get_text().strip('\t\r\n'))
        output_row.append(abfahrt[2].get_text().strip('\t\r\n'))
        ankunft = cell[3].find_all('span')
        output_row.append(ankunft[0].get_text().strip('\t\r\n'))
        output_row.append(ankunft[1].get_text().strip('\t\r\n'))
        output_row.append(ankunft[2].get_text().strip('\t\r\n'))
        output_row.append(cell[4].get_text().strip('\t\r\n'))
        
        output.append(output_row)
        
print(output)

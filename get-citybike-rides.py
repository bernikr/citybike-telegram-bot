import requests
from bs4 import BeautifulSoup
import csv
import getpass
import umlaut
import os.path
from datetime import datetime

outputfile = 'rides.csv'
last_existing_time = datetime.min # saves the time of the last known ride

# get the login data from the user
login_data = {}
login_data["username"] = raw_input("Username: ")
login_data["password"] = getpass.getpass("Password: ")

# look if a csv file already exists so only new rides will be loaded
# if there are any errors (not existing, wrong content in column, etc) just assume the file needs to be (re)created
try:
    with open(outputfile, 'r') as f:
        last_existing_time = datetime.strptime(list(csv.reader(f))[-1][3], '%d.%m.%Y %H:%M')
except:
    print("Error in reading existing file. It will be created or overwritten.")
    
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


newdata = True #helper for aborting the double loop
# load all pages and add them to the outputs
for i in range(0, line_num, 5):
    if(not newdata): #check if the inner loop was aborted
        break
    
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

        # Cutoff the Euro-sign from the price
        output_row[6] = output_row[6][2:]
        
        # remove newlines and replace umlaute
        output_row = [umlaut.normalize(t.replace('\n', ' ').strip()) for t in output_row]

        # check if the row is newer then the last ride from the csv
        time = datetime.strptime(output_row[3], '%d.%m.%Y %H:%M')
        if(time > last_existing_time):
            # add the row to the output array
            output.append(output_row)
        else:
            # stop the datacollection if the ride already exists
            print("All new data loaded. Abort data collection")
            newdata = False
            break

# reverse the output array so the newest rides come last
output.reverse()

# write the output array to the csv
print("writing csv")
with open(outputfile, 'ab') as f:
    writer = csv.writer(f)
    # if it is a new file or has an error, delete the content and write a header
    if(last_existing_time == datetime.min):
        f.truncate()
        writer.writerow(['date', 'id', 'start_station', 'start_time', 'end_station', 'end_time', 'price'])
    writer.writerows(output)

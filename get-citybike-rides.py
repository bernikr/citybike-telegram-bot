import csv
from datetime import datetime

from citybike import CitybikeAccount


def main():
    outputfile = 'rides.csv'
    last_existing_time = datetime.min  # saves the time of the last known ride

    # get the login data from the user
    username = input("Username: ")
    password = input("Password: ")

    # look if a csv file already exists so only new rides will be loaded
    # if there are any errors (not existing, wrong content in column, etc) just assume the file needs to be (re)created
    try:
        with open(outputfile, 'r') as f:
            last_existing_time = datetime.strptime(list(csv.reader(f))[-1][2], '%d.%m.%Y %H:%M')
    except:
        print("Error in reading existing file. It will be created or overwritten.")

    # start a request session to store the login cookie
    print("logging in")
    my_acc = CitybikeAccount(username, password)
    print("loged in as: " + my_acc.username)

    # append the output rows to this array
    output = []

    pages = my_acc.get_page_count()
    print(str(pages) + " pages found")

    newdata = True  # helper for aborting the double loop
    # load all pages and add them to the outputs
    for i in range(1, pages + 1):
        if not newdata:  # check if the inner loop was aborted
            break

        # load the current table
        print("Loading page " + str(i) + "/" + str(pages))

        # read the rows
        for output_row in my_acc.load_page(i):
            # check if the row is newer then the last ride from the csv
            time = datetime.strptime(output_row[2], '%d.%m.%Y %H:%M')
            if time > last_existing_time:
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
    with open(outputfile, 'a') as f:
        writer = csv.writer(f)
        # if it is a new file or has an error, delete the content and write a header
        if last_existing_time == datetime.min:
            f.truncate()
            writer.writerow(['date', 'start_station', 'start_time', 'end_station', 'end_time', 'price', 'elevation'])
        writer.writerows(output)


if __name__ == "__main__":
    main()

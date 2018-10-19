import csv
from datetime import datetime
import sqlite3
from citybike import CitybikeAccount
from db import DB


def main():
    db = DB()

    for u in db.get_users():
        # start a request session to store the login cookie
        print("logging in")
        my_acc = CitybikeAccount(u['username'], u['password'])
        print("loged in as: " + my_acc.username)

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
                db.insert_ride(output_row, u['id'])


if __name__ == "__main__":
    main()

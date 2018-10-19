from citybike import CitybikeAccount
from db import DB


def main():
    db = DB()

    for u in db.get_users():
        # start a request session to store the login cookie
        print("logging in")
        my_acc = CitybikeAccount(u['username'], u['password'])
        print("loged in as: " + my_acc.username)

        rides = my_acc.get_rides(since=db.get_newest_end_time(u['id']))

        for r in rides:
            db.insert_ride(r, u['id'])


if __name__ == "__main__":
    main()

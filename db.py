import sqlite3


class DB:
    def __init__(self):
        self.db = sqlite3.connect('citybikes.db')
        self.db.row_factory = sqlite3.Row

        self.c = self.db.cursor()
        self.c.execute('CREATE TABLE IF NOT EXISTS user ('
                       'id INTEGER PRIMARY KEY,'
                       'username TEXT NOT NULL,'
                       'password TEXT'
                       ')')

        self.c.execute('CREATE TABLE IF NOT EXISTS station ('
                       'id INTEGER PRIMARY KEY,'
                       'name TEXT UNIQUE NOT NULL,'
                       'bezirk INTEGER,'
                       'lat NUMERIC,'
                       'lon NUMERIC)')

        self.c.execute('CREATE TABLE IF NOT EXISTS ride ('
                       'id INTEGER PRIMARY KEY,'
                       'user INTEGER REFERENCES user(id),'
                       'date TEXT,'
                       'start_station INTEGER REFERENCES station(id),'
                       'end_station INTEGER REFERENCES station(id),'
                       'start_time TEXT,'
                       'end_time TEXT,'
                       'price NUMERIC,'
                       'elevation NUMERIC,'
                       'unique (user, start_time) ON CONFLICT IGNORE'
                       ')')

        self.db.commit()

    def get_users(self):
        self.c.execute('SELECT * FROM user')
        return self.c.fetchall()

    def insert_ride(self, ride, user_id):
        ride['user_id'] = user_id
        self.c.execute('INSERT INTO ride'
                       '(user, date, start_station, end_station, start_time, end_time, price, elevation)'
                       'VALUES (:user_id, :date,'
                       '(SELECT id FROM station WHERE name=:start_station),'
                       '(SELECT id FROM station WHERE name=:end_station),'
                       ':start_time, :end_time, :price, :elevation)', ride)
        self.db.commit()

    def update_station(self, station):
        self.c.execute('INSERT OR REPLACE INTO station(id, name, bezirk, lat, lon)'
                       'VALUES(:id, :name, :bezirk, :lat, :lon)', station)
        self.db.commit()


import sqlite3
from datetime import datetime


class DB:
    def __enter__(self):
        self.db = sqlite3.connect('citybikes.db', detect_types=sqlite3.PARSE_DECLTYPES)
        self.db.row_factory = sqlite3.Row
        self.c = self.db.cursor()
        return self

    def __exit__(self, exc_type, exc_val, exc_tb):
        self.db.close()

    def init_tables(self):
        self.c.execute('CREATE TABLE IF NOT EXISTS user ('
                       'id INTEGER PRIMARY KEY,'
                       'username TEXT NOT NULL,'
                       'password TEXT,'
                       'cookie_dump BLOB'
                       ')')

        self.c.execute('CREATE TABLE IF NOT EXISTS station ('
                       'id INTEGER PRIMARY KEY,'
                       'name TEXT UNIQUE NOT NULL,'
                       'bezirk INTEGER,'
                       'lat NUMERIC,'
                       'lon NUMERIC)')

        self.c.execute('CREATE TABLE IF NOT EXISTS ride ('
                       'id INTEGER PRIMARY KEY,'
                       'user INTEGER NOT NULL REFERENCES user(id),'
                       'date DATE,'
                       'start_station INTEGER NOT NULL REFERENCES station(id),'
                       'end_station INTEGER NOT NULL REFERENCES station(id),'
                       'start_time DATETIME,'
                       'end_time DATETIME,'
                       'price NUMERIC,'
                       'elevation NUMERIC,'
                       'UNIQUE (user, start_time) ON CONFLICT IGNORE'
                       ')')

        self.db.commit()

    def insert_ride(self, ride, user_id):
        ride['user_id'] = user_id
        self.c.execute('INSERT INTO ride'
                       '(user, date, start_station, end_station, start_time, end_time, price, elevation)'
                       'VALUES (:user_id, :date,'
                       '(SELECT id FROM station WHERE name=:start_station),'
                       '(SELECT id FROM station WHERE name=:end_station),'
                       ':start_time, :end_time, :price, :elevation)', ride)
        self.db.commit()

    def update_stations(self, stations):
        self.c.executemany('INSERT OR REPLACE INTO station(id, name, bezirk, lat, lon)'
                           'VALUES(:id, :name, :bezirk, :lat, :lon)', stations)
        self.db.commit()

    def get_newest_end_time(self, user_id):
        self.c.execute('SELECT MAX(end_time) FROM ride WHERE user=?', [user_id])
        timestamp = self.c.fetchone()[0]
        if timestamp is not None:
            return datetime.strptime(timestamp, '%Y-%m-%d %H:%M:%S')
        else:
            return datetime.min

    def get_user(self, user_id):
        self.c.execute('SELECT * FROM user WHERE id=?', [user_id])
        return self.c.fetchone()

    def delete_user(self, user_id):
        self.c.execute('DELETE FROM user WHERE id=?', [user_id])
        self.c.execute('DELETE FROM ride WHERE user=?', [user_id])
        self.db.commit()

    def new_user(self, user_id, username, password):
        self.c.execute('INSERT INTO user(id, username, password) VALUES (?,?,?)', [user_id, username, password])
        self.db.commit()

    def store_cookie_dump(self, user_id, cookie_dump):
        self.c.execute('UPDATE user SET cookie_dump=? WHERE id=?', (cookie_dump, user_id))
        self.db.commit()

import json
import os

DATA_FILE = "data.json"

if os.path.isfile(DATA_FILE):
    with open(DATA_FILE) as f:
        data = json.load(f)
else:
    data = {}


def write_data():
    with open(DATA_FILE, 'w') as f:
        json.dump(data, f)


def set_setting(user_id, setting, value):
    user_id = str(user_id)
    setting = str(setting)

    if user_id not in data:
        data[user_id] = {}
    data[user_id][setting] = value
    write_data()


def get_setting(user_id, setting):
    user_id = str(user_id)
    setting = str(setting)

    if user_id not in data or setting not in data[user_id]:
        return None
    else:
        return data[user_id][setting]

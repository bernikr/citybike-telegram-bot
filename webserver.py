from datetime import datetime, date

from flask import Flask, request, Response, json
from flask.json import JSONEncoder

from citybikeAPI import CitybikeAccount, LoginError

app = Flask(__name__)


@app.route('/rides', methods=['POST'])
def hello_world():
    try:
        acc = CitybikeAccount({'username': str(request.form['username']), 'password': str(request.form['password'])})
    except LoginError:
        return json.dumps({'success': False, 'error': 'Login invalid'})

    def generate():
        yield '{"success": true, '
        yield '"user": ' + json.dumps(dict(username=acc.user['username'], name=acc.user['name'])) + ", "

        rides = acc.get_rides(yield_ride_count=True)

        yield '"count": ' + str(next(rides)) + ", "

        yield '"rides": [\n'
        first = True
        for ride in rides:
            if not first:
                yield ',\n'
            yield json.dumps(ride, cls=DateTimeEncoder)
            first = False
        yield '\n]}'
    return Response(generate(), mimetype='text/json')


class DateTimeEncoder(JSONEncoder):
    def default(self, o):
        if isinstance(o, datetime):
            return o.strftime("%Y-%m-%dT%H:%M:%S")
        if isinstance(o, date):
            return o.strftime("%Y-%m-%d")


if __name__ == "__main__":
    app.run()

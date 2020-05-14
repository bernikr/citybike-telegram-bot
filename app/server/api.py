from datetime import datetime, date
from json import JSONEncoder

from flask import Blueprint, json, request, Response

from app.apis.citybikeAPI import CitybikeAccount, LoginError

api = Blueprint('api', __name__, url_prefix='/api')


@api.route('/rides', methods=['POST'])
def hello_world():
    args = request.json
    since = None
    try:
        acc = CitybikeAccount({'username': args['username'], 'password': args['password']})
        if "since" in args:
            since = datetime.fromisoformat(args['since'])
    except LoginError:
        return json.dumps({'success': False, 'error': 'Login invalid'})
    except KeyError:
        return json.dumps({'success': False, 'error': 'Wrong Arguments'})
    except ValueError:
        return json.dumps({'success': False, 'error': 'Invalid Datetime'})

    def generate():
        yield '{"success": true, '
        yield '"user": ' + json.dumps(dict(username=acc.user['username'], name=acc.user['name'])) + ", "

        rides = acc.get_rides(yield_ride_count=True, since=since)

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


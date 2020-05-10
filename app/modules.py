from app.api import api
from app.helpBot import start_handler, help_handler
from app.stationBot import location_info_handler


def attach_handlers(dp):
    dp.add_handler(location_info_handler)
    dp.add_handler(start_handler)
    dp.add_handler(help_handler)


def register_blueprints(app):
    app.register_blueprint(api)
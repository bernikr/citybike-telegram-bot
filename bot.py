from helpBot import start_handler, help_handler
from stationBot import location_info_handler


def attach_handlers(dp):
    dp.add_handler(location_info_handler)
    dp.add_handler(start_handler)
    dp.add_handler(help_handler)

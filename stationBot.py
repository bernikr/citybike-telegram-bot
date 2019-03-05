import logging

from telegram.ext import MessageHandler, Filters, ConversationHandler, CommandHandler

import stationService
from stationMapper import station_to_text
from utils import Location, reply_function

logger = logging.getLogger(__name__)


def get_nearby_stations(update, context):
    if update.message:
        logger.info("%s: request for location info in chat " % update.message.chat_id)
        nearby = stationService.get_nearby_station_info(get_location_from_update(update))
        nearby_text = [station_to_text(s) for s in nearby]
        reply_function(update, context)("\n\n".join(nearby_text))

        home_station = stationService.get_home_station_info(update.message.chat_id, get_location_from_update(update))
        if home_station is None:
            reply_function(update, context)("No home station set.\nUse /sethome to set it")
        else:
            reply_function(update, context)("*Your Home Station:*\n" + station_to_text(home_station))
    return -1


def set_home_init(update, context):
    logger.info("%s: request to set home" % update.message.chat_id)
    reply_function(update, context)("Send a location to set its nearest station as home.\nUse /cancel to cancel, "
                                    "or /delete to remove it")
    return "SETHOME"


def set_home(update, context):
    if not update.message:
        return invalid_message(update, context)

    logger.info("%s: sent home location" % update.message.chat_id)
    home_station = stationService.set_home(update.message.chat_id, get_location_from_update(update))
    reply_function(update, context)("*Home set to:*\n" + station_to_text(home_station))
    return -1


def invalid_message(update, context):
    logger.info("%s: sent invalid message location" % update.message.chat_id)
    logger.info(update.message)
    reply_function(update, context)("Please send a location next to your Home Station")


def delete_home(update, context):
    logger.info("%s: request to delete home" % update.message.chat_id)
    stationService.delete_home(update.message.chat_id)
    reply_function(update, context)("Your Home Station was deleted!\nUse /sethome to set it again")
    return -1


def get_location_from_update(update):
    return Location(update.message.location.latitude, update.message.location.longitude)


location_info_handler = ConversationHandler(entry_points=[MessageHandler(Filters.location, get_nearby_stations),
                                                          CommandHandler("sethome", set_home_init)],
                                            states={"SETHOME": [CommandHandler("cancel", lambda x, y: -1),
                                                                CommandHandler("delete", delete_home),
                                                                MessageHandler(Filters.location, set_home)]},
                                            fallbacks=[MessageHandler(None, invalid_message)])

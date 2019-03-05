from telegram import ReplyKeyboardRemove
from telegram.ext import MessageHandler, Filters, ConversationHandler, CommandHandler

import stationService
from utils import Location


def reply(update, context, replies):
    if isinstance(replies, str):
        replies = [replies]
    for r in replies:
        context.bot.send_message(chat_id=update.message.chat_id, text=r, parse_mode="Markdown",
                                 disable_web_page_preview=True, reply_markup=ReplyKeyboardRemove())


def get_nearby_stations(update, context):
    if update.message:
        reply(update, context, stationService.get_nearby_stations_message(
            get_location_from_update(update),
            update.message.chat_id
        ))
        return -1


def set_home_init(update, context):
    reply(update, context,
          "Send a location to set its nearest station as home.\nUse /cancel to cancel, or /delete to remove it")
    return "SETHOME"


def set_home(update, context):
    if not update.message:
        return invald_message(update, context)

    replies = stationService.set_home(get_location_from_update(update), update.message.chat_id)
    reply(update, context, replies)
    return -1


def invald_message(update, context):
    context.bot.send_message(chat_id=update.message.chat_id, text="Please send a location next to your Home Station")


def delete_home(update, context):
    reply(update, context, stationService.delete_home(update.message.chat_id))
    return -1


def get_location_from_update(update):
    return Location(update.message.location.latitude, update.message.location.longitude)


location_info_handler = ConversationHandler(entry_points=[MessageHandler(Filters.location, get_nearby_stations),
                                                          CommandHandler("sethome", set_home_init)],
                                            states={"SETHOME": [CommandHandler("cancel", lambda x, y: -1),
                                                                CommandHandler("delete", delete_home),
                                                                MessageHandler(Filters.location, set_home)]},
                                            fallbacks=[MessageHandler(None, invald_message)])

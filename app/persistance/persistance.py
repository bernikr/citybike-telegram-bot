import json
import logging

from sqlalchemy import Column, types, PrimaryKeyConstraint

from app.persistance.db import Base, DB, db_engine

logger = logging.getLogger(__name__)


#################
# User Settings #
#################
class UserSetting(Base):
    __tablename__ = 'user_setting'

    user_id = Column(types.Integer)
    setting = Column(types.String)
    value = Column(types.String)

    __table_args__ = (PrimaryKeyConstraint('user_id', 'setting'),)


def set_setting(user_id, setting, value):
    logging.info("set_setting, user: %s, %s: %s" % (user_id, setting, value))
    user_id = int(user_id)
    setting = str(setting)
    value = json.dumps(value)

    session = DB()
    session.merge(UserSetting(user_id=user_id, setting=setting, value=value))
    session.commit()


def get_setting(user_id, setting):
    logging.info("get_setting, user: %s, %s" % (user_id, setting))
    user_id = int(user_id)
    setting = str(setting)

    session = DB()
    setting = session.query(UserSetting) \
        .filter(UserSetting.user_id == user_id, UserSetting.setting == setting) \
        .first()

    if setting is None:
        return None
    else:
        return json.loads(setting.value)


####
# Create all database tables on load
Base.metadata.create_all(db_engine)

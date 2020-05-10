import json
import logging
import os

from sqlalchemy import Column, types, create_engine, PrimaryKeyConstraint
from sqlalchemy.ext.declarative import declarative_base
from sqlalchemy.orm import scoped_session, sessionmaker

logger = logging.getLogger(__name__)
Base = declarative_base()

db_engine = create_engine(os.environ.get('DATABASE_URL', 'sqlite:///citybikes.db'))
DB = scoped_session(sessionmaker(bind=db_engine))


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
    setting = session.query(UserSetting).filter(UserSetting.user_id == user_id, UserSetting.setting == setting).first()
    if setting is None:
        return None
    else:
        return json.loads(setting.value)


####
# Create all database tables on load
Base.metadata.create_all(db_engine)

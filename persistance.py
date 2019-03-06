import json
import logging

from sqlalchemy import Column, types, create_engine, PrimaryKeyConstraint
from sqlalchemy.ext.declarative import declarative_base
from sqlalchemy.orm import scoped_session, sessionmaker

logger = logging.getLogger(__name__)
Base = declarative_base()

db_engine = create_engine('sqlite:///citybikes.db')
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


#######################
# Static Station Data #
#######################
class Station(Base):
    __tablename__ = 'station'

    id = Column(types.Integer, primary_key=True)
    name = Column(types.String, nullable=False)
    lat = Column(types.Float)
    lon = Column(types.Float)


def save_stations_data(stations):
    session = DB()
    for s in stations:
        session.merge(Station(id=s.id, name=s.name, lat=s.loc.lat, lon=s.loc.lon))
    session.commit()


####
# Create all database tables on load
Base.metadata.create_all(db_engine)

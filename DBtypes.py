from sqlalchemy import Column, UniqueConstraint, ForeignKey
from sqlalchemy import types
from sqlalchemy.ext.declarative import declarative_base
from sqlalchemy.orm import relationship

Base = declarative_base()


class User(Base):
    __tablename__ = 'user'

    id          = Column(types.Integer, primary_key=True)
    username    = Column(types.String)
    password    = Column(types.String)
    cookie_dump = Column(types.Binary)

    rides = relationship('Ride', back_populates="user")

    def __repr__(self):
        return "<User(id='%s', username='%s'>" % (self.id, self.username)


class Station(Base):
    __tablename__ = 'station'

    id      = Column(types.Integer, primary_key=True)
    name    = Column(types.String, unique=True, nullable=False)
    bezirk  = Column(types.Integer)
    lat     = Column(types.Float)
    lon     = Column(types.Float)

    def __repr__(self):
        return "<Station(id='%s', name='%s'>" % (self.id, self.name)


class Ride(Base):
    __tablename__ = 'ride'

    id                  = Column(types.Integer, primary_key=True)
    user_id             = Column(types.Integer, ForeignKey(User.id))
    date                = Column(types.Date)
    start_station_id    = Column(types.Integer, ForeignKey(Station.id))
    end_station_id      = Column(types.Integer, ForeignKey(Station.id))
    start_time          = Column(types.DateTime)
    end_time            = Column(types.DateTime)
    price               = Column(types.Float)
    elevation           = Column(types.Integer)

    user            = relationship(User, back_populates="rides")
    start_station   = relationship(Station, foreign_keys=start_station_id)
    end_station     = relationship(Station, foreign_keys=end_station_id)

    __table_args__ = (UniqueConstraint(user_id, start_time),)

    def __init__(self, user=None, start_station_name=None, start_time=None, end_station_name=None, end_time=None,
                 price=None, elevation=None, date=None):
        self.user = user
        self.date = date
        self.start_time = start_time
        self.end_time = end_time
        self.price = price
        self.elevation = elevation
        self.start_station_name = start_station_name
        self.end_station_name = end_station_name

    def link_stations(self, session):
        self.start_station = get_station_by_name(session, self.start_station_name)
        self.end_station = get_station_by_name(session, self.end_station_name)


def get_station_by_name(session, name):
    return session.query(Station).filter(Station.name == name).one()


def create_all(engine):
    Base.metadata.create_all(engine)
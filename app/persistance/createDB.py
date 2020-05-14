from app.persistance.db import Base, db_engine
from app.persistance.persistance import UserSetting
from app.persistance.tasks import Task


def create_db():
    Base.metadata.create_all(db_engine)

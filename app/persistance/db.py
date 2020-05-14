import os

from sqlalchemy import create_engine
from sqlalchemy.ext.declarative import declarative_base
from sqlalchemy.orm import sessionmaker, scoped_session

Base = declarative_base()
db_engine = create_engine(os.environ.get('DATABASE_URL', 'sqlite:///citybikes.db'))
DB = scoped_session(sessionmaker(bind=db_engine))

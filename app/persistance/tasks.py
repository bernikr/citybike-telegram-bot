import logging

from sqlalchemy import Column, types

from app.persistance.db import Base, DB

logger = logging.getLogger(__name__)


#################
# User Settings #
#################
class Task(Base):
    __tablename__ = 'task'

    id = Column(types.Integer, primary_key=True)
    task = Column(types.String)
    time = Column(types.DateTime)
    data = Column(types.String)


def create_task(task, time, data=None):
    logging.info("create_tast, task: %s, time: %s, data: %s" % (task, time, data))

    task = str(task)

    session = DB()
    session.merge(Task(task=task, time=time, data=data))
    session.commit()


def pop_tasks(time):
    logging.info("pop_tasks, time: %s" % time)

    session = DB()
    all_tasks = session.query(Task).filter(Task.time <= time).all()
    session.query(Task).filter(Task.time <= time).delete()
    session.commit()

    distinct_tasks = []
    s = set()
    for t in all_tasks:
        if (t.task, t.data) not in s:
            s.add((t.task, t.data))
            distinct_tasks.append(t)

    return distinct_tasks
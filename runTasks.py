import logging
import sys
from datetime import datetime, timedelta
from app.persistance.tasks import create_task, pop_tasks

logging.basicConfig(format="%(asctime)s - %(name)s - %(levelname)s - %(message)s",
                    level=logging.INFO, handlers=[logging.StreamHandler(sys.stdout)])
logger = logging.getLogger(__name__)

if __name__ == '__main__':
    tasks = pop_tasks(datetime.now())
    for t in tasks:
        logger.info("Execute Task '%s' with data: %s" % (t.task, t.data))
        if t.task == 'reload_rides':
            from app.tasks.reloadRides import reload_rides
            reload_rides(t.data)
        else:
            logger.warning("Task '%s' does not exist" % t.task)

from flask import Flask

from app.persistance.createDB import create_db
from app.server.modules import register_blueprints

app = Flask(__name__)

register_blueprints(app)

if __name__ == "__main__":
    create_db()
    app.run()

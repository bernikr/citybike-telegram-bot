from app.server.api import api


def register_blueprints(app):
    app.register_blueprint(api)

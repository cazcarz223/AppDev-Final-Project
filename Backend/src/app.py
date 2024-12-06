from db import db, Event, User
from flask import Flask, request
from datetime import datetime
import json
import os

app = Flask(__name__)
db_filename = "ems.db"

app.config["SQLALCHEMY_DATABASE_URI"] = f"sqlite:///{db_filename}"
app.config["SQLALCHEMY_TRACK_MODIFICATIONS"] = False
app.config["SQLALCHEMY_ECHO"] = True

db.init_app(app)
with app.app_context():
    db.create_all()

# Retrieve all events
@app.route("/api/events/", methods=["GET"])
def get_events():
    events = Event.query.all()
    serialized_events = [events.serialize() for event in events]
    return json.dumps({"events": serialized_events}), 200

# Create a new event
@app.route("/api/events/", methods=["POST"])
def add_event():
    data = json.loads(request.data)
    if not data:
        return json.dumps({"error": "Empty request"}), 400
    
    name = data.get("name")
    date_str = data.get("date")
    location = data.get("location")

    # Basic validation checks
    if not name or not date_str or not location:
        return json.dumps({"error": "Incomplete request: 'name', 'date', and 'location' are required"}), 400
    
    if not isinstance(name, str) or not isinstance(location, str):
        return json.dumps({"error": "Expected strings for 'name' and 'location'"}), 400
    
    # Attempt to parse the date
    try:
        event_date = datetime.fromisoformat(date_str)
    except ValueError:
        return json.dumps({"error": "Invalid date format. Use ISO 8601 (e.g., '2024-12-25T10:00:00')"}), 400
    
    new_event = Event(name=name, date=event_date, location=location)
    db.session.add(new_event)
    db.session.commit()
    
    return json.dumps(new_event.serialize()), 201

# Get event details by ID
@app.route("/api/events/<int:event_id>/", methods=["GET"])
def get_event_by_id(event_id):
    event = Event.query.get(event_id)
    if not event:
        return json.dumps({"error": "Event not found"}), 404
    return json.dumps(event.serialize()), 200


# Remove an event by ID
@app.route("/api/events/<int:event_id>/", methods=["DELETE"])
def remove_event(event_id):
    event = Event.query.get(event_id)
    if not event:
        return json.dumps({"error": "Event not found"}), 404
    db.session.delete(event)
    db.session.commit()
    return json.dumps(event.serialize()), 200

@app.route("/api/users/", methods=["POST"])
def add_user():
    data = request.get_json() 
    if not data:
        return json.dumps({"error": "Empty request"}), 400
    
    username = data.get("username")
    password = data.get("password")
    name = data.get("name")

    if not username or not password or not name:
        return json.dumps({"error": "Incomplete request: 'username', 'password', and 'name' are required."}), 400
    
    if not isinstance(username, str) or not isinstance(password, str) or not isinstance(name, str):
        return json.dumps({"error": "Expected strings for 'username', 'password', and 'name'."}), 400

    existing_user = User.query.filter_by(username=username).first()
    if existing_user:
        return json.dumps({"error": "Username already taken"}), 409

    new_user = User(username=username, password=password, name=name)
    db.session.add(new_user)
    db.session.commit()
    
    return json.dumps(new_user.serialize()), 201

# Get user details by ID
@app.route("/api/users/<int:user_id>/", methods=["GET"])
def fetch_user_by_id(user_id):
    user = User.query.get(user_id)
    if not user:
        return json.dumps({"error": "User not found"}), 404
    return json.dumps(user.serialize()), 200


# Associate a user with an event (attendee or creator)
@app.route("/api/events/<int:event_id>/add_user/", methods=["POST"])
def link_user_to_event(event_id):
    data = request.get_json()
    if not data:
        return json.dumps({"error": "Empty request"}), 400
    user_id, user_type = data.get("user_id"), data.get("type")
    if user_id is None or not user_type:
        return json.dumps({"error": "Incomplete request"}), 400
    if not isinstance(user_id, int) or not isinstance(user_type, str):
        return json.dumps({"error": "Invalid data types for 'user_id' and 'type'"}), 400

    event = Event.query.get(event_id)
    user = User.query.get(user_id)
    if not event:
        return json.dumps({"error": "Event not found"}), 404
    if not user:
        return json.dumps({"error": "User not found"}), 404

    if user_type.lower() == "attendee":
        event.attendees.append(user)
    elif user_type.lower() == "creator":
        event.creators.append(user)
    else:
        return json.dumps({"error": "Invalid user type specified. Must be 'attendee' or 'creator'."}), 400

    db.session.commit()
    return json.dumps(event.serialize()), 200

# Run the app
if __name__ == "__main__":
    app.run(host="0.0.0.0", port=8000, debug=True)

from flask_sqlalchemy import SQLAlchemy
from datetime import datetime

db = SQLAlchemy()

# Table linking Events and Users who attend them (many-to-many)
event_attendee_link = db.Table(
    "event_attendee_link",
    db.Model.metadata,
    db.Column("event_id", db.Integer, db.ForeignKey("events.id"), nullable=False),  
    db.Column("user_id", db.Integer, db.ForeignKey("users.id"), nullable=False)   
)

# Table linking Events and Users who create them (many-to-many)
event_creator_link = db.Table(
    "event_creator_link",
    db.Model.metadata,
    db.Column("event_id", db.Integer, db.ForeignKey("events.id"), nullable=False), 
    db.Column("user_id", db.Integer, db.ForeignKey("users.id"), nullable=False)    
)

# User object representing a user in the app
class User(db.Model):
    __tablename__ = "users"  
    id = db.Column(db.Integer, primary_key=True, autoincrement=True)  
    username = db.Column(db.String, nullable=False) 
    password = db.Column(db.String, nullable=False) 
    name = db.Column(db.String, nullable=False)    

    # Which events this user is attending
    events_attending = db.relationship(
        "Event",
        secondary=event_attendee_link,
        back_populates="attendees"
    )

    # Which events this user has created
    events_created = db.relationship(
        "Event",
        secondary=event_creator_link,
        back_populates="creators"
    )
    
    # Constructor to create new User
    def __init__(self, username, password, name):
        self.username = username
        self.password = password
        self.name = name

     # Serializes user to dictionary showing its id, username, and name 
    def serialize_basic(self):
        return {
            "id": self.id,
            "username": self.username,
            "name": self.name
        }

    # Serializes user to dictionary showing its id, name, username, password, and events 
    def serialize(self):
        # Returns a full dictionary representation of the user, including related events
        return {
            "id": self.id,
            "name": self.name,
            "username": self.username,
            "password": self.password,  
            "events_created": [event.serialize_basic() for event in self.events_created],
            "events_attending": [event.serialize_basic() for event in self.events_attending],
        }
    
# Event object representing an event in the app
class Event(db.Model):
    __tablename__ = "events"  
    id = db.Column(db.Integer, primary_key=True, autoincrement=True)  
    name = db.Column(db.String, nullable=False)       
    date = db.Column(db.DateTime, nullable=False)      
    location = db.Column(db.String, nullable=False)    

    # Which users created this event
    creators = db.relationship(
        "User",
        secondary=event_creator_link,
        back_populates="events_created"
    )

    # Which users are attending this event
    attendees = db.relationship(
        "User",
        secondary=event_attendee_link,
        back_populates="events_attending"
    )
        
    # Constructor to create new Event
    def __init__(self, name, date, location):
        self.name = name
        self.date = date
        self.location = location

    # Serializes Event to dictionary showing its id and name 
    def serialize_basic(self):
        return {
            "id": self.id,
            "name": self.name
        }

    # Serializes Event to dictionary showing its id, name, data, location, creators, and attendees 
    def serialize(self):
        return {
            "id": self.id,
            "name": self.name,
            "date": self.date.isoformat() if self.date else None,  
            "location": self.location,
            "creators": [creator.serialize_basic() for creator in self.creators],
            "attendees": [attendee.serialize_basic() for attendee in self.attendees]
        }

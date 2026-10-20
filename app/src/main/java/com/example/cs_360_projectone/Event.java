package com.example.cs_360_projectone;

//class that is used to create an event object
public class Event {

    //declaration of class variables
    int eventId;
    int userID;
    String name;
    String date;
    String time;
    String description;

    //the class constructor
    public Event(int eventId, int userID, String name, String date, String time, String description) {
        this.eventId = eventId;
        this.userID = userID;
        this.name = name;
        this.date = date;
        this.time = time;
        this.description = description;
    }

    //getters for the class variables
    public int getEventId() {
        return eventId;
    }
    public int getUserId() {
        return userID;
    }

    public String getName() {
        return name;
    }

    public String getDate() {
        return date;
    }

    public String getTime() {
        return time;
    }

    public String getDescription() {
        return description;
    }

}

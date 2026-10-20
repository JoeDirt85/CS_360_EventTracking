package com.example.cs_360_projectone;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.Cursor;
import android.content.ContentValues;

import java.util.ArrayList;

public class Database extends SQLiteOpenHelper {

    //declares the string name for the database
    private static final String DATABASE_NAME = "Events.db";
    //declares the version of the database
    private static final int VERSION = 2;

    //constructor for the database class
    public Database( Context context) {
        super(context, DATABASE_NAME, null, VERSION);
    }

    //configure method to allow for foreign key use
    public void onConfigure(SQLiteDatabase db) {
        super.onConfigure(db);
        db.setForeignKeyConstraintsEnabled(true);
    }

    //declaration of the UserInformation table class
    private static final class UserInformation {
        private static final String TABLE = "UserInformation";
        private static final String COL_ID = "ID";
        private static final String COL_USERNAME = "Username";
        private static final String COL_PASSWORD = "Password";
        private static final String COL_PHONE = "Phone";
        private static final String COL_SMS_OPT_IN = "SmsOptIn";
    }

    //declaration for the EventInformation table class
    private static final class EventInformation {
        private static final String TABLE = "Events";
        private static final String COL_ID = "ID";
        private static final String COL_USER_ID = "User_ID";
        private static final String COL_EVENT_NAME = "Name";
        private static final String COL_DATE = "Date";
        private static final String COL_TIME = "Time";
        private static final String COL_DESCRIPTION = "Description";

    }

    //onCreate method, creates the two tables
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table " + UserInformation.TABLE + " (" +
                UserInformation.COL_ID + " integer primary key autoincrement, " +
                UserInformation.COL_USERNAME + " text," +
                UserInformation.COL_PASSWORD + " text,"  +
                UserInformation.COL_PHONE + " text," +
                UserInformation.COL_PHONE + " text," +
                UserInformation.COL_SMS_OPT_IN + " integer default 0)");

        db.execSQL("Create table " + EventInformation.TABLE + " (" +
                EventInformation.COL_ID + " integer primary key autoincrement, " +
                EventInformation.COL_USER_ID + " integer," +
                EventInformation.COL_EVENT_NAME + " text," +
                EventInformation.COL_DATE + " text," +
                EventInformation.COL_TIME + " text," +
                EventInformation.COL_DESCRIPTION + " text," +
                "FOREIGN KEY(" + EventInformation.COL_USER_ID + ") REFERENCES " +
                UserInformation.TABLE + "(" + UserInformation.COL_ID + "))" );
    }

    //onUpgrade method, for updating the table
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < 2) {
            db.execSQL(
                    "ALTER TABLE UserInformation " +
                            "ADD COLUMN SmsOptIn INTEGER NOT NULL DEFAULT 0"
            );
        }
    }

    //method for inserting user information into the database
    public boolean addUserInformation(String username, String password) {
        SQLiteDatabase db = getWritableDatabase();

        //creates empty set of values
        ContentValues values = new ContentValues();
        //puts username and password values into columns
        values.put(UserInformation.COL_USERNAME, username);
        values.put(UserInformation.COL_PASSWORD, password);

        //inserts values into the table
        long result = db.insert(UserInformation.TABLE, null, values);

        return result != -1;
    }

    //method for adding event information to the database
    public long addEventInformationAndGetId(int userId, String name, String date, String time, String description) {
        SQLiteDatabase db = getWritableDatabase();

        //creates empty set of values
        ContentValues values = new ContentValues();
        //puts values into columns
        values.put(EventInformation.COL_USER_ID, userId);
        values.put(EventInformation.COL_EVENT_NAME, name);
        values.put(EventInformation.COL_DATE, date);
        values.put(EventInformation.COL_TIME, time);
        values.put(EventInformation.COL_DESCRIPTION, description);

        //inserts values into data table
        return db.insert(EventInformation.TABLE, null, values);
    }

    //method for reading user information in database
    public boolean checkUserInformation(String username, String password) {
        SQLiteDatabase db = getReadableDatabase();

        //initialize and declares string query for accessing user information
        String query = "SELECT " + UserInformation.COL_ID +
                " From " + UserInformation.TABLE +
                " WHERE " + UserInformation.COL_USERNAME + " = ?" +
                " AND " + UserInformation.COL_PASSWORD + " = ?";

        //runs query and returns cursor with results
        Cursor cursor = db.rawQuery(query, new String[]{username, password});

        //checks if the cursor contains a row result
        boolean validUser = cursor.moveToFirst();

        //releases the cursor resources
        cursor.close();

        //returns true or false
        return validUser;

    }

    //method for reading username information, used to check database when crating account
    public boolean checkUserName(String username) {
        SQLiteDatabase db = getReadableDatabase();

        //initialize and declares string query for accessing username
        String query = "SELECT " + UserInformation.COL_ID +
                " FROM " + UserInformation.TABLE +
                " WHERE " + UserInformation.COL_USERNAME + " = ?";

        //runs query and returns cursor with results
        Cursor cursor = db.rawQuery(query, new String[]{username});

        //checks if the cursor contains a row result
        boolean validUsername = cursor.moveToFirst();

        //releases the cursor resources
        cursor.close();

        //returns true or false
        return validUsername;
    }

    //method for getting userId
    public int getUserId(String username, String password){
        SQLiteDatabase db = getReadableDatabase();

        //initialize and declares string query for accessing userId, from matching username and password
        String query = "SELECT " + UserInformation.COL_ID +
                " FROM " + UserInformation.TABLE +
                " WHERE " + UserInformation.COL_USERNAME + " = ?" + " AND " +
                UserInformation.COL_PASSWORD + " = ?";

        //adds query results to cursor
        Cursor cursor = db.rawQuery(query, new String[]{username, password});

        //checks if cursor holds the correct value
        if (cursor.moveToFirst()) {
            int userId = cursor.getInt(0);
            cursor.close();
            return userId;
        }

        //releases the cursor
        cursor.close();

        return -1;
    }

    //method for getting eventIds associated with user
    public ArrayList<Integer> getEventIdsForUser(int userId) {
        ArrayList<Integer> eventIds = new ArrayList<>();

        SQLiteDatabase db = getReadableDatabase();

        //initialize and declare string query for accessing eventIds from matching userId
        String query = "SELECT " + EventInformation.COL_ID +
                " FROM " + EventInformation.TABLE +
                " WHERE " + EventInformation.COL_USER_ID + " = ?";

        //adds query result to cursor
        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(userId)});

        //loops through cursor and adds ids to eventIds array
        while (cursor.moveToNext()) {
            eventIds.add(cursor.getInt(0));
        }

        cursor.close();
        return eventIds;
    }

    //method that gets a users phone number
    public String getPhone(int userId) {
        SQLiteDatabase db = getReadableDatabase();

        //initialize and declare string query for accessing phone number from matching userId
        String query = "SELECT " + UserInformation.COL_PHONE +
                " FROM " + UserInformation.TABLE +
                " WHERE " + UserInformation.COL_ID + " = ?";

        //adds query results to cursor
        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(userId)});

        //checks if cursor holds the correct value
        if (cursor.moveToFirst()) {
            String phone = cursor.getString(0);
            cursor.close();
            return phone;
        }

        cursor.close();
        return null;
    }

    // Saves the user's SMS notification preference
    public boolean updateSmsOptIn(int userId, boolean optedIn) {
        SQLiteDatabase db = getWritableDatabase();

        //creates empty value set
        ContentValues values = new ContentValues();
        values.put(UserInformation.COL_SMS_OPT_IN, optedIn ? 1 : 0);

        int rowsUpdated = db.update(UserInformation.TABLE, values, UserInformation.COL_ID + " = ?", new String[]{String.valueOf(userId)});

        return rowsUpdated > 0;
    }

    // Retrieves the user's SMS notification preference
    public boolean getSmsOptIn(int userId) {
        SQLiteDatabase db = getReadableDatabase();

        //creates string query for getting sms opt in settings
        String query = "SELECT " + UserInformation.COL_SMS_OPT_IN +
                " FROM " + UserInformation.TABLE +
                " WHERE " + UserInformation.COL_ID + " = ?";

        //adds sms opt in settings into cursor
        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(userId)});

        boolean optedIn = false;

        if (cursor.moveToFirst()) {
            optedIn = cursor.getInt(0) == 1;
        }

        cursor.close();
        return optedIn;
    }

    //method that returns events associated with a single user
    public ArrayList<Event> selectEventInformation(int userId) {

        //declares arraylist that gets populated with the different events
        ArrayList<Event> events = new ArrayList<>();


        SQLiteDatabase db = getReadableDatabase();

        //initialize and create as tring query to select all from table that matches userId
        String query = " SELECT * FROM " + EventInformation.TABLE +
                " WHERE " + EventInformation.COL_USER_ID + " = ?";

        //retrieves matching events from the database
        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(userId)});

        //adds the events to the array list
        while (cursor.moveToNext()) {
            //reads the columns for their information
            int id = cursor.getInt(cursor.getColumnIndexOrThrow(EventInformation.COL_ID));
            int userID = cursor.getInt(cursor.getColumnIndexOrThrow(EventInformation.COL_USER_ID));
            String name = cursor.getString(cursor.getColumnIndexOrThrow(EventInformation.COL_EVENT_NAME));
            String date = cursor.getString(cursor.getColumnIndexOrThrow(EventInformation.COL_DATE));
            String time = cursor.getString(cursor.getColumnIndexOrThrow(EventInformation.COL_TIME));
            String description = cursor.getString(cursor.getColumnIndexOrThrow(EventInformation.COL_DESCRIPTION));

            //creates event class object
            Event event = new Event(id, userID, name, date, time, description);
            events.add(event);
        }

        cursor.close();

        return events;
    }


    //method that selects the information for a single event
    public Event selectSingleEvent(int id, int userId) {
        SQLiteDatabase db = getReadableDatabase();

        String query = "SELECT * FROM " + EventInformation.TABLE +
                " WHERE " + EventInformation.COL_ID + "= ?" + " AND " +
                EventInformation.COL_USER_ID + "= ?";

        //adds query results to cursor
        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(id), String.valueOf(userId)});

        //checks if cursor holds the correct value
        if (cursor.moveToFirst()) {
            int eventId = cursor.getInt(cursor.getColumnIndexOrThrow(EventInformation.COL_ID));
            int userID = cursor.getInt(cursor.getColumnIndexOrThrow(EventInformation.COL_USER_ID));
            String name = cursor.getString(cursor.getColumnIndexOrThrow(EventInformation.COL_EVENT_NAME));
            String date = cursor.getString(cursor.getColumnIndexOrThrow(EventInformation.COL_DATE));
            String time = cursor.getString(cursor.getColumnIndexOrThrow(EventInformation.COL_TIME));
            String description = cursor.getString(cursor.getColumnIndexOrThrow(EventInformation.COL_DESCRIPTION));

            Event event = new Event(eventId, userID, name, date, time, description);

            cursor.close();
            return event;
        }

        cursor.close();
        return null;

    }

    //method for updating phone number
    public boolean updatePhone(int userId, String phoneNumber) {
        SQLiteDatabase db = getWritableDatabase();

        //creates empty set of values
        ContentValues values = new ContentValues();
        //puts values into column
        values.put(UserInformation.COL_PHONE, phoneNumber);

        //updates value in the table
        int rowUpdated = db.update(UserInformation.TABLE, values, UserInformation.COL_ID + " = ?", new String[]{String.valueOf(userId)});

        return rowUpdated > 0;
    }

    //method used to update event information
    public boolean updateEventInformation(int id, int userId, String name, String date, String time, String description) {
        SQLiteDatabase db = getWritableDatabase();

        //creates empty set of values
        ContentValues values = new ContentValues();
        //puts values into columns
        values.put(EventInformation.COL_EVENT_NAME, name);
        values.put(EventInformation.COL_DATE, date);
        values.put(EventInformation.COL_TIME, time);
        values.put(EventInformation.COL_DESCRIPTION, description);

        //updates values in the table
        int rowsUpdated = db.update(EventInformation.TABLE, values, EventInformation.COL_ID + " = ?" + " AND " + EventInformation.COL_USER_ID + " = ?",
                new String[] {String.valueOf(id), String.valueOf(userId)});

        return rowsUpdated > 0;
    }


    //method for deleting event information
    public boolean deleteEvent(int id, int userId) {
        SQLiteDatabase db = getWritableDatabase();

        int rowsDeleted = db.delete(EventInformation.TABLE, EventInformation.COL_ID + " = ?" + " AND " + EventInformation.COL_USER_ID + " = ?",
                new String[]{String.valueOf(id), String.valueOf(userId)});

        return rowsDeleted > 0;

    }
}

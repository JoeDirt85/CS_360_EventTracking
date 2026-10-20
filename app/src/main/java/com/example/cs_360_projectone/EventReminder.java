package com.example.cs_360_projectone;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

import java.time.ZoneId;

//handles the scheduling and cancellation of event reminders
public class EventReminder {

    //declares and stores application context
    private final Context context;
    //declares and stores access to alarm scheduling
    private final AlarmManager alarmManager;

    //initializes the reminder manager
    public EventReminder(Context context) {
        this.context = context.getApplicationContext();
        alarmManager = (AlarmManager)
                this.context.getSystemService(Context.ALARM_SERVICE);
    }

    //calculates reminder time and schedules 24 hours before
    public boolean scheduleReminder(int eventId, int userId, String eventName, String eventDate, String eventTime) {

        //sets date and time format
        DateTimeFormatter formatter =
                DateTimeFormatter.ofPattern("MM/dd/yyyy hh:mm a");

        try {
            //combines date and time of event into new object
            LocalDateTime eventDateTime = LocalDateTime.parse(
                    eventDate + " " + eventTime,
                    formatter
            );

            //calculates reminder time
            LocalDateTime reminderDateTime =
                    eventDateTime.minusHours(24);

            //converts reminder time into milliseconds for alarm manager
            long reminderTimeMillis = reminderDateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();

            //does not schedule reminder if date is passed
            if (reminderTimeMillis <= System.currentTimeMillis()) {
                return false;
            }

            return setAlarm(eventId, userId, eventName, reminderTimeMillis);

        }
        //return if date and time can't be parsed
        catch (DateTimeParseException e) {
            return false;
        }
    }

    //method for canceling reminder
    public void cancelReminder(int eventId) {
        //returns if there is no alarm set
        if (alarmManager == null) {
            return;
        }

        //creates new intent that identifies the receiver
        Intent intent = new Intent(context, EventReminderReceiver.class);
        //sets the action from when reminder was originally scheduled
        intent.setAction("com.example.cs_360_projectone.EVENT_REMINDER");

        //retrieves the pending intent
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, eventId, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        //removes scheduled alarm and cancels pending intent
        alarmManager.cancel(pendingIntent);
        pendingIntent.cancel();
    }

    //schedules the alarm
    public boolean setAlarm(int eventId, int userId, String eventName, long reminderTimeMillis) {

        //checks if time has passed and returns false
        if (alarmManager == null ||
                reminderTimeMillis <= System.currentTimeMillis()) {
            return false;
        }

        //creates new intent for that targets receiver
        Intent intent = new Intent(context, EventReminderReceiver.class);
        //identifies type of broadcast
        intent.setAction("com.example.cs_360_projectone.EVENT_REMINDER");
        //passes information needed for the intent
        intent.putExtra("EVENT_ID", eventId);
        intent.putExtra("USER_ID", userId);
        intent.putExtra("EVENT_NAME", eventName);

        //wraps intent in pending intent
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, eventId, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        //schedules alarm to trigger
        alarmManager.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, reminderTimeMillis, pendingIntent);

        return true;
    }
}

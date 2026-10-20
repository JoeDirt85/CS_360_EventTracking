package com.example.cs_360_projectone;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.telephony.SmsManager;


public class EventReminderReceiver extends BroadcastReceiver {

    //called when alarm is activated
    @Override
    public void onReceive(Context context, Intent intent) {

        //retrieves userId and eventName from eventReminder
        int userId = intent.getIntExtra("USER_ID", -1);
        String eventName = intent.getStringExtra("EVENT_NAME");

        //stops if required info is missing
        if (userId == -1 || eventName == null) {
            return;
        }

        //creates database helper
        Database database = new Database(context);

        // Check the user's current SMS preference.
        if (!database.getSmsOptIn(userId)) {
            return;
        }

        // Retrieve the current saved phone number.
        String phone = database.getPhone(userId);

        if (phone == null || phone.trim().isEmpty()) {
            return;
        }

        // Verify that SMS permission is still granted.
        if (context.checkSelfPermission(
                Manifest.permission.SEND_SMS)
                != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        //message that is sent
        String message = "Reminder: Your event \"" +
                eventName + "\" is coming up in 24 hours!";

        try {
            //retrieves android's sms service
            SmsManager smsManager = context.getSystemService(
                    SmsManager.class);

            //sends text if permission available
            if (smsManager != null) {
                smsManager.sendTextMessage(phone, null, message, null, null);
            }
        } catch (SecurityException e) {
            // Permission may have changed before sending.
        }
    }
}

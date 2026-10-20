package com.example.cs_360_projectone;

import android.os.Bundle;

import android.Manifest;
import android.content.pm.PackageManager;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.core.content.ContextCompat;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import java.util.ArrayList;


public class EventDetails extends AppCompatActivity implements View.OnClickListener {

    //declare database helper
    private Database database;
    //declare eventReminder helper
    private EventReminder reminderScheduler;
    //declares variables for passed information
    private int userId;
    private int eventId;
    private String phone;
    private boolean smsOptIn = false;

    //declare variables for editText widgets
    private EditText name;
    private EditText date;
    private EditText time;
    private EditText description;
    private EditText phoneNumber;


    //declare variables for button widgets
    private Button back;
    private Button edit;
    private Button addPhone;
    private Button yes;
    private Button no;
    private Button save;
    private Button delete;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_event);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.eventDetails), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        //initializes the database and eventReminder
        database = new Database(this);
        reminderScheduler = new EventReminder(this);

        //initializes useId and eventId through intent transfers
        userId = getIntent().getIntExtra("USER_ID", -1);
        eventId = getIntent().getIntExtra("EVENT_ID", -1);

        //initialize and connects Edit text variables to their matching widget
        name = findViewById(R.id.eventTitleDetail);
        date = findViewById(R.id.eventDateDetail);
        time = findViewById(R.id.eventTimeDetail);
        description = findViewById(R.id.eventDescription);
        phoneNumber = findViewById(R.id.phoneNumber);

        //initialize and connects button variables to their matching widget
        back = findViewById(R.id.backButton);
        edit = findViewById(R.id.editButton);
        addPhone = findViewById(R.id.addPhone);
        yes = findViewById(R.id.yesButton);
        no = findViewById(R.id.noButton);
        save = findViewById(R.id.saveButton);
        delete = findViewById(R.id.deleteButton);

        //setup and connect buttons to the onClickListeners
        back.setOnClickListener(this);
        edit.setOnClickListener(this);
        addPhone.setOnClickListener(this);
        yes.setOnClickListener(this);
        no.setOnClickListener(this);
        save.setOnClickListener(this);
        delete.setOnClickListener(this);

        //retrieves phone number from database if already exists
        phone = database.getPhone(userId);
        //sets phone number if not null
        if (phone != null) {
            phoneNumber.setText(phone);
        }

        //initializes smsOptIn variable through database query
        smsOptIn = database.getSmsOptIn(userId);

        //branch that checks if the event exists in database
        if (eventId != -1) {
            //query to select single event information
            Event event = database.selectSingleEvent(eventId, userId);
            //sets the textViews with data from database
            if (event != null) {
                name.setText(event.getName());
                date.setText(event.getDate());
                time.setText(event.getTime());
                description.setText(event.getDescription());
            }
        }

    }

    //registers a launcher to request sms notifications
    private final ActivityResultLauncher<String> smsPermissionLauncher =
            registerForActivityResult(
                    //request asks for one android permission
                    new ActivityResultContracts.RequestPermission(), isGranted -> {
                        //enables sms notification
                        if (isGranted) {
                            enableSmsNotifications();
                        }
                        //disables sms notification
                        else {
                            smsOptIn = false;
                            database.updateSmsOptIn(userId, false);
                            //message confirming choice
                            Toast.makeText(this,
                                    "SMS permission denied.",
                                    Toast.LENGTH_LONG).show();
                        }
                    });

    //enables notifications and saves preferences
    private void enableSmsNotifications() {
        //saves users preference in database
        boolean success = database.updateSmsOptIn(userId, true);

        //checks if save was successful
        if (success) { //success
            smsOptIn = true;
            Toast.makeText(this,
                    "SMS notifications enabled",
                    Toast.LENGTH_SHORT).show();
        }

        else { //unsuccessful
            smsOptIn = false;
            Toast.makeText(this,
                    "Could not save SMS preference",
                    Toast.LENGTH_SHORT).show();
        }
    }

    //handles the logic for all the button clicks
    @Override
    public void onClick(View v) {
        //instantiates database
        database = new Database(this);

        //declares strings that get their data from edit text fields
        String enteredName = name.getText().toString().trim();
        String enteredDate = date.getText().toString().trim();
        String enteredTime = time.getText().toString().trim();
        String enteredDescription = description.getText().toString().trim();

        //Back Button: returns to the display screen
        if (v.getId() == R.id.backButton) {
            finish();
        }

        //allows for the edit text fields to be edited
        else if (v.getId() == R.id.editButton) {
            name.setFocusable(true);
            name.setFocusableInTouchMode(true);

            date.setFocusable(true);
            date.setFocusableInTouchMode(true);

            time.setFocusable(true);
            time.setFocusableInTouchMode(true);

            description.setFocusable(true);
            description.setFocusableInTouchMode(true);

            phoneNumber.setFocusable(true);
            phoneNumber.setFocusableInTouchMode(true);
        }
        //Save Button: saves user entered info
        else if (v.getId() == R.id.saveButton) {

            //checks if the EditText fields are empty
            if (enteredName.isEmpty() || enteredDate.isEmpty() ||
                    enteredTime.isEmpty() || enteredDescription.isEmpty()) {
                //notifies user if fields are empty
                Toast.makeText(this,
                        "Please enter information to every field.",
                        Toast.LENGTH_SHORT).show();

            }
            //branch if fields aren't empty
            else {
                //checks for new or existing event
                if (eventId != -1) {
                    //if existing, updates with the new information
                    boolean success = database.updateEventInformation(eventId, userId, enteredName, enteredDate, enteredTime, enteredDescription);

                    if (success) { //success
                        Toast.makeText(this,
                                "Event information successfully saved",
                                Toast.LENGTH_SHORT).show();

                        //sets notification reminder
                        if (smsOptIn) {
                            reminderScheduler.scheduleReminder(eventId, userId, enteredName, enteredDate, enteredTime);
                        }
                        finish();
                    }
                    //save was unsuccessful
                    else {
                        Toast.makeText(this,
                                "Event information couldn't be saved",
                                Toast.LENGTH_SHORT).show();
                    }
                }
                else {
                    //event is new, adds new event
                    long newEventId = database.addEventInformationAndGetId(userId, enteredName, enteredDate, enteredTime, enteredDescription);

                    //checks if sms is opt in
                    if (newEventId != -1) {
                        //sets reminder
                        if (smsOptIn) {
                            reminderScheduler.scheduleReminder((int) newEventId, userId, enteredName, enteredDate, enteredTime);
                        }
                        //notifies that event was saved
                        Toast.makeText(this, "Event added successfully!",
                                Toast.LENGTH_SHORT).show();
                        finish();
                    }
                    //failed to save event
                    else {
                        Toast.makeText(this, "Failed to add event.",
                                Toast.LENGTH_SHORT).show();
                    }
                }
            }
        }
        //Delete Button: deletes an event
        else if (v.getId() == R.id.deleteButton) {
            //checks if its an existing event or not
            if (eventId != -1) {
                //deletes existing event
                boolean deleted = database.deleteEvent(eventId, userId);

                if (deleted) {//successful delete
                    Toast.makeText(this,
                            "Event deleted",
                            Toast.LENGTH_SHORT).show();
                    reminderScheduler.cancelReminder(eventId);
                    finish();
                }
                else {//delete failed
                    Toast.makeText(this,
                            "Could not delete event",
                            Toast.LENGTH_SHORT).show();
                }
            }
            //no event to delete
            else {
                Toast.makeText(this,
                        "No event to delete",
                        Toast.LENGTH_SHORT).show();
            }
        }
        //Add Phone Number Button: adds a phone number to database
        else if (v.getId() == R.id.addPhone) {

            //initializes string for the entered number
            String enteredPhoneNumber = phoneNumber.getText().toString().trim();
            //checks if the EditText fields are empty
            if (enteredPhoneNumber.isEmpty()) {
                Toast.makeText(this,
                        "Please enter phone number.",
                        Toast.LENGTH_SHORT).show();
            }
            else {
                //updates phone number in database
                boolean success = database.updatePhone(userId, enteredPhoneNumber);

                //number updated successfully
                if (success) {
                    Toast.makeText(this,
                            "Phone number successfully saved",
                            Toast.LENGTH_SHORT).show();
                    phone = enteredPhoneNumber;
                }
                //number couldn't be updated
                else {
                    Toast.makeText(this,
                            "Phone number couldn't be saved",

                            Toast.LENGTH_SHORT).show();
                }
            }
        }
        //Yes Button: saves the optIn settings
        else if (v.getId() == R.id.yesButton) {
            //checks if there is a saved number
            if (phone == null || phone.trim().isEmpty()) {
                Toast.makeText(this,
                        "Please save a phone number first.",
                        Toast.LENGTH_SHORT).show();
            }
            //checks if permissions are already granted
            else if (ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS) == PackageManager.PERMISSION_GRANTED) {
                //saves the users preferences
                enableSmsNotifications();

            }
            //requests permissions if not already granted
            else {
                smsPermissionLauncher.launch(
                        Manifest.permission.SEND_SMS);
            }
        }
        //No Button: saves the optOut settings
        else if (v.getId() == R.id.noButton) {
            //saves preferences in the database
            boolean success = database.updateSmsOptIn(userId, false);

            //updates activities preference
            smsOptIn = false;

            //retrieves event id for all user events
            ArrayList<Integer> eventIds = database.getEventIdsForUser(userId);

            //cancels notification for all events
            for (int id : eventIds) {
                reminderScheduler.cancelReminder(id);
            }

            //displays message for success or failure
            Toast.makeText(this,
                    success ? "SMS notifications disabled"
                            : "Preference could not be saved",
                    Toast.LENGTH_SHORT).show();
        }
    }
}
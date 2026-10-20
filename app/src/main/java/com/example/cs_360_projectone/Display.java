package com.example.cs_360_projectone;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.View;
import android.widget.Button;
import android.content.Intent;

import java.util.ArrayList;

public class Display extends AppCompatActivity implements View.OnClickListener {

    //declares variables that are used in the class
    private RecyclerView eventRecyclerView;
    private EventAdapter eventAdapter;
    private ArrayList<Event> eventList;
    private Database database;
    private Button add;
    private int userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_display);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.displayScreen), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        //initializes the database connection
        database = new Database(this);

        //initializes the userId from information passed from main activities intent
        userId = getIntent().getIntExtra("USER_ID", -1);

        //connects to the recyclerView in xml
        eventRecyclerView = findViewById(R.id.eventRecyclerView);

        //initializes the eventList from the database
        eventList = database.selectEventInformation(userId);

        //initializes the event Adapter
        eventAdapter = new EventAdapter(eventList);

        //initializes the displays layout
        eventRecyclerView.setLayoutManager(new GridLayoutManager(this, 2));

        eventRecyclerView.setAdapter(eventAdapter);

        //initialize button
        add = findViewById(R.id.addEvent);

        //sets up and connect on click listener
        add.setOnClickListener(this);
    }
    //loads the events
    private void loadEvents() {
        eventList.clear();
        eventList.addAll(database.selectEventInformation(userId));
        eventAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onResume() {
        super.onResume();
        //loads events after resuming the app
        if (database != null && eventAdapter != null) {
            loadEvents();
        }
    }

    //handles button click event
    @Override
    public void onClick(View v) {
        //checks if the addEvent button was clicked
        if (v.getId() == R.id.addEvent) {

            //creates new intent from the display activity to the eventDetails activity
            Intent intent = new Intent(Display.this, EventDetails.class);

            //includes the userId in the intent
            intent.putExtra("USER_ID", userId);
            //launches the screen
            startActivity(intent);

        }


    }
}
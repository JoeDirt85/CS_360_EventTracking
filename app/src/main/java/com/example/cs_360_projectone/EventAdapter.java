package com.example.cs_360_projectone;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Button;
import android.content.Intent;


import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

//creates a connection between the list of events and the RecyclerView
public class EventAdapter extends RecyclerView.Adapter<EventAdapter.EventViewHolder> {
    //declaration of eventList array to store events
    private final ArrayList<Event> eventList;

    //class constructor
    public EventAdapter(ArrayList<Event> eventList) {
        this.eventList = eventList;
    }


    //references the view that makes up a single event
    public static class EventViewHolder extends RecyclerView.ViewHolder {

        //declarations for the widget variables
        TextView eventTitle;
        TextView eventDate;
        TextView eventTime;
        Button details;

        //class constructor
        public EventViewHolder(@NonNull View itemView) {
            super(itemView);

            //connects the variables to the widgets
            eventTitle = itemView.findViewById(R.id.eventTitle);
            eventDate = itemView.findViewById(R.id.eventDate);
            eventTime = itemView.findViewById(R.id.eventTime);

            details = itemView.findViewById(R.id.detailsButton);
        }
    }

    @NonNull
    @Override
    public EventViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //inflates the layout for individual item
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.activity_event_item, parent, false);

        //creates viewHolder to reference the views in a new item
        return new EventViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull EventViewHolder holder, int position) {
        //retrieves the event from its position in list
        Event event = eventList.get(position);

        //sets and displays events information
        holder.eventTitle.setText(event.getName());
        holder.eventDate.setText(event.getDate());
        holder.eventTime.setText(event.getTime());

        //logic for when the detail button on event is clicked
        holder.details.setOnClickListener(v -> {
            //creates intent to open EventDetails activity
            Intent intent = new Intent(v.getContext(), EventDetails.class);

            //passes the userId and eventId to the next activity
            intent.putExtra("USER_ID", event.getUserId());
            intent.putExtra("EVENT_ID", event.getEventId());

            //launches activity
            v.getContext().startActivity(intent);
        });
    }

    //returns the number of events
    @Override
    public int getItemCount() {
        return eventList.size();
    }
}

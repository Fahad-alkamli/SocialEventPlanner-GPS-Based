package com.example.d0l1.socialeventplanner.View.Model;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.example.d0l1.socialeventplanner.Model.Event;
import com.example.d0l1.socialeventplanner.R;

import java.util.ArrayList;

public class CustomeEventAdapter extends ArrayAdapter<Event>{
    final String TAG="Fahad";
    public CustomeEventAdapter(Context context, ArrayList<Event> Titles) {
        super(context, R.layout.custom_event_display_item,Titles);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        //This function will give you each view so u can manipulate it and change it however you want, in this sense
        //I will put my data into the mockTemplate
        LayoutInflater inflater=LayoutInflater.from(getContext());
        //Get the view so we can alter it
        View customView=inflater.inflate(R.layout.custom_event_display_item,parent,false);
        Event SingleEventElement=getItem(position);
        TextView titleElement=(TextView) customView.findViewById(R.id.Title);

        TextView id=(TextView)  customView.findViewById(R.id.ID);
        TextView date=(TextView)  customView.findViewById(R.id.date);
        TextView time=(TextView) customView.findViewById(R.id.timeTextView);
        //In case the array is empty
        if(SingleEventElement.getAttendees() != null && SingleEventElement.getAttendees().length>0)
        {
            ListAdapter adapt=new ArrayAdapter<String>(this.getContext(),android.R.layout.simple_list_item_1,SingleEventElement.getAttendees());
            ListView people=(ListView) customView.findViewById(R.id.eventParticipants);
            people.setAdapter(adapt);
            setListViewHeightBasedOnItems(people);
        }else
        {
            //Mention that the user didn't add anyone to this event
            String[] temp={"This event doesn't have Attendees"};
            ListAdapter adapt=new ArrayAdapter<String>(this.getContext(),android.R.layout.simple_list_item_1,temp);
            ListView people=(ListView) customView.findViewById(R.id.eventParticipants);
            people.setAdapter(adapt);
        }


        //Set the things
        date.setText(SingleEventElement.getStartDate()+" - "+SingleEventElement.getEndDate());
        time.setText(SingleEventElement.getStartTime()+" - "+SingleEventElement.getEndTime());
        titleElement.setText(SingleEventElement.getTitle());
        id.setText(SingleEventElement.getID());

        return customView;
    }

    //http://stackoverflow.com/questions/1778485/android-listview-display-all-available-items-without-scroll-with-static-header
    public static boolean setListViewHeightBasedOnItems(ListView listView) {

        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter != null) {

            int numberOfItems = listAdapter.getCount();

            // Get total height of all items.
            int totalItemsHeight = 0;
            for (int itemPos = 0; itemPos < numberOfItems; itemPos++) {
                View item = listAdapter.getView(itemPos, null, listView);
                item.measure(0, 0);
                totalItemsHeight += item.getMeasuredHeight();
            }

            // Get total height of all item dividers.
            int totalDividersHeight = listView.getDividerHeight() *
                    (numberOfItems - 1);

            // Set list height.
            ViewGroup.LayoutParams params = listView.getLayoutParams();
            params.height = totalItemsHeight + totalDividersHeight;
            listView.setLayoutParams(params);
            listView.requestLayout();

            return true;

        } else {
            return false;
        }

    }

}


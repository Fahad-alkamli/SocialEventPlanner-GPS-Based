package com.example.d0l1.socialeventplanner.View.Model;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;

import com.example.d0l1.socialeventplanner.Controller.pickContactWindow_Controller;
import com.example.d0l1.socialeventplanner.R;

import java.util.ArrayList;


public class CustomContactaAdapter extends ArrayAdapter<String>{

/*
The customeContact adapter will insert an array of names to a contact view controller.
Each contact view controller will have a checkbox and a click on the checkbox will add the selected name to the array of names.
 */
    public CustomContactaAdapter(Context context, ArrayList<String> names) {

        super(context, R.layout.contact_item,names);

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater=LayoutInflater.from(getContext());
        View CustomView=inflater.inflate(R.layout.contact_item,parent,false);
        String singleItem=getItem(position);
        CheckBox name=(CheckBox)CustomView.findViewById(R.id.nameCheckBox);
        name.setText(singleItem);
        //Set listeners
        name.setOnClickListener(new pickContactWindow_Controller(null));
        return CustomView;
    }
}

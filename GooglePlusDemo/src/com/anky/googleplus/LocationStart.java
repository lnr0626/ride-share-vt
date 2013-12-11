package com.anky.googleplus;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Displays examples of integrating with the Google+ Platform for Android.
 */
public class LocationStart extends ListActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String[] values = new String[] { "Perry Street Parking Garage", "Surge Parking lot", "Burruss Hall/Board of Visitors",
                "Washington Street Parking lot", "Alumni Mall" };
            // use your own layout
        ListAdapter adapter = new ArrayAdapter<String>(LocationStart.this,
                android.R.layout.simple_list_item_1, values);
            setListAdapter(adapter);
    }
    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
      Bundle extras =getIntent().getExtras();
      String currentPersonName = extras.getString("name");
      String email = extras.getString("email");
      String DrivRid = extras.getString("DrivRid");
      String timePick =extras.getString("timePick");
      String smoke = extras.getString("smoke");
      int hour = extras.getInt("hour");
      int min = extras.getInt("min");
      int seat =extras.getInt("seatNum");
      
      String item = (String) getListAdapter().getItem(position);
      Toast.makeText(this, timePick + " selected", Toast.LENGTH_LONG).show();
      
      Intent i = new Intent(LocationStart.this, LocationEnd.class);
  	  i.putExtra("name", currentPersonName);
  	  i.putExtra("email", email);
  	  i.putExtra("smoke", smoke);
      i.putExtra("DrivRid2", DrivRid);
      i.putExtra("timePick", timePick);
      i.putExtra("hour", hour);
      i.putExtra("min", min);
      i.putExtra("seatNum", seat);
      i.putExtra("SLoc", item);
      startActivity(i);
  
    }
}

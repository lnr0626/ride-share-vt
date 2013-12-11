package com.anky.googleplus;

import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.app.Activity;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.Toast;

public class DriverInfo extends Activity implements OnClickListener {

	private Button submit;
	private TimePicker timPick;
	private EditText numPick;
	private Spinner spinner1;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_driver_info);
		submit =(Button) findViewById(R.id.submitButton);
		submit.setOnClickListener(this);
		timPick = (TimePicker) findViewById(R.id.timePicker1);
		numPick = (EditText) findViewById(R.id.editText1);
		
		addingItemsOnSpinner();
	}
	public void addingItemsOnSpinner(){
		
		spinner1 = (Spinner) findViewById(R.id.spinner1);
		List<String> list = new ArrayList<String>();
		list.add("Non-Smoking");
		list.add("Smoking");
		
		ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
			android.R.layout.simple_spinner_item, list);
		dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinner1.setAdapter(dataAdapter);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.driver_info, menu);
		return true;
	}


	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch(v.getId()) {
        case R.id.submitButton: 
        	if(numPick.getText().toString().equals("")){
        		Toast.makeText(this,  "Did not select number of seat available", Toast.LENGTH_LONG).show();
        		break;
        	}
        	Bundle extras =getIntent().getExtras();
        	String currentPersonName = extras.getString("name");
        	String DrivRid = extras.getString("DrivRid");
        	String email = extras.getString("email");
        	int hour, min, seat;
        	String time;
        	String smokeNonSmoke;
        	
        	seat = Integer.parseInt(numPick.getText().toString());
        	smokeNonSmoke = String.valueOf(spinner1.getSelectedItem());
            Toast.makeText(this, smokeNonSmoke + " selected", Toast.LENGTH_LONG).show();

            
        	hour = timPick.getCurrentHour();
        	min =  timPick.getCurrentMinute();
        	String timpickhour = timPick.getCurrentHour().toString();
        	String timpickmin = timPick.getCurrentMinute().toString();
        	time = timpickhour + ":" + timpickmin;
        	Intent i = new Intent(DriverInfo.this, LocationStart.class);
        	i.putExtra("smoke", smokeNonSmoke);
        	i.putExtra("name", currentPersonName);
        	i.putExtra("email", email);
            i.putExtra("DrivRid", DrivRid);
            i.putExtra("hour", hour);
            i.putExtra("min", min);
        	i.putExtra("timePick", time);
        	i.putExtra("seatNum", seat);
        	
            startActivity(i);
            
			break;
		}
	}

}

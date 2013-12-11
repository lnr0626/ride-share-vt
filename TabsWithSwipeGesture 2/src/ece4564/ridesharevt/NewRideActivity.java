package ece4564.ridesharevt;

import ece4564.ridesharevt.R;

import java.util.ArrayList;
import java.util.List;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;

import android.os.Bundle;
import android.preference.PreferenceManager;
import android.app.Activity;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.Toast;

public class NewRideActivity extends Activity implements OnClickListener {

	private Button buttonRider, buttonDriver, buttonNext;
	private Spinner StartLocation, EndLocation, SNS;
	private TimePicker DepartTime;
	private EditText SeatNumber;
	static String currentPersonName;
	static String email;
	static String DriverRider;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_driver_rider);
		buttonRider = (Button) findViewById(R.id.RiderButton);
		buttonRider.setOnClickListener(this);
		buttonDriver = (Button) findViewById(R.id.DriverButton);
		buttonDriver.setOnClickListener(this);
		buttonNext = (Button) findViewById(R.id.NextButton);
		buttonNext.setOnClickListener(this);
		SeatNumber =(EditText) findViewById(R.id.seatNum);
		DepartTime = (TimePicker) findViewById(R.id.DepartureTime);
		DepartTime.setVisibility(View.INVISIBLE);
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
		currentPersonName = prefs.getString("name", "");
		email = prefs.getString("email", "");
		Toast.makeText(this, currentPersonName + " selected", Toast.LENGTH_LONG)
				.show();
		addingEndLocations();
		addingStartLocations();
		addingSmokeOption();

	}

	public void addingEndLocations() {
		EndLocation = (Spinner) findViewById(R.id.EndLocationSpinner);
		String[] EndValues = new String[] { "Village", "Hunter's Ridge",
				"Pheasant Run", "Maple Ridge", "Foxridge" };
		ArrayAdapter adapter = new ArrayAdapter<String>(NewRideActivity.this,
				android.R.layout.simple_spinner_item, EndValues);
		EndLocation.setAdapter(adapter);

	}

	public void addingStartLocations() {
		StartLocation = (Spinner) findViewById(R.id.StartLocationSpinner);
		String[] StartValues = new String[] { "Perry Street Parking Garage",
				"Surge Parking lot", "Burruss Hall/Board of Visitors",
				"Washington Street Parking lot", "Alumni Mall" };
		ArrayAdapter adapter = new ArrayAdapter<String>(NewRideActivity.this,
				android.R.layout.simple_spinner_item, StartValues);
		StartLocation.setAdapter(adapter);
	}

	public void addingSmokeOption() {

		SNS = (Spinner) findViewById(R.id.SmokeSpinner);
		List<String> list = new ArrayList<String>();
		list.add("Non-Smoking");
		list.add("Smoking");

		ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item, list);
		dataAdapter
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		SNS.setAdapter(dataAdapter);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.driver_rider, menu);
		return true;
	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.DriverButton:
			DepartTime.setVisibility(View.VISIBLE);
			DriverRider = "driver";
			// person.storeName("Driver");
			// String DriverRider="driver";
			// Intent i = new Intent(DriverRider.this, DriverInfo.class);
			// i.putExtra("name", currentPersonName);
			// i.putExtra("DrivRid", DriverRider);
			// i.putExtra("email", email);
			// startActivity(i);

			break;
		case R.id.RiderButton:
			DepartTime.setVisibility(View.INVISIBLE);
			DriverRider = "rider";
			// person.storeName("Rider");
			// String RiderDriv="rider";
			// String timePick = "NULL";
			// String seat = "NULL";
			// Intent j = new Intent(DriverRider.this, LocationStart.class);
			// j.putExtra("name", currentPersonName);
			// j.putExtra("email", email);
			// j.putExtra("timePick", timePick);
			// j.putExtra("seatNum", seat);
			// j.putExtra("DrivRid", RiderDriv);
			// startActivity(j);

			break;
		case R.id.NextButton:

			String StartLoc,
			EndLoc,
			smokeNoSmoke,
			seat;

			smokeNoSmoke = String.valueOf(SNS.getSelectedItem());
			StartLoc = String.valueOf(StartLocation.getSelectedItem());
			EndLoc = String.valueOf(EndLocation.getSelectedItem());
			seat = SeatNumber.getText().toString();
			
			String timpickhour = DepartTime.getCurrentHour().toString();
			String timpickmin = DepartTime.getCurrentMinute().toString();
			String time = timpickhour + ":" + timpickmin;

			Intent i = new Intent(NewRideActivity.this, FinalStop.class);
			i.putExtra("name", currentPersonName);
			i.putExtra("email", email);
			i.putExtra("driverrider", DriverRider);
			i.putExtra("startloc", StartLoc);
			i.putExtra("endloc", EndLoc);
			i.putExtra("departuretime", time);
			i.putExtra("SNS", smokeNoSmoke);
			i.putExtra("seatNum", seat);

			startActivity(i);
			
			break;

		}
	}

}

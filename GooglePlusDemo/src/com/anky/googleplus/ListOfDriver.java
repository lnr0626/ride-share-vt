package com.anky.googleplus;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import android.app.ListActivity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

public class ListOfDriver extends ListActivity {

	String result;
	String url = "http://atrayan.no-ip.org:4659/add";

	static String currentPersonName;
	static String email;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_list_of_driver);

		
		Bundle extras =getIntent().getExtras();
	      currentPersonName = extras.getString("name");
	      email = extras.getString("email");
	      String LocE = extras.getString("endLoc");
	      
	     url = url + "?endLoc=" + LocE;
	      
	    String newURL = url.replaceAll(" ", "%20");
		DriverList task = new DriverList();
		task.execute(newURL);

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.list_of_driver, menu);
		return true;
	}

	private static String convertInputStreamToString(InputStream inputStream)
			throws IOException {
		BufferedReader bufferedReader = new BufferedReader(
				new InputStreamReader(inputStream));
		String line = "";
		String result = "";
		while ((line = bufferedReader.readLine()) != null)
			result += line;

		inputStream.close();
		return result;

	}

	public class DriverList extends AsyncTask<String, Void, ArrayList<Driver>> {

		@Override
		protected ArrayList<Driver> doInBackground(String... urls) {
			// TODO Auto-generated method stub

			// Hashmap for ListView
			ArrayList<Driver> driverList = new ArrayList<Driver>();

			// getting JSON string from URL
			HttpClient client = new DefaultHttpClient();
			String url = urls[0];
			HttpGet request = new HttpGet(url);
			try {
				HttpResponse response = client.execute(request);

				InputStream inputStream = response.getEntity().getContent();

				// convert inputstream to string
				if (inputStream != null)
					result = convertInputStreamToString(inputStream);
				else
					return driverList;

				Object obj = JSONValue.parse(result);
				JSONArray array = (JSONArray) obj;

				// looping through All Contacts
				for (int i = 0; i < array.size(); i++) {
					JSONObject c = (JSONObject) array.get(i);

					// Storing each json item in variable

					String id = (String) c.get("ID");
					String name = (String) c.get("name");
					String email = (String) c.get("email");
					String numSeats = (String) c.get("numSeats");
					String status = (String) c.get("status");
					String time = (String) c.get("tod");
					String startLoc = (String) c.get("startLoc");
					String endLoc = (String) c.get("endLoc");
					String smoke = (String) c.get("smoke");

					Log.d("tag", name + time);
					Driver drivers = new Driver();
					drivers.id = id;
					drivers.name = name;
					drivers.numSeats = numSeats;
					drivers.status = status;
					drivers.tod = time;
					drivers.startLoc = startLoc;
					drivers.endLoc = endLoc;
					drivers.smoke = smoke;
					drivers.email = email;

					driverList.add(drivers);

				}
			} catch (ClientProtocolException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return driverList;
		}

		@Override
		public void onPostExecute(ArrayList<Driver> output) {
			
			ListAdapter adapter = new ArrayAdapter<Driver>(ListOfDriver.this,
					android.R.layout.simple_list_item_1, output);
			ListOfDriver.this.setListAdapter(adapter);
			ListOfDriver.this.getListView().setOnItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> arg0, View arg1,
						int arg2, long arg3) {
					ListOfDriver.this.onListItemClick((ListView) arg0, arg1, arg2, arg3);
				}		
			});
		   // Toast.makeText(ListOfDriver.this, "hello" + " selected", Toast.LENGTH_LONG).show();

		}
		
	}
	@Override
	public void onListItemClick(ListView l, View v, int position, long id){
	Driver item = (Driver) getListAdapter().getItem(position);
    Toast.makeText(ListOfDriver.this, item.name + " selected", Toast.LENGTH_LONG).show();
    String DriverEmail = item.email;
    Intent i = new Intent(ListOfDriver.this, Chat.class);
    i.putExtra("name", currentPersonName);
    i.putExtra("email", email);
    i.putExtra("driverEmail", DriverEmail);
    
    startActivity(i);
	}
	
}

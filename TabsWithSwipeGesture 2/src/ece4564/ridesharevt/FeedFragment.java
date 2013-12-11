package ece4564.ridesharevt;

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

import ece4564.ridesharevt.R;
import ece4564.ridesharevt.MatchesFragment.FetchDriverListTask;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class FeedFragment extends ListFragment {

	String result;
	private final static String URL = "http://atrayan.no-ip.org:4659/add";

	String currentPersonName;
	String email;
	String endLoc;
	String requestUrl;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View rootView = inflater.inflate(R.layout.fragment_feed, container,
				false);
		super.onCreate(savedInstanceState);

		return rootView;

	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(activity);
		currentPersonName = prefs.getString("name", "");
		email = prefs.getString("email", "");
		endLoc = prefs.getString("endLoc",null);
		
		//if(endLoc != null) {
			requestUrl = URL;// + "?endLoc=" + endLoc;
			
			String newURL = requestUrl.replaceAll(" ", "%20");
			FetchDriverListTask task = new FetchDriverListTask();
			task.execute(newURL);
		//}
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

	public class FetchDriverListTask extends
			AsyncTask<String, Void, ArrayList<Driver>> {

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

			ListAdapter adapter = new ArrayAdapter<Driver>(getActivity(),
					android.R.layout.simple_list_item_1, output);
			FeedFragment.this.setListAdapter(adapter);
			FeedFragment.this.getListView().setOnItemClickListener(
					new OnItemClickListener() {

						@Override
						public void onItemClick(AdapterView<?> arg0, View arg1,
								int arg2, long arg3) {
							FeedFragment.this.onListItemClick((ListView) arg0,
									arg1, arg2, arg3);
						}
					});

		}
	}

	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		Driver item = (Driver) getListAdapter().getItem(position);
		Toast.makeText(getActivity(), item.name + " selected",
				Toast.LENGTH_LONG).show();
		String DriverEmail = item.email;
		Intent i = new Intent(getActivity(), ChatActivity.class);
		i.putExtra("name", currentPersonName);
		i.putExtra("email", email);
		i.putExtra("driverEmail", DriverEmail);

		startActivity(i);
	}
}

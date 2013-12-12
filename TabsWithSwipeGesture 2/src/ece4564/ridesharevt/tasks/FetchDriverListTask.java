package ece4564.ridesharevt.tasks;

import android.support.v4.app.ListFragment;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import ece4564.ridesharevt.Driver;

/**
 * Created by lloyd on 12/11/13.
 * //TODO: Fill in class info
 *
 * @author lloyd
 */
public class FetchDriverListTask extends AsyncTask<String, Void, ArrayList<Driver>> {

    private ListFragment fragmentToUpdate;

    private String result;

    public FetchDriverListTask(ListFragment fragment) {
        fragmentToUpdate = fragment;
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

        ListAdapter adapter = new ArrayAdapter<Driver>(fragmentToUpdate.getActivity(),
                android.R.layout.simple_list_item_1, output);
        fragmentToUpdate.setListAdapter(adapter);
        fragmentToUpdate.getListView().setOnItemClickListener(
                new AdapterView.OnItemClickListener() {

                    @Override
                    public void onItemClick(AdapterView<?> arg0, View arg1,
                                            int arg2, long arg3) {
                        fragmentToUpdate.onListItemClick((ListView) arg0,
                                arg1, arg2, arg3);
                    }
                });

    }
}

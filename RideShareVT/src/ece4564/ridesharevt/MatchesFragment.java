package ece4564.ridesharevt;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import ece4564.ridesharevt.tasks.FetchDriverListTask;

public class MatchesFragment extends ListFragment {

    String result;
    private final static String URL = "http://atrayan.no-ip.org:4659/add";

    String currentPersonName;
    String email;
    String endLoc;
    String requestUrl;
    SharedPreferences prefs;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_matches, container,
                false);
        super.onCreate(savedInstanceState);

        return rootView;

    }

    public void refresh() {
        endLoc = prefs.getString("endLoc", null);
        if (endLoc != null) {
            requestUrl = URL + "?endLoc=" + endLoc;

            String newURL = requestUrl.replaceAll(" ", "%20");
            FetchDriverListTask task = new FetchDriverListTask(this);
            task.execute(newURL);
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        prefs = PreferenceManager.getDefaultSharedPreferences(activity);
        currentPersonName = prefs.getString("name", "");
        email = prefs.getString("email", "");

        refresh();
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

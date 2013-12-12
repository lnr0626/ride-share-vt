package ece4564.ridesharevt;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

public class FinalStop extends Activity implements OnClickListener {
    TextView finalText, finalText2, finalText3, finalText4, finalText5, finalText6;


    public static final String URL = "http://10.0.2.2:8080/add";
    static String LocS;

    static String LocE;

    static String currentPersonName;

    static String DrivRid;

    static String email;
    static int min;

    static int hour;

    static String seat;
    static String smoke;

    private Button drivButton, ridButton;
    static String timepick;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_final_stop);


        Bundle extras = getIntent().getExtras();
        String DRID;
        DRID = extras.getString("driverrider");
        DrivRid = DRID.trim();
        seat = extras.getString("seatNum");
        timepick = extras.getString("departuretime");
//		hour = extras.getInt("hour");
//		min = extras.getInt("min");
        LocS = extras.getString("startloc");
        LocE = extras.getString("endloc");
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        currentPersonName = prefs.getString("name", "");
        email = prefs.getString("email", "");
        smoke = extras.getString("SNS");
        String theRider = "rider";
        String theDriver = "driver";

        finalText = (TextView) findViewById(R.id.textViewFinal);
        finalText2 = (TextView) findViewById(R.id.textViewFinal2);
        finalText3 = (TextView) findViewById(R.id.textViewFinal3);
        finalText4 = (TextView) findViewById(R.id.textViewFinal4);
        finalText5 = (TextView) findViewById(R.id.textViewFinal5);
        finalText6 = (TextView) findViewById(R.id.textViewFinal6);

        drivButton = (Button) findViewById(R.id.button1);
        drivButton.setOnClickListener(this);

        finalText.setText(DrivRid);
        finalText2.setText("Start Location: " + LocS);
        finalText3.setText("End Location: " + LocE);
        if (DrivRid.equals("driver")) {
            finalText4.setText("Departure Time: " + timepick);
        } else
            finalText4.setVisibility(View.INVISIBLE);

        finalText5.setText(currentPersonName);
        finalText6.setText(smoke);

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.final_stop, menu);
        return true;
    }

    public class GetXMLTask extends AsyncTask<String, Void, String> {
        HttpClient client = new DefaultHttpClient();

        @Override
        protected String doInBackground(String... urls) {
            String line = null;
            StringBuilder sb = new StringBuilder();
            String url = urls[0];
            HttpGet request = new HttpGet(url);

            try {
                HttpResponse response = client.execute(request);
                final int statusCode = response.getStatusLine().getStatusCode();

                if (statusCode != HttpStatus.SC_OK) {
                    Log.w(getClass().getSimpleName(), "Error " + statusCode
                            + " for URL " + url);
                }

                InputStream in = response.getEntity().getContent();
                BufferedReader reader = new BufferedReader(
                        new InputStreamReader(in));

                while ((line = reader.readLine()) != null) {
                    sb.append(line + "\n");
                }

            } catch (Exception e) {
                e.printStackTrace();
            }

            String httpresponseval = sb.toString();

            return httpresponseval;

        }

        @Override
        protected void onPostExecute(String output) {
            Toast.makeText(getBaseContext(), DrivRid + " " + "driver", Toast.LENGTH_LONG)
                    .show();
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(FinalStop.this);

            if (DrivRid.equals("driver")) {
                Intent i = new Intent(FinalStop.this, MainActivity.class);
                i.putExtra("name", currentPersonName);
                i.putExtra("email", email);
                startActivity(i);

            } else if (DrivRid.equals("rider")) {
                Intent i = new Intent(FinalStop.this, MainActivity.class);

                prefs.edit().putString("endLoc", LocE).commit();

                startActivity(i);
            }

        }
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button1:

                String drivRide = DrivRid;
                String sHour = String.valueOf(hour);
                String sMin = String.valueOf(min);
                String finalTime = sHour + ":" + sMin;
                String sSeat = String.valueOf(seat);
                String name = currentPersonName;
                String smokeNonSmoke = smoke;

                Toast.makeText(FinalStop.this, "still skips" + " selected",
                        Toast.LENGTH_LONG).show();

                String newURL = "http://atrayan.no-ip.org:4659/add?" + "name="
                        + name + "&" + "numSeats=" + sSeat + "&" + "status="
                        + DrivRid + "&" + "tod=" + timepick + "&" + "startLoc="
                        + LocS + "&" + "endLoc=" + LocE + "&" + "smoke=" + smoke
                        + "&" + "email=" + email;

                newURL = newURL.replaceAll(" ", "%20");
                // make POST request to the given URL
                GetXMLTask task = new GetXMLTask();
                task.execute(newURL);
                break;

        }

    }


}

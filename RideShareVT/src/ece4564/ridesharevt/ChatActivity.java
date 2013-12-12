package ece4564.ridesharevt;


import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.lloydramey.smalltalk.Network;

import java.util.ArrayList;

import ece4564.ridesharevt.R;

public class ChatActivity extends Activity {

    String myEmail;
    ListView myList;
    ArrayList<Network.Message> messages;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chat_main);

        messages = new ArrayList<Network.Message>();

        myEmail = "";

        //finding the list view
        myList = (ListView) findViewById(R.id.myList);
        myList.setAdapter(new MyCustomAdapter());
        myList.setCacheColorHint(0);

    }


    /**
     * This class serves as the adapter that draws rows and provides data to the list
     *
     * @author nora
     */
    class MyCustomAdapter extends BaseAdapter {

        /**
         * returns the count of elements in the Array that is used to draw the text in rows
         *
         * @see android.widget.Adapter#getCount()
         */
        @Override
        public int getCount() {
            return messages.size();
        }

        /**
         * Get the data item associated with the specified position in the data set.
         *
         * @param position The position of the row that was clicked (0-n)
         * @see android.widget.Adapter#getItem(int)
         */
        @Override
        public String getItem(int position) {
            return messages.get(position).body;
        }

        /**
         * Get the row id associated with the specified position in the list.
         *
         * @param position The position of the row that was clicked (0-n)
         * @see android.widget.Adapter#getItemId(int)
         */
        @Override
        public long getItemId(int position) {
            return position;
        }

        /**
         * Returns the complete row that the System draws.
         * It is called every time the System needs to draw a new row;
         * You can control the appearance of each row inside this function.
         *
         * @param position    The position of the row that was clicked (0-n)
         * @param convertView The View object of the row that was last created. null if its the first row
         * @param parent      The ViewGroup object of the parent view
         * @see android.widget.Adapter#getView(int, android.view.View, android.view.ViewGroup)
         */

        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = getLayoutInflater();
            View row;
            if (!messages.get(position).from.email.equals(myEmail)) {
                row = inflater.inflate(R.layout.list_row_layout_even, parent, false);
                TextView textLabel = (TextView) row.findViewById(R.id.text);

                textLabel.setText(messages.get(position).body);

            } else {
                row = inflater.inflate(R.layout.list_row_layout_odd, parent, false);
                TextView textLabel = (TextView) row.findViewById(R.id.text);

                textLabel.setText(messages.get(position).body);
            }

            return (row);
        }
    }

}
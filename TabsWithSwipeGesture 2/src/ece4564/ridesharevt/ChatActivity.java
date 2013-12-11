package ece4564.ridesharevt;



import ece4564.ridesharevt.R;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.lloydramey.smalltalk.Network;

public class ChatActivity extends Activity {

	String[] data = {"Sample User Data ","Sample User Data ","Sample User Data ","Sample User Data ",
			"Sample User Data ","Sample User Data ","Sample User Data ","Sample User Data ",
			"Sample User Data ","Sample User Data ","Sample User Data ","Sample User Data ",
			"Sample User Data ","Sample User Data ","Sample User Data ","Sample User Data "};
	Drawable[] usrimg=null;
	String bgimg = "",_user="",_pass="";
	int odd_resID,even_resID;
	ListView myList;

    /** Messenger for communicating with the service. */
    Messenger mService = null;

    /** Flag indicating whether we have called bind on the service. */
    boolean mBound;



    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch(msg.what) {
                case SmallTalkService.MSG_LOGIN_REJECTED:
                    Toast.makeText(ChatActivity.this, "Login Failed", Toast.LENGTH_LONG).show();
                    finish();
                    break;
                default:
                    super.handleMessage(msg);
                    break;
            }
        }
    };

    private ServiceConnection mConnection = new ServiceConnection() {
        public void onServiceConnected(ComponentName className, IBinder service) {
            // This is called when the connection with the service has been
            // established, giving us the object we can use to
            // interact with the service.  We are communicating with the
            // service using a Messenger, so here we get a client-side
            // representation of that from the raw IBinder object.
            mService = new Messenger(service);
            mBound = true;
            Bundle data = new Bundle();
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(ChatActivity.this);
            data.putInt(SmallTalkService.SERVER_PORT_PREF,
                    prefs.getInt(SmallTalkService.SERVER_PORT_PREF,
                            SmallTalkService.DEF_SERVER_PORT));
            data.putString(SmallTalkService.SERVER_URL_PREF,
                    prefs.getString(SmallTalkService.SERVER_URL_PREF,
                            SmallTalkService.DEF_SERVER_URL));
            Message start = Message.obtain(null, SmallTalkService.MSG_CONNECT);
            start.setData(data);
            try {
                mService.send(start);
                Network.User user = new Network.User();
                user.email = prefs.getString("email", "");
                user.first = prefs.getString("name", "");
                Message msg = Message.obtain(null, SmallTalkService.MSG_LOGIN, user);
                msg.replyTo = messenger;
                try {
                    mService.send(msg);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }

        public void onServiceDisconnected(ComponentName className) {
            // This is called when the connection with the service has been
            // unexpectedly disconnected -- that is, its process crashed.
            mService = null;
            mBound = false;
        }
    };

    private final Messenger messenger = new Messenger(handler);
    @Override
    protected void onStart() {
        super.onStart();
        // Bind to the service
        bindService(new Intent(this, SmallTalkService.class), mConnection,
                Context.BIND_AUTO_CREATE);
    }

    @Override
    protected void onStop() {
        super.onStop();
        // Unbind from the service
        if (mBound) {
            Message msg = Message.obtain(null, SmallTalkService.MSG_DISCONNECT);
            try {
                mService.send(msg);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
            unbindService(mConnection);
            mBound = false;
        }
    }

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.chat_main);
		
		//finding the list view
		myList = (ListView)findViewById(R.id.myList);
		myList.setAdapter(new MyCustomAdapter());
		myList.setCacheColorHint(0);
		
	}

	
	
	/**
	 * This class serves as the adapter that draws rows and provides data to the list 
	 * @author nora
	 *
	 */
	class MyCustomAdapter extends BaseAdapter {
		
		/**
		 * returns the count of elements in the Array that is used to draw the text in rows 
		 * @see android.widget.Adapter#getCount()
		 */
		@Override
		public int getCount() {
			return data.length;
		}

		/**
		 * Get the data item associated with the specified position in the data set.
		 * (not Implemented at this point)
		 * @param position The position of the row that was clicked (0-n)
		 * @see android.widget.Adapter#getItem(int)
		 */
		@Override
		public String getItem(int position) {
			// TODO Auto-generated method stub
			return null;
		}

		/**
		 * Get the row id associated with the specified position in the list.
		 * (not implemented at this point)
		 * @param position The position of the row that was clicked (0-n)
		 * @see android.widget.Adapter#getItemId(int)
		 */
		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		/**
		 * Returns the complete row that the System draws.
		 * It is called every time the System needs to draw a new row;
		 * You can control the appearance of each row inside this function.
		 * @param position The position of the row that was clicked (0-n)
		 * @param convertView The View object of the row that was last created. null if its the first row
		 * @param parent The ViewGroup object of the parent view
		 * @see android.widget.Adapter#getView(int, android.view.View, android.view.ViewGroup)
		 */

		public View getView(int position, View convertView, ViewGroup parent) {
			LayoutInflater inflater = getLayoutInflater();
			View row;
			String even_color,odd_color;
			SharedPreferences prefList = getSharedPreferences("PrefsFile",MODE_PRIVATE);;
			even_color = prefList.getString("even_bubble_color","pink");
			odd_color = prefList.getString("odd_bubble_color","green");
			int even_color_id=getResources().getIdentifier(even_color,"drawable","com.teks.chilltwit"),
				odd_color_id=getResources().getIdentifier(odd_color,"drawable","com.teks.chilltwit");
			ImageView even_view,odd_view;
			//System.out.println("Timeline: Position: "+position+", Length: "+data.length);
//			if(position!=data.length-1){
			if(position%2==0){
				row = inflater.inflate(R.layout.list_row_layout_even, parent, false);
				TextView textLabel = (TextView) row.findViewById(R.id.text);
				
				textLabel.setText(data[position]);
				
			}else{
				row = inflater.inflate(R.layout.list_row_layout_odd, parent, false);
				TextView textLabel = (TextView) row.findViewById(R.id.text);
				
				textLabel.setText(data[position]);
			}

			return (row);
		}
	}
	
}
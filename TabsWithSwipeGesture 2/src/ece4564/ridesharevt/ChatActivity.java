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

import java.util.ArrayList;

public class ChatActivity extends Activity {

    String myEmail;
    String cid;
	ListView myList;
    ArrayList<Network.Message> messages;

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
                case SmallTalkService.MSG_NEW_MESSAGE:
                    Toast.makeText(ChatActivity.this, "New Message", Toast.LENGTH_LONG).show();
                    break;
                case SmallTalkService.MSG_NEW_CONVERSATION:
                    Toast.makeText(ChatActivity.this, "New Converstaion", Toast.LENGTH_LONG).show();
                    cid = ((Network.Conversation)msg.obj).id;
                    break;
                case SmallTalkService.MSG_MESSAGES_IN_CONVERSATION:
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
                    myEmail = user.email;
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

        Bundle extras = getIntent().getExtras();

        String otherEmail = extras.getString("other_email");

        Network.Conversation convo = new Network.Conversation();
        convo.userEmails.add(myEmail);
        convo.userEmails.add(otherEmail);
        convo.name = otherEmail;
        Message msg = Message.obtain(null, SmallTalkService.MSG_START_CONVERSTAION, convo);
        try {
            mService.send(msg);
        } catch (RemoteException e) {
            e.printStackTrace();
        }

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
			return messages.size();
		}

		/**
		 * Get the data item associated with the specified position in the data set.
		 * @param position The position of the row that was clicked (0-n)
		 * @see android.widget.Adapter#getItem(int)
		 */
		@Override
		public String getItem(int position) {
			return messages.get(position).body;
		}

		/**
		 * Get the row id associated with the specified position in the list.
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
		 * @param position The position of the row that was clicked (0-n)
		 * @param convertView The View object of the row that was last created. null if its the first row
		 * @param parent The ViewGroup object of the parent view
		 * @see android.widget.Adapter#getView(int, android.view.View, android.view.ViewGroup)
		 */

		public View getView(int position, View convertView, ViewGroup parent) {
			LayoutInflater inflater = getLayoutInflater();
			View row;
			if(!messages.get(position).from.email.equals(myEmail)) {
				row = inflater.inflate(R.layout.list_row_layout_even, parent, false);
				TextView textLabel = (TextView) row.findViewById(R.id.text);
				
				textLabel.setText(messages.get(position).body);
				
			}else{
				row = inflater.inflate(R.layout.list_row_layout_odd, parent, false);
				TextView textLabel = (TextView) row.findViewById(R.id.text);

                textLabel.setText(messages.get(position).body);
			}

			return (row);
		}
	}
	
}
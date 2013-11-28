package com.lloydramey.android.smalltalkclient;

import android.app.Activity;
import android.app.ActionBar;
import android.app.Fragment;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.os.Build;
import android.widget.Button;
import android.widget.Toast;

import com.lloydramey.smalltalk.Network;

public class MainActivity extends Activity {
    /** Messenger for communicating with the service. */
    Messenger mService = null;

    /** Flag indicating whether we have called bind on the service. */
    boolean mBound;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction()
                    .add(R.id.container, new PlaceholderFragment())
                    .commit();
        }
    }

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

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch(msg.what) {
                case SmallTalkService.MSG_LOGIN_REJECTED:
                    Toast.makeText(MainActivity.this, "Login Failed", Toast.LENGTH_LONG).show();
                    finish();
                    break;
                default:
                    super.handleMessage(msg);
                    break;
            }
        }
    };

    private final Messenger messenger = new Messenger(handler);

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
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
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
                String name = prefs.getString(ConfigurationActivity.USER_NAME_PREF, "Lloyd");
                String email = prefs.getString(ConfigurationActivity.USER_EMAIL_PREF, "lnr0626@gmail.com");
                Network.User user = new Network.User();
                user.email = email;
                user.first = name;
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

    /**
     * A placeholder fragment containing a simple view.
     */
    public class PlaceholderFragment extends Fragment {

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_main, container, false);
            ((Button)rootView.findViewById(R.id.disconnect)).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Message discon = Message.obtain(null, SmallTalkService.MSG_DISCONNECT);
                    discon.replyTo = messenger;
                    try {
                        mService.send(discon);
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                }
            });
            ((Button)rootView.findViewById(R.id.reconnect)).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Message recon = Message.obtain(null, SmallTalkService.MSG_RECONNECT);
                    try {
                        mService.send(recon);
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                }
            });
            return rootView;
        }
    }

}

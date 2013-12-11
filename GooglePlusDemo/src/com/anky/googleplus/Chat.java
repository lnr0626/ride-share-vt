package com.anky.googleplus;

import com.google.android.gms.plus.PlusClient;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.plus.PlusClient;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;

public class Chat extends Activity implements OnClickListener,
PlusClient.ConnectionCallbacks, PlusClient.OnConnectionFailedListener {

    private PlusClient mPlusClient;
    private View mSignOutButton;
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_chat);
		
        mPlusClient = new PlusClient.Builder(this, this, this)
        .setActions("http://schemas.google.com/AddActivity")
        .build();
        
        mSignOutButton = findViewById(R.id.sign_out_button);
        mSignOutButton.setOnClickListener(this);
        
		Bundle extras = getIntent().getExtras();
		String currentPersonName = extras.getString("name");
		String email = extras.getString("email");
	}

    @Override
    public void onStart() {
        super.onStart();
        mPlusClient.connect();
    }
    
    @Override
    public void onStop() {
        mPlusClient.disconnect();
        super.onStop();
    }
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.chat, menu);
		return true;
	}

	@Override
	public void onConnectionFailed(ConnectionResult result) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onConnected(Bundle connectionHint) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onDisconnected() {
		// TODO Auto-generated method stub
        mPlusClient.connect();
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
        if (mPlusClient.isConnected()) {
            mPlusClient.clearDefaultAccount();
            mPlusClient.disconnect();
            mPlusClient.connect();
            finish();
        }
	}

}

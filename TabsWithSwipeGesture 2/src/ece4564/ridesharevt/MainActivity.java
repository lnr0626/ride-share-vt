package ece4564.ridesharevt;

import ece4564.ridesharevt.adapter.TabsPagerAdapter;
import ece4564.ridesharevt.R;
import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.plus.PlusClient;



public class MainActivity extends FragmentActivity implements
		ActionBar.TabListener, PlusClient.ConnectionCallbacks, PlusClient.OnConnectionFailedListener {

	private PlusClient mPlusClient;
	
	
	private ViewPager viewPager;
	private TabsPagerAdapter mAdapter;
	private ActionBar actionBar;
	// Tab titles
	private String[] tabs = { "Ride Feed", "Matches", "Your Rides" };

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		mPlusClient = new PlusClient.Builder(this, this, this).setActions("http://schemas.google.com/AddActivity")
		        .build();

		
		// Initilization
		viewPager = (ViewPager) findViewById(R.id.pager);
		actionBar = getActionBar();
		mAdapter = new TabsPagerAdapter(getSupportFragmentManager());

		viewPager.setAdapter(mAdapter);
		actionBar.setHomeButtonEnabled(false);
		
		actionBar.setDisplayShowHomeEnabled(false); 
		actionBar.setDisplayShowTitleEnabled(false); 
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);		

		// Adding Tabs
		for (String tab_name : tabs) {
			actionBar.addTab(actionBar.newTab().setText(tab_name)
					.setTabListener(this));
		}

		/**
		 * on swiping the viewpager make respective tab selected
		 * */
		viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

			@Override
			public void onPageSelected(int position) {
				// on changing the page
				// make respected tab selected
				actionBar.setSelectedNavigationItem(position);
			}

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {
			}

			@Override
			public void onPageScrollStateChanged(int arg0) {
			}
		});
	}
	
	@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.activity_main_actions, menu);
 
        return super.onCreateOptionsMenu(menu);
    }
    /**
     * On selecting action bar icons
     * */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Take appropriate action for each action item click
        switch (item.getItemId()) {
        case R.id.action_profile:
            // user profile
        	UserSelected();
            return true;
        case R.id.action_new:
            // new ride
            NewRideSelected();
            return true;
        case R.id.action_chat:
            // chat 
        	ChatSelected();
            return true;
     
        default:
            return super.onOptionsItemSelected(item);
        }
    }
    
 
    /**
     * Launching new activity
     * */
    @Override
    public void onStart(){
    	super.onStart();
    	mPlusClient.connect();
    }
    
    @Override
    public void onStop(){
    	mPlusClient.disconnect();
    	super.onStop();
    }
    
    private void UserSelected() {
    	if (mPlusClient.isConnected()){
    		mPlusClient.clearDefaultAccount();
    		mPlusClient.disconnect();
    		mPlusClient.connect();
    		Intent i = new Intent(MainActivity.this, Login.class);
    		//finish();
    		startActivity(i);
    	}
    	}
        //Intent i = new Intent(MainActivity.this, UserProfile.class);
        //startActivity(i);
    //}
    private void ChatSelected() {
        Intent i = new Intent(MainActivity.this, ChatActivity.class);
        startActivity(i);
    }
    private void NewRideSelected() {
        Intent i = new Intent(MainActivity.this, NewRideActivity.class);
        startActivity(i);
    }



	@Override
	public void onTabReselected(Tab tab, FragmentTransaction ft) {
	}

	@Override
	public void onTabSelected(Tab tab, FragmentTransaction ft) {
		// on tab selected
		// show respected fragment view
		viewPager.setCurrentItem(tab.getPosition());
	}

	@Override
	public void onTabUnselected(Tab tab, FragmentTransaction ft) {
	}

	@Override
	public void onConnected(Bundle connectionHint) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onDisconnected() {
		mPlusClient.connect();
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onConnectionFailed(ConnectionResult result) {
		// TODO Auto-generated method stub
		
	}

}

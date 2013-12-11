package ece4564.ridesharevt.adapter;

import ece4564.ridesharevt.YourRidesFragment;
import ece4564.ridesharevt.MatchesFragment;
import ece4564.ridesharevt.FeedFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

public class TabsPagerAdapter extends FragmentPagerAdapter {

	public TabsPagerAdapter(FragmentManager fm) {
		super(fm);
	}

	@Override
	public Fragment getItem(int index) {

		switch (index) {
		case 0:
			// Top Rated fragment activity
			return new FeedFragment();
		case 1:
			// Games fragment activity
			return new MatchesFragment();
		case 2:
			// Movies fragment activity
			return new YourRidesFragment();
		}

		return null;
	}

	@Override
	public int getCount() {
		// get item count - equal to number of tabs
		return 3;
	}

}

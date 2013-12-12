package ece4564.ridesharevt.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import ece4564.ridesharevt.ConversationsFragment;
import ece4564.ridesharevt.FeedFragment;
import ece4564.ridesharevt.MatchesFragment;

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
                return new ConversationsFragment();
        }

        return null;
    }

    @Override
    public int getCount() {
        // get item count - equal to number of tabs
        return 3;
    }

}

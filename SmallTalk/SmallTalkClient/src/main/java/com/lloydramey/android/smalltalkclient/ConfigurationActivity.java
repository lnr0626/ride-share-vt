package com.lloydramey.android.smalltalkclient;

import java.util.Locale;

import android.app.Activity;
import android.app.ActionBar;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v13.app.FragmentPagerAdapter;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.lloydramey.smalltalk.android.SmallTalkService;

public class ConfigurationActivity extends Activity implements ActionBar.TabListener {

    public final static String USER_NAME_PREF = "user_name";
    public final static String USER_EMAIL_PREF = "user_email";

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v13.app.FragmentStatePagerAdapter}.
     */
    SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    ViewPager mViewPager;

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_configuration);

        // Set up the action bar.
        final ActionBar actionBar = getActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        // When swiping between different sections, select the corresponding
        // tab. We can also use ActionBar.Tab#select() to do this if we have
        // a reference to the Tab.
        mViewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                actionBar.setSelectedNavigationItem(position);
            }
        });

        // For each of the sections in the app, add a tab to the action bar.
        for (int i = 0; i < mSectionsPagerAdapter.getCount(); i++) {
            // Create a tab with text corresponding to the page title defined by
            // the adapter. Also specify this Activity object, which implements
            // the TabListener interface, as the callback (listener) for when
            // this tab is selected.
            actionBar.addTab(
                    actionBar.newTab()
                            .setText(mSectionsPagerAdapter.getPageTitle(i))
                            .setTabListener(this));
        }
    }

    @Override
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
        // When the given tab is selected, switch to the corresponding page in
        // the ViewPager.
        mViewPager.setCurrentItem(tab.getPosition());
    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
    }

    @Override
    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            return PlaceholderFragment.newInstance(position + 1, ConfigurationActivity.this);
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            Locale l = Locale.getDefault();
            switch (position) {
                case 0:
                    return getString(R.string.title_section1).toUpperCase(l);
                case 1:
                    return getString(R.string.title_section2).toUpperCase(l);
            }
            return null;
        }
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";
        private SharedPreferences prefs;
        private Context context;

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber, Context context) {
            PlaceholderFragment fragment = new PlaceholderFragment(context);
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        public PlaceholderFragment(Context context) {
            prefs = PreferenceManager.getDefaultSharedPreferences(context);
            this.context = context;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {
            View rootView;
            TextView textView;
            switch(getArguments().getInt(ARG_SECTION_NUMBER)) {
                case 1:
                    rootView = inflater.inflate(R.layout.fragment_user_info, container, false);
                    ((TextView) rootView.findViewById(R.id.user_name)).addTextChangedListener(new TextWatcher() {
                        @Override
                        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                        }
                        @Override
                        public void onTextChanged(CharSequence s, int start, int before, int count) {
                        }
                        @Override
                        public void afterTextChanged(Editable s) {
                            if(s.toString().isEmpty()) {
                                prefs.edit().remove(USER_NAME_PREF).commit();
                            } else {
                                prefs.edit().putString(USER_NAME_PREF, s.toString()).commit();
                            }
                        }
                    });
                    ((TextView) rootView.findViewById(R.id.user_email)).addTextChangedListener(new TextWatcher() {
                        @Override
                        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                        }
                        @Override
                        public void onTextChanged(CharSequence s, int start, int before, int count) {
                        }
                        @Override
                        public void afterTextChanged(Editable s) {
                            if(s.toString().isEmpty()) {
                                prefs.edit().remove(USER_EMAIL_PREF).commit();
                            } else {
                                prefs.edit().putString(USER_EMAIL_PREF, s.toString()).commit();
                            }
                        }
                    });
                    ((TextView) rootView.findViewById(R.id.user_name)).setText(prefs.getString(USER_NAME_PREF, ""));
                    ((TextView) rootView.findViewById(R.id.user_email)).setText(prefs.getString(USER_EMAIL_PREF, ""));
                    ((Button) rootView.findViewById(R.id.login)).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            context.startService(new Intent(context, SmallTalkService.class));
                            startActivity(new Intent(context, MainActivity.class));
                        }
                    });

                    break;
                case 2:
                    rootView = inflater.inflate(R.layout.fragment_server_info, container, false);
                    ((TextView) rootView.findViewById(R.id.server_url)).addTextChangedListener(new TextWatcher() {
                        @Override
                        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                        }
                        @Override
                        public void onTextChanged(CharSequence s, int start, int before, int count) {
                        }
                        @Override
                        public void afterTextChanged(Editable s) {
                            if(s.toString().isEmpty()) {
                                prefs.edit().remove(SmallTalkService.SERVER_URL_PREF).commit();
                            } else {
                                prefs.edit().putString(SmallTalkService.SERVER_URL_PREF, s.toString()).commit();
                            }
                        }
                    });
                    ((TextView) rootView.findViewById(R.id.server_url)).setText(prefs.getString(SmallTalkService.SERVER_URL_PREF, ""));

                    ((TextView) rootView.findViewById(R.id.server_port)).addTextChangedListener(new TextWatcher() {
                        @Override
                        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                        }

                        @Override
                        public void onTextChanged(CharSequence s, int start, int before, int count) {
                        }

                        @Override
                        public void afterTextChanged(Editable s) {
                            if (s.toString().isEmpty()) {
                                prefs.edit().remove(SmallTalkService.SERVER_PORT_PREF).commit();
                            } else {
                                prefs.edit().putInt(SmallTalkService.SERVER_PORT_PREF, Integer.valueOf(s.toString())).commit();
                            }
                        }
                    });
                    ((TextView) rootView.findViewById(R.id.server_port)).setText("" + prefs.getInt(SmallTalkService.SERVER_PORT_PREF, SmallTalkService.DEF_SERVER_PORT));

                    break;
                default:
                    rootView = inflater.inflate(R.layout.fragment_configuration, container, false);
                    textView = (TextView) rootView.findViewById(R.id.section_label);
                    textView.setText(Integer.toString(getArguments().getInt(ARG_SECTION_NUMBER)));
                    break;
            }
            return rootView;
        }
    }

}

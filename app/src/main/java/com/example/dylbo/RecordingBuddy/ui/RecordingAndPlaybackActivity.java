package com.example.dylbo.RecordingBuddy.ui;


import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Window;

import com.example.dylbo.RecordingBuddy.R;
import com.example.dylbo.RecordingBuddy.database.AppDatabase;

import java.util.ArrayList;
import java.util.List;

public class RecordingAndPlaybackActivity extends AppCompatActivity {


    // Extra for the task ID to be received in the intent
    public static final String EXTRA_SONG_ID = "extraSongId";
    public static final String EXTRA_BAND_ID = "extraBandId";
    // Constant for logging
    private static final String TAG = RecordingAndPlaybackActivity.class.getSimpleName();

    //General song variables

    private int mSongID; //Song id passed through when activity starts
    private String mSongTitle;//Song title passed through when activity starts
    private AppDatabase mDb;//Database
    public ViewPagerAdapter adapter;
    public ExtendedPlaybackFragment extendedPlaybackFragment;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private RecordFragment recordFragment;
    private PlaybackFragment playbackFragment;
    private ContainerPlaybackFragment containerPlaybackFragment;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rec_playback_tabs);//Set main layout for song view

        Log.d(TAG, "Activity");
        ////////////////////////////////////////////////////////////////////////////////////////////////////
        ////////////////////Get extra from intent extra/////////////////////////
        Intent intent = getIntent();
        Bundle bundle;
        bundle = intent.getExtras();//get bundle of extras
        if (intent != null) {
            mSongID = bundle.getInt(EXTRA_SONG_ID);//get song id from bundle
            Log.d(TAG, "mSongID: " + mSongID);
            mSongTitle = bundle.getString(EXTRA_BAND_ID);//get song title from bundle
            Log.d(TAG, "mSongTitle: " + mSongTitle);
        }

        ////////////////////////////////////////////////////////////////////////////////////////////////////
        ////////////////////Set Up viewpager, tabs and fragments/////////////////////////
        viewPager =  findViewById(R.id.pager);
        adapter = new ViewPagerAdapter(getSupportFragmentManager());

        // Add Fragments to adapter one by one
        recordFragment = new RecordFragment();
        recordFragment.setArguments(bundle);

        playbackFragment = new PlaybackFragment();
        playbackFragment.setArguments(bundle);

        containerPlaybackFragment = new ContainerPlaybackFragment();
        containerPlaybackFragment.setArguments(bundle);

        /*
        // Add Fragments to adapter one by one
        playbackFragment = new PlaybackFragment();
        playbackFragment.setArguments(bundle);

        // Add Fragments to adapter one by one
        extendedPlaybackFragment = new ExtendedPlaybackFragment();
        extendedPlaybackFragment.setArguments(bundle);
*/
        adapter.addFragment(recordFragment, "RECORD");
        adapter.addFragment(containerPlaybackFragment, "PLAYBACK");

        viewPager.setAdapter(adapter);

        tabLayout = findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);

    }


    // Adapter for the viewpager using FragmentPagerAdapter
    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            Log.d(TAG, "debug fragment" + title);
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
            //Log.d(TAG, "debug fragment tag" +  mFragmentList.get(0).getFragmentManager().getFragments());
            Log.d(TAG, "debug fragment list size" +  mFragmentList.size());

        }


        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }

}

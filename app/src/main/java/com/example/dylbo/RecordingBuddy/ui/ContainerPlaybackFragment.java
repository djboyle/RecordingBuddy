package com.example.dylbo.RecordingBuddy.ui;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.dylbo.RecordingBuddy.R;
import com.example.dylbo.RecordingBuddy.Utils.MainViewModel;
import com.example.dylbo.RecordingBuddy.database.SongEntry;

import java.util.List;

public class ContainerPlaybackFragment extends Fragment
{
    // ...


    // Constant for logging
    private static final String TAG = ContainerPlaybackFragment.class.getSimpleName();

    // Extra for the task ID to be received in the intent
    public static final String EXTRA_SONG_ID = "extraSongId";
    public static final String EXTRA_BAND_ID = "extraBandId";
    //fragment codes to swap out child fragment
    private static final int FRAGMENT_PLAYBACK = 100;
    private static final int FRAGMENT_EXTENDED_PLAYBACK = 200;

    private int mSongID;
    private int mBandID;
    private int mPosition;

    private FragmentManager fragmentManager;

    public ContainerPlaybackFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_container_playback, container, false);
        mSongID = getArguments().getInt(EXTRA_SONG_ID);
        mBandID = getArguments().getInt(EXTRA_BAND_ID);

        Bundle mBundle = new Bundle();

        mBundle = getArguments();

        //Create Fragment manager
        fragmentManager = getFragmentManager();
        //Begin fragment transaction
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        PlaybackFragment playbackFragment = new PlaybackFragment();
        playbackFragment.setArguments(mBundle);
        fragmentTransaction.add(R.id.fragment_container, playbackFragment);
        fragmentTransaction.commit();

        setupViewModel();
        return rootView;

    }



    private void setupViewModel() {
        //Setup initial View model
        MainViewModel viewModel = ViewModelProviders.of(this).get(MainViewModel.class);

        //Observe for any changes to song sections
        viewModel.getSongs().observe(this, new Observer<List<SongEntry>>() {
            @Override
            public void onChanged(@Nullable List<SongEntry> songEntries) {
                Log.d(TAG, "Updating list of tasks from LiveData in ViewModel");

            }
        });

    }



    public void onArticleSelected(int position) {
        mPosition = position;
        ///Have two cases for exdtend and contract
        //Create new fragment and transaction
        ExtendedPlaybackFragment extendedPlaybackFragment = new ExtendedPlaybackFragment();
        FragmentTransaction transaction = fragmentManager.beginTransaction();

        // Replace whatever is in the fragment_container view with this fragment,
        // and add the transaction to the back stack
        transaction.replace(R.id.fragment_container, extendedPlaybackFragment);
        transaction.addToBackStack(null);

// Commit the transaction
        transaction.commit();

    }
}

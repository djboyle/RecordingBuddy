package com.example.dylbo.RecordingBuddy.ui;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;


import com.example.dylbo.RecordingBuddy.R;
import com.example.dylbo.RecordingBuddy.Utils.AudioPlay;

import com.example.dylbo.RecordingBuddy.Utils.MainViewModel;
import com.example.dylbo.RecordingBuddy.adapters.RecordingsAdapter;
import com.example.dylbo.RecordingBuddy.database.AppDatabase;
import com.example.dylbo.RecordingBuddy.database.SongEntry;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;

public class PlaybackFragment extends Fragment
        implements RecordingsAdapter.ItemClickListener, RecordingsAdapter.ItemLongClickListener {



    // Constant for logging
    private static final String TAG = PlaybackFragment.class.getSimpleName();
    //Define views and adapter
    private RecyclerView mRecordingsRV;
    private RecordingsAdapter mRecordingsAdapter;
    private ImageButton mPlayButton;
    private ProgressBar mProgressBar;
    private TextView mCurrentlyPlayingTextView;
    private ImageButton mExpandPlayback;
    private int mRecordingDuration;
    private Timer timer; //Timer used in play function
    private boolean playing= FALSE;
    private boolean firstPlay=TRUE;
    private boolean mREC_EN_FLAG = FALSE;
    private int mActiveSongIndex = 0;
    private int mSongID;
    private int mBandID;
    private AppDatabase mDb;//Database
    private AudioPlay mAudioPlay;
    private ArrayList<String> mRecordingLocations;
    // Extra for the task ID to be received in the intent
    public static final String EXTRA_SONG_ID = "extraSongId";
    public static final String EXTRA_BAND_ID = "extraBandId";
    //fragment codes to swap out child fragment
    private static final int FRAGMENT_PLAYBACK = 100;
    private static final int FRAGMENT_EXTENDED_PLAYBACK = 200;
    private OnChildFragmentInteractionListener mParentListener;


    public PlaybackFragment() {
        // Required empty public constructor
    }

    public interface OnChildFragmentInteractionListener {
        void messageFromChildToParent(String myString);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        // check if parent Fragment implements listener
        if (getParentFragment() instanceof OnChildFragmentInteractionListener) {
            mParentListener = (OnChildFragmentInteractionListener) getParentFragment();
        } else {
            throw new RuntimeException("The parent fragment must implement OnChildFragmentInteractionListener");
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        mDb = AppDatabase.getInstance(getActivity());//Get instance of Database
        //onAttachToParentFragment(this.getParentFragment());
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_playback, container, false);
        mSongID = getArguments().getInt(EXTRA_SONG_ID);
        mBandID = getArguments().getInt(EXTRA_BAND_ID);

        mRecordingsRV = rootView.findViewById(R.id.rv_recordings_list);
        mProgressBar = rootView.findViewById(R.id.recording_play_PB);
        mCurrentlyPlayingTextView = rootView.findViewById(R.id.currently_playing_TV);

        //Set up linear manager for recycler view
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        mRecordingsRV.setLayoutManager(layoutManager);
        mRecordingsRV.setItemAnimator(null);

        //Attache adapter
        mRecordingsAdapter = new RecordingsAdapter(getActivity(), this, this);
        mRecordingsRV.setAdapter(mRecordingsAdapter);

        //////////////////////Expand button setup////////////////////
        mExpandPlayback = rootView.findViewById(R.id.expand_playback_IB);
        mExpandPlayback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Launch PlaySongActivity
                Log.d(TAG, "butttonclickcyl");
                mParentListener.messageFromChildToParent("Hello, parent. I am the child fragment.");

            }
        });


        //////////////////////Play button setup////////////////////
        mPlayButton = rootView.findViewById(R.id.play_pause_rec_IB);
        mPlayButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Launch PlaySongActivity
                if (mRecordingLocations !=null &&mRecordingLocations.size()!=0){
                    if(firstPlay){
                        initialRecordingPlay();
                    }else {
                        if (playing) {
                            mPlayButton.setImageResource(R.drawable.ic_play_full_circle);
                            mAudioPlay.pause();
                            playing=FALSE;
                        } else {
                            mPlayButton.setImageResource(R.drawable.ic_pause_full_circle);
                            mAudioPlay.resume();
                            playing=TRUE;
                        }
                    }
                }
            }
        });





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
                mRecordingLocations = songEntries.get(mSongID-1).getRecordingFileLocations();
                mRecordingsAdapter.setRecordingsLocation(mRecordingLocations, mSongID);
            }
        });
    }


    public void setmCurrentlyPlayingTextView(int position){
        File file = new File(mRecordingLocations.get(position));
        mCurrentlyPlayingTextView.setText(file.getName());
    }

    private void initialRecordingPlay(){
        if(timer!=null){
            timer.cancel();
        }
        mAudioPlay = new AudioPlay(mRecordingLocations.get(mActiveSongIndex));
        mPlayButton.setImageResource(R.drawable.ic_pause_full_circle);
        mAudioPlay.start();
        firstPlay =FALSE;
        playing=TRUE;
        mRecordingDuration= mAudioPlay.mediaPlayer.getDuration();

        mProgressBar.setMax(mRecordingDuration);
        ////Set on complete listener
        mAudioPlay.mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {

                mAudioPlay.stop();
                mPlayButton.setImageResource(R.drawable.ic_play_full_circle);
                firstPlay=TRUE;
                playing=FALSE;
                mProgressBar.setProgress(mRecordingDuration);
                timer.cancel();
            }
        });

        timer = new Timer();        // A thread of execution is instantiated
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                if (mAudioPlay.mediaPlayer != null && playing) {
                    try {
                        int mCurrentPosition = mAudioPlay.mediaPlayer.getCurrentPosition();
                        Log.d(TAG, "currentPosition:" + mCurrentPosition);
                        mProgressBar.setProgress(mCurrentPosition);
                    }catch (Exception e) {
                        Log.e(TAG, "setProgress() failed");
                    }
                }
            }
        }, 0, 1000);

        //If there is nothing in the currently playing tV update to active recording
        CharSequence emptyTV = "";
        if (mCurrentlyPlayingTextView.getText().equals(emptyTV)) {
            Log.d(TAG, "debug curently playing tv is empty");
            setmCurrentlyPlayingTextView(mRecordingsAdapter.getActiveRecording());
        }
    }

    @Override
    public void onItemClickListener(int position) {

        // Stop any currently playing recording and start selected recording
        if(!mREC_EN_FLAG) {
            Log.d(TAG, "did this happen");
            if (mAudioPlay != null) {
                if (mAudioPlay.mediaPlayer != null) {
                    mAudioPlay.stop();
                }
            }
            mActiveSongIndex = position;
            mRecordingsAdapter.setmActiveRecording(mActiveSongIndex);
            initialRecordingPlay();
            //Get active recording and update text view
            setmCurrentlyPlayingTextView(mRecordingsAdapter.getActiveRecording());
        }
    }

    @Override
    public void onItemLongClicked(final int position) {
        // Launch AddTaskActivity adding the itemId as an extra in the intent
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mParentListener = null;
    }







}


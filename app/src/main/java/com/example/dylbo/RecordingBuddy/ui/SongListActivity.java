package com.example.dylbo.RecordingBuddy.ui;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;

import com.example.dylbo.RecordingBuddy.R;
import com.example.dylbo.RecordingBuddy.Utils.AppExecutors;
import com.example.dylbo.RecordingBuddy.Utils.MainViewModel;
import com.example.dylbo.RecordingBuddy.adapters.SongAdapter;
import com.example.dylbo.RecordingBuddy.database.AppDatabase;
import com.example.dylbo.RecordingBuddy.database.BandEntry;
import com.example.dylbo.RecordingBuddy.database.SongEntry;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
//This activity displays all the songs present for each band
public class SongListActivity extends AppCompatActivity
        implements SongAdapter.ItemClickListener, SongAdapter.ItemLongClickListener{

    private static final String TAG = SongListActivity.class.getSimpleName();
    // Extra for the task ID to be received in the intent
    public static final String EXTRA_BAND_ID = "extraBandId";
    public static final String EXTRA_BAND_TITLE = "extraBandTitle";

    private AppDatabase mDb;
    private List<SongEntry> mSongs;

    private RecyclerView mSongRV;
    private SongAdapter mSongAdapter;

    private String mTitle;
    private int mBandID;

    private int songNum;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_song_list);//Set main layout for song view
        Log.d(TAG, "Activity");
        ////////////////////////////////////////////////////////////////////////////////////////////////////
        ////////////////////Get extra from intent extra/////////////////////////
        Intent intent = getIntent();
        Bundle bundle;
        bundle = intent.getExtras();//get bundle of extras
        if (intent != null) {
            mBandID = bundle.getInt(EXTRA_BAND_ID);//get song id from bundle
            Log.d(TAG, "mBandID: " + mBandID);
            mTitle = bundle.getString(EXTRA_BAND_TITLE);//get song title from bundle
            Log.d(TAG, "mTitle: " + mTitle);
        }


        mDb = AppDatabase.getInstance(getApplicationContext());

        //Recyclerview

        /*
         * Using findViewById, we get a reference to our RecyclerView from xml. This allows us to
         * do things like set the adapter of the RecyclerView and toggle the visibility.
         */
        mSongRV = findViewById(R.id.rv_song);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        mSongRV.setLayoutManager(layoutManager);
        mSongRV.setItemAnimator(null);

        mSongAdapter = new SongAdapter(this, this, this);
        mSongRV.setAdapter(mSongAdapter);

        FloatingActionButton fabButton = findViewById(R.id.fabSong);

        fabButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                AlertDialog.Builder builder = new AlertDialog.Builder(SongListActivity.this,R.style.MyAlertDialogStyle);
                AlertDialog dialog;
                builder.setTitle("Title");

                // I'm using fragment here so I'm using getView() to provide ViewGroup
                // but you can provide here any other instance of ViewGroup from your Fragment / Activity
                View viewInflated = LayoutInflater.from(getBaseContext()).inflate(R.layout.dialogue_add_title,null, false);
                // Set up the input
                final EditText input = viewInflated.findViewById(R.id.input);
                // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
                builder.setView(viewInflated);

                // Set up the buttons
                builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        mTitle = input.getText().toString();
                        //Get song column and find largest number
                        //Get All tasks and check if empty
                        AppExecutors.getInstance().diskIO().execute(new Runnable() {
                            @Override
                            public void run() {
                                //get all songs
                                mSongs = mDb.getSongDao().listLoadAllSongs();
                                //if there are songs get the last ID and inncrement for new song
                                if (mSongs != null) {

                                    songNum = mDb.getSongDao().LoadLastID()+1;
                                    //Log.d(TAG, "songs=" + songNum);
                                } else {songNum =0;}//Else new list and make songNum =0
                                Date date = new Date();
                                ArrayList<String> recordings = new ArrayList<>();
                                Log.d(TAG, "recordings=" + recordings);
                                final SongEntry song = new SongEntry(songNum,mBandID, mTitle,recordings, date);
                                Log.d(TAG, "SongID1=" + songNum);
                                mDb.getSongDao().insertSong(song);

                                //Database

                                // Create a new intent to start an AddTaskActivity
                                //Intent addSongIntent = new Intent(SongListActivity.this, SongListActivity.class);
                                //Bundle mBundle = new Bundle();
                                //mBundle.putString(SongListActivity.EXTRA_BAND_TITLE, mTitle);
                                //mBundle.putInt(SongListActivity.EXTRA_BAND_ID, songNum);
                                //addSongIntent.putExtras(mBundle);
                                //startActivity(addSongIntent);
                            }
                        });
                    }
                });
                builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                dialog = builder.create();
                dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
                dialog.show();




            }
        });

        setupViewModel();

    }

    private void setupViewModel() {
        //Setup initial View model for updating recycler view using SongAdapter
        MainViewModel viewModel = ViewModelProviders.of(this).get(MainViewModel.class);


        viewModel.getbandSongs(mBandID).observe(this, new Observer<List<SongEntry>>() {
            @Override
            public void onChanged(@Nullable List<SongEntry> songEntries) {
                Log.d(TAG, "Updating list of tasks from LiveData in ViewModel");
                mSongAdapter.setSongs(songEntries);
               // mSongs=bandEntries;
            }

        });

    }
    @Override
    public void onItemClickListener(final int position) {
        // Launch recordingsActivity
        AppExecutors.getInstance().diskIO().execute(new Runnable() {
            @Override
            public void run() {
                SongEntry song;
                Log.d(TAG, "Launching RecordingAndPlaybackActivity");
                Intent recAndPBIntent = new Intent(SongListActivity.this, RecordingAndPlaybackActivity.class);
                song = mDb.getSongDao().LoadSong(position);
                Bundle mBundle = new Bundle();
                mBundle.putInt(RecordingsActivity.EXTRA_BAND_ID, song.bandID);
                mBundle.putInt(RecordingsActivity.EXTRA_SONG_ID, song.getSongID());
                Log.d(TAG, "Debug: songID" + song.getSongID());
                Log.d(TAG, "Debug: position" + position);
                recAndPBIntent.putExtras(mBundle);
                startActivity(recAndPBIntent);
            }
        });


    }
    @Override
    public void onItemLongClicked(final int position) {
        Log.d(TAG, "Deleting Song");
        // Delete song
        AppExecutors.getInstance().diskIO().execute(new Runnable() {
            @Override
            public void run() {
                List<SongEntry> mSongs = mSongAdapter.getSongs();
                mDb.getSongDao().deleteSong(mSongs.get(position));
            }
        });
    }

}


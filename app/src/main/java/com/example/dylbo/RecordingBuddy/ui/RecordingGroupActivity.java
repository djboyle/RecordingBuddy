package com.example.dylbo.RecordingBuddy.ui;

import android.Manifest;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import com.example.dylbo.RecordingBuddy.R;
import com.example.dylbo.RecordingBuddy.Utils.AppExecutors;
import com.example.dylbo.RecordingBuddy.Utils.MainViewModel;
import com.example.dylbo.RecordingBuddy.adapters.RecordingGroupAdapter;
import com.example.dylbo.RecordingBuddy.database.AppDatabase;
import com.example.dylbo.RecordingBuddy.database.BandEntry;


import java.util.ArrayList;
import java.util.Date;
import java.util.List;
//This is the main ui Activity displaying the users bands/recording groups
public class RecordingGroupActivity extends AppCompatActivity
            implements RecordingGroupAdapter.ItemClickListener, RecordingGroupAdapter.ItemLongClickListener{

        private static final String TAG = com.example.dylbo.RecordingBuddy.ui.RecordingGroupActivity.class.getSimpleName();


        private AppDatabase mDb;
        private List<BandEntry> mBands;

        private RecyclerView mRecGroupRV;
        private RecordingGroupAdapter mRecGroupAdapter;


        ///////////////////////////////////////////////////////////
        // Requesting permission to RECORD_AUDIO andEXTERNAL_STORAGE
        private boolean permissionToRecordAccepted = false;
        private String [] permissions = {Manifest.permission.RECORD_AUDIO,Manifest.permission.WRITE_EXTERNAL_STORAGE};
        private static final int REQUEST_RECORD_AUDIO_PERMISSION = 200;
        private static final int ALL_PERMISSIONS = 100;
        // Requesting permission to EXTERNAL_STORAGE
        private boolean permissionToWriteExtDirAccepted = false;
        private static final int REQUEST_WRITE_EXTERNAL_STORAGE_PERMISSION = 300;

        @Override
        public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
            if (grantResults.length == 2) {
                if(grantResults[0]==PackageManager.PERMISSION_GRANTED) {
                    permissionToRecordAccepted = true;
                }
                Log.d(TAG, "permissionToRecordAccepted" + permissionToRecordAccepted);
                if(grantResults[1]==PackageManager.PERMISSION_GRANTED) {
                    permissionToWriteExtDirAccepted = true;
                }
                Log.d(TAG, "permissionToWriteExtDirAccepted" + permissionToWriteExtDirAccepted);
            } else {
                ///resolve permissions not given by user
            }
            switch (requestCode) {
                case ALL_PERMISSIONS:
                    if (grantResults.length == permissions.length) {
                        Log.d(TAG, "grantResults" + PackageManager.PERMISSION_GRANTED);

                        permissionToRecordAccepted = true;
                    } else {
                        // permission denied, boo! Disable the
                        // functionality that depends on this permission.

                    }
                    break;
                case REQUEST_WRITE_EXTERNAL_STORAGE_PERMISSION:
                    if (grantResults.length > 0
                            && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                        permissionToWriteExtDirAccepted = true;
                    } else {
                        // permission denied, boo! Disable the
                        // functionality that depends on this permission.

                    }
                    break;
            }

        }


        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_recording_groups);
            ActivityCompat.requestPermissions(this, permissions, ALL_PERMISSIONS);
            //ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_WRITE_EXTERNAL_STORAGE_PERMISSION);
            Log.d(TAG, "Activity");


            mDb = AppDatabase.getInstance(getApplicationContext());

            //Recyclerview

            /*
             * Using findViewById, we get a reference to our RecyclerView from xml. This allows us to
             * do things like set the adapter of the RecyclerView and toggle the visibility.
             */
            mRecGroupRV = findViewById(R.id.rv_recording_groups);

            LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
            mRecGroupRV.setLayoutManager(layoutManager);
            mRecGroupRV.setItemAnimator(null);

            mRecGroupAdapter = new RecordingGroupAdapter(this, this, this);
            mRecGroupRV.setAdapter(mRecGroupAdapter);
            //
            FloatingActionButton fabButton = findViewById(R.id.fabRecordingGroup);

            fabButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {


                }
            });
            setupViewModel();
            createExampleBands();
        }

        private void setupViewModel() {
            //Setup initial View model
            MainViewModel viewModel = ViewModelProviders.of(this).get(MainViewModel.class);


            viewModel.getBands().observe(this, new Observer<List<BandEntry>>() {
                @Override
                public void onChanged(@Nullable List<BandEntry> bandEntries) {
                    Log.d(TAG, "Updating list of tasks from LiveData in ViewModel");
                    mRecGroupAdapter.setBands(bandEntries);
                    mBands=bandEntries;
                }

            });

        }
        @Override
        public void onItemClickListener(final int position) {
            // Launch AddTaskActivity adding the itemId as an extra in the intent
            AppExecutors.getInstance().diskIO().execute(new Runnable() {
                @Override
                public void run() {
                    BandEntry band;
                    Intent SongsIntent = new Intent(RecordingGroupActivity.this, SongListActivity.class);
                    band = mDb.getBandDao().LoadBand(position);
                    Bundle mBundle = new Bundle();
                    mBundle.putString(SongListActivity.EXTRA_BAND_TITLE, band.getBandName());
                    mBundle.putInt(SongListActivity.EXTRA_BAND_ID, band.getId());
                    SongsIntent.putExtras(mBundle);
                    startActivity(SongsIntent);
                }
            });


        }
        @Override
        public void onItemLongClicked(final int position) {
            // Delete song
            AppExecutors.getInstance().diskIO().execute(new Runnable() {
                @Override
                public void run() {
                    List<BandEntry> mSongs = mRecGroupAdapter.getBands();
                    mDb.getBandDao().deleteSong(mSongs.get(position));
                }
            });
        }
        private void createExampleBands() {
            final String[] BandNames = new String[]{"Solo","Band","Random"};
            // Check if there are any songs in DB
            AppExecutors.getInstance().diskIO().execute(new Runnable() {
                @Override
                public void run() {
                    List<BandEntry> mBands = mDb.getBandDao().listLoadAllBands();
                    //Log.d(TAG, "mSongs" +mSongs.size());
                    if (mBands.size()==0){
                        //Create sample Song
                        final Date date = new Date();
                        ArrayList<String> recordings = new ArrayList<String>();
                        Log.d(TAG, "recordings=" + recordings);

                        //Create Sample Sections
                        for (int i = 0; i < 3; i++) {
                            final BandEntry band = new BandEntry(i, BandNames[i], date);
                            mDb.getBandDao().insertBand(band);
                        }
                    }


                }
            });
    }

}

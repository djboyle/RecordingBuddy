package com.example.dylbo.RecordingBuddy.ui;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.dylbo.RecordingBuddy.R;
import com.example.dylbo.RecordingBuddy.Utils.AppExecutors;
import com.example.dylbo.RecordingBuddy.Utils.AudioPlay;
import com.example.dylbo.RecordingBuddy.database.AppDatabase;
import com.example.dylbo.RecordingBuddy.database.SongEntry;

import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;


public class LameActivity {

    private static final String TAG = AudioPlay.class.getSimpleName();

    static {
        System.loadLibrary("mp3lame");
    }

    public native void initEncoder(int numChannels, int sampleRate, int bitRate, int mode, int quality);

    private native void destroyEncoder();

    private native int encodeFile(String sourcePath, String targetPath);

    public static final int NUM_CHANNELS = 1;
    public static final int SAMPLE_RATE = 16000;
    public static final int BITRATE = 128;
    public static final int MODE = 1;
    public static final int QUALITY = 2;
    private AudioRecord mRecorder;
    private short[] mBuffer;
    private final String startRecordingLabel = "Start recording";
    private final String stopRecordingLabel = "Stop recording";
    private boolean mIsRecording = false;
    private File mRawFile;
    private File mEncodedFile;

    private String mfilename = null;
    private Context mContext;
    private int mSongID;
    private int mBandID;
    private SongEntry songEntry = null;
    private AppDatabase mDb;
    // Class variables for the List that holds task data and the Context
    private ArrayList<String> mSongRecordings;

    public LameActivity (Context context, String filename, int BandID, int SongID){
        this.mfilename = filename;
        this.mContext = context;
        this.mSongID = SongID;
        this.mBandID = BandID;
        mDb = AppDatabase.getInstance(context);

    }
    /*


        @Override
        public void onDestroy() {
            mRecorder.release();
            destroyEncoder();
            super.onDestroy();
        }
    */
    public void startRecording() {
        mIsRecording = true;
        mRecorder.startRecording();
        mRawFile = getFile("raw");
        startBufferedWrite(mRawFile);
        Log.e(TAG, "Raw file check to " + mRawFile.getName());

    }
    public void stopRecording() {
        mIsRecording = false;
        mRecorder.stop();
        mEncodedFile = getFile("mp3");
        int result = encodeFile(mRawFile.getAbsolutePath(), mEncodedFile.getAbsolutePath());
        if (result == 0) {
            Log.e(TAG, "Encoded to " + mEncodedFile.getName());
        }
        //saveNewRecordingtoDB(mEncodedFile.getAbsolutePath());


    }

    public String[] getFileloaction() {
        Log.d(TAG, "Encoded raw filename: " + mRawFile.getName());
        return new String [] {mEncodedFile.getAbsolutePath(),mRawFile.getAbsolutePath()};

    }


    public void initRecorder() {
        int bufferSize = AudioRecord.getMinBufferSize(SAMPLE_RATE, AudioFormat.CHANNEL_IN_MONO,
                AudioFormat.ENCODING_PCM_16BIT);
        mBuffer = new short[bufferSize];
        mRecorder = new AudioRecord(MediaRecorder.AudioSource.MIC, SAMPLE_RATE, AudioFormat.CHANNEL_IN_MONO,
                AudioFormat.ENCODING_PCM_16BIT, bufferSize);
        initEncoder(NUM_CHANNELS, SAMPLE_RATE, BITRATE, MODE, QUALITY);
    }

    private void startBufferedWrite(final File file) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                DataOutputStream output = null;
                try {
                    output = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(file)));
                    while (mIsRecording) {
                        int readSize = mRecorder.read(mBuffer, 0, mBuffer.length);
                        for (int i = 0; i < readSize; i++) {
                            output.writeShort(mBuffer[i]);
                        }
                    }
                } catch (IOException e) {
                    final String message = e.getMessage();
                    Log.e(TAG, message);
                } finally {
                    if (output != null) {
                        try {
                            output.flush();
                        } catch (IOException e) {
                            final String message = e.getMessage();
                            Log.e(TAG, message);
                        } finally {
                            try {
                                output.close();
                            } catch (IOException e) {
                                final String message = e.getMessage();
                                Log.e(TAG, message);
                            }
                        }
                    }
                }
            }
        }).start();
    }

    ////////////////////////////////////////////////////////////////
    //Save song to database when finished recording
    ///////////////////////////////////////////////////////////////
    public void saveNewRecordingtoDB(final String filename){
        //////////////////////////////////////////////////////////////////////////////////////////////
        AppExecutors.getInstance().diskIO().execute(new Runnable() {
            @Override
            public void run() {
                songEntry = mDb.getSongDao().LoadSong(mSongID);
                //Date date = new Date();
                mSongRecordings = songEntry.getRecordingFileLocations();
                Log.d(TAG, "recordings Array: "+ mSongRecordings);
                Log.d(TAG, "filename: "+ filename);
                mSongRecordings.add(0,filename);
                songEntry.setRecordingFileLocations(mSongRecordings);
                mDb.getSongDao().updateSong(songEntry);
            }
        });
    }


    private File getFile(final String suffix) {
        Date date =  new Date();
        DateFormat df = new SimpleDateFormat("dd-MMM-yyyy-hh-mm-ss", Locale.ENGLISH);
        String recordingDate= df.format(date);
        File myDirectory = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC), "Recordings");
        myDirectory.mkdir();

        return new File(myDirectory.getAbsolutePath(),  recordingDate + "." + suffix);
    }
}



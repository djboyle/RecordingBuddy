package com.example.dylbo.RecordingBuddy.Utils;
import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.util.Log;

import com.example.dylbo.RecordingBuddy.database.AppDatabase;
import com.example.dylbo.RecordingBuddy.database.BandEntry;
import com.example.dylbo.RecordingBuddy.database.SongEntry;

import java.util.List;

public class MainViewModel extends AndroidViewModel {

    // Constant for logging
    private static final String TAG = MainViewModel.class.getSimpleName();

    private LiveData<List<SongEntry>> songs;
    private LiveData<List<BandEntry>> bands;

    public MainViewModel(Application application) {
        super(application);
        AppDatabase database = AppDatabase.getInstance(this.getApplication());
        Log.d(TAG, "Actively retrieving the tasks from the DataBase");
        bands = database.getBandDao().loadAllBands();
    }

    public LiveData<List<SongEntry>> getbandSongs(int bandID) {
        AppDatabase database = AppDatabase.getInstance(this.getApplication());
        songs = database.getSongDao().loadBandSongs(bandID);
        return songs;
    }
    public LiveData<List<SongEntry>> getSongs() {
        AppDatabase database = AppDatabase.getInstance(this.getApplication());
        songs = database.getSongDao().loadAllSongs();
        return songs;
    }
    public LiveData<List<BandEntry>> getBands() {
        return bands;
    }
}

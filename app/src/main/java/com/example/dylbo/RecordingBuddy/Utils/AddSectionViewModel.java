package com.example.dylbo.RecordingBuddy.Utils;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;

import com.example.dylbo.RecordingBuddy.database.AppDatabase;
import com.example.dylbo.RecordingBuddy.database.SongEntry;


// COMPLETED (5) Make this class extend ViewModel
public class AddSectionViewModel extends ViewModel {

    // COMPLETED (6) Add a task member variable for the TaskEntry object wrapped in a LiveData
    private LiveData<SongEntry> song;

    // COMPLETED (8) Create a constructor where you call loadTaskById of the taskDao to initialize the tasks variable
    // Note: The constructor should receive the database and the taskId
    public AddSectionViewModel(AppDatabase database, int songId) {
        song = database.getSongDao().loadSongById(songId);
    }

    // COMPLETED (7) Create a getter for the task variable
    public LiveData<SongEntry> getSection() {
        return song;
    }
}

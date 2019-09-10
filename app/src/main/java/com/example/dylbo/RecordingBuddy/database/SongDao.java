package com.example.dylbo.RecordingBuddy.database;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

@Dao
public interface SongDao {


    @Insert
    void insertSong(SongEntry songEntry);

    @Update(onConflict = OnConflictStrategy.REPLACE)
    void updateSong(SongEntry songEntry);

    @Delete
    void deleteSong(SongEntry songEntry);

    @Query("SELECT * FROM songEntry ORDER BY title")
    LiveData<List<SongEntry>> loadAllSongs();

    @Query("SELECT * FROM songEntry ORDER BY title")
    List<SongEntry> listLoadAllSongs();

    @Query("SELECT * FROM songEntry WHERE songID = :songID")
    SongEntry LoadSong(int songID);

    @Query("SELECT songID, MAX(songID) FROM songEntry")
    int LoadLastID();

    @Query("SELECT * FROM songEntry WHERE songID = :songID")
    LiveData<SongEntry> loadSongById(int songID);


    @Query("SELECT * FROM songEntry WHERE bandID = :bandID ORDER BY songID")
    LiveData<List<SongEntry>> loadBandSongs(int bandID);

    @Query("SELECT * FROM songEntry WHERE bandID = :bandID ORDER BY songID")
    List<SongEntry> listloadBandSongs(int bandID);
}
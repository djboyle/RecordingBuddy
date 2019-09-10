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
public interface BandDao {


    @Insert
    void insertBand(BandEntry bandEntry);

    @Update(onConflict = OnConflictStrategy.REPLACE)
    void updateBand(BandEntry bandEntry);

    @Delete
    void deleteSong(BandEntry bandEntry);

    @Query("SELECT * FROM band ORDER BY bandName")
    LiveData<List<BandEntry>> loadAllBands();

    @Query("SELECT * FROM band ORDER BY bandName")
    List<BandEntry> listLoadAllBands();

    @Query("SELECT * FROM band WHERE id = :id")
    BandEntry LoadBand(int id);

    @Query("SELECT id, MAX(id) FROM band")
    int LoadLastID();


}

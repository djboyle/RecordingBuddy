package com.example.dylbo.RecordingBuddy.database;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

import java.util.ArrayList;
import java.util.Date;

@Entity(tableName = "band")
public class BandEntry {

    @PrimaryKey
    public int id;
    private String bandName;
    private ArrayList<String> recordingFileLocations;
    private Date createdAt;


    public BandEntry(int id, String bandName , Date createdAt) {
        this.id = id;
        this.bandName = bandName;
        this.createdAt = createdAt;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getBandName() { return bandName; }

    public ArrayList<String> getRecordingFileLocations() { return recordingFileLocations; }

    public void setRecordingFileLocations(ArrayList<String> recordingFileLocations) {
        this.recordingFileLocations=recordingFileLocations;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

}
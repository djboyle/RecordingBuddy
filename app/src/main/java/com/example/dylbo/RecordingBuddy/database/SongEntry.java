package com.example.dylbo.RecordingBuddy.database;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.PrimaryKey;

import java.util.ArrayList;
import java.util.Date;

import static android.arch.persistence.room.ForeignKey.CASCADE;

@Entity(foreignKeys = @ForeignKey(
        entity = BandEntry.class,
        parentColumns = "id",
        childColumns = "bandID",
        onDelete = CASCADE),
        indices ={@Index("bandID")})
public class SongEntry {
    @PrimaryKey (autoGenerate = true)
    private int songID;
    @ColumnInfo
    public int bandID;
    private String title;
    private ArrayList<String> recordingFileLocations;
    private Date createdAt;

    @Ignore
    public SongEntry(int bandID, String title, ArrayList<String> recordingFileLocations , Date createdAt) {
        this.bandID = bandID;
        this.title = title;
        this.createdAt = createdAt;
        this.recordingFileLocations = recordingFileLocations;
    }

    public SongEntry(int songID, int bandID, String title, ArrayList<String> recordingFileLocations , Date createdAt) {
        this.songID=songID;
        this.bandID = bandID;
        this.title = title;
        this.createdAt = createdAt;
        this.recordingFileLocations = recordingFileLocations;
    }
    public int getSongID() {
        return songID;
    }

    public void setId(int id) {
        this.songID = songID;
    }

    public String getTitle() { return title; }

    public ArrayList<String> getRecordingFileLocations() { return recordingFileLocations; }

    public void setRecordingFileLocations(ArrayList<String> recordingFileLocations) {
        this.recordingFileLocations=recordingFileLocations;
    }

    public Date getCreatedAt() {
        return createdAt;
    }}

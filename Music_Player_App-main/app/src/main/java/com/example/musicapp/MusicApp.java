package com.example.musicapp;

import android.app.Application;
import androidx.room.Room;
import com.example.musicapp.data.AppDatabase;

public class MusicApp extends Application {

    private static AppDatabase database;

    @Override
    public void onCreate() {
        super.onCreate();
        // Initialize the Room database instance
        database = Room.databaseBuilder(getApplicationContext(), AppDatabase.class, "music_app_db").build();
    }

    public static AppDatabase getDatabase() {
        return database;
    }
}

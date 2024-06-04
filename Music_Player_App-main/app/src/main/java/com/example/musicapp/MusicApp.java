package com.example.musicapp;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;

import androidx.room.Room;
import com.example.musicapp.data.AppDatabase;

public class MusicApp extends Application {

    private static AppDatabase database;
    public static final String CHANNEL_ID = "CHANNEL EXAMPLE";



    @Override
    public void onCreate() {
        super.onCreate();
        // Initialize the Room database instance

        database = Room.databaseBuilder(getApplicationContext(), AppDatabase.class, "music_app_db").build();

        createNotificationChannel();
    }

    public static AppDatabase getDatabase() {
        return database;
    }
    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID,
                    "Channel 1", NotificationManager.IMPORTANCE_DEFAULT);

            NotificationManager manager = getSystemService(NotificationManager.class);
            if (manager != null){
                manager.createNotificationChannel(channel);
            }
        }
    }
}

package com.example.musicapp.data;

import androidx.room.Database;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import com.example.musicapp.model.Playlist;

@Database(entities = {Playlist.class}, version = 1)
@TypeConverters(Converters.class)
public abstract class AppDatabase extends RoomDatabase {
    public abstract PlaylistDAO playlistDAO();
}

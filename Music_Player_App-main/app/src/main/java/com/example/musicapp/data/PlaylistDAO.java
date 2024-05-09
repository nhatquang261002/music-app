package com.example.musicapp.data;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.musicapp.model.Playlist;
import com.example.musicapp.model.Song;

import java.util.List;

@Dao
public interface PlaylistDAO {

    @Insert
    void insert(Playlist playlist);

    @Query("SELECT * FROM playlists")
    List<Playlist> getAllPlaylists();

    @Query("SELECT * FROM playlists WHERE id = :playlistId")
    Playlist getPlaylistById(long playlistId);

    @Update
    void update(Playlist playlist);

    @Delete
    void delete(Playlist playlist);

    // Song operations
    @Query("UPDATE playlists SET songIds = songIds || :songId || ',' WHERE id = :playlistId")
    void addSongIdToPlaylist(long playlistId, String songId);

    @Query("UPDATE playlists SET songIds = REPLACE(songIds, :songId || ',', '') WHERE id = :playlistId")
    void removeSongIdFromPlaylist(long playlistId, String songId);
}

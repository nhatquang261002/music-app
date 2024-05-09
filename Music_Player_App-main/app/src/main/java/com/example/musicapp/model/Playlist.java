package com.example.musicapp.model;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Entity(tableName = "playlists")
public class Playlist implements Serializable {

    @PrimaryKey(autoGenerate = true)
    private long id;
    private String name;
    private String description;
    private List<String> songIds; // List of song IDs

    public Playlist(String name, String description, List<String> songIds) {
        this.name = name;
        this.description = description;
        this.songIds = songIds;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<String> getSongIds() {
        return songIds;
    }

    public void setSongIds(List<String> songIds) {
        this.songIds = songIds;
    }

    public void addSongId(String songId) {
        if (songIds == null) {
            songIds = new ArrayList<>();
        }
        songIds.add(songId);
    }

    public void removeSongId(String songId) {
        if (songIds != null) {
            songIds.remove(songId);
        }
    }

    @Override
    public String toString() {
        return "Playlist{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", songIds=" + songIds +
                '}';
    }
}

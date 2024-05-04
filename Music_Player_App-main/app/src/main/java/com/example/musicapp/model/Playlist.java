package com.example.musicapp.model;

import com.example.musicapp.model.Song;

import java.io.Serializable;
import java.util.ArrayList;

public class Playlist implements Serializable {

    private String id;
    private String name;
    private String description;
    private ArrayList<Song> songs;

    public Playlist(String name, String description, ArrayList<Song> songs) {
        this.name = name;
        this.description = description;
        this.songs = songs;
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

    public ArrayList<Song> getSongs() {
        return songs;
    }

    public void setSongs(ArrayList<Song> songs) {
        this.songs = songs;
    }

    public void addSong(Song song) {
        if (songs == null) {
            songs = new ArrayList<>();
        }
        songs.add(song);
    }

    public void removeSong(Song song) {
        if (songs != null) {
            songs.remove(song);
        }
    }

    @Override
    public String toString() {
        return "Playlist{" +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", songs=" + songs +
                '}';
    }
}

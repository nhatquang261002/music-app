package com.example.musicapp.model;

import java.io.Serializable;


public class Song implements Serializable {

    private String id;
    private String name_song;
    private String name_artist;
    private String thumbnail;
    private String position;
    private String lyric;
    private String source;
    private String code;
    private Integer duration;

    public Song(String id, String name_song, String name_artist, String thumbnail, String position, String lyric, String source, String code, Integer duration) {
        this.id = id;
        this.name_song = name_song;
        this.name_artist = name_artist;
        this.thumbnail = thumbnail;
        this.position = position;
        this.lyric = lyric;
        this.source = source;
        this.code = code;
        this.duration = duration;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName_song() {
        return name_song;
    }

    public void setName_song(String name_song) {
        this.name_song = name_song;
    }

    public String getName_artist() {
        return name_artist;
    }

    public void setName_artist(String name_artist) {
        this.name_artist = name_artist;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public String getLyric() {
        return lyric;
    }

    public void setLyric(String lyric) {
        this.lyric = lyric;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Integer getDuration() {
        return duration;
    }

    public void setDuration(Integer duration) {
        this.duration = duration;
    }
}

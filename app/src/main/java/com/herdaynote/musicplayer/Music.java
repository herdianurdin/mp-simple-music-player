package com.herdaynote.musicplayer;

import android.graphics.Bitmap;

public class Music {
    private final String title, artist, path;
    private final Bitmap cover;

    public Music(String title, String artist, String path, Bitmap cover) {
        this.title = title;
        this.artist = artist;
        this.path = path;
        this.cover = cover;
    }

    public String getTitle() {
        return title;
    }

    public String getArtist() {
        return artist;
    }

    public String getPath() {
        return path;
    }

    public Bitmap getCover() { return cover;}
}

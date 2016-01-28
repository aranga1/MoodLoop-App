package com.aranga.moodloop.ui;

import java.util.ArrayList;

/**
 * Created by aakashranga on 1/8/16.
 */
public class AlbumFile {
    private String albumname;
    private String artist;
    private String albumSongs;
    private String path;

    public AlbumFile(String albumname, String artist, String albumSongs, String path) {
        this.albumname = albumname;
        this.artist = artist;
        this.albumSongs = albumSongs;
        this.path = path;
    }

    public String getAlbumname() {
        return albumname;
    }

    public String getArtist() {
        return artist;
    }

    public String getAlbumSongs() {
        return albumSongs;
    }

    public String getPath() {
        return path;
    }
}

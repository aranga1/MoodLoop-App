package com.aranga.moodloop.ui;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;

/**
 * Created by aakashranga on 1/5/16.
 */
public class MusicFile {
    private String songTitle;
    private String artistName;
    private String albumName;
    private String path;
    //private MediaMetadataRetriever albumArtRetriever;
    //private Bitmap imageBitmap;

    public MusicFile(String title, String artist, String album, String path) {
        this.songTitle = title;
        this.artistName = artist;
        this.albumName = album;
        this.path = path;
        //albumArtRetriever = new MediaMetadataRetriever();
      //  makeBitmap();
    }

    /*public void makeBitmap() {
        albumArtRetriever.setDataSource(path);
        byte[] data = albumArtRetriever.getEmbeddedPicture();
        if (data != null) {
            imageBitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
        }
        else {
            imageBitmap = BitmapFactory.decodeResource()
        }
    }

    public Bitmap getImageBitmap() {
        return imageBitmap;
    }*/

    public String getSongTitle() {
        return songTitle;
    }

    public String getArtistName() {
        return artistName;
    }

    public String getAlbumName() {
        return albumName;
    }

    public String getPath() {
        return path;
    }
}

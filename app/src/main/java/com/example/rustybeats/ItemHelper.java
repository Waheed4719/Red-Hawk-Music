package com.example.rustybeats;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;

public class ItemHelper {

    private String song_title;
    private Bitmap bitmap;

    public ItemHelper(String song_title){
        this.song_title = song_title;

    }
    public ItemHelper(String song_title, Bitmap bitmap){
        this.song_title = song_title;
        this.bitmap = bitmap;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public String getSong_title(){
        return song_title;
    }




}

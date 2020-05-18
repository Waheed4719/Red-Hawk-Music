package com.example.rustybeats;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;

import java.io.File;

public class ItemHelper {

    private String song_title;
    private Bitmap bitmap;
    private File path;

    public ItemHelper(String song_title){
        this.song_title = song_title;

    }
    public ItemHelper(String song_title, Bitmap bitmap){
        this.song_title = song_title;
        this.bitmap = bitmap;
    }
    public ItemHelper(String song_title, File path){
        this.song_title = song_title;
        this.path = path;
    }

    public Bitmap getBitmap() {
        if(!path.getName().endsWith(".m4a")) {
            MediaMetadataRetriever mmr = new MediaMetadataRetriever();
            mmr.setDataSource(path.getPath());

            byte[] data = mmr.getEmbeddedPicture();

            if (data != null) {
                bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
            }
        }
        return bitmap;
    }

    public void setBitmap(Bitmap bm){
        this.bitmap = bm;
    }


    public File getFile(){
        return path;
    }

    public String getSong_title(){
        return song_title;
    }




}

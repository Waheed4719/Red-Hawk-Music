package com.example.rustybeats;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.media.MediaMetadataRetriever;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import java.util.ArrayList;

public class ArrAdapter extends ArrayAdapter<ItemHelper>{

    ArrayList<ItemHelper> song;
    private Context mContext;
    private Typeface tf;
    int mResource;
    public ArrAdapter(@NonNull Context context, int resource, ArrayList<ItemHelper> objects) {
        super(context, resource, objects);
        mContext = context;
        mResource = resource;
        tf = Typeface.createFromAsset(context.getAssets(),"fonts/poppins_semibold.ttf");
    }

   @SuppressLint("WrongConstant")
   public View getView(int position, View convertView, ViewGroup parent){
        String song_title = getItem(position).getSong_title();
        Bitmap bitmap = getItem(position).getBitmap();

       LayoutInflater inflater = LayoutInflater.from(mContext);
       convertView = inflater.inflate(mResource,parent,false);

       TextView song = (TextView) convertView.findViewById(R.id.song_title);
       TextView album = (TextView) convertView.findViewById(R.id.album_name);
       ImageView image = (ImageView) convertView.findViewById(R.id.coverArt);
       if(bitmap != null){
           image.setImageBitmap(bitmap);
       }

       song.setText(song_title);
       song.setTypeface(tf);
       album.setTypeface(Typeface.createFromAsset(mContext.getAssets(), "fonts/poppins_medium.ttf"));

       return convertView;

   }

}

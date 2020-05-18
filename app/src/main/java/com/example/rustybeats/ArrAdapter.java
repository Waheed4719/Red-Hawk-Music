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

import java.io.File;
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
//        Bitmap bitmap = getItem(position).getBitmap();
        File file = getItem(position).getFile();
        ItemHelper item = getItem(position);




        View row = convertView;
        MusicViewHolder holder = null;

        if(row == null){
            LayoutInflater inflater = LayoutInflater.from(mContext);
            row = inflater.inflate(mResource,parent,false);
            holder = new MusicViewHolder(row);
            row.setTag(holder);
        }
        else{
                holder = (MusicViewHolder) row.getTag();
        }

            BitmapWorkerTask bwt = new BitmapWorkerTask(holder.image,item);
            bwt.execute(file);


       holder.song.setText(song_title);
       holder.song.setTypeface(tf);
       holder.album.setTypeface(Typeface.createFromAsset(mContext.getAssets(), "fonts/poppins_medium.ttf"));

       return row;

   }


   class MusicViewHolder{
        ImageView image;
        TextView song,album;

        MusicViewHolder(View v){
            song = (TextView) v.findViewById(R.id.song_title);
            album = (TextView) v.findViewById(R.id.album_name);
            image = (ImageView) v.findViewById(R.id.coverArt);
        }

   }




}

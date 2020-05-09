package com.example.redhawkmusicplayer;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;

public class ArrAdapter extends ArrayAdapter<ItemHelper> implements View.OnClickListener{

    ArrayList<ItemHelper> song;
    private Context mContext;
    int mResource;
    public ArrAdapter(@NonNull Context context, int resource, ArrayList<ItemHelper> objects) {
        super(context, resource, objects);
        mContext = context;
        mResource = resource;
    }

   public View getView(int position, View convertView, ViewGroup parent){
        String song_title = getItem(position).getSong_title();


       LayoutInflater inflater = LayoutInflater.from(mContext);
       convertView = inflater.inflate(mResource,parent,false);

       TextView song = (TextView) convertView.findViewById(R.id.song_title);
       ImageView image = (ImageView) convertView.findViewById(R.id.test);

       image.setOnClickListener(this);
       image.setTag(position);

       song.setText(song_title);


       return convertView;

   }
   @Override
   public void onClick(View v){
       Toast.makeText(mContext,"Hi",Toast.LENGTH_SHORT).show();
       int position=(Integer) v.getTag();
       Toast.makeText(mContext,""+position,Toast.LENGTH_SHORT).show();
       Object object= getItem(position);
       ItemHelper dataModel=(ItemHelper) object;
    Snackbar.make(v,"hello",Snackbar.LENGTH_SHORT).setAction("No Action",null).show();
       switch (v.getId())
       {

           case R.id.test:
               Snackbar.make(v, "Release date ", Snackbar.LENGTH_LONG)
                       .setAction("No action", null).show();
               break;
       }
   }
}

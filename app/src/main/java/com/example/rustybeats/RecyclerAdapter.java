package com.example.rustybeats;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class RecyclerAdapter  extends RecyclerView.Adapter<RecyclerAdapter.RecyclerHolder>  {

    ArrayList<ItemHelper> groups;
    private OnItemClickListener mListener;
    private Context mContext;

    public void setOnItemClickListener(OnItemClickListener listener){
        mListener = listener;
    }




    public interface OnItemClickListener{
        void onItemClick(int position, View itemView);
    }

    public RecyclerAdapter(Context mContext,ArrayList<ItemHelper> groups) {

        this.mContext = mContext;
        this.groups = groups;
    }

    @NonNull
    @Override
    public RecyclerHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_design,parent,false);
        RecyclerHolder recyclerHolder = new RecyclerHolder(view,mListener);
        return recyclerHolder;
    }

    @SuppressLint("ResourceAsColor")
    @Override
    public void onBindViewHolder(@NonNull RecyclerHolder holder, int position) {

        ItemHelper item = groups.get(position);
        holder.group_name.setText(item.getSong_title());

        if(item.getSong_title() != "All Songs"){
            holder.underline.setBackgroundColor(0);
        }

    }

    @Override
    public int getItemCount() {
        return groups.size();
    }


    public class RecyclerHolder extends RecyclerView.ViewHolder{

        private TextView group_name;
        private LinearLayout underline;

        public RecyclerHolder(@NonNull final View itemView, final OnItemClickListener listener) {
            super(itemView);
            group_name = itemView.findViewById(R.id.group_name);
            underline = itemView.findViewById(R.id.underline);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(listener!=null){
                        int position = getAdapterPosition();
                        if(position !=RecyclerView.NO_POSITION){
                            listener.onItemClick(position,itemView);
                        }
                    }
                }
            });
        }
    }



}

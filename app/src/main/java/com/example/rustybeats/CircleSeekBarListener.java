package com.example.rustybeats;

import android.media.MediaPlayer;

import static com.example.rustybeats.MainActivity.myMediaPlayer;

public class CircleSeekBarListener implements CircularSeekBar.OnCircularSeekBarChangeListener {
    private MediaPlayer myMediaPlayer;

    public CircleSeekBarListener(MediaPlayer myMediaPlayer) {
        this.myMediaPlayer = myMediaPlayer;

    }

    @Override
    public void onProgressChanged(CircularSeekBar circularSeekBar, int progress, boolean fromUser) {
        // TODO Insert your code here
        if(fromUser){
            myMediaPlayer.seekTo(progress);
        }
    }

    @Override
    public void onStopTrackingTouch(CircularSeekBar seekBar) {

    }

    @Override
    public void onStartTrackingTouch(CircularSeekBar seekBar) {
        myMediaPlayer.seekTo(seekBar.getProgress());
    }
}
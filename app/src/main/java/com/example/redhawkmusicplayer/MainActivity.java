package com.example.redhawkmusicplayer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.snackbar.Snackbar;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.karumi.dexter.listener.single.PermissionListener;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private BottomSheetBehavior bottomSheetBehavior;
    private LinearLayout bottomsheet,drawerpull;
    private ListView listView;
    private String[] Music;
    private TextView songtitle,songtitlemini,cur_time,full_time;
    private ImageView play_btn,prev_btn,next_btn,play_btn_mini,prev_btn_mini,next_btn_mini;
    private Boolean bool;
    private SeekBar seekBar;
    static MediaPlayer myMediaPlayer;
    int pos;
//    Thread updateSeekBar;
    private Runnable runnable;
    private Handler handler;
    GlobalFunctions uiBars = new GlobalFunctions();
    private View decorView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        play_btn = findViewById(R.id.play_btn);
        play_btn_mini = findViewById(R.id.play_btn_mini);
        prev_btn = findViewById(R.id.prev_btn);
        prev_btn_mini = findViewById(R.id.prev_btn_mini);
        next_btn = findViewById(R.id.next_btn);
        next_btn_mini = findViewById(R.id.next_btn_mini);
        cur_time = findViewById(R.id.cur_time);
        full_time = findViewById(R.id.full_time);

        handler = new Handler();


        songtitle = findViewById(R.id.song_name);
        songtitle.setSelected(true);
        songtitlemini = findViewById(R.id.song_title);
        songtitlemini.setSelected(true);

        listView = findViewById(R.id.lv);
        drawerpull = findViewById(R.id.drawerpull);
        seekBar = findViewById(R.id.seekbar);
        seekBar.getProgressDrawable().setColorFilter(getResources().getColor(android.R.color.holo_blue_light), PorterDuff.Mode.MULTIPLY);
        seekBar.getThumb().setColorFilter(getResources().getColor(android.R.color.holo_blue_light),PorterDuff.Mode.SRC_IN);


        decorView = getWindow().getDecorView();
        decorView.setOnSystemUiVisibilityChangeListener(new View.OnSystemUiVisibilityChangeListener() {
            @Override
            public void onSystemUiVisibilityChange(int visibility) {
                if(visibility==0){
                    decorView.setSystemUiVisibility(uiBars.hideSystemBars());
                }
            }
        });

        bottomsheet = findViewById(R.id.bottomSheet);
        bottomSheetBehavior = BottomSheetBehavior.from(bottomsheet);

        prepareBottomSheet(bottomSheetBehavior);


      Dexter.withActivity(this)
              .withPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
              .withListener(new PermissionListener() {
                  @Override
                  public void onPermissionGranted(PermissionGrantedResponse permissionGrantedResponse) {
                        display();
                  }

                  @Override
                  public void onPermissionDenied(PermissionDeniedResponse permissionDeniedResponse) {

                  }

                  @Override
                  public void onPermissionRationaleShouldBeShown(PermissionRequest permissionRequest, PermissionToken permissionToken) {
                        permissionToken.continuePermissionRequest();
                  }
              }).check();

      if(myMediaPlayer!=null){
          myMediaPlayer.stop();
          myMediaPlayer.release();
      }

    }


    private void updateSeekBar(){
        seekBar.setProgress((myMediaPlayer.getCurrentPosition()));

        Long time = Long.valueOf(myMediaPlayer.getCurrentPosition());
        long seconds = time/1000;
        long minutes = seconds/60;
        seconds = seconds % 60;

        if(seconds<10) {
            String current_time = String.valueOf(minutes) + ":0" + String.valueOf(seconds);
            cur_time.setText(current_time);
        } else {
            String current_time = String.valueOf(minutes) + ":" + String.valueOf(seconds);
            cur_time.setText(current_time);
        }

        if(myMediaPlayer.isPlaying()){
            runnable = new Runnable() {
                @Override
                public void run() {
                    Log.i("seekbar", "updating");
                    updateSeekBar();
                }
            };
            handler.postDelayed(runnable,1000);
        }
    }



  public ArrayList<File> findSong(File file){
        ArrayList<File> arrayList = new ArrayList<>();

        File[] files = file.listFiles();
        if(files!=null){
            for(File singleFile:files){
                if(singleFile.isDirectory() && !singleFile.isHidden()){
                    arrayList.addAll(findSong(singleFile));
                }
                else{
                    if(singleFile.getName().endsWith(".mp3") || singleFile.getName().endsWith(".wav")){
                        arrayList.add(singleFile);
                    }
                }
            }
        }

        return arrayList;

  }



  void display(){

        final ArrayList<File> mySongs = findSong(Environment.getExternalStorageDirectory());


        Music = new String[mySongs.size()];
       final ArrayList<ItemHelper> list = new ArrayList<>();
        for(int i = 0; i< mySongs.size(); i++){
            Music[i] = mySongs.get(i).getName().toString().replace(".mp3", "").replace(".wav","");
            ItemHelper item = new ItemHelper(Music[i]);
            list.add(item);

        }

      ArrAdapter adapter = new ArrAdapter(getApplicationContext(),R.layout.listitem,list);

      listView.setAdapter(adapter);

      listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
          @Override
          public void onItemClick(final AdapterView<?> parent, final View view, final int position, final long id) {
              pos = position;
              ItemHelper lists = list.get(position);

              Uri u = Uri.parse(mySongs.get(position).toString());
              if(myMediaPlayer!=null){
                  myMediaPlayer.stop();
                  myMediaPlayer.release();
              }
              seekBar.setProgress(0);
              cur_time.setText("0:00");

              myMediaPlayer = MediaPlayer.create(getApplicationContext(),u);
              myMediaPlayer.start();

              songtitle.setText(mySongs.get(position).getName());
              songtitlemini.setText(mySongs.get(position).getName());
              play_btn.setBackgroundResource(R.mipmap.ic_pause);
              play_btn_mini.setBackgroundResource(R.drawable.ic_pause_black_24dp);
              int Duration = myMediaPlayer.getDuration()/1000;
              seekBar.setProgress(myMediaPlayer.getCurrentPosition());
              seekBar.setMax(myMediaPlayer.getDuration());
              full_time.setText(Duration / 60 + ":" + Duration % 60);

              myMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                  @Override
                  public void onCompletion(MediaPlayer mp) {
                      seekBar.setProgress(0);
                      mp.seekTo(seekBar.getProgress());
                      if(myMediaPlayer.isPlaying()){
                          Log.i("paused", "here");
                          myMediaPlayer.pause();
//                          cur_time.setText("0:00");
//                          play_btn.setBackgroundResource(R.mipmap.ic_play);
//                          play_btn_mini.setBackgroundResource(R.drawable.ic_play_arrow_black_24dp);
                      }
                      cur_time.setText("0:00");
                      play_btn.setBackgroundResource(R.mipmap.ic_play);
                      play_btn_mini.setBackgroundResource(R.drawable.ic_play_arrow_black_24dp);
                      Log.i("restarted", "here");
                  }
              });

              //Updates Seekbar while music plays
                updateSeekBar();

              seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                  @Override
                  public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                        if(fromUser){
                            myMediaPlayer.seekTo(progress);
                        }

                  }

                  @Override
                  public void onStartTrackingTouch(SeekBar seekBar) {

                  }

                  @Override
                  public void onStopTrackingTouch(SeekBar seekBar) {
                        myMediaPlayer.seekTo(seekBar.getProgress());
                  }
              });





              play_btn.setOnClickListener(new View.OnClickListener() {
                  @Override
                  public void onClick(View v) {
//                      seekBar.setMax(myMediaPlayer.getDuration());
                        if(myMediaPlayer.isPlaying()){
                            play_btn.setBackgroundResource(R.mipmap.ic_play);
                            play_btn_mini.setBackgroundResource(R.drawable.ic_play_arrow_black_24dp);
                            myMediaPlayer.pause();
                        }
                        else{
                            play_btn.setBackgroundResource(R.mipmap.ic_pause);
                            play_btn_mini.setBackgroundResource(R.drawable.ic_pause_black_24dp);
                            myMediaPlayer.start();
                            updateSeekBar();
                        }
                  }
              });


              play_btn_mini.setOnClickListener(new View.OnClickListener() {
                  @Override
                  public void onClick(View v) {
                      play_btn.performClick();
                  }
              });
              next_btn.setOnClickListener(new View.OnClickListener() {
                  @Override
                  public void onClick(View v) {
                      myMediaPlayer.stop();
                      myMediaPlayer.release();

                      Toast.makeText(getApplicationContext(),""+ pos, Toast.LENGTH_SHORT).show();
                      pos = ((pos+1) % mySongs.size());
                      Uri u = Uri.parse(mySongs.get(pos).toString());
                      myMediaPlayer = MediaPlayer.create(getApplicationContext(),u);
                      myMediaPlayer.start();
                      seekBar.setMax(myMediaPlayer.getDuration());
                      songtitle.setText(mySongs.get(pos).getName().toString());
                      songtitlemini.setText(mySongs.get(pos).getName());
                  }
              });
              next_btn_mini.setOnClickListener(new View.OnClickListener() {
                  @Override
                  public void onClick(View v) {
                      next_btn.performClick();
                  }
              });

              prev_btn.setOnClickListener(new View.OnClickListener() {
                  @Override
                  public void onClick(View v) {
                      myMediaPlayer.stop();
                      myMediaPlayer.release();

                      if(pos!=0){ pos = (Math.abs(pos-1)%mySongs.size()); }
                      else{ pos = mySongs.size() - 1; }

                      Uri u = Uri.parse(mySongs.get(pos).toString());
                      myMediaPlayer = MediaPlayer.create(getApplicationContext(),u);
                      myMediaPlayer.start();
                      seekBar.setMax(myMediaPlayer.getDuration());
                      songtitle.setText(mySongs.get(pos).getName().toString());
                      songtitlemini.setText(mySongs.get(pos).getName());
                  }
              });
                prev_btn_mini.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        prev_btn.performClick();
                    }
                });



          }
      });

  }



    private void prepareBottomSheet(BottomSheetBehavior bottomSheetBehavior){


        bottomSheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                switch (newState)
                {
                    case BottomSheetBehavior.STATE_EXPANDED:

                    Animation aniFade = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.fade_out);
                    drawerpull.startAnimation(aniFade);
                    drawerpull.setVisibility(View.INVISIBLE);
                    break;

                    case BottomSheetBehavior.STATE_COLLAPSED:

                        Animation aniFad = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.fade_in);
                        drawerpull.startAnimation(aniFad);
                        drawerpull.setVisibility(View.VISIBLE);
                    break;

                }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {

        }
        });


    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus){
        super.onWindowFocusChanged(hasFocus);
        if(hasFocus){
            decorView.setSystemUiVisibility(uiBars.hideSystemBars());
        }
    }


}

package com.example.redhawkmusicplayer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.media.MediaPlayer;
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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private BottomSheetBehavior bottomSheetBehavior;
    private LinearLayout bottomsheet,drawerpull;
    private ListView listView;
    private String[] Music;
    private TextView songtitle;
    private ImageView play_btn;
    private Boolean bool;
    private SeekBar seekBar;
    static MediaPlayer myMediaPlayer;
    Thread updateSeekBar;


    GlobalFunctions uiBars = new GlobalFunctions();
    private View decorView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        play_btn = findViewById(R.id.play_btn);
        songtitle = findViewById(R.id.song_title);
        listView = findViewById(R.id.lv);
        drawerpull = findViewById(R.id.drawerpull);
        seekBar = findViewById(R.id.seekbar);


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
        drawerpull.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(bottomSheetBehavior.getState() != BottomSheetBehavior.STATE_EXPANDED){
                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                }
                else{
                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                }
            }
        });


        bool = false;
        play_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(bool == false){
                    play_btn.setBackground(ContextCompat.getDrawable(getApplicationContext(),R.drawable.ic_stop_black_24dp));
                    bool = true;
                }
                else{
                    play_btn.setBackground(ContextCompat.getDrawable(getApplicationContext(),R.drawable.ic_play_arrow_black_24dp));
                    bool = false;
                }

            }
        });




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




      updateSeekBar = new Thread(){
        @Override
        public void run(){
            int totalDuration = myMediaPlayer.getDuration();
            int currentPosition = 0;

            while (currentPosition<totalDuration){
                try{
                    sleep(500);
                    currentPosition = myMediaPlayer.getCurrentPosition();
                    seekBar.setProgress(currentPosition);
                }
                catch(InterruptedException e){
                    e.printStackTrace();
                }
            }
        }
      };

      if(myMediaPlayer!=null){
          myMediaPlayer.stop();
          myMediaPlayer.release();
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
//        ArrayAdapter adapter = new ArrayAdapter(getApplicationContext(),android.R.layout.simple_list_item_1,Music);

      listView.setAdapter(adapter);

      listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
          @Override
          public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
              ItemHelper lists = list.get(position);
//              Toast.makeText(getApplicationContext(),""+lists.getSong_title() ,Toast.LENGTH_SHORT).show();

              Snackbar.make(view, lists.getSong_title(), Snackbar.LENGTH_LONG)
                      .setAction("No action", null).show();
              songtitle.setText(parent.getItemAtPosition(position).toString());
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

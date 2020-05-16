package com.example.rustybeats;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import com.example.rustybeats.CircularSeekBar;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;
import com.example.rustybeats.CircularSeekBar.OnCircularSeekBarChangeListener;
import java.io.File;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private BottomSheetBehavior bottomSheetBehavior;
    private LinearLayout bottomsheet,drawerpull;
    private ConstraintLayout nowPlaying;
    private ListView listView;
    private String[] Music;
    private TextView songtitle,songtitlemini,cur_time,full_time;
    private ImageView play_btn,prev_btn,next_btn,play_btn_mini,prev_btn_mini,next_btn_mini,album_art,mini_album_art;
    private Boolean bool;
    private SeekBar seekBar;
    private CircularSeekBar seekBar2;
    static MediaPlayer myMediaPlayer;
    int pos;
    private Runnable runnable,runnable2;
    private Handler handler,handler2;
    GlobalFunctions uiBars = new GlobalFunctions();
    private View decorView;
    RecyclerView recyclerView;
    RecyclerAdapter adapter;
    private String[] allPaths;
    private File storage;
    TinyDB tinydb;
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
        recyclerView = findViewById(R.id.music_groups);

        recyclerViewFunc();


        handler = new Handler();
        handler2 = new Handler();

        songtitle = findViewById(R.id.song_name);
        songtitle.setSelected(true);
        songtitlemini = findViewById(R.id.song_title);
        songtitlemini.setSelected(true);
        album_art = findViewById(R.id.album_art);
        mini_album_art = findViewById(R.id.mini_album_art);
        listView = findViewById(R.id.lv);
        drawerpull = findViewById(R.id.drawerpull);
        nowPlaying = findViewById(R.id.NowPlaying);
        seekBar = findViewById(R.id.seekbar);
        seekBar2 = findViewById(R.id.circularSeekBar1);
        tinydb = new TinyDB(this);

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
                      Log.i("message", "hello");
                      runOnUiThread(new Runnable() {
                          @Override
                          public void run() {
                              display();
                          }
                      });

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


    protected void onDestroy(){
        super.onDestroy();
        if(myMediaPlayer!=null){
            myMediaPlayer.stop();
            myMediaPlayer.release();
        }
    }

    private void recyclerViewFunc() {
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false));

        final ArrayList<ItemHelper> groups = new ArrayList<>();
        groups.add(new ItemHelper("All Songs"));
        groups.add(new ItemHelper("Artists"));
        groups.add(new ItemHelper("Albums"));
        groups.add(new ItemHelper("Playlists"));
        groups.add(new ItemHelper("Genres"));
        groups.add(new ItemHelper("Spotify"));

        adapter = new RecyclerAdapter(getApplicationContext(),groups);
        recyclerView.setAdapter(adapter);

        adapter.setOnItemClickListener(new RecyclerAdapter.OnItemClickListener() {
            @SuppressLint("ResourceAsColor")
            @Override
            public void onItemClick(int position, View itemView) {
                Toast.makeText(getApplicationContext(),"" + groups.get(position).getSong_title(),Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void updateSeekBar(){
        seekBar.setProgress((myMediaPlayer.getCurrentPosition()));
        seekBar2.setProgress((myMediaPlayer.getCurrentPosition()));

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

    void display(){
        final ArrayList<File> mySongs;

        allPaths = StorageUtil.getStorageDirectories(this);

        for(String path : allPaths){
            storage = new File(path);
            Method.load_Directory_Files(storage);
        }

        mySongs = Constant.allMediaList;

        Music = new String[mySongs.size()];
       final ArrayList<ItemHelper> list = new ArrayList<>();
        for(int i = 0; i< mySongs.size(); i++){
            Music[i] = mySongs.get(i).getName().toString().replace(".mp3", "").replace(".wav","").replace(".ogg","").replace(".m4a","");
            Bitmap bitmap = null;
            ItemHelper item = new ItemHelper(Music[i],mySongs.get(i));
            list.add(item);
        }

      ArrAdapter adapter = new ArrAdapter(getApplicationContext(),R.layout.listitem,list);

      listView.setAdapter(adapter);

      listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
          @SuppressLint("ResourceAsColor")
          @Override
          public void onItemClick(final AdapterView<?> parent, final View view, final int position, final long id) {
              pos = position;
              final ItemHelper lists = list.get(position);

              Uri u = Uri.parse(mySongs.get(position).toString());
              if(myMediaPlayer!=null){
                  myMediaPlayer.stop();
                  myMediaPlayer.release();
              }
              seekBar.setProgress(0);
              seekBar2.setProgress(0);
              cur_time.setText("0:00");

              myMediaPlayer = MediaPlayer.create(getApplicationContext(),u);
              myMediaPlayer.start();

              if(lists.getBitmap()!=null){
                  album_art.setImageBitmap(lists.getBitmap());
                  mini_album_art.setImageBitmap(list.get(pos).getBitmap());
              }
              else{
                  album_art.setImageResource(R.drawable.red);
                  mini_album_art.setImageResource(R.drawable.red);
              }

              songtitle.setText(mySongs.get(position).getName());
              songtitlemini.setText(mySongs.get(position).getName());
              play_btn.setBackgroundResource(R.drawable.pause_white);
              play_btn_mini.setBackgroundResource(R.drawable.pause_circle);
              int Duration = myMediaPlayer.getDuration()/1000;
              seekBar.setProgress(myMediaPlayer.getCurrentPosition());
              seekBar2.setProgress(myMediaPlayer.getCurrentPosition());
              seekBar2.setMax(myMediaPlayer.getDuration());
              seekBar.setMax(myMediaPlayer.getDuration());
              full_time.setText(Duration / 60 + ":" + Duration % 60);

              myMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                  @Override
                  public void onCompletion(MediaPlayer mp) {
                      seekBar.setProgress(0);
                      seekBar2.setProgress(0);
                      mp.seekTo(seekBar.getProgress());
                      if(myMediaPlayer.isPlaying()){
                          Log.i("paused", "here");
                          myMediaPlayer.pause();
                      }
                      cur_time.setText("0:00");
                      play_btn.setBackgroundResource(R.drawable.play_white);
                      play_btn_mini.setBackgroundResource(R.drawable.play_circle);
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


              seekBar2.setOnSeekBarChangeListener(new CircleSeekBarListener(myMediaPlayer));


              play_btn.setOnClickListener(new View.OnClickListener() {
                  @Override
                  public void onClick(View v) {
//                      seekBar.setMax(myMediaPlayer.getDuration());
                        if(myMediaPlayer.isPlaying()){
                            play_btn.setBackgroundResource(R.drawable.play_white);
                            play_btn_mini.setBackgroundResource(R.drawable.play_circle);
                            myMediaPlayer.pause();
                        }
                        else{
                            play_btn.setBackgroundResource(R.drawable.pause_white);
                            play_btn_mini.setBackgroundResource(R.drawable.pause_circle);
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
                      if(list.get(pos).getBitmap()!=null){
                          album_art.setImageBitmap(list.get(pos).getBitmap());
                          mini_album_art.setImageBitmap(list.get(pos).getBitmap());
                      }
                      else{
                          album_art.setImageResource(R.drawable.red);
                          mini_album_art.setImageResource(R.drawable.red);
                      }
                      myMediaPlayer = MediaPlayer.create(getApplicationContext(),u);
                      myMediaPlayer.start();
                      seekBar.setMax(myMediaPlayer.getDuration());
                      seekBar2.setMax(myMediaPlayer.getDuration());
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
                      if(list.get(pos).getBitmap()!=null){
                          album_art.setImageBitmap(list.get(pos).getBitmap());
                          mini_album_art.setImageBitmap(list.get(pos).getBitmap());
                      }
                      else{
                          album_art.setImageResource(R.drawable.red);
                          mini_album_art.setImageResource(R.drawable.red);
                      }
                      myMediaPlayer = MediaPlayer.create(getApplicationContext(),u);
                      myMediaPlayer.start();
                      seekBar.setMax(myMediaPlayer.getDuration());
                      seekBar2.setMax(myMediaPlayer.getDuration());
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
                Animation aniFadeOut = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.fade_out);
                Animation aniFadeIn = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.fade_in);
                switch (newState)
                {
                    case BottomSheetBehavior.STATE_EXPANDED:


                    drawerpull.startAnimation(aniFadeOut);
                    nowPlaying.startAnimation(aniFadeIn);
                    drawerpull.setVisibility(View.GONE);
                    nowPlaying.setVisibility(View.VISIBLE);

                    break;

                    case BottomSheetBehavior.STATE_COLLAPSED:

                        drawerpull.startAnimation(aniFadeIn);
                        nowPlaying.startAnimation(aniFadeOut);
                        nowPlaying.setVisibility(View.GONE);
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




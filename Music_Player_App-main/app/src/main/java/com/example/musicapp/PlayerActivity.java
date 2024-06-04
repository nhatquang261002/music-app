package com.example.musicapp;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.musicapp.model.Song;
import com.example.musicapp.service.MyService;
import com.example.musicapp.service.ZingMp3Api;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class PlayerActivity extends AppCompatActivity {
    private TextView tv_nameSong, tv_nameArtist, duration_played, duration_total;
    private ImageView cover_art, next_btn, prev_btn, back_btn;
    private FloatingActionButton playPause_btn;
    private SeekBar seekBar;

    private ExoPlayer exoPlayer;
    private Handler handler = new Handler();
    private Song song;
    private int position;
    private ArrayList<Song> songList = new ArrayList<>();

    private Thread prevThread, nextThread;

    private MyService myService;
    private boolean isServiceConnected;
    private ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            MyService.MyBinder myBinder = (MyService.MyBinder) service;
            isServiceConnected = true;
            myService = myBinder.getMyService();

        }
        @Override
        public void onServiceDisconnected(ComponentName name) {
            myService = null;
            isServiceConnected = false;
        }
    };



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_player);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        initViews();
        GetIntentMethod();


        //___________________________________________
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (isServiceConnected && fromUser) {
                    myService.seekTo(progress * 1000);
                }
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        PlayerActivity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(isServiceConnected){
                    int mCurrentPosition = myService.getCurrentPosition();
                    seekBar.setProgress(mCurrentPosition);
                    duration_played.setText(formattedTime(mCurrentPosition));
                }
                handler.postDelayed(this, 1000);
            }
        });




        playPause_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (myService.isPlaying()) {
                    myService.pauseMusic();
                    playPause_btn.setImageResource(R.drawable.ic_play);
                } else {
                    myService.resumeMusic();
                    playPause_btn.setImageResource(R.drawable.ic_pause);
                }
            }
        });

        back_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        prevThread();
        nextThread();
    }



    private void initViews() {
        tv_nameSong = findViewById(R.id.tv_nameSong);
        tv_nameArtist = findViewById(R.id.tv_nameArtist);
        duration_played = findViewById(R.id.duration_played);
        duration_total = findViewById(R.id.duration_total);
        cover_art = findViewById(R.id.cover_art);
        next_btn = findViewById(R.id.next_btn);
        prev_btn = findViewById(R.id.prev_btn);
        back_btn = findViewById(R.id.back_btn);
        playPause_btn = findViewById(R.id.playPause_btn);
        seekBar = findViewById(R.id.seekBar);
    }

    private void GetIntentMethod() {
        Intent intent = getIntent();
        position = intent.getIntExtra("position", 0);
        Bundle bundle = intent.getExtras();
        if (bundle != null) {
            songList = (ArrayList<Song>) bundle.getSerializable("songsList");
        }


        if (songList == null || songList.isEmpty()) {
            return;
        }

        // Get the song at the specified position
        song = songList.get(position);

        // Update UI with the song details
        tv_nameSong.setText(song.getName_song());
        tv_nameArtist.setText(song.getName_artist());
        Picasso.with(PlayerActivity.this).load(song.getThumbnail()).into(cover_art);
        duration_total.setText(formattedTime(song.getDuration()));
        seekBar.setMax(song.getDuration());


        // To service
        Intent intent_service = new Intent(this, MyService.class);
        Bundle service_bundle = new Bundle();
        service_bundle.putSerializable("object_song", song);
        intent_service.putExtras(service_bundle);   // Nhận ở onStartCommand

        startService(intent_service);
        bindService(intent_service, mServiceConnection, Context.BIND_AUTO_CREATE);
    }




    // Inside your PlayerActivity class
    private String formattedTime(int currentPosition) {
        String totalout = "";
        String totalNew = "";
        String seconds = String.valueOf(currentPosition % 60);
        String minutes = String.valueOf(currentPosition / 60);
        totalout = minutes + ":" + seconds;
        totalNew = minutes + ":" + "0" + seconds;
        if (seconds.length() == 1) {
            return totalNew;
        } else {
            return totalout;
        }
    }

    private void prevThread() {
        prevThread = new Thread() {
            @Override
            public void run() {
                super.run();
                prev_btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        prevBtnClicked();
                    }
                });
            }
        };
        prevThread.start();
    }

    public void prevBtnClicked() {
        Intent intent = new Intent(this, MyService.class);
        stopService(intent);

        // Bound Service
        if (isServiceConnected){
            unbindService(mServiceConnection);
            isServiceConnected = false;
        }

        position = ((position - 1) < 0 ? (songList.size() - 1) : (position - 1)); // Âm thì trả về cuối danh sách, còn lại = pos -1
        song = songList.get(position);


        //_________________
        Intent intent_service = new Intent(this, MyService.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable("object_song", song);
        intent_service.putExtras(bundle);   // Nhận
        startService(intent_service);
        bindService(intent_service, mServiceConnection, Context.BIND_AUTO_CREATE);


        tv_nameSong.setText(song.getName_song());
        tv_nameArtist.setText(song.getName_artist());

        Picasso.with(PlayerActivity.this).load(song.getThumbnail()).into(cover_art);

        duration_total.setText(formattedTime(song.getDuration()));
        seekBar.setMax(song.getDuration());

        playPause_btn.setImageResource(R.drawable.ic_pause);
    }

    public void nextBtnClicked() {
        Intent intent = new Intent(this, MyService.class);
        stopService(intent);

        // Bound Service
        if (isServiceConnected){
            unbindService(mServiceConnection);
            isServiceConnected = false;
        }


        position = ((position + 1) % songList.size()); // không bị ra khỏi mảng
        song = songList.get(position);

        //_________________
        Intent intent_service = new Intent(this, MyService.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable("object_song", song);
        intent_service.putExtras(bundle);   // Nhận
        startService(intent_service);
        bindService(intent_service, mServiceConnection, Context.BIND_AUTO_CREATE);

        tv_nameSong.setText(song.getName_song());
        tv_nameArtist.setText(song.getName_artist());

        Picasso.with(PlayerActivity.this).load(song.getThumbnail()).into(cover_art);

        duration_total.setText(formattedTime(song.getDuration()));
        seekBar.setMax(song.getDuration());
        playPause_btn.setImageResource(R.drawable.ic_pause);
    }

    private void nextThread() {
        nextThread = new Thread(){
            @Override
            public void run() {
                super.run();
                next_btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        nextBtnClicked();
                    }
                });
            }
        };
        nextThread.start();
    }
}
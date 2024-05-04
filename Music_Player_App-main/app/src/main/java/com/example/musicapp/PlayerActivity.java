package com.example.musicapp;

import static com.example.musicapp.adapter.TrendingSongAdapter.mList;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.musicapp.model.Song;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class PlayerActivity extends AppCompatActivity {
    private TextView tv_nameSong, tv_nameArtist, duration_played, duration_total;
    private ImageView cover_art, next_btn, prev_btn, back_btn, shuffle_btn, repeat_btn;
    private FloatingActionButton playPause_btn;
    private SeekBar seekBar;

    private ExoPlayer exoPlayer;
    private Handler handler = new Handler();
    private Song song;
    int position = -1;
    ArrayList<Song> listSongs = new ArrayList<>();

    private Thread playThread, prevThread, nextThread;


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
        PlayMusic();




        //___________________________________________
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (exoPlayer!=null && fromUser) {
                    exoPlayer.seekTo(progress * 1000);
                }
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });
        PlayerActivity.this.runOnUiThread(new Runnable() {  // Cập nhật thời lượng hiện tại
            @Override
            public void run() {
                if(exoPlayer!=null){
                    int mCurrentPosition = (int) exoPlayer.getCurrentPosition()/1000;
                    seekBar.setProgress(mCurrentPosition);
                    duration_played.setText(formattedTime(mCurrentPosition));
                }
                handler.postDelayed(this, 1000);
            }
        });
        playPause_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (exoPlayer.isPlaying()) {
                    exoPlayer.pause();
                    playPause_btn.setImageResource(R.drawable.ic_play);
                } else {
                    exoPlayer.play();
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

        //playThread();
        //prevThread();
        //nextThread();

    }
    @Override
    protected void onResume() {

        //playThread();
        prevThread();
        nextThread();

        super.onResume();
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
        shuffle_btn = findViewById(R.id.shuffle_btn);
        repeat_btn = findViewById(R.id.repeat_btn);
        playPause_btn = findViewById(R.id.playPause_btn);
        seekBar = findViewById(R.id.seekBar);
    }
    private void GetIntentMethod() {
        Intent intent = getIntent();
        position = intent.getIntExtra("position", -1);

        if (position == -1) {
            return;
        }
        listSongs = mList;
        song = mList.get(position);
        if (song == null) {
            return;
        }
        tv_nameSong.setText(song.getName_song());
        tv_nameArtist.setText(song.getName_artist());
        Picasso.with(PlayerActivity.this).load(song.getThumbnail()).into(cover_art);// do data vao img_icon

        duration_total.setText(formattedTime(song.getDuration()));
        seekBar.setMax(song.getDuration());

    }
    private void PlayMusic() {
        String url = "https://mp3.zing.vn/xhr/media/get-source?type=audio&key=" + song.getCode(); ;
        RequestQueue requestQueue = Volley.newRequestQueue(PlayerActivity.this);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        try {
                            JSONObject jsonObject =new JSONObject(s);
                            JSONObject jsonObject_data = jsonObject.getJSONObject("data");
                            JSONObject jsonObject_source = jsonObject_data.getJSONObject("source");
                            String url = jsonObject_source.getString("128");
                            exoPlayer = new ExoPlayer.Builder(PlayerActivity.this).build();
                            MediaItem mediaItem = MediaItem.fromUri(url);
                            //MediaItem mediaItem = MediaItem.fromUri(song.getSource());

                            exoPlayer.setMediaItem(mediaItem);
                            exoPlayer.prepare();
                            exoPlayer.setPlayWhenReady(true);// lấy source nhạc

                        } catch (JSONException e) {
                            throw new RuntimeException(e);
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {

                    }
                });
        requestQueue.add(stringRequest);
    }
    private String formattedTime(int mCurrentPosition) {
        String totalout = "";
        String totalNew = "";
        String seconds = String.valueOf(mCurrentPosition%60);
        String minutes = String.valueOf(mCurrentPosition/60);
        totalout = minutes + ":" + seconds;
        totalNew = minutes + ":0" + seconds;
        if(seconds.length()==1){
            return  totalNew;
        }else {
            return  totalout;
        }
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        exoPlayer.release();
    }

    private void prevThread() {
        prevThread = new Thread(){
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
        exoPlayer.stop();
        exoPlayer.release();
        position = ((position - 1) < 0 ? (listSongs.size() - 1) : (position - 1)); // Âm thì trả về cuối danh sách, còn lại = pos -1
        song = listSongs.get(position);

        PlayMusic();

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
    public void nextBtnClicked() {
        exoPlayer.stop();
        exoPlayer.release();
        position = ((position + 1) % listSongs.size()); // không bị ra khỏi mảng
        song = listSongs.get(position);

        PlayMusic();

        tv_nameSong.setText(song.getName_song());
        tv_nameArtist.setText(song.getName_artist());

        Picasso.with(PlayerActivity.this).load(song.getThumbnail()).into(cover_art);

        duration_total.setText(formattedTime(song.getDuration()));
        seekBar.setMax(song.getDuration());
        playPause_btn.setImageResource(R.drawable.ic_pause);
    }



}
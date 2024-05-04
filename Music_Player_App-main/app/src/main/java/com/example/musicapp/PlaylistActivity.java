package com.example.musicapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.musicapp.adapter.PlaylistAdapter;
import com.example.musicapp.adapter.SongAdapter;
import com.example.musicapp.model.Playlist;
import com.example.musicapp.model.Song;

import java.util.ArrayList;

public class PlaylistActivity extends AppCompatActivity {
    private TextView playlist_name, playlist_description;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_playlist);

//// playlist_name.findViewById(R.id.playlist_name_title);
       // playlist_description.findViewById(R.id.playlist_description);



        // Retrieve playlist data from Intent
        Intent intent = getIntent();
        Playlist playlist = (Playlist) intent.getSerializableExtra("playlist");
        ArrayList<Song> songs = playlist.getSongs();
       // playlist_name.setText(playlist.getName());
        // playlist_description.setText(playlist.getDescription());
        RecyclerView songListRecyclerView = findViewById(R.id.playlist_recycler_view);
        songListRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        songListRecyclerView.setAdapter(new SongAdapter(songs));

        ImageButton backButton = findViewById(R.id.back_btn);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        EdgeToEdge.enable(this);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.playlist_activity), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

}
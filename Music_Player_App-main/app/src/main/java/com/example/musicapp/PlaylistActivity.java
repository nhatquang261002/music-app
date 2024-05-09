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
import com.example.musicapp.service.MusicService;

import java.util.ArrayList;
import java.util.List;

public class PlaylistActivity extends AppCompatActivity {
    private TextView playlist_name, playlist_description;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_playlist);

        playlist_name = findViewById(R.id.playlist_name_title);
        playlist_description = findViewById(R.id.playlist_description);




        // Retrieve playlist data from Intent
        Intent intent = getIntent();
        Playlist playlist = (Playlist) intent.getSerializableExtra("playlist");
        List<String> songIds = playlist.getSongIds();
        playlist_name.setText(playlist.getName());
        playlist_description.setText(playlist.getDescription());
        fetchSongsForPlaylist(songIds);


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

    private void fetchSongsForPlaylist(List<String> songIds) {
        if(songIds.isEmpty()) return;

        MusicService.fetchSongs(this, new MusicService.SongCallback() {
            @Override
            public void onSuccess(ArrayList<Song> songs) {
                // Filter the fetched songs based on the IDs in the playlist
                ArrayList<Song> playlistSongs = new ArrayList<>();
                for (Song song : songs) {
                    if (songIds.contains(song.getId())) {
                        playlistSongs.add(song);
                    }
                }

                // Set up RecyclerView for songs in the playlist
                RecyclerView songListRecyclerView = findViewById(R.id.playlist_recycler_view);
                songListRecyclerView.setLayoutManager(new LinearLayoutManager(PlaylistActivity.this));
                songListRecyclerView.setAdapter(new SongAdapter(playlistSongs));
            }

            @Override
            public void onError(String message) {
                // Handle error
            }
        });
    }

}
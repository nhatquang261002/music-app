package com.example.musicapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.musicapp.adapter.SongAdapter;
import com.example.musicapp.data.PlaylistDAO;
import com.example.musicapp.fragment.PlaylistBottomSheetFragment;
import com.example.musicapp.model.Playlist;
import com.example.musicapp.model.Song;
import com.example.musicapp.service.MusicService;

import java.util.ArrayList;
import java.util.List;

public class PlaylistActivity extends AppCompatActivity implements PlaylistBottomSheetFragment.PlaylistOptionsListener {
    private TextView playlist_name, playlist_description;
    private Playlist currentPlaylist;
    private PlaylistDAO playlistDAO;

    private final ActivityResultLauncher<Intent> editPlaylistLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK) {
                    // Handle the updated playlist data
                    if (result.getData() != null) {
                        currentPlaylist = (Playlist) result.getData().getSerializableExtra("updatedPlaylist");
                        if (currentPlaylist != null) {
                            playlist_name.setText(currentPlaylist.getName());
                            playlist_description.setText(currentPlaylist.getDescription());
                            // Reload songs if necessary
                        }
                    }
                }
            }
    );

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_toolbar, menu);
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_playlist);

        playlist_name = findViewById(R.id.playlist_name_title);
        playlist_description = findViewById(R.id.playlist_description);

        // Initialize playlistDAO
        playlistDAO = MusicApp.getDatabase().playlistDAO();

        // Retrieve playlist data from Intent
        Intent intent = getIntent();
        currentPlaylist = (Playlist) intent.getSerializableExtra("playlist");
        if (currentPlaylist != null) {
            List<String> songIds = currentPlaylist.getSongIds();
            playlist_name.setText(currentPlaylist.getName());
            playlist_description.setText(currentPlaylist.getDescription());
            fetchSongsForPlaylist(songIds);
        }

        ImageButton optionsButton = findViewById(R.id.options_btn);
        optionsButton.setOnClickListener(v -> {
            PlaylistBottomSheetFragment bottomSheetFragment = new PlaylistBottomSheetFragment();
            bottomSheetFragment.setPlaylist(currentPlaylist);
            bottomSheetFragment.setPlaylistOptionsListener(PlaylistActivity.this);
            bottomSheetFragment.show(getSupportFragmentManager(), bottomSheetFragment.getTag());
        });

        ImageButton backButton = findViewById(R.id.back_btn);
        backButton.setOnClickListener(v -> finish());

        EdgeToEdge.enable(this);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.playlist_activity), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void fetchSongsForPlaylist(List<String> songIds) {
        if (songIds.isEmpty()) return;

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
                songListRecyclerView.setAdapter(new SongAdapter(playlistSongs, PlaylistActivity.this));

            }

            @Override
            public void onError(String message) {
                // Handle error
            }
        });
    }

    @Override
    public void onEditPlaylist(Playlist playlist) {
        Intent intent = new Intent(this, EditPlaylistActivity.class);
        intent.putExtra("playlist", playlist);
        editPlaylistLauncher.launch(intent);
    }

    @Override
    public void onDeletePlaylist(Playlist playlist) {
        new Thread(() -> {
            // Delete the playlist from the database
            playlistDAO.delete(playlist);
            runOnUiThread(() -> {
                // Notify the fragment to refresh
                Intent resultIntent = new Intent();
                setResult(RESULT_OK, resultIntent);
                // Exit the activity
                finish();
            });
        }).start();
    }

    @Override
    public void finish() {
        Intent resultIntent = new Intent();
        resultIntent.putExtra("updatedPlaylist", currentPlaylist);
        setResult(RESULT_OK, resultIntent);
        super.finish();
    }
}

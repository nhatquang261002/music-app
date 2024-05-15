package com.example.musicapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.musicapp.data.PlaylistDAO;
import com.example.musicapp.model.Playlist;

public class EditPlaylistActivity extends AppCompatActivity {
    private EditText editTextName;
    private EditText editTextDescription;
    private Playlist currentPlaylist;
    private PlaylistDAO playlistDAO;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_playlist);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }

        ImageButton backButton = findViewById(R.id.back_btn);
        backButton.setOnClickListener(v -> finish());

        editTextName = findViewById(R.id.edit_text_name);
        editTextDescription = findViewById(R.id.edit_text_description);
        Button saveButton = findViewById(R.id.button_save);

        // Initialize playlistDAO
        playlistDAO = MusicApp.getDatabase().playlistDAO();

        // Retrieve playlist data from Intent
        Intent intent = getIntent();
        currentPlaylist = (Playlist) intent.getSerializableExtra("playlist");

        // Initialize fields with current playlist data
        if (currentPlaylist != null) {
            editTextName.setText(currentPlaylist.getName());
            editTextDescription.setText(currentPlaylist.getDescription());
        }

        saveButton.setOnClickListener(v -> savePlaylist());
    }

    private void savePlaylist() {
        String updatedName = editTextName.getText().toString().trim();
        String updatedDescription = editTextDescription.getText().toString().trim();

        // Update playlist data
        if (currentPlaylist != null) {
            currentPlaylist.setName(updatedName);
            currentPlaylist.setDescription(updatedDescription);

            // Save to database
            new Thread(() -> {
                playlistDAO.update(currentPlaylist);
                Intent resultIntent = new Intent();
                resultIntent.putExtra("updatedPlaylist", currentPlaylist);
                setResult(RESULT_OK, resultIntent);
                runOnUiThread(this::finish);
            }).start();
        }
    }
}

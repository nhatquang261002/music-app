package com.example.musicapp;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.musicapp.adapter.SongAdapter;
import com.example.musicapp.data.PlaylistDAO;
import com.example.musicapp.model.Song;
import com.example.musicapp.service.MusicService;

import java.util.ArrayList;

public class SearchActivity extends AppCompatActivity {

    private RecyclerView searchResultsRecyclerView;
    private SongAdapter songAdapter;
    private ArrayList<Song> searchResultsList;
    private EditText searchEditText;
    private ProgressBar loadingIndicator;
    private TextView emptyResultText;
    private Handler handler = new Handler(Looper.getMainLooper());
    private Runnable searchRunnable;
    private PlaylistDAO mPlaylistDAO;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        searchEditText = findViewById(R.id.edt_search);
        searchResultsRecyclerView = findViewById(R.id.recycler_view);
        loadingIndicator = findViewById(R.id.loading_indicator);
        emptyResultText = findViewById(R.id.empty_result_text);

        ImageButton backButton = findViewById(R.id.btn_back);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        searchResultsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mPlaylistDAO = MusicApp.getDatabase().playlistDAO();
        searchResultsList = new ArrayList<>();
        songAdapter = new SongAdapter(searchResultsList, this, mPlaylistDAO);
        searchResultsRecyclerView.setAdapter(songAdapter);

        setupSearchListener();
    }

    private void setupSearchListener() {
        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                handler.removeCallbacks(searchRunnable);
                searchRunnable = new Runnable() {
                    @Override
                    public void run() {
                        performSearch(charSequence.toString());
                    }
                };
                handler.postDelayed(searchRunnable, 300); // Adjust the delay as needed (in milliseconds)
            }

            @Override
            public void afterTextChanged(Editable editable) {}
        });
    }

    private void performSearch(String query) {
        if (query.isEmpty()) {
            searchResultsList.clear();
            songAdapter.notifyDataSetChanged();
            emptyResultText.setVisibility(View.GONE); // Hide empty result text when query is empty
            return;
        }

        loadingIndicator.setVisibility(View.VISIBLE); // Show loading indicator
        emptyResultText.setVisibility(View.GONE); // Hide empty result text initially

        MusicService.searchSongs(this, query, new MusicService.SongCallback() {
            @Override
            public void onSuccess(ArrayList<Song> songs) {
                loadingIndicator.setVisibility(View.GONE); // Hide loading indicator
                searchResultsList.clear();
                searchResultsList.addAll(songs);
                songAdapter.notifyDataSetChanged();

                if (songs.isEmpty()) {
                    emptyResultText.setVisibility(View.VISIBLE); // Show empty result text when no results found
                } else {
                    emptyResultText.setVisibility(View.GONE); // Hide empty result text if there are results
                }
            }

            @Override
            public void onError(String message) {
                loadingIndicator.setVisibility(View.GONE); // Hide loading indicator
                Log.e("SearchActivity", "Search error: " + message);
                emptyResultText.setVisibility(View.VISIBLE); // Show empty result text when error occurs
                emptyResultText.setText(message); // Show the error message
            }
        });
    }



}

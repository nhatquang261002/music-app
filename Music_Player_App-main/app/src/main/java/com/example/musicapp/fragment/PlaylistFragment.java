package com.example.musicapp.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.musicapp.MusicApp;
import com.example.musicapp.PlaylistActivity;
import com.example.musicapp.R;
import com.example.musicapp.adapter.PlaylistAdapter;
import com.example.musicapp.data.PlaylistDAO;
import com.example.musicapp.model.Playlist;

import java.util.ArrayList;
import java.util.List;

public class PlaylistFragment extends Fragment {

    private RecyclerView recyclerView;
    private PlaylistAdapter playlistAdapter;
    private LinearLayout addPlaylistButton;
    private PlaylistDAO playlistDAO;
    private List<Playlist> playlists;

    private final ActivityResultLauncher<Intent> editPlaylistLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == AppCompatActivity.RESULT_OK) {
                    loadPlaylists();
                }
            }
    );

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_playlist, container, false);

        recyclerView = view.findViewById(R.id.playlist_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        playlistDAO = MusicApp.getDatabase().playlistDAO();
        playlists = new ArrayList<>();

        addPlaylistButton = view.findViewById(R.id.add_playlist_button);
        addPlaylistButton.setOnClickListener(v -> {
            // Handle adding a new playlist
        });

        loadPlaylists();

        return view;
    }

    private void loadPlaylists() {
        new Thread(() -> {
            playlists = playlistDAO.getAllPlaylists();
            getActivity().runOnUiThread(() -> {
                playlistAdapter = new PlaylistAdapter(playlists, PlaylistFragment.this::onPlaylistClicked);
                recyclerView.setAdapter(playlistAdapter);
            });
        }).start();
    }

    private void onPlaylistClicked(Playlist playlist) {
        Intent intent = new Intent(getContext(), PlaylistActivity.class);
        intent.putExtra("playlist", playlist);
        editPlaylistLauncher.launch(intent);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
    }
}

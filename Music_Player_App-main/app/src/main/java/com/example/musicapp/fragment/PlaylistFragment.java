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
                    loadPlaylists(null);
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
        addPlaylistButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AddPlaylistBottomSheetFragment bottomSheetFragment = new AddPlaylistBottomSheetFragment();
                bottomSheetFragment.setAddPlaylistListener(new AddPlaylistBottomSheetFragment.AddPlaylistListener() {
                    @Override
                    public void onPlaylistAdded(Playlist playlist) {
                        addPlaylist(playlist);
                    }
                });
                bottomSheetFragment.show(getChildFragmentManager(), bottomSheetFragment.getTag());
            }
        });

        loadPlaylists(null);

        return view;
    }

    public void addPlaylist(Playlist playlist) {
        playlists.add(playlist);
        int position = playlists.size() - 1;
        playlistAdapter.notifyItemInserted(position);
        Log.d("Playlist", " " + playlists.size());
        new Thread(new Runnable() {
            @Override
            public void run() {
                // Insert the playlist into the database
                playlistDAO.insert(playlist);
                // Update the UI on the main thread
                // Retrieve the updated list of playlists from the database
                List<Playlist> updatedPlaylists = playlistDAO.getAllPlaylists();
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        // Update the list of playlists in the adapter
                        playlists.clear();
                        playlists.addAll(updatedPlaylists);
                        // Notify the adapter of the data change
                        playlistAdapter.notifyDataSetChanged();
                    }
                });
            }
        }).start();
    }

    private void loadPlaylists(@Nullable Runnable callback) {
        new Thread(() -> {
            playlists = playlistDAO.getAllPlaylists();
            if (getActivity() != null) {
                getActivity().runOnUiThread(() -> {
                    playlistAdapter = new PlaylistAdapter(playlists, this::onPlaylistClicked);
                    recyclerView.setAdapter(playlistAdapter);
                    if (callback != null) {
                        callback.run();
                    }
                });
            }
        }).start();
    }

    private void onPlaylistClicked(Playlist playlist) {
        loadPlaylists(() -> {
            // Find the updated playlist in the list
            Playlist updatedPlaylist = findUpdatedPlaylist(playlist.getId());
            if (updatedPlaylist != null) {
                // Launch PlaylistActivity with the updated playlist
                Intent intent = new Intent(getContext(), PlaylistActivity.class);
                intent.putExtra("playlist", updatedPlaylist);
                editPlaylistLauncher.launch(intent);
            } else {
                // Handle the case where the playlist is not found
                Log.e("PlaylistFragment", "Updated playlist not found");
            }
        });
    }

    private Playlist findUpdatedPlaylist(long playlistId) {
        for (Playlist updatedPlaylist : playlists) {
            if (updatedPlaylist.getId() == playlistId) {
                return updatedPlaylist;
            }
        }
        return null;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
    }
}

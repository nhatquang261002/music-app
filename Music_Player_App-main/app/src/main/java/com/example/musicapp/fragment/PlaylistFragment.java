package com.example.musicapp.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.musicapp.PlaylistActivity;
import com.example.musicapp.R;
import com.example.musicapp.adapter.PlaylistAdapter;
import com.example.musicapp.model.Playlist;
import com.example.musicapp.model.Song;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.util.ArrayList;

public class PlaylistFragment extends Fragment {

    private RecyclerView recyclerView;
    private PlaylistAdapter playlistAdapter;
    private LinearLayout addPlaylistButton;
    private ArrayList<Playlist> playlists;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        playlists = new ArrayList<>();
        ArrayList<Song> exampleSongs = new ArrayList<Song>();

        exampleSongs.add(new Song("song_id", "song_name", "artist_name", "thubnail", "1", "", "", "", 120));
        exampleSongs.add(new Song("song_id", "song_name", "artist_name", "thubnail", "1", "", "", "", 120));
        exampleSongs.add(new Song("song_id", "song_name", "artist_name", "thubnail", "1", "", "", "", 120));
        playlists.add(new Playlist("Example Playlist", "", exampleSongs));
    }

    public interface OnPlaylistAddedListener {
        void onPlaylistAdded(Playlist playlist);
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_playlist, container, false);

        recyclerView = view.findViewById(R.id.playlist_recycler_view);
        addPlaylistButton = view.findViewById(R.id.add_playlist_button);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        playlistAdapter = new PlaylistAdapter(playlists, new PlaylistAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Playlist playlist) {
                Intent intent = new Intent(getContext(), PlaylistActivity.class);
                intent.putExtra("playlist", playlist);
                startActivity(intent);
            }
        });

        recyclerView.setAdapter(playlistAdapter);

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

        return view;
    }

    public void addPlaylist(Playlist playlist) {
        playlists.add(playlist);
        int position = playlists.size() - 1;
        playlistAdapter.notifyItemInserted(position);
        Log.d("Playlist", " " + playlists.size());
    }
}


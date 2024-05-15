package com.example.musicapp.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.musicapp.R;
import com.example.musicapp.model.Playlist;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

public class PlaylistBottomSheetFragment extends BottomSheetDialogFragment {

    private Playlist playlist;
    private PlaylistOptionsListener listener;

    public interface PlaylistOptionsListener {
        void onEditPlaylist(Playlist playlist);
        void onDeletePlaylist(Playlist playlist);
    }

    public void setPlaylistOptionsListener(PlaylistOptionsListener listener) {
        this.listener = listener;
    }

    public void setPlaylist(Playlist playlist) {
        this.playlist = playlist;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_playlist_bottom_sheet, container, false);

        TextView editPlaylist = view.findViewById(R.id.edit_playlist);
        TextView deletePlaylist = view.findViewById(R.id.delete_playlist);

        editPlaylist.setOnClickListener(v -> {
            if (listener != null) {
                listener.onEditPlaylist(playlist);
            }
            dismiss();
        });

        deletePlaylist.setOnClickListener(v -> {
            if (listener != null) {
                listener.onDeletePlaylist(playlist);
            }
            dismiss();
        });

        return view;
    }
}

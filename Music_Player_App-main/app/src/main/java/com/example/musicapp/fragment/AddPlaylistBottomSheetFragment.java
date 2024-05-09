package com.example.musicapp.fragment;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.musicapp.R;
import com.example.musicapp.model.Playlist;
import com.example.musicapp.model.Song;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.util.ArrayList;

public class AddPlaylistBottomSheetFragment extends BottomSheetDialogFragment {

    private EditText editTextPlaylistName;
    private EditText editTextPlaylistDescription;
    private Button buttonAddPlaylist;

    private AddPlaylistListener addPlaylistListener;

    public interface AddPlaylistListener {
        void onPlaylistAdded(Playlist playlist);
    }

    public void setAddPlaylistListener(AddPlaylistListener listener) {
        this.addPlaylistListener = listener;
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_playlist_bottom_sheet, container, false);

        editTextPlaylistName = view.findViewById(R.id.edit_text_playlist_name);
        editTextPlaylistDescription = view.findViewById(R.id.edit_text_playlist_description);
        buttonAddPlaylist = view.findViewById(R.id.button_add_playlist);

        buttonAddPlaylist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String playlistName = editTextPlaylistName.getText().toString().trim();
                String playlistDescription = editTextPlaylistDescription.getText().toString().trim();

                // Validate input and add playlist to app
                if (!playlistName.isEmpty()) {
                    ArrayList<String> songs = new ArrayList<>();
                    Playlist newPlaylist = new Playlist(playlistName, playlistDescription, songs); // Replace "" and null with actual data
                    addPlaylistListener.onPlaylistAdded(newPlaylist); // Notify the listener
                    dismiss();
                }
            }
        });

        return view;
    }
}

package com.example.musicapp.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.musicapp.PlayerActivity;
import com.example.musicapp.R;
import com.example.musicapp.data.PlaylistDAO;
import com.example.musicapp.model.Playlist;
import com.example.musicapp.model.Song;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class SongAdapter extends RecyclerView.Adapter<SongAdapter.ViewHolder> {

    private ArrayList<Song> songs;
    private Context context;
    private boolean isInPlaylist;
    private PlaylistDAO playlistDAO;
    private Playlist currentPlaylist;

    public SongAdapter(ArrayList<Song> songs, Context context, PlaylistDAO playlistDAO) {
        this.songs = songs;
        this.context = context;
        this.playlistDAO = playlistDAO;
    }

    public SongAdapter(ArrayList<Song> songs, Context context, PlaylistDAO playlistDAO, Playlist currentPlaylist) {
        this.songs = songs;
        this.context = context;
        this.isInPlaylist = false;
        this.playlistDAO = playlistDAO;
        this.currentPlaylist = currentPlaylist;
    }

    public SongAdapter(ArrayList<Song> songs, Context context, boolean isInPlaylist, PlaylistDAO playlistDAO, Playlist currentPlaylist) {
        this.songs = songs;
        this.context = context;
        this.isInPlaylist = isInPlaylist;
        this.playlistDAO = playlistDAO;
        this.currentPlaylist = currentPlaylist;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_song, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Song song = songs.get(position);

        holder.songTitleTextView.setText(song.getName_song());
        holder.songArtistTextView.setText(song.getName_artist());

        // Load song thumbnail using Picasso library
        Picasso.with(context).load(song.getThumbnail()).into(holder.songAvatar);

        // Set click listener for the song item
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickGoToPlayerActivity(position);
            }
        });

        // Set click listener for the option button
        holder.songOptionsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isInPlaylist) {
                    showPopupMenu(holder.songOptionsButton, song);
                } else {
                    holder.songOptionsButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            showPlaylistDialog(song);
                        }
                    });
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return songs.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView songTitleTextView;
        TextView songArtistTextView;
        ImageView songAvatar;
        ImageButton songOptionsButton;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            songTitleTextView = itemView.findViewById(R.id.tv_nameSong);
            songArtistTextView = itemView.findViewById(R.id.tv_nameArtist);
            songAvatar = itemView.findViewById(R.id.thumbnail_img);
            songOptionsButton = itemView.findViewById(R.id.option_btn);
        }


    }

    private void onClickGoToPlayerActivity(int position) {
        Intent intent = new Intent(context, PlayerActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable("songsList", songs);
        intent.putExtras(bundle);
        intent.putExtra("position", position);
        context.startActivity(intent);
    }

    private void showPopupMenu(View view, Song song) {
        PopupMenu popupMenu = new PopupMenu(context, view);
        popupMenu.getMenuInflater().inflate(R.menu.menu_add_to_playlist, popupMenu.getMenu());
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                int itemId = item.getItemId();
               return false;
            }
        });
        popupMenu.show();
    }

    // Inside showPlaylistDialog() method
    private void showPlaylistDialog(final Song song) {
        // Create a new thread to perform database operations
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                // Retrieve all playlists from the database
                final List<Playlist> playlists = playlistDAO.getAllPlaylists();

                // Use the main thread to update the UI
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        // Extract playlist names from the list
                        final List<String> playlistNames = new ArrayList<>();
                        final boolean[] checkedPlaylists = new boolean[playlists.size()];

                        for (int i = 0; i < playlists.size(); i++) {
                            Playlist playlist = playlists.get(i);
                            // Check if the playlist contains the song
                            if (!playlist.getSongIds().contains(song.getId())) {
                                playlistNames.add(playlist.getName());
                                checkedPlaylists[playlistNames.size() - 1] = false; // Initialize all playlists as unchecked
                            }
                        }

                        // Create a dialog with a scrollable list of playlists and checkboxes
                        AlertDialog.Builder builder = new AlertDialog.Builder(context);
                        builder.setTitle("Save To Playlist");

                        builder.setMultiChoiceItems(playlistNames.toArray(new String[0]), checkedPlaylists, new DialogInterface.OnMultiChoiceClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int position, boolean isChecked) {
                                // Update the checked state of the playlist
                                checkedPlaylists[position] = isChecked;
                            }
                        });

                        // Add a submit button
                        builder.setPositiveButton("Submit", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int which) {
                                // Add the song to all the checked playlists
                                for (int i = 0; i < checkedPlaylists.length; i++) {
                                    if (checkedPlaylists[i]) {
                                        saveToPlaylist(song, playlistNames.get(i));
                                    }
                                }
                            }
                        });

                        // Add a cancel button
                        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int which) {
                                // Cancel button clicked, do nothing
                            }
                        });

                        builder.show();
                    }
                });
            }
        });

        // Start the thread
        thread.start();
    }

    private void saveToPlaylist(final Song song, final String playlistName) {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                // Get the playlist from the database
                final Playlist playlist = getPlaylistByName(playlistName);

                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        if (playlist != null) {
                            // Add the song to the playlist
                            playlistDAO.addSongIdToPlaylist(playlist.getId(), song.getId());
                            String message = "Song '" + song.getName_song() + "' saved to playlist '" + playlistName + "'";
                            Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
                        } else {
                            // Playlist doesn't exist, handle accordingly
                            Toast.makeText(context, "Playlist not found", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
        thread.start();
    }

    private Playlist getPlaylistByName(String playlistName) {
        // Retrieve all playlists from the database
        final List<Playlist> playlists = playlistDAO.getAllPlaylists();

        // Search for the playlist in the list
        for (Playlist playlist : playlists) {
            if (playlist.getName().equals(playlistName)) {
                return playlist;
            }
        }
        return null;
    }

}

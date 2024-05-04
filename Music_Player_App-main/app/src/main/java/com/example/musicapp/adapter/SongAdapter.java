package com.example.musicapp.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;
import com.example.musicapp.R;
import com.example.musicapp.model.Song;

import java.util.ArrayList;


public class SongAdapter extends RecyclerView.Adapter<SongAdapter.ViewHolder> {

    private ArrayList<Song> songs;

    public SongAdapter(ArrayList<Song> songs) {
        this.songs = songs;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // Inflate song item layout
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_song, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Song song = songs.get(position);
        // Set song details to the view holder elements
        holder.songTitleTextView.setText(song.getName_song());
        holder.songArtistTextView.setText(song.getName_artist());
        // ... set other song details to view elements (optional: image, duration)

        // Optional: Set click listener for each song item
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle song item click event (play song, open details, etc.)
                Toast.makeText(v.getContext(), "Song clicked: " + song.getName_song(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return songs.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView songTitleTextView;
        TextView songArtistTextView;
        ImageView songAvatar;
        ImageButton songOptionsButton;

        public ViewHolder(View itemView) {
            super(itemView);
            songOptionsButton = itemView.findViewById(R.id.option_btn);
            songAvatar = itemView.findViewById(R.id.thumbnail_img);
            songTitleTextView = itemView.findViewById(R.id.tv_nameSong);
            songArtistTextView = itemView.findViewById(R.id.tv_nameArtist);
            // ... initialize other view elements
        }
    }
}
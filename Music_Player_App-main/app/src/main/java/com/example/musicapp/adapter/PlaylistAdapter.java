package com.example.musicapp.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.musicapp.MainActivity;
import com.example.musicapp.R;
import com.example.musicapp.fragment.PlaylistFragment;
import com.example.musicapp.model.Playlist;

import java.util.ArrayList;


public class PlaylistAdapter extends RecyclerView.Adapter<PlaylistAdapter.ViewHolder> {

    private ArrayList<Playlist> playlists;
    private OnItemClickListener listener;



    public PlaylistAdapter(ArrayList<Playlist> playlists, OnItemClickListener listener) {
        this.playlists = playlists;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_playlist, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Playlist playlist = playlists.get(position);
        holder.bind(playlist, listener);
    }

    @Override
    public int getItemCount() {
        return playlists.size();
    }

    public interface OnItemClickListener {
        void onItemClick(Playlist playlist);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        private TextView playlistName;
        private ImageButton backButton;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            playlistName = itemView.findViewById(R.id.playlist_name);
        }

        public void bind(final Playlist playlist, final OnItemClickListener listener) {
            playlistName.setText(playlist.getName());
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onItemClick(playlist);
                }
            });

        }

    }
}
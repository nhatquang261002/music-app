package com.example.musicapp.adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.musicapp.PlayerActivity;
import com.example.musicapp.R;
import com.example.musicapp.model.Song;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class SongAdapter extends RecyclerView.Adapter<SongAdapter.ViewHolder> {

    private ArrayList<Song> songs;
    private ArrayList<String> songIds;
    private Context context;

    public SongAdapter(ArrayList<Song> songs, Context context) {
        this.songs = songs;
        this.context = context;
    }





    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_song, parent, false);
        return new ViewHolder(view);
    }


    public void setSongIds(ArrayList<String> songIds) {
        this.songIds = songIds;
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
        bundle.putStringArrayList("songIds", songIds);
        intent.putExtras(bundle);
        intent.putExtra("position", position);
        context.startActivity(intent);
    }
}

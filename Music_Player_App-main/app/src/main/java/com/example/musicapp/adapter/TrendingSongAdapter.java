package com.example.musicapp.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
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

public class TrendingSongAdapter extends RecyclerView.Adapter<TrendingSongAdapter.SongViewHolder> {
    private Context mContext;
    private ArrayList<Song> mList;
    private PlaylistDAO mPlaylistDAO;

    public TrendingSongAdapter(Context context, ArrayList<Song> list, PlaylistDAO playlistDAO) {
        this.mContext = context;
        this.mList = list;
        this.mPlaylistDAO = playlistDAO;
    }

    public void setData(Context mContext, ArrayList<Song> list){
        this.mContext = mContext;
        this.mList = list;
        notifyDataSetChanged();
    }

    private Playlist getPlaylistByName(String playlistName) {
        List<Playlist> playlists = mPlaylistDAO.getAllPlaylists();
        for (Playlist playlist : playlists) {
            if (playlist.getName().equals(playlistName)) {
                return playlist;
            }
        }
        return null;
    }

    private void saveToPlaylist(final Song song, final String playlistName) {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                final Playlist playlist = getPlaylistByName(playlistName);

                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        if (playlist != null) {
                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    mPlaylistDAO.addSongIdToPlaylist(playlist.getId(), song.getId());
                                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                                        @Override
                                        public void run() {
                                            notifyDataSetChanged();
                                            String message = "Song '" + song.getName_song() + "' saved to playlist '" + playlistName + "'";
                                            Toast.makeText(mContext, message, Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }
                            }).start();
                        } else {
                            Toast.makeText(mContext, "Playlist not found", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });

        thread.start();
    }

    @NonNull
    @Override
    public SongViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_trending_song, parent, false);
        return new SongViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SongViewHolder holder, int position) {
        final Song song = mList.get(position);
        if (song == null) {
            return;
        }

        holder.tv_index.setText(song.getPosition());
        if (position == 0) {
            holder.tv_index.setTextColor(Color.parseColor("#3468f0"));
        } else if (position == 1) {
            holder.tv_index.setTextColor(Color.parseColor("#15c5a1"));
        } else if (position == 2) {
            holder.tv_index.setTextColor(Color.parseColor("#e57437"));
        }

        holder.tv_nameSong.setText(song.getName_song());
        holder.tv_nameArtist.setText(song.getName_artist());
        Picasso.with(mContext).load(song.getThumbnail()).into(holder.thumbnail_img);
        holder.item_song.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickGoToPlayerActivity(position);
            }
        });

        holder.option_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPlaylistDialog(song);
            }
        });
    }

    private void showPlaylistDialog(final Song song) {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                final List<Playlist> playlists = mPlaylistDAO.getAllPlaylists();

                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        final List<String> playlistNames = new ArrayList<>();
                        final boolean[] checkedPlaylists = new boolean[playlists.size()];

                        for (int i = 0; i < playlists.size(); i++) {
                            Playlist playlist = playlists.get(i);
                            if (!playlist.getSongIds().contains(song.getId())) {
                                playlistNames.add(playlist.getName());
                                checkedPlaylists[playlistNames.size() - 1] = false;
                            }
                        }

                        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                        builder.setTitle("Save To Playlist");

                        builder.setMultiChoiceItems(playlistNames.toArray(new String[0]), checkedPlaylists, new DialogInterface.OnMultiChoiceClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int position, boolean isChecked) {
                                checkedPlaylists[position] = isChecked;
                            }
                        });

                        builder.setPositiveButton("Submit", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int which) {
                                for (int i = 0; i < checkedPlaylists.length; i++) {
                                    if (checkedPlaylists[i]) {
                                        saveToPlaylist(song, playlistNames.get(i));
                                    }
                                }
                            }
                        });

                        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int which) {
                            }
                        });

                        builder.show();
                    }
                });
            }
        });

        thread.start();
    }

    private void onClickGoToPlayerActivity(int position) {
        Intent intent = new Intent(mContext, PlayerActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable("songsList", mList);
        intent.putExtras(bundle);
        intent.putExtra("position", position);
        mContext.startActivity(intent);
    }

    @Override
    public int getItemCount() {
        if (mList != null) {
            return mList.size();
        }
        return 0;
    }

    public class SongViewHolder extends RecyclerView.ViewHolder {
        ImageView thumbnail_img;
        TextView tv_index, tv_nameSong, tv_nameArtist;
        ImageView option_btn;
        RelativeLayout item_song;

        public SongViewHolder(@NonNull View itemView) {
            super(itemView);
            thumbnail_img = itemView.findViewById(R.id.thumbnail_img);
            tv_index = itemView.findViewById(R.id.tv_index);
            tv_nameSong = itemView.findViewById(R.id.tv_nameSong);
            tv_nameArtist = itemView.findViewById(R.id.tv_nameArtist);
            option_btn = itemView.findViewById(R.id.option_btn);
            item_song = itemView.findViewById(R.id.item_trending_song);
        }
    }
}

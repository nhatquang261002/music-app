package com.example.musicapp.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.musicapp.PlayerActivity;
import com.example.musicapp.R;
import com.example.musicapp.model.Song;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class TrendingSongAdapter extends RecyclerView.Adapter<TrendingSongAdapter.SongViewHolder> {
    Context mContext;
    public static ArrayList<Song> mList;    // Truyền đến PlayerActivity


    public void setData(Context mContext, ArrayList<Song> list){
        this.mContext = mContext;
        this.mList = list;
        /*
        Nó thông báo cho AdapterView rằng dữ liệu đã thay đổi và AdapterView
        cần cập nhật giao diện người dùng để phản ánh các thay đổi mới.
         */
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public SongViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_trending_song,
                parent, false);
        return new SongViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull SongViewHolder holder, int position) {
        // Hàm xét dữ liệu lên
        final Song song = mList.get(position);
        if (song == null) {
            return;
        }

        holder.tv_index.setText(song.getPosition());
        if (position == 0){
            holder.tv_index.setTextColor(Color.parseColor("#3468f0"));
        } else if (position == 1) {
            holder.tv_index.setTextColor(Color.parseColor("#15c5a1"));
        } else if (position == 2) {
            holder.tv_index.setTextColor(Color.parseColor("#e57437"));
        }

        holder.tv_nameSong.setText(song.getName_song());
        holder.tv_nameArtist.setText(song.getName_artist());
        Picasso.with(mContext).load(song.getThumbnail()).into(holder.thumbnail_img);// do data vao img_icon
        holder.item_song.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickGoToPlayerActivity(position);
            }
        });
    }
    private void onClickGoToPlayerActivity(int position) {
        Intent intent = new Intent(mContext, PlayerActivity.class);
        Bundle bundle = new Bundle();
        bundle.putInt("position", position);
        intent.putExtras(bundle);
        mContext.startActivity(intent);
    }

    @Override
    public int getItemCount() {
        if (mList != null) {
            return mList.size();
        }
        return 0;
    }
    public class SongViewHolder extends RecyclerView.ViewHolder{
        ImageView thumbnail_img;
        TextView tv_index, tv_nameSong, tv_nameArtist;
        ImageView option_btn;
        RelativeLayout item_song;

        public SongViewHolder(@NonNull View itemView) {
            super(itemView);
            thumbnail_img =itemView.findViewById(R.id.thumbnail_img);
            tv_index = itemView.findViewById(R.id.tv_index);
            tv_nameSong = itemView.findViewById(R.id.tv_nameSong);
            tv_nameArtist = itemView.findViewById(R.id.tv_nameArtist);
            option_btn = itemView.findViewById(R.id.option_btn);
            item_song = itemView.findViewById(R.id.item_trending_song);
        }
    }
}

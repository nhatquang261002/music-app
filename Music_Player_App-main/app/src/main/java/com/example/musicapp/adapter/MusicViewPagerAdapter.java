package com.example.musicapp.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.musicapp.fragment.SongFragment;
import com.example.musicapp.fragment.SongInforFragment;
import com.example.musicapp.fragment.SongLyricFragment;

public class MusicViewPagerAdapter extends FragmentStateAdapter {

    public MusicViewPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        if (position == 0) {
            return new SongInforFragment();
        } else if (position == 2) {
            return new SongLyricFragment();
        }
        return new SongFragment();
    }

    @Override
    public int getItemCount() {
        return 3;
    }
}

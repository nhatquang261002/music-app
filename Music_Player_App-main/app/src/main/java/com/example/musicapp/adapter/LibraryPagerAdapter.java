package com.example.musicapp.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.musicapp.fragment.AlbumFragment;
import com.example.musicapp.fragment.LibraryFragment;
import com.example.musicapp.fragment.PlaylistFragment;

import java.util.HashMap;
import java.util.Map;

public class LibraryPagerAdapter extends FragmentStateAdapter {

    private final Map<Integer, Fragment> fragmentMap = new HashMap<>();

    public LibraryPagerAdapter(@NonNull LibraryFragment fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                PlaylistFragment playlistFragment = new PlaylistFragment();
                fragmentMap.put(position, playlistFragment);
                return playlistFragment;
            case 1:
                return new AlbumFragment();
            default:
                return new PlaylistFragment();
        }
    }

    @Override
    public int getItemCount() {
        return 2;
    }

    public Fragment getFragment(int position) {
        return fragmentMap.get(position);
    }
}

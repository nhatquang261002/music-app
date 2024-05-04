package com.example.musicapp.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.musicapp.fragment.SearchFragment;
import com.example.musicapp.fragment.search.SearchAlbumFragment;
import com.example.musicapp.fragment.search.SearchArtistFragment;
import com.example.musicapp.fragment.search.SearchPhoneFragment;
import com.example.musicapp.fragment.search.SearchSongFragment;

public class SearchViewPagerAdapter extends FragmentStateAdapter {
    public SearchViewPagerAdapter(@NonNull SearchFragment fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position){
            case 0:
                return new SearchPhoneFragment();
            case 1:
                return new SearchSongFragment();
            case 2:
                return new SearchAlbumFragment();
            case 3:
                return new SearchArtistFragment();
            default:
                return new SearchPhoneFragment();
        }
    }

    @Override
    public int getItemCount() {
        return 4;
    }
}

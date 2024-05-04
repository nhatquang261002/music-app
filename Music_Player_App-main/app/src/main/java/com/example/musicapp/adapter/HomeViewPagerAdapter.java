package com.example.musicapp.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.musicapp.fragment.AccountFragment;
import com.example.musicapp.fragment.DiscoveryFragment;
import com.example.musicapp.fragment.LibraryFragment;
import com.example.musicapp.fragment.TrendingFragment;

public class HomeViewPagerAdapter extends FragmentStateAdapter {


    public HomeViewPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position){
            case 0:
                return new LibraryFragment();
            case 1:
                return new DiscoveryFragment();
            case 2:
                return new TrendingFragment();
            case 3:
                return new AccountFragment();
            default:
                return new LibraryFragment();
        }
    }

    @Override
    public int getItemCount() { // Trả về số lượng tab (4 tab)
        return 4;
    }
}

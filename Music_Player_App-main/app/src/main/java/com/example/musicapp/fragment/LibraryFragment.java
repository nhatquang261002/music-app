package com.example.musicapp.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import com.example.musicapp.R;
import com.example.musicapp.adapter.LibraryPagerAdapter;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

public class LibraryFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_library, container, false);

        ViewPager2 viewPager2 = view.findViewById(R.id.view_pager2);
        viewPager2.setAdapter(new LibraryPagerAdapter(this));

        TabLayout tabLayout = view.findViewById(R.id.tab_layout);

        // Link the TabLayout and the ViewPager2 together
        new TabLayoutMediator(tabLayout, viewPager2,
                (tab, position) -> {
                    switch (position) {
                        case 0:
                            tab.setText("Playlists");
                            break;
                        case 1:
                            tab.setText("Albums");
                            break;
                    }
                }
        ).attach();

        getChildFragmentManager().beginTransaction()
                .replace(R.id.fragment_playlist, new PlaylistFragment(), "PLAYLIST_FRAGMENT_TAG")
                .commit();

        return view;
    }
}


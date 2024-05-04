package com.example.musicapp;

import android.os.Bundle;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.musicapp.adapter.MusicViewPagerAdapter;
import com.example.musicapp.databinding.ActivityTestBinding;

public class TestActivity extends AppCompatActivity {

    private ActivityTestBinding mActivityTestBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //EdgeToEdge.enable(this);



        mActivityTestBinding = ActivityTestBinding.inflate(getLayoutInflater());
        setContentView(mActivityTestBinding.getRoot());

        //initToolbar();
        initUI();
    }


    private void initUI() {
        MusicViewPagerAdapter musicViewPagerAdapter = new MusicViewPagerAdapter(this);
        mActivityTestBinding.viewpager2.setAdapter(musicViewPagerAdapter);
        mActivityTestBinding.indicator3.setViewPager(mActivityTestBinding.viewpager2);
        mActivityTestBinding.viewpager2.setCurrentItem(1);
    }
}
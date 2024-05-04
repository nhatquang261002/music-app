package com.example.musicapp.fragment;

import android.app.Activity;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.musicapp.R;
import com.example.musicapp.adapter.SearchViewPagerAdapter;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;


public class SearchFragment extends Fragment {
    public static String TAG = SearchFragment.class.getName();
    ImageButton button;
    EditText edt_search;
    ViewPager2 viewPager2;
    TabLayout tabLayout;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search, container, false);
        //fragmentSearchBinding = FragmentSearchBinding.inflate(inflater, container, false);

        button = view.findViewById(R.id.btn_back);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getFragmentManager() != null) {
                    getFragmentManager().popBackStack();
                }
            }
        });

        edt_search = view.findViewById(R.id.edt_search);
        edt_search.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH){
                    // logic search
                    handleSearch();
                }
                return false;
            }
        });

        viewPager2 = view.findViewById(R.id.viewpager_2);
        tabLayout = view.findViewById(R.id.tab_layout1);
        initUI(view);


        return view;
    }

    private void handleSearch() {



        hideSoftKeyBoard();
    }
    public void hideSoftKeyBoard() {
        try {
            InputMethodManager inputMethodManager = (InputMethodManager)
                    getActivity().getSystemService(Activity.INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), 0);

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    private void initUI(View view) {
        SearchViewPagerAdapter searchViewPagerAdapter = new SearchViewPagerAdapter(this);
        viewPager2.setAdapter(searchViewPagerAdapter);
        new TabLayoutMediator(tabLayout, viewPager2, new TabLayoutMediator.TabConfigurationStrategy() {
            @Override
            public void onConfigureTab(@NonNull TabLayout.Tab tab, int i) {
                switch (i){
                    case 0:
                        tab.setIcon(R.drawable.ic_smartphone);
                        break;
                    case 1:
                        tab.setText("Bài hát");
                        break;
                    case 2:
                        tab.setText("Album");
                        break;
                    case 3:
                        tab.setText("Nghệ sĩ");
                        break;
                }

            }
        }).attach();


    }


}
package com.simple.chris.pebble;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.fragment.app.FragmentTransaction;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;

import com.gauravk.bubblenavigation.BubbleNavigationConstraintView;
import com.gauravk.bubblenavigation.listener.BubbleNavigationChangeListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;

import me.ibrahimsn.lib.OnItemSelectedListener;
import me.ibrahimsn.lib.SmoothBottomBar;

public class Main extends AppCompatActivity{

    /*int currentFragment = R.id.home;

    FragmentTransaction ftInRight;
    FragmentTransaction ftInLeft;

    private ArrayList<HashMap<String, String>> featured;
    private ArrayList<HashMap<String, String>> all;

    HomeFragment hf;
    //BrowseFragment bf;
    SearchFragment sf;

    SmoothBottomBar smoothBottomBar;

    Bundle allBundle;
    Bundle featuredBundle;

    private static final int NUM_PAGES = 4;
    private CustomViewPager mPager;
    private PagerAdapter pagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        UIElements.INSTANCE.setTheme(Main.this);
        setContentView(R.layout.activity_main);
        get();

        *//*SwipeRefreshLayout swipeRefreshLayout = findViewById(R.id.swipeToRefresh);
        swipeRefreshLayout.setOnRefreshListener(() -> {
            Intent connect = new Intent(Main.this, ActivityConnecting.class);
            startActivity(connect);
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            finish();
        });*//*


        mPager = findViewById(R.id.viewPager);
        pagerAdapter = new ScreenSlidePagerAdapter(getSupportFragmentManager());
        mPager.setAdapter(pagerAdapter);
        mPager.setOffscreenPageLimit(4);
        mPager.setScrollDurationFactor(2.5);
        mPager.setPagingEnabled(true);


        BubbleNavigationConstraintView bottomBar = findViewById(R.id.bottomBar);
        bottomBar.setNavigationChangeListener(new BubbleNavigationChangeListener() {
            @Override
            public void onNavigationChanged(View view, int position) {

            }
        });

        *//*BubbleNavigationConstraintView bubbleNavigation = findViewById(R.id.navigation_header_container);*//*
        *//*bubbleNavigation.setNavigationChangeListener((view, position) -> {
            ftFromRight = getSupportFragmentManager().beginTransaction();
            ftFromRight.setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_right);
            ftFromLeft = getSupportFragmentManager().beginTransaction();
            ftFromLeft.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left);
            Fragment selectedFragment = null;

            switch (bubbleNavigation.getCurrentActiveItemPosition()){
                case 0:
                    selectedFragment = hf;
                    break;
                case 1:
                    selectedFragment = bf;
                    Log.e("Info", bf.toString());
                    //selectedFragment = new BrowseFragment();
                    break;
                case 2:
                    selectedFragment = sf;
                    //selectedFragment = new SearchFragment();
                    break;
                default:


            }

            switch (currentFragment){
                case 0:
                    ftFromLeft.replace(R.id.fragmentHolder,
                            selectedFragment).commit();
                    currentFragment = bubbleNavigation.getCurrentActiveItemPosition();
                    break;

                case 1:
                    if (currentFragment < bubbleNavigation.getCurrentActiveItemPosition()){
                        ftFromLeft.replace(R.id.fragmentHolder,
                                selectedFragment).commit();
                        currentFragment = bubbleNavigation.getCurrentActiveItemPosition();
                    } else {
                        ftFromRight.replace(R.id.fragmentHolder,
                                selectedFragment).commit();
                    }
                    currentFragment = bubbleNavigation.getCurrentActiveItemPosition();
                    break;

                case 2:
                    ftFromRight.replace(R.id.fragmentHolder,
                            selectedFragment).commit();
                    currentFragment = bubbleNavigation.getCurrentActiveItemPosition();
                    break;
            }
        });*//*
    }

    private BottomNavigationView.OnNavigationItemSelectedListener navigationItemSelectedListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                    ftInLeft = getSupportFragmentManager().beginTransaction();
                    ftInLeft.setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_right);
                    ftInRight = getSupportFragmentManager().beginTransaction();
                    ftInRight.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left);
                    Fragment selectedFragment = null;

                    switch ((menuItem.getItemId())){
                        case R.id.home:
                            mPager.setCurrentItem(0);
                            break;
                        case R.id.browse:
                            mPager.setCurrentItem(1);
                            break;
                        case R.id.search:
                            mPager.setCurrentItem(2);
                            break;
                        case R.id.settings:
                            mPager.setCurrentItem(3);
                            break;
                        default:


                    }
                    return true;
                }
            };

    public void get(){
        featured = (ArrayList<HashMap<String, String>>) getIntent().getSerializableExtra("featured");
        all = (ArrayList<HashMap<String, String>>) getIntent().getSerializableExtra("items");
        hf = new HomeFragment();
        bf = new BrowseFragment();
        sf = new SearchFragment();

        allBundle = new Bundle();
        featuredBundle = new Bundle();

        allBundle.putSerializable("items", all);
        featuredBundle.putSerializable("featured", featured);

        hf.setArguments(featuredBundle);
        bf.setArguments(allBundle);

        *//*FragmentTransaction mt = getSupportFragmentManager().beginTransaction();
        mt.replace(R.id.fragmentHolder, hf);
        mt.commit();*//*

    }

    *//*public void getAndSendHome(){
        Bundle featuredBundle = new Bundle();
        featuredBundle.putSerializable("featured", featured);
        hf.setArguments(featuredBundle);

        FragmentTransaction mt = getSupportFragmentManager().beginTransaction();
        mt.replace(R.id.fragmentHolder, hf);
        mt.commit();
    }*//*


    private class ScreenSlidePagerAdapter extends FragmentStatePagerAdapter {
        public ScreenSlidePagerAdapter(FragmentManager fragmentManager) {
            super(fragmentManager);
        }

        @NonNull
        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return hf;
                case 1:
                    return bf;
                case 2:
                    return sf;
                case 3:
                    return new SearchFragment();
                default:
                    Toast.makeText(Main.this, "Too far", Toast.LENGTH_SHORT).show();
            }
            return null;
        }

        @Override
        public int getCount() {
            return NUM_PAGES;
        }
    }*/
}

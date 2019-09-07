package com.simple.chris.pebble;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ScrollView;

import java.util.ArrayList;
import java.util.HashMap;

public class TestLayout extends AppCompatActivity implements AdapterView.OnItemClickListener{

    private ArrayList<HashMap<String, String>> featured;
    private ArrayList<HashMap<String, String>> all;

    boolean isAnimating = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Values.darkMode){
            setTheme(R.style.ThemeDark);
        } else {
            setTheme(R.style.ThemeLight);
        }
        setContentView(R.layout.activity_browse_screen);

        featured = (ArrayList<HashMap<String, String>>) getIntent().getSerializableExtra("featured");
        all = (ArrayList<HashMap<String, String>>) getIntent().getSerializableExtra("items");
        Log.e("TAG", ""+featured);
        RecyclerView featuredRecycler = findViewById(R.id.recyclerView);
        FeaturedAdapter featuredAdapter = new FeaturedAdapter(featured, TestLayout.this);
        LinearLayoutManager layoutManager = new LinearLayoutManager(TestLayout.this, LinearLayoutManager.HORIZONTAL, false);
        featuredRecycler.setLayoutManager(layoutManager);
        featuredRecycler.setAdapter(featuredAdapter);
        ExpandedHeightScrollView allGrid = findViewById(R.id.gv_items);
        GridAdapterUserFriendly UIAdapter = new GridAdapterUserFriendly(TestLayout.this, all);
        allGrid.setAdapter(UIAdapter);
        allGrid.setOnItemClickListener(this);



        ImageView menuIcon = findViewById(R.id.menuIcon);
        ImageView background = findViewById(R.id.background);
        menuIcon.setOnClickListener(view -> {
            Log.e("TAG", "Here");
            if (Values.darkMode){
                Values.darkMode = false;
                setTheme(R.style.ThemeLight);
                TestLayout.this.recreate();
            } else {
                Values.darkMode = true;
                setTheme(R.style.ThemeDark);
                TestLayout.this.recreate();
            }
        });

        ScrollView master = findViewById(R.id.master);
        master.setOnScrollChangeListener(new View.OnScrollChangeListener() {
            @Override
            public void onScrollChange(View view, int i, int i1, int i2, int i3) {
                Log.e("INFO", ""+master.getScrollY());
                if (master.getScrollY() >= 300) {
                    if (!isAnimating) {
                        isAnimating = true;
                        UIAnimations.imageViewObjectAnimator(menuIcon, "alpha", 0, 100, 0, new LinearInterpolator());
                        boolean change = new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                isAnimating = false;
                            }
                        }, 100);

                    }
                } else if (master.getScrollY() < 300) {
                    if (!isAnimating) {
                        isAnimating = true;
                        UIAnimations.imageViewObjectAnimator(menuIcon, "alpha", 1, 200, 0, new LinearInterpolator());
                        boolean change = new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                isAnimating = false;
                            }
                        }, 200);
                    }
                }
            }
        });
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Intent details = new Intent(TestLayout.this, GradientDetails.class);
        HashMap<String, String> info = (HashMap<String, String>) parent.getItemAtPosition(position);

        String gradientName = info.get("backgroundName");
        String startColour = info.get("leftColour");
        String endColour = info.get("rightColour");
        String description = info.get("description");

        details.putExtra("gradientName", gradientName);
        details.putExtra("startColour", startColour);
        details.putExtra("endColour", endColour);
        details.putExtra("description", description);

        ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(TestLayout.this, view.findViewById(R.id.gradient), gradientName);
        startActivity(details, options.toBundle());
    }
}

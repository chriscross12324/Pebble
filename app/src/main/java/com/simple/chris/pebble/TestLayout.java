package com.simple.chris.pebble;

import android.animation.ValueAnimator;
import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.LinearInterpolator;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import java.util.ArrayList;
import java.util.HashMap;

public class TestLayout extends AppCompatActivity implements AdapterView.OnItemClickListener {

    boolean isAnimating = false;
    SwipeRefreshLayout refresh;
    ConstraintLayout main, optionsMenu, themeHolder, doneButton;
    ImageView screenDim;
    TextView themeInformation;
    EditText search;
    private ArrayList<HashMap<String, String>> featured;
    private ArrayList<HashMap<String, String>> all;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Values.darkMode) {
            setTheme(R.style.ThemeDark);
        } else {
            setTheme(R.style.ThemeLight);
        }
        setContentView(R.layout.activity_browse_screen);
        Values.saveValues(TestLayout.this);

        //ConstraintLayout
        main = findViewById(R.id.main);
        themeHolder = findViewById(R.id.themeHolder);
        doneButton = findViewById(R.id.doneButton);

        //ImageView
        screenDim = findViewById(R.id.screenDim);

        //TextView
        themeInformation = findViewById(R.id.themeInformation);

        //EditText
        search = findViewById(R.id.search);


        refresh = findViewById(R.id.refresh);
        featured = (ArrayList<HashMap<String, String>>) getIntent().getSerializableExtra("featured");
        all = (ArrayList<HashMap<String, String>>) getIntent().getSerializableExtra("items");
        Log.e("TAG", "" + featured);
        RecyclerView featuredRecycler = findViewById(R.id.recyclerView);
        FeaturedAdapter featuredAdapter = new FeaturedAdapter(featured, TestLayout.this);
        LinearLayoutManager layoutManager = new LinearLayoutManager(TestLayout.this, LinearLayoutManager.HORIZONTAL, false);
        featuredRecycler.setLayoutManager(layoutManager);
        featuredRecycler.setAdapter(featuredAdapter);
        featuredRecycler.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
        featuredRecycler.addOnItemTouchListener(
                new RecyclerItemClickListener(TestLayout.this, featuredRecycler, new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {

                    }

                    @Override
                    public void onLongItemClick(View view, int position) {

                    }
                })
        );
        ExpandedHeightScrollView allGrid = findViewById(R.id.gv_items);
        GridAdapterUserFriendly UIAdapter = new GridAdapterUserFriendly(TestLayout.this, all);
        allGrid.setAdapter(UIAdapter);
        allGrid.setOnItemClickListener(this);

        if (!Values.autoTheme) {
            if (Values.darkMode) {
                themeInformation.setText("Current theme: Dark");
                refresh.setProgressBackgroundColorSchemeColor(ContextCompat.getColor(this, R.color.colorDarkThemeForeground));
                refresh.setColorSchemeColors(ContextCompat.getColor(this, R.color.colorLightThemeForeground));
            } else {
                themeInformation.setText("Current theme: Light");
                refresh.setProgressBackgroundColorSchemeColor(ContextCompat.getColor(this, R.color.colorLightThemeForeground));
                refresh.setColorSchemeColors(ContextCompat.getColor(this, R.color.colorDarkThemeForeground));
            }
        } else {
            if (Values.darkMode) {
                themeInformation.setText("Current theme: Dark (Auto)");
                refresh.setProgressBackgroundColorSchemeColor(ContextCompat.getColor(this, R.color.colorDarkThemeForeground));
                refresh.setColorSchemeColors(ContextCompat.getColor(this, R.color.colorLightThemeForeground));
            } else {
                themeInformation.setText("Current theme: Light (Auto)");
                refresh.setProgressBackgroundColorSchemeColor(ContextCompat.getColor(this, R.color.colorLightThemeForeground));
                refresh.setColorSchemeColors(ContextCompat.getColor(this, R.color.colorDarkThemeForeground));
            }
        }

        refresh.setOnRefreshListener(() -> {
            Intent GL = new Intent(TestLayout.this, ActivityConnecting.class);
            startActivity(GL);
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            finish();
        });

        ImageView menuIcon = findViewById(R.id.menuIcon);
        menuIcon.setOnClickListener(view -> {
            screenDim.setVisibility(View.VISIBLE);
            UIAnimations.imageViewObjectAnimator(screenDim, "alpha", 1, 300, 0, new DecelerateInterpolator());
            themeHolder.setVisibility(View.VISIBLE);
            doneButton.setVisibility(View.VISIBLE);
            UIAnimations.constraintLayoutObjectAnimator(themeHolder, "alpha", 1, 250, 10, new DecelerateInterpolator());
            UIAnimations.constraintLayoutObjectAnimator(doneButton, "alpha", 1, 250, 10, new DecelerateInterpolator());
            UIAnimations.constraintLayoutObjectAnimator(themeHolder, "translationY", 0, 700, 250, new DecelerateInterpolator(3));
            UIAnimations.constraintLayoutObjectAnimator(doneButton, "translationY", 0, 700, 300, new DecelerateInterpolator(3));
        });

        doneButton.setOnClickListener(view -> {
            UIAnimations.constraintLayoutObjectAnimator(themeHolder, "translationY",
                    Math.round((90 * getResources().getDisplayMetrics().density) + themeHolder.getHeight()), 250,
                    50, new DecelerateInterpolator());
            UIAnimations.constraintLayoutObjectAnimator(doneButton, "translationY",
                    Math.round((90 * getResources().getDisplayMetrics().density) + themeHolder.getHeight()), 250,
                    0, new DecelerateInterpolator());
            UIAnimations.constraintLayoutAlpha(themeHolder, 0, 300);
            UIAnimations.constraintLayoutAlpha(doneButton, 0, 300);
            UIAnimations.imageViewObjectAnimator(screenDim, "alpha", 0, 300, 150, new DecelerateInterpolator());
        });
        themeHolder.setOnClickListener(view -> {
            if (Values.darkMode) {
                Values.darkMode = false;
                Values.optionsOpen = true;
                //TestLayout.this.recreate();
                Intent restart = new Intent(TestLayout.this, TestLayout.class);
                restart.putExtra("featured", featured);
                restart.putExtra("items", all);
                startActivity(restart);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                finish();
            } else {
                Values.darkMode = true;
                Values.optionsOpen = true;
                //TestLayout.this.recreate();
                Intent restart = new Intent(TestLayout.this, TestLayout.class);
                restart.putExtra("featured", featured);
                restart.putExtra("items", all);
                startActivity(restart);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                finish();
            }
        });

        themeHolder.post(() -> {
            themeHolder.setTranslationY((90 * getResources().getDisplayMetrics().density) + themeHolder.getHeight());
            doneButton.setTranslationY((74 * getResources().getDisplayMetrics().density) + themeHolder.getHeight());
            /*if (Values.optionsOpen) {
                Values.optionsOpen = false;
                themeHolder.setTranslationY(0);
                themeHolder.setAlpha(1);
                doneButton.setTranslationY(0);
                doneButton.setAlpha(1);
                screenDim.setImageAlpha(1);
            }*/
        });

        /*createOption.setOnClickListener(view -> {
            Toast.makeText(this, "Come back later", Toast.LENGTH_SHORT).show();
        });*/

        ScrollView master = findViewById(R.id.master);
        master.setOnScrollChangeListener((view, i, i1, i2, i3) -> {
            Log.e("INFO", "" + master.getScrollY());
            if (master.getScrollY() >= 300) {
                if (!isAnimating) {
                    isAnimating = true;
                    UIAnimations.imageViewObjectAnimator(menuIcon, "alpha", 0, 100, 0, new LinearInterpolator());
                    boolean change = new Handler().postDelayed(() ->
                            isAnimating = false, 100);

                }
            } else if (master.getScrollY() < 300) {
                if (!isAnimating) {
                    isAnimating = true;
                    UIAnimations.imageViewObjectAnimator(menuIcon, "alpha", 1, 200, 0, new LinearInterpolator());
                    boolean change = new Handler().postDelayed(() ->
                            isAnimating = false, 200);
                }
            }
        });

        search.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (b) {
                    ValueAnimator valueAnimator = new ValueAnimator();
                } else {

                }
            }
        });

    }

    /*public boolean onMenuItemClick(MenuItem item) {
        switch (item.getItemId()) {
            case
        }
    }*/

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



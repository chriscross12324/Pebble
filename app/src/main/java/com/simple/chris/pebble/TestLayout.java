package com.simple.chris.pebble;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.LinearInterpolator;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import java.util.ArrayList;
import java.util.HashMap;

import eightbitlab.com.blurview.BlurView;
import eightbitlab.com.blurview.RenderScriptBlur;

public class TestLayout extends AppCompatActivity implements AdapterView.OnItemClickListener {

    boolean isAnimating = false;
    SwipeRefreshLayout refresh;
    ConstraintLayout optionsMenu;
    LinearLayout themeOption, createOption;
    BlurView optionsBlur;
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

        optionsMenu = findViewById(R.id.optionsMenu);
        optionsBlur = findViewById(R.id.optionsBlur);
        themeOption = findViewById(R.id.themeOption);
        createOption = findViewById(R.id.createOption);

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

        if (Values.darkMode) {
            refresh.setProgressBackgroundColorSchemeColor(ContextCompat.getColor(this, R.color.colorDarkThemeForeground));
            refresh.setColorSchemeColors(ContextCompat.getColor(this, R.color.colorLightThemeForeground));
        } else {
            refresh.setProgressBackgroundColorSchemeColor(ContextCompat.getColor(this, R.color.colorLightThemeForeground));
            refresh.setColorSchemeColors(ContextCompat.getColor(this, R.color.colorDarkThemeForeground));
        }
        refresh.setOnRefreshListener(() -> {
            Intent GL = new Intent(TestLayout.this, ActivityConnecting.class);
            startActivity(GL);
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            finish();
        });

        if (Values.optionsOpen){
            Values.optionsOpen = false;
            optionsMenu.setVisibility(View.VISIBLE);
            optionsMenu.setAlpha(1);
        }
        ImageView menuIcon = findViewById(R.id.menuIcon);
        menuIcon.setOnClickListener(view -> {
            Log.e("TAG", "Here");
            optionsMenu.setVisibility(View.VISIBLE);
            UIAnimations.constraintLayoutObjectAnimator(optionsMenu, "alpha", 1, 300, 0, new DecelerateInterpolator());
        });
        optionsBlur.setOnClickListener(view -> {
            Log.e("Blur", "Hi");
            UIAnimations.constraintLayoutObjectAnimator(optionsMenu, "alpha", 0, 300, 0, new DecelerateInterpolator());
            Handler handler = new Handler();
            handler.postDelayed(() -> optionsMenu.setVisibility(View.GONE), 300);
        });
        themeOption.setOnClickListener(view -> {
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
        createOption.setOnClickListener(view -> {
            Toast.makeText(this, "Come back later", Toast.LENGTH_SHORT).show();
        });

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

        View decorView = getWindow().getDecorView();
        ViewGroup rootView = decorView.findViewById(android.R.id.content);
        Drawable windowBackground = decorView.getBackground();
        optionsBlur.setupWith(rootView)
                .setFrameClearDrawable(windowBackground)
                .setBlurAlgorithm(new RenderScriptBlur(TestLayout.this))
                .setBlurRadius(25f)
                .setHasFixedTransformationMatrix(false);
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

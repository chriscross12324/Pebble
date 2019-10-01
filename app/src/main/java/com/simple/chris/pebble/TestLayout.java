package com.simple.chris.pebble;

import android.animation.ValueAnimator;
import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.LinearInterpolator;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Switch;
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
    boolean isExpanded = false;
    SwipeRefreshLayout refresh;
    ScrollView master;
    ConstraintLayout main, optionsMenu, themeHolder, doneButton;
    LinearLayout searchButton;
    Button lightThemeButton, darkThemeButton, blackThemeButton;
    ImageView screenDim, searchIcon;
    TextView featuredTitle, allTitle, themeInformation;
    EditText search;
    private ArrayList<HashMap<String, String>> featured;
    private ArrayList<HashMap<String, String>> all;
    private ArrayList<HashMap<String, String>> searchResult = new ArrayList<HashMap<String, String>>();

    String searchField;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        switch (Values.theme) {
            case "light":
                setTheme(R.style.ThemeLight);
                break;
            case "dark":
                setTheme(R.style.ThemeDark);
                break;
            case "black":
                setTheme(R.style.ThemeBlack);
                break;
        }
        setContentView(R.layout.activity_browse_screen);
        Values.saveValues(TestLayout.this);

        //ScrollView
        master = findViewById(R.id.master);

        //ConstraintLayout
        main = findViewById(R.id.main);
        themeHolder = findViewById(R.id.themeHolder);
        doneButton = findViewById(R.id.doneButton);

        //LinearLayout
        searchButton = findViewById(R.id.searchButton);

        //Button
        lightThemeButton = findViewById(R.id.lightThemeButton);
        darkThemeButton = findViewById(R.id.darkThemeButton);
        blackThemeButton = findViewById(R.id.blackThemeButton);

        //ImageView
        screenDim = findViewById(R.id.screenDim);
        searchIcon = findViewById(R.id.searchIcon);

        //TextView
        featuredTitle = findViewById(R.id.featuredTitle);
        allTitle = findViewById(R.id.allTitle);
        themeInformation = findViewById(R.id.themeInformation);

        //EditText
        search = findViewById(R.id.search);


        //ArrayList
        featured = (ArrayList<HashMap<String, String>>) getIntent().getSerializableExtra("featured");
        all = (ArrayList<HashMap<String, String>>) getIntent().getSerializableExtra("items");

        refresh = findViewById(R.id.refresh);

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

        loadThemes();

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

        lightThemeButton.setOnClickListener(view1 -> {
            if (!Values.theme.equals("light")){
                Values.theme = "light";
                loadThemes();
                master.smoothScrollTo(0, 0);
                refreshLayout();
            }
        });
        darkThemeButton.setOnClickListener(view1 -> {
            if (!Values.theme.equals("dark")){
                Values.theme = "dark";
                loadThemes();
                master.smoothScrollTo(0, 0);
                refreshLayout();
            }
        });
        blackThemeButton.setOnClickListener(view1 -> {
            if (!Values.theme.equals("black")){
                Values.theme = "black";
                loadThemes();
                master.smoothScrollTo(0, 0);
                refreshLayout();
            }
        });

        themeHolder.post(() -> {
            themeHolder.setTranslationY((90 * getResources().getDisplayMetrics().density) + themeHolder.getHeight());
            doneButton.setTranslationY((74 * getResources().getDisplayMetrics().density) + themeHolder.getHeight());
        });

        /*createOption.setOnClickListener(view -> {
            Toast.makeText(this, "Come back later", Toast.LENGTH_SHORT).show();
        });*/

        ScrollView master = findViewById(R.id.master);
        master.setOnScrollChangeListener((view, i, i1, i2, i3) -> {
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

        searchButton.setOnClickListener(view -> {
            searchResult.clear();
            searchField = search.getText().toString();
            //Log.e("Search", searchField);
            for (int count = 0; count < all.size(); count++){//int count = all.size() - 1; count >= 0; count--
                //Log.e("INFO", ""+all.get(count).get("backgroundName"));
                HashMap<String, String> searched = new HashMap<String, String>();
                if (all.get(count).get("backgroundName").toLowerCase().contains(searchField.toLowerCase()) && !searchField.equals("")){
                    Log.e("INFO", "HERE2");
                    searched.put("backgroundName", all.get(count).get("backgroundName"));
                    searched.put("leftColour", all.get(count).get("leftColour"));
                    searched.put("rightColour", all.get(count).get("rightColour"));
                    searched.put("description", all.get(count).get("description"));

                    searchResult.add(searched);
                    Log.e("Stuff", ""+all.get(count).get("backgroundName"));
                }

            }
            Log.e("Searched", ""+searchResult);

            if (!searchResult.isEmpty()){
                GridAdapterUserFriendly UIAdapterSearched = new GridAdapterUserFriendly(TestLayout.this, searchResult);
                featuredTitle.setVisibility(View.GONE);
                featuredRecycler.setVisibility(View.GONE);
                allTitle.setText("Results");
                allGrid.setAdapter(UIAdapterSearched);
                //searchIcon.setBackgroundResource(R.drawable.icon_add);
                //searchIcon.setRotation(90 );
            } else {
                featuredTitle.setVisibility(View.VISIBLE);
                featuredRecycler.setVisibility(View.VISIBLE);
                allTitle.setText("All");
                allGrid.setAdapter(UIAdapter);
            }
        });
        search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) { }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                //searchIcon.setBackgroundResource(R.drawable.icon_search);
                //searchIcon.setRotation(0);
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });

    }

    /*private ArrayList<String> searchInList(ArrayList<String> list, String searchString) {
        ArrayList<String> results = new ArrayList<>();
        for (int i = 0; i < all.size(); i++){
            if (all.get("backgroundName").equals(searchString))
        }
    }*/

    public void loadThemes(){
        switch (Values.theme) {
            case "light":
                themeInformation.setText("Current theme: Light");
                refresh.setProgressBackgroundColorSchemeColor(ContextCompat.getColor(this, R.color.colorLightThemeForeground));
                refresh.setColorSchemeColors(ContextCompat.getColor(this, R.color.colorDarkThemeForeground));
                lightThemeButton.setBackgroundResource(R.drawable.options_button_selected);
                darkThemeButton.setBackgroundResource(R.drawable.options_button);
                blackThemeButton.setBackgroundResource(R.drawable.options_button);
                lightThemeButton.setTextColor(getResources().getColor(android.R.color.white));
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P){
                    lightThemeButton.setOutlineSpotShadowColor(getResources().getColor(R.color.pebbleEnd));
                }
                break;
            case "dark":
                themeInformation.setText("Current theme: Dark");
                refresh.setProgressBackgroundColorSchemeColor(ContextCompat.getColor(this, R.color.colorDarkThemeForeground));
                refresh.setColorSchemeColors(ContextCompat.getColor(this, R.color.colorLightThemeForeground));
                lightThemeButton.setBackgroundResource(R.drawable.options_button);
                darkThemeButton.setBackgroundResource(R.drawable.options_button_selected);
                blackThemeButton.setBackgroundResource(R.drawable.options_button);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P){
                    darkThemeButton.setOutlineSpotShadowColor(getResources().getColor(R.color.pebbleEnd));
                }
                break;
            case "black":
                themeInformation.setText("Current theme: Black");
                refresh.setProgressBackgroundColorSchemeColor(ContextCompat.getColor(this, R.color.colorBlackThemeForeground));
                refresh.setColorSchemeColors(ContextCompat.getColor(this, R.color.colorLightThemeForeground));
                lightThemeButton.setBackgroundResource(R.drawable.options_button);
                darkThemeButton.setBackgroundResource(R.drawable.options_button);
                blackThemeButton.setBackgroundResource(R.drawable.options_button_selected);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P){
                    blackThemeButton.setOutlineSpotShadowColor(getResources().getColor(R.color.pebbleEnd));
                }
                break;
        }
    }

    public void refreshLayout() {
        UIAnimations.constraintLayoutObjectAnimator(themeHolder, "translationY",
                Math.round((90 * getResources().getDisplayMetrics().density) + themeHolder.getHeight()), 250,
                50, new DecelerateInterpolator());
        UIAnimations.constraintLayoutObjectAnimator(doneButton, "translationY",
                Math.round((90 * getResources().getDisplayMetrics().density) + themeHolder.getHeight()), 250,
                0, new DecelerateInterpolator());
        UIAnimations.constraintLayoutAlpha(themeHolder, 0, 300);
        UIAnimations.constraintLayoutAlpha(doneButton, 0, 300);
        UIAnimations.imageViewObjectAnimator(screenDim, "alpha", 0, 300, 150, new DecelerateInterpolator());
        Handler handler = new Handler();
        handler.postDelayed(() -> {
            Intent restart = new Intent(TestLayout.this, TestLayout.class);
            restart.putExtra("featured", featured);
            restart.putExtra("items", all);
            startActivity(restart);
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            finish();
        }, 300);
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

        Vibration.INSTANCE.hFeedack(TestLayout.this);
    }

}



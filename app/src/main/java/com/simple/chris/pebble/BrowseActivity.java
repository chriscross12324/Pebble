package com.simple.chris.pebble;

import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import java.util.ArrayList;
import java.util.HashMap;

public class BrowseActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {

    //Core
    Context context = BrowseActivity.this;

    SwipeRefreshLayout refresh;
    ScrollView master;
    ScrollableGridView allGrid;
    ConstraintLayout main, themeHolder, doneButton, notification;
    ImageView screenDim;
    TextView featuredTitle, allTitle, themeInformation, notificationText;
    /*EditText search;*/
    private ArrayList<HashMap<String, String>> featured;
    private ArrayList<HashMap<String, String>> all;
    private ArrayList<HashMap<String, String>> searchResult = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        UIElements.INSTANCE.setTheme(BrowseActivity.this);
        //setContentView(R.layout.activity_browse_screen);
        Values.saveValues(BrowseActivity.this);

        //ScrollView
        master = findViewById(R.id.master);

        //Grid
        allGrid = findViewById(R.id.gv_items);

        //ConstraintLayout
        main = findViewById(R.id.main);
        themeHolder = findViewById(R.id.themeHolder);
        doneButton = findViewById(R.id.doneButton);
        notification = findViewById(R.id.notification);

        //LinearLayout
        /*searchButton = findViewById(R.id.searchButton);*/
        CheckBox checkbox = findViewById(R.id.checkbox);
        checkbox.setOnCheckedChangeListener((compoundButton, b) -> {

        });

        //ImageView
        screenDim = findViewById(R.id.screenDim);
        /*searchIcon = findViewById(R.id.searchIcon);*/

        //TextView
        featuredTitle = findViewById(R.id.featuredTitle);
        allTitle = findViewById(R.id.allTitle);
        themeInformation = findViewById(R.id.themeInformation);
        notificationText = findViewById(R.id.notificationText);


        //EditText
        /*search = findViewById(R.id.search);*/

        //ArrayList
        featured = (ArrayList<HashMap<String, String>>) getIntent().getSerializableExtra("featured");
        all = (ArrayList<HashMap<String, String>>) getIntent().getSerializableExtra("items");

        refresh = findViewById(R.id.refresh);


        RecyclerView featuredRecycler = findViewById(R.id.recyclerView);
        FeaturedAdapter featuredAdapter = new FeaturedAdapter(featured, BrowseActivity.this);
        LinearLayoutManager layoutManager = new LinearLayoutManager(BrowseActivity.this, LinearLayoutManager.HORIZONTAL, false);
        featuredRecycler.setLayoutManager(layoutManager);
        featuredRecycler.setAdapter(featuredAdapter);
        featuredRecycler.setOnClickListener(view -> {

        });
        featuredRecycler.addOnItemTouchListener(
                new RecyclerItemClickListener(BrowseActivity.this, featuredRecycler, new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {

                    }

                    @Override
                    public void onLongItemClick(View view, int position) {

                    }
                })
        );
        GridAdapter UIAdapter = new GridAdapter(BrowseActivity.this, all);
        /*allGrid.setAdapter(UIAdapter);
        allGrid.setOnItemClickListener(this);
        allGridLayout();*/

        refresh.setOnRefreshListener(() -> {
            Intent GL = new Intent(BrowseActivity.this, ActivityConnecting.class);
            startActivity(GL);
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            finish();
        });

        /*searchButton.setOnClickListener(view -> {
            if (!searchChanged) {
                searchResult.clear();
                search.setText("");
                searchField = "";


                searchIcon.animate().rotation(0).start();

                searchIcon.setImageResource(R.drawable.icon_search);
                featuredTitle.setVisibility(View.VISIBLE);
                featuredRecycler.setVisibility(View.VISIBLE);
                allTitle.setText("All");
                allGrid.setAdapter(UIAdapter);


                searchChanged = true;
            } else {
                searchChanged = false;
                searchResult.clear();
                searchField = search.getText().toString();
                //Log.e("Search", searchField);
                for (int count = 0; count < all.size(); count++) {//int count = all.size() - 1; count >= 0; count--
                    //Log.e("INFO", ""+all.get(count).get("backgroundName"));
                    HashMap<String, String> searched = new HashMap<String, String>();
                    if (all.get(count).get("backgroundName").replace(" ", "").toLowerCase()
                            .contains(searchField.replace(" ", "").toLowerCase())
                            && !searchField.equals("")) {
                        searched.put("backgroundName", all.get(count).get("backgroundName"));
                        searched.put("startColour", all.get(count).get("startColour"));
                        searched.put("endColour", all.get(count).get("endColour"));
                        searched.put("description", all.get(count).get("description"));

                        searchResult.add(searched);
                    }

                }
            }


            try {
                InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
            } catch (Exception e) {
                Log.w("pebble.browse_screen", "Search wasn't focused: " + e.getLocalizedMessage());
            }


            Log.e("Searched", "" + searchResult);


            if (!searchResult.isEmpty()) {
                GridAdapter UIAdapterSearched = new GridAdapter(BrowseActivity.this, searchResult);
                featuredTitle.setVisibility(View.GONE);
                featuredRecycler.setVisibility(View.GONE);
                allTitle.setText("Results");
                allGrid.setAdapter(UIAdapterSearched);
                searchIcon.setImageResource(R.drawable.icon_add);
                //searchIcon.setRotation(90);
                searchIcon.animate().rotation(45).start();
                master.smoothScrollTo(0, 0);
                //searchButton.setBackgroundColor(getResources().getColor(android.R.color.holo_red_dark));
            } else {
                if (!searchField.equals("")) {
                    //Display "Nothing found"
                    //Toast.makeText(this, "Field not empty", Toast.LENGTH_SHORT).show();
                    //searchIcon.setImageResource(R.drawable.icon_search);
                    playAnimation();
                    searchIcon.setImageResource(R.drawable.icon_add);
                    searchIcon.animate().rotation(45).start();
                } else {
                    featuredTitle.setVisibility(View.VISIBLE);
                    featuredRecycler.setVisibility(View.VISIBLE);
                    allTitle.setText("All");
                    allGrid.setAdapter(UIAdapter);
                    //Toast.makeText(this, "Field empty", Toast.LENGTH_SHORT).show();
                    //searchButton.setBackgroundColor(getResources().getColor(android.R.color.transparent));
                }

            }
        });
        search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                //searchIcon.setBackgroundResource(R.drawable.icon_search);
                //searchIcon.setRotation(0);
                searchIcon.setImageResource(R.drawable.icon_search);
                searchIcon.animate().rotation(0).start();
                searchChanged = true;
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });*/

    }

    /*private ArrayList<String> searchInList(ArrayList<String> list, String searchString) {
        ArrayList<String> results = new ArrayList<>();
        for (int i = 0; i < all.size(); i++){
            if (all.get("backgroundName").equals(searchString))
        }
    }*/

    public void allGridLayout() {
        ViewGroup.LayoutParams allGridParams = allGrid.getLayoutParams();
        allGridParams.height = Calculations.screenMeasure(context, "height");
        allGrid.setLayoutParams(allGridParams);

        Log.i("INFO", "pebble.browse_activity: " + allGrid.getHeight());
        Log.i("INFO", "Pebble.browse_activity: Actual " + Calculations.screenMeasure(context, "height"));
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
            Intent restart = new Intent(BrowseActivity.this, BrowseActivity.class);
            restart.putExtra("featured", featured);
            restart.putExtra("items", all);
            startActivity(restart);
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            finish();
        }, 300);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Intent details = new Intent(BrowseActivity.this, GradientDetails.class);
        HashMap<String, String> info = (HashMap<String, String>) parent.getItemAtPosition(position);

        String gradientName = info.get("backgroundName");
        String startColour = info.get("startColour");
        String endColour = info.get("endColour");
        String description = info.get("description");

        details.putExtra("gradientName", gradientName);
        details.putExtra("startColour", startColour);
        details.putExtra("endColour", endColour);
        details.putExtra("description", description);

        ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(BrowseActivity.this, view.findViewById(R.id.gradient), gradientName);
        startActivity(details, options.toBundle());

        Vibration.INSTANCE.hFeedack(BrowseActivity.this);
    }

    public void playAnimation() {
        notification.setAlpha(1);
        Vibration.INSTANCE.notification(BrowseActivity.this);
        UIAnimations.constraintLayoutObjectAnimator(notification, "translationY",
                0, 500,
                0, new DecelerateInterpolator(3));
        UIAnimations.constraintLayoutObjectAnimator(notification, "translationY",
                Math.round(-45 * getResources().getDisplayMetrics().density), 500,
                3000, new DecelerateInterpolator(3));
        UIAnimations.constraintLayoutAlpha(notification, 0, 3500);
    }

}



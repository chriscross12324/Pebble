package com.simple.chris.pebble;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.LinearInterpolator;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.viewpager.widget.ViewPager;

import java.util.ArrayList;
import java.util.HashMap;

import eightbitlab.com.blurview.BlurView;
import eightbitlab.com.blurview.RenderScriptBlur;

public class GradientsScreen extends AppCompatActivity implements AdapterView.OnItemClickListener {

    ConstraintLayout changelogHolder, connectionNotification;
    LinearLayout connectingDialog, hideChangelogButton;
    TextView connectingDialogBody, connectionStatusText, featuredTitle, browseTitle;
    BlurView changelogBlur;
    ExpandedHeightScrollView gridView, blocker;
    SwipeRefreshLayout swipeToRefresh;
    ScrollView scrollView;

    private ViewPager featuredGradients;

    int screenWidth;
    int screenHeight;
    int imageViewHeight;
    boolean connected = false;
    String connectionType;

    int appVersion;

    Object module;
    View moduleView;
    View featuredPage;
    DisplayMetrics displayMetrics;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Values.darkMode) {
            setTheme(R.style.ThemeDark);
        } else {
            setTheme(R.style.ThemeLight);
        }
        setContentView(R.layout.activity_gradients_grid);
        ImageView background = findViewById(R.id.background);
        if (Values.darkMode) {
            background.setBackgroundResource(R.drawable.placeholder_gradient_dark);
        } else {
            background.setBackgroundResource(R.drawable.placeholder_gradient_light);
        }
        Values.saveValues(GradientsScreen.this);

        /** Declare UI Elements*/
        //Constraint Layout
        changelogHolder = findViewById(R.id.changelogHolder);
        connectionNotification = findViewById(R.id.connectionNotification);

        //Linear Layout
        connectingDialog = findViewById(R.id.connectingDialog);
        hideChangelogButton = findViewById(R.id.hideChangelogButton);

        //TextView
        connectingDialogBody = findViewById(R.id.connectingDialogBody);
        connectionStatusText = findViewById(R.id.connectionStatusText);
        featuredTitle = findViewById(R.id.featuredTitle);
        browseTitle = findViewById(R.id.browseTitle);

        //ExpandedHeightScrollView
        gridView = findViewById(R.id.gv_items);
        blocker = findViewById(R.id.blocker);

        //ViewPager
        featuredGradients = findViewById(R.id.featuredGradients);

        //Misc.
        changelogBlur = findViewById(R.id.changelogBlur);
        swipeToRefresh = findViewById(R.id.swipeToRefresh);
        scrollView = findViewById(R.id.scrollView);

        //Values
        displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        appVersion = BuildConfig.VERSION_CODE;
        connectionNotification.setTranslationY(-45 * getResources().getDisplayMetrics().density);
        screenWidth = displayMetrics.widthPixels;
        screenHeight = displayMetrics.heightPixels;
        int maxNumColumns = screenWidth / 418;
        Log.e("TAG", "" + maxNumColumns + screenWidth);
        gridView.setNumColumns(maxNumColumns);

        //SwipeToRefresh
        if (Values.darkMode){
            swipeToRefresh.setProgressBackgroundColorSchemeColor(ContextCompat.getColor(this, R.color.colorDarkThemeForeground));
            swipeToRefresh.setColorSchemeColors(ContextCompat.getColor(this, R.color.colorLightThemeForeground));
        } else {
            swipeToRefresh.setProgressBackgroundColorSchemeColor(ContextCompat.getColor(this, R.color.colorLightThemeForeground));
            swipeToRefresh.setColorSchemeColors(ContextCompat.getColor(this, R.color.colorDarkThemeForeground));
        }


        ArrayList<HashMap<String, String>> list = (ArrayList<HashMap<String, String>>) getIntent().getSerializableExtra("items");
        ArrayList<HashMap<String, String>> flist = (ArrayList<HashMap<String, String>>) getIntent().getSerializableExtra("featured");
        connectionChecker();

        FeaturedAdapterUserFriendly featuredAdapterUserFriendly = new FeaturedAdapterUserFriendly(GradientsScreen.this, flist);
        featuredGradients.setAdapter(featuredAdapterUserFriendly);

        featuredGradients.setPageTransformer(true, new ViewPagerStack());
        featuredGradients.setOffscreenPageLimit(10);
        //featuredGradients.setAdapter(featuredAdapter);
        featuredGradients.setTranslationY(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, -50,
                GradientsScreen.this.getResources().getDisplayMetrics()));


        featuredGradients.setOnTouchListener((view, motionEvent) -> {
            //view.getParent().requestDisallowInterceptTouchEvent(true);
            swipeToRefresh.setEnabled(false);
            switch (motionEvent.getAction()) {
                case MotionEvent.ACTION_UP:
                    swipeToRefresh.setEnabled(true);
                    break;
            }
            return false;
        });

        featuredGradients.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                //featuredGradients.getParent().requestDisallowInterceptTouchEvent(true);
                //swipeToRefresh.requestDisallowInterceptTouchEvent(true);
            }
        });


        gridView.setOnItemClickListener(this);
        gridView.postDelayed(() -> gridView.setTranslationY(screenHeight), 0);
        gridView.setAlpha(1);
        //gridView.setEnabled(false);


        swipeToRefresh.setOnRefreshListener(() -> {
            gridView.setEnabled(false);
            Intent GL = new Intent(GradientsScreen.this, ActivityConnecting.class);
            startActivity(GL);
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            finish();
        });


        if (Values.uiDesignerMode) {
            try {
                GridAdapterUIDesigner gridAdapterUIDesigner = new GridAdapterUIDesigner(GradientsScreen.this, list);
                gridView.setAdapter(gridAdapterUIDesigner);
            } catch (Exception e) {
                Log.e("Err", "" + e.getLocalizedMessage());
            }

        } else {
            try {
                GridAdapterUserFriendly gridAdapterUserFriendly = new GridAdapterUserFriendly(GradientsScreen.this, list);
                gridView.setAdapter(gridAdapterUserFriendly);
            } catch (Exception e) {
                Log.e("Err", "" + e.getLocalizedMessage());
            }

        }

        UIAnimations.gridViewObjectAnimator(gridView, "translationY", 0, 800, 500, new DecelerateInterpolator(3));

        Handler h1 = new Handler();
        h1.postDelayed(() -> {
            swipeToRefresh.setVisibility(View.VISIBLE);

            UIAnimations.textViewObjectAnimator(featuredTitle, "alpha", 1, 800, 300, new DecelerateInterpolator());
            UIAnimations.textViewObjectAnimator(browseTitle, "alpha", 1, 800, 300, new DecelerateInterpolator());
            UIAnimations.viewPagerObjectAnimator(featuredGradients, "alpha", 1, 800, 300, new DecelerateInterpolator());
            UIAnimations.viewPagerObjectAnimator(featuredGradients, "translationY", 0, 700, 300, new DecelerateInterpolator());

        }, 0);

        Handler handler = new Handler();
        handler.postDelayed(() -> {

            View decorView = getWindow().getDecorView();
            ViewGroup rootView = decorView.findViewById(android.R.id.content);
            Drawable windowBackground = decorView.getBackground();
            changelogBlur.setupWith(rootView)
                    .setFrameClearDrawable(windowBackground)
                    .setBlurAlgorithm(new RenderScriptBlur(GradientsScreen.this))
                    .setBlurRadius(15f)
                    .setHasFixedTransformationMatrix(false);


            if (Values.lastVersion < appVersion) {
                UIAnimations.blurViewObjectAnimator(changelogBlur, "alpha", 1, 500, 0, new DecelerateInterpolator());
                ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(changelogHolder, "alpha", 1);
                objectAnimator.setDuration(200);
                objectAnimator.setInterpolator(new DecelerateInterpolator());
                objectAnimator.start();
            } else if (Values.lastVersion == appVersion){
                blocker.setVisibility(View.GONE);
                UIAnimations.constraintLayoutVisibility(changelogHolder, View.GONE, 0);
            } else {
                Toast.makeText(this, "Are you from the future?", Toast.LENGTH_SHORT).show();
                finish();
            }
            hideChangelogButton.setOnClickListener(v -> {
                 UIAnimations.blurViewObjectAnimator(changelogBlur, "alpha", 0, 1000, 0, new DecelerateInterpolator());
                ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(changelogHolder, "alpha", 0);
                objectAnimator.setDuration(200);
                objectAnimator.setInterpolator(new DecelerateInterpolator());
                objectAnimator.start();
                blocker.setVisibility(View.GONE);
                UIAnimations.constraintLayoutVisibility(changelogHolder, View.GONE, 200);
                Values.lastVersion = appVersion;
                Values.saveValues(GradientsScreen.this);

            });
        }, 2000);

    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Intent intent = new Intent(GradientsScreen.this, GradientDetails.class);
        //Toast.makeText(this, "" + gridView.getHeight(), Toast.LENGTH_SHORT).show();

        @SuppressWarnings("unchecked")
        HashMap<String, String> map = (HashMap<String, String>) parent.getItemAtPosition(position);

        blocker.setVisibility(View.VISIBLE);

        moduleView = view;
        module = parent;

        String backgroundName = map.get("backgroundName");
        String leftColour = map.get("leftColour");
        String rightColour = map.get("rightColour");
        String description = map.get("description");

        intent.putExtra("backgroundName", backgroundName);
        intent.putExtra("leftColour", leftColour);
        intent.putExtra("rightColour", rightColour);
        intent.putExtra("description", description);

        ImageView imageView = view.findViewById(R.id.backgroundGradient);
        ConstraintLayout constraintLayout = view.findViewById(R.id.holder);
        ValueAnimator slideAnimation = ValueAnimator.ofInt(imageView.getHeight(), constraintLayout.getHeight()).setDuration(600);
        slideAnimation.addUpdateListener(animation -> {
            imageView.getLayoutParams().height = (Integer) animation.getAnimatedValue();
            imageView.requestLayout();
        });
        AnimatorSet set = new AnimatorSet();
        set.play(slideAnimation);
        set.setInterpolator(new DecelerateInterpolator(2));
        set.setDuration(350);
        set.start();
        if (imageViewHeight == 0) {
            imageViewHeight = imageView.getHeight();
        }


        Handler handler = new Handler();
        handler.postDelayed(() -> {
            ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(GradientsScreen.this, view.findViewById(R.id.cardView), backgroundName);
            startActivity(intent, options.toBundle());
        }, 400);

    }

    private class ViewPagerStack implements ViewPager.PageTransformer {

        @Override
        public void transformPage(@NonNull View page, float position) {
            featuredPage = page;
            if (position >= 0) {

                float scale = 1f - 0.25f * position;
                if (scale >= 0) {
                    //ViewGroup.LayoutParams layoutParams = page.getLayoutParams();
                    //layoutParams.width = Math.round(1f - 0.25f * position);
                    page.setScaleX(1f - 0.15f * position);
                    //page.setLayoutParams(layoutParams);
                } else {
                    page.setScaleX(0f);
                }

                page.setScaleY(1f);

                page.setTranslationX(-page.getWidth() * position);
                double val = (-50 * (Math.pow(0.5, position) - 1) / (0.5 - 1));
                page.setTranslationY((float) val);
                //Log.e("INFO", "" + position);

            }
            page.setOnClickListener(view -> {
                Intent intent = new Intent(GradientsScreen.this, GradientDetails.class);

                blocker.setVisibility(View.VISIBLE);

                intent.putExtra("backgroundName", "Shallow Lake");
                intent.putExtra("leftColour", "#89f7fe");
                intent.putExtra("rightColour", "#66a6ff");
                intent.putExtra("description", "A lake where you can see the bottom through the clear water");

                UIAnimations.textViewObjectAnimator(view.findViewById(R.id.gradientName), "alpha", 0, 200, 0, new LinearInterpolator());


                Handler handler = new Handler();
                handler.postDelayed(() -> {
                    ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(GradientsScreen.this, view.findViewById(R.id.gradient), "Shallow Lake");
                    startActivity(intent, options.toBundle());
                }, 200);

            });
        }
    }

    @Override
    protected void onResume() {
        if (Values.currentActivity != null && Values.currentActivity.equals("GradientDetails")) {
            try {
                ImageView imageView = moduleView.findViewById(R.id.backgroundGradient);
                imageView.getLayoutParams().height = imageViewHeight;
                imageView.requestLayout();

                ValueAnimator slideAnimation = ValueAnimator.ofInt(imageView.getHeight(), imageViewHeight).setDuration(600);
                slideAnimation.addUpdateListener(animation -> {
                    imageView.getLayoutParams().height = (Integer) animation.getAnimatedValue();
                    imageView.requestLayout();
                });
                AnimatorSet set = new AnimatorSet();
                set.play(slideAnimation);
                set.setInterpolator(new DecelerateInterpolator(3));
                set.start();
            } catch (Exception e) {
                Log.e("TAG", e.getLocalizedMessage());
            }
            try {
                TextView gradientName = featuredPage.findViewById(R.id.gradientName);
                UIAnimations.textViewObjectAnimator(gradientName, "alpha", 1, 200, 0, new LinearInterpolator());
            } catch (Exception e2) {
                Log.e("TAG", e2.getLocalizedMessage());
            }

            Values.currentActivity = "GradientsScreen";
            blocker.setVisibility(View.GONE);
        }
        super.onResume();
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        gridView.setNumColumns(newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE ? 4 : 2);
        featuredGradients.invalidate();
        super.onConfigurationChanged(newConfig);
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }

    /*public void appCrashHandler() {
        try {
            titleHolder.setVisibility(View.INVISIBLE);
        } catch (Exception e) {
            Log.e("TAG", "" + e.getLocalizedMessage());
        }
    }*/

    public boolean isInterenetConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() != null;
    }

    public boolean isNetworkTypeCellular() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = cm.getActiveNetworkInfo();
        return networkInfo.getType() == ConnectivityManager.TYPE_MOBILE;
    }

    public void connectionChecker() {
        connectionNotification.setAlpha(1);
        Handler handler = new Handler();
        handler.postDelayed(() -> {
            if (connected) {
                if (isInterenetConnected()) {
                    swipeToRefresh.setEnabled(true);
                    scrollView.setEnabled(true);
                    if (isNetworkTypeCellular()) {
                        if (!connectionType.equals("Cellular")) {
                            connectionStatusText.setText("Connection: Cellular");
                            playAnimation(0);
                            connectionType = "Cellular";
                        }
                    } else {
                        if (!connectionType.equals("Wi-Fi")) {
                            connectionStatusText.setText("Connection: Wi-Fi");
                            playAnimation(0);
                            connectionType = "Wi-Fi";
                        }
                    }
                } else {
                    if (!connectionType.equals("Null")) {
                        connectionStatusText.setText("No Connection");
                        playAnimation(0);
                        connectionType = "Null";
                    }
                    swipeToRefresh.setEnabled(false);
                    scrollView.setEnabled(false);
                }
            }

            connectionChecker();
        }, 5000);
    }

    public void playAnimation(int delay) {
        Handler handler = new Handler();
        handler.postDelayed(() -> {
            Vibration.INSTANCE.notification(GradientsScreen.this);
            UIAnimations.constraintLayoutObjectAnimator(connectionNotification, "translationY",
                    0, 500,
                    0, new DecelerateInterpolator(3));
            UIAnimations.constraintLayoutObjectAnimator(connectionNotification, "translationY",
                    Math.round(-45 * getResources().getDisplayMetrics().density), 500,
                    3000, new DecelerateInterpolator(3));
        }, delay);

    }


}

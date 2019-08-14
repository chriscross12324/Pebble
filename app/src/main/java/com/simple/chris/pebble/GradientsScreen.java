package com.simple.chris.pebble;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.ActivityOptions;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.LinearInterpolator;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
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

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.RetryPolicy;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

import eightbitlab.com.blurview.BlurView;
import eightbitlab.com.blurview.RenderScriptBlur;

public class GradientsScreen extends AppCompatActivity implements AdapterView.OnItemClickListener {

    BlurView changelogBlur;
    ImageView top, title, bottom;
    ExpandedHeightScrollView gridView;
    Dialog noConnectionDialog, cellularDataWarningDialog;
    Button openSystemSettingsNoConnection, dontAskAgain, tryWifi;
    ConstraintLayout titleHolder, changelogHolder, connectionNotification;
    LinearLayout connectingDialog, retry, useButton, hideChangelogButton;
    SwipeRefreshLayout swipeToRefresh;
    ScrollView scrollView;
    TextView connectingDialogBody, connectionStatusText;
    private ViewPager featuredGradients;
    private ViewPagerAdapter featuredAdapter;
    private ArrayList<ViewPagerModel> featuredContents;

    int screenHeight;
    int imageViewHeight;
    boolean connected = false;
    String connectionType;

    int appVersion;

    Object module;
    View moduleView;

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

        appVersion = BuildConfig.VERSION_CODE;
        changelogBlur = findViewById(R.id.changelogBlur);
        changelogHolder = findViewById(R.id.changelogHolder);
        hideChangelogButton = findViewById(R.id.hideChangelogButton);

        titleHolder = findViewById(R.id.titleHolder);
        connectingDialog = findViewById(R.id.connectingDialog);
        connectingDialogBody = findViewById(R.id.connectingDialogBody);
        connectionNotification = findViewById(R.id.connectionNotification);
        connectionStatusText = findViewById(R.id.connectionStatusText);
        scrollView = findViewById(R.id.scrollView);

        featuredGradients = findViewById(R.id.featuredGradients);
        featuredContents = new ArrayList<>();
        int startColours[] = {ContextCompat.getColor(GradientsScreen.this, R.color.atmosphereTL),
                ContextCompat.getColor(GradientsScreen.this, R.color.coldNightTL),
                ContextCompat.getColor(GradientsScreen.this, R.color.sunshineTL),
                ContextCompat.getColor(GradientsScreen.this, R.color.wintersDayTL),
                ContextCompat.getColor(GradientsScreen.this, R.color.shallowLakeTL),
                ContextCompat.getColor(GradientsScreen.this, R.color.greenGrassTL)};
        int endColours[] = {ContextCompat.getColor(GradientsScreen.this, R.color.atmosphereBR),
                ContextCompat.getColor(GradientsScreen.this, R.color.coldNightBR),
                ContextCompat.getColor(GradientsScreen.this, R.color.sunshineBR),
                ContextCompat.getColor(GradientsScreen.this, R.color.wintersDayBR),
                ContextCompat.getColor(GradientsScreen.this, R.color.shallowLakeBR),
                ContextCompat.getColor(GradientsScreen.this, R.color.greenGrassBR)};
        String names[] = {"Atmosphere", "Cold Night", "Sunshine", "Winters Day", "Shallow Lake", "Green Grass"};

        for (int i = 0; i < names.length; i++) {
            ViewPagerModel viewPagerModel = new ViewPagerModel();

            viewPagerModel.startColour = startColours[i];
            viewPagerModel.endColour = endColours[i];
            viewPagerModel.name = names[i];

            featuredContents.add(viewPagerModel);
        }

        featuredAdapter = new ViewPagerAdapter(featuredContents, GradientsScreen.this);
        featuredGradients.setPageTransformer(true, new ViewPagerStack());
        featuredGradients.setOffscreenPageLimit(4);
        featuredGradients.setAdapter(featuredAdapter);


        connectionNotification.setTranslationY(-45 * getResources().getDisplayMetrics().density);


        //Create Dialogs
        noConnectionDialog = new Dialog(this);
        cellularDataWarningDialog = new Dialog(this);
        top = findViewById(R.id.imageView9);
        bottom = findViewById(R.id.imageView8);
        title = findViewById(R.id.title);

        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);

        gridView = findViewById(R.id.gv_items);
        gridView.setOnItemClickListener(this);
        gridView.postDelayed(() -> {
            DisplayMetrics displayMetrics = new DisplayMetrics();
            getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
            screenHeight = displayMetrics.heightPixels;
            gridView.setTranslationY(screenHeight);
        }, 10);
        gridView.setAlpha(1);
        gridView.setEnabled(false);
        titleHolder.setAlpha(0);

        scrollView.setEnabled(false);

        swipeToRefresh = findViewById(R.id.swipeToRefresh);
        swipeToRefresh.setEnabled(false);
        swipeToRefresh.setOnRefreshListener(() -> {
            gridView.setEnabled(false);
            Intent GL = new Intent(GradientsScreen.this, GradientsScreen.class);
            startActivity(GL);
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            finish();
        });
        if (isInterenetConnected()) {
            if (isNetworkTypeCellular()) {
                if (Values.askData) {
                    showCellularWarningDialog();
                    connectionType = "Cellular";
                } else {
                    getItems();
                    connectionType = "Cellular";
                }
            } else {
                getItems();
                connectionType = "Wi-Fi";
            }
        } else {
            getItems();
            connectionType = "Null";
            //showNoConnectionDialog();
        }
        connectionChecker();

    }

    private void getItems() {
        playConnectingDialog();

        StringRequest stringRequest = new StringRequest(Request.Method.GET, "https://script.google.com/macros/s/AKfycbwFkoSBTbmeB6l9iIiZWGczp9sDEjqX0jiYeglczbLKFAXsmtB1/exec?action=getItems",
                this::parseItems,

                error -> {

                }
        );

        int socketTimeOut = 10000;
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeOut, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);

        stringRequest.setRetryPolicy(policy);

        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(stringRequest);

    }

    private void parseItems(String jsonResponse) {
        connected = true;
        swipeToRefresh.setEnabled(true);
        scrollView.setEnabled(true);
        ArrayList<HashMap<String, String>> list = new ArrayList<>();
        ArrayList<HashMap<String, String>> featuredList = new ArrayList<>();

        try {
            JSONObject jobj = new JSONObject(jsonResponse);
            JSONArray jarray = jobj.getJSONArray("items");

            for (int i = jarray.length() - 1; i >= 0; i--) { //int i = 0; i < jarray.length(); i++

                JSONObject jo = jarray.getJSONObject(i);

                String backgroundName = jo.getString("backgroundName").replace(" ", "\n");
                String leftColour = jo.getString("leftColour");
                String rightColour = jo.getString("rightColour");
                String description = jo.getString("description");
                String featured = jo.getString("featured");

                HashMap<String, String> item = new HashMap<>();
                item.put("backgroundName", backgroundName);
                item.put("leftColour", leftColour);
                item.put("rightColour", rightColour);
                item.put("description", description);
                HashMap<String, String> flist = new HashMap<>();
                flist.put("featured", featured);

                list.add(item);
                featuredList.add(flist);
                //Log.e("INFO", ""+featuredList);
            }
        } catch (JSONException e) {
            e.printStackTrace();
            Log.e("Info", "Failed " + e.getLocalizedMessage());
        } catch (Exception ex) {
            Log.e("Info", "Failed " + ex.getLocalizedMessage());
        }

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

        Handler h1 = new Handler();
        h1.postDelayed(() -> {
            Handler h1I = new Handler();
            swipeToRefresh.setVisibility(View.VISIBLE);
            h1I.postDelayed(() -> {
                ObjectAnimator OA1 = ObjectAnimator.ofFloat(gridView, "translationY", 0);
                OA1.setDuration(800);
                OA1.setInterpolator(new DecelerateInterpolator(3));
                OA1.start();

            }, 1000);

            ObjectAnimator OA2 = ObjectAnimator.ofFloat(titleHolder, "alpha", 1);
            OA2.setDuration(300);
            OA2.setInterpolator(new LinearInterpolator());
            OA2.start();

            ObjectAnimator OA3 = ObjectAnimator.ofFloat(connectingDialog, "alpha", 0);
            OA3.setDuration(300);
            OA3.setInterpolator(new LinearInterpolator());
            OA3.start();
        }, 300);

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


            if (Values.lastVersion != appVersion) {
                UIAnimations.blurViewObjectAnimator(changelogBlur, "alpha", 1, 500, 0, new DecelerateInterpolator());
                ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(changelogHolder, "alpha", 1);
                objectAnimator.setDuration(200);
                objectAnimator.setInterpolator(new DecelerateInterpolator());
                objectAnimator.start();
                swipeToRefresh.setEnabled(false);
                scrollView.setEnabled(false);
                gridView.setEnabled(false);
            } else {
                gridView.setEnabled(true);
                UIAnimations.constraintLayoutVisibility(changelogHolder, View.GONE, 0);
            }
            hideChangelogButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    UIAnimations.blurViewObjectAnimator(changelogBlur, "alpha", 0, 1000, 0, new DecelerateInterpolator());
                    ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(changelogHolder, "alpha", 0);
                    objectAnimator.setDuration(200);
                    objectAnimator.setInterpolator(new DecelerateInterpolator());
                    objectAnimator.start();
                    swipeToRefresh.setEnabled(true);
                    scrollView.setEnabled(true);
                    gridView.setEnabled(true);
                    UIAnimations.constraintLayoutVisibility(changelogHolder, View.GONE, 200);
                    Values.lastVersion = appVersion;
                    Values.saveValues(GradientsScreen.this);
                }
            });
        }, 2000);

    }

    public void showNoConnectionDialog() {
        noConnectionDialog.setContentView(R.layout.dialog_no_connection);
        retry = noConnectionDialog.findViewById(R.id.retryButton);
        openSystemSettingsNoConnection = noConnectionDialog.findViewById(R.id.openSystemSettingsButton);

        WindowManager.LayoutParams lp = Objects.requireNonNull(noConnectionDialog.getWindow()).getAttributes();
        Window window = noConnectionDialog.getWindow();
        lp.dimAmount = 0f;
        noConnectionDialog.getWindow().setAttributes(lp);
        lp.gravity = Gravity.CENTER;
        window.setAttributes(lp);

        retry.setOnClickListener(v -> {
            noConnectionDialog.dismiss();
            if (isInterenetConnected()) {
                if (isNetworkTypeCellular()) {
                    showCellularWarningDialog();
                } else {
                    getItems();
                }
            } else {
                showNoConnectionDialog();
            }
        });
        openSystemSettingsNoConnection.setOnClickListener(v -> {
            noConnectionDialog.dismiss();
            startActivityForResult(new Intent(Settings.ACTION_SETTINGS), 0);
        });

        noConnectionDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        noConnectionDialog.setCancelable(false);
        noConnectionDialog.show();
    }

    public void showCellularWarningDialog() {
        cellularDataWarningDialog.setContentView(R.layout.dialog_data_warning);
        useButton = cellularDataWarningDialog.findViewById(R.id.useButton);
        dontAskAgain = cellularDataWarningDialog.findViewById(R.id.dontAskAgain);
        tryWifi = cellularDataWarningDialog.findViewById(R.id.tryWifi);

        WindowManager.LayoutParams lp = Objects.requireNonNull(cellularDataWarningDialog.getWindow()).getAttributes();
        Window window = cellularDataWarningDialog.getWindow();
        lp.dimAmount = 0f;
        cellularDataWarningDialog.getWindow().setAttributes(lp);
        lp.gravity = Gravity.CENTER;
        window.setAttributes(lp);

        useButton.setOnClickListener(v -> {
            cellularDataWarningDialog.dismiss();
            if (isInterenetConnected()) {
                getItems();
            } else {
                showNoConnectionDialog();
            }
        });
        dontAskAgain.setOnClickListener(v -> {
            cellularDataWarningDialog.dismiss();
            if (isInterenetConnected()) {
                Values.askData = false;
                Values.saveValues(GradientsScreen.this);
                getItems();
            } else {
                showNoConnectionDialog();
            }
        });
        tryWifi.setOnClickListener(v -> {
            cellularDataWarningDialog.dismiss();
            if (isInterenetConnected()) {
                if (isNetworkTypeCellular()) {
                    showCellularWarningDialog();
                } else {
                    getItems();
                }
            } else {
                showNoConnectionDialog();
            }
        });

        cellularDataWarningDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        cellularDataWarningDialog.setCancelable(false);
        cellularDataWarningDialog.show();
    }

    public void playConnectingDialog() {
        ObjectAnimator OA1 = ObjectAnimator.ofFloat(connectingDialog, "alpha", 1);
        OA1.setDuration(300);
        OA1.setInterpolator(new LinearInterpolator());
        OA1.start();
        ImageView connectingAnimation = findViewById(R.id.animationView);

        connectingAnimation.setBackgroundResource(R.drawable.loading_animation);
        AnimationDrawable animationDrawable = (AnimationDrawable) connectingAnimation.getBackground();
        animationDrawable.start();

        Handler handler = new Handler();
        handler.postDelayed(() -> {
            if (!connected) {
                connectionNotification.setAlpha(1);
                playAnimation(0);
                UIAnimations.textViewChanger(connectionStatusText, "Trying a self fix", 7500);
                playAnimation(8000);
            }

        }, 8000);


        Handler handler2 = new Handler();
        handler2.postDelayed(() -> {
            if (!connected) {
                Intent reload = new Intent(GradientsScreen.this, GradientsScreen.class);
                startActivity(reload);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                finish();
            }
        }, 25000);


    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Intent intent = new Intent(GradientsScreen.this, GradientDetails.class);
        Toast.makeText(this, "" + gridView.getHeight(), Toast.LENGTH_SHORT).show();

        @SuppressWarnings("unchecked")
        HashMap<String, String> map = (HashMap<String, String>) parent.getItemAtPosition(position);

        gridView.setEnabled(false);
        swipeToRefresh.setEnabled(false);
        scrollView.setEnabled(false);

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

    @Override
    protected void onResume() {
        if (Values.currentActivity != null && Values.currentActivity.equals("GradientDetails")) {
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

            Values.currentActivity = "GradientsScreen";
            gridView.setEnabled(true);
            swipeToRefresh.setEnabled(true);
            scrollView.setEnabled(true);
        }
        super.onResume();
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        gridView.setNumColumns(newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE ? 4 : 2);
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

    private class ViewPagerStack implements ViewPager.PageTransformer {

        @Override
        public void transformPage(@NonNull View page, float position) {
            switch (Math.round(position)){
                case 0:
                    page.setScaleX(1f - 0.05f * position);
                    page.setScaleY(1f);

                    page.setTranslationX(-page.getWidth() * position);
                    page.setTranslationY(-30 * position);
                    Log.e("INFO", ""+position);
                    break;
                case 1:
                    page.setScaleX(1f - 0.05f * position);
                    page.setScaleY(1f);

                    page.setTranslationX(-page.getWidth() * position);
                    page.setTranslationY(-30 * position);
                    Log.e("INFO", ""+position);
                    break;
                case 2:
                    page.setScaleX(1f - 0.05f * position);
                    page.setScaleY(1f);

                    page.setTranslationX(-page.getWidth() * position);
                    page.setTranslationY(-20 * position);
                    Log.e("INFO", ""+position);
                    break;
                case 3:
                    page.setScaleX(1f - 0.05f * position);
                    page.setScaleY(1f);

                    page.setTranslationX(-page.getWidth() * position);
                    page.setTranslationY(-10 * position);
                    Log.e("INFO", ""+position);
                    break;
                case 4:
                    page.setScaleX(1f - 0.05f * position);
                    page.setScaleY(1f);

                    page.setTranslationX(-page.getWidth() * position);
                    page.setTranslationY(0 * position);
                    Log.e("INFO", ""+position);
                    break;
                default:
                    page.setScaleX(1f - 0.05f * position);
                    page.setScaleY(1f);

                    page.setTranslationX(-page.getWidth() * position);
                    page.setTranslationY(0 * position);
                    Log.e("INFO", ""+position);
                    break;

            }
            if (position >= 0){

            }
        }
    }

}

package com.simple.chris.pebble;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;
import java.util.Random;

public class ActivityConnecting extends AppCompatActivity {

    ConstraintLayout.LayoutParams params;
    RequestQueue mQueue;

    ConstraintLayout connectingDialog, notification, main, optionsUI;
    Dialog noConnectionDialog, cellularDataWarningDialog;
    Button retry, openSystemSettingsNoConnection, useButton, dontAskAgain, tryWifi;
    TextView notificationText, connectionStatusText, connectingDialogBody;
    ImageView background;

    Handler handler1 = new Handler();
    Handler handler2 = new Handler();
    Handler handler3 = new Handler();
    Handler handler4 = new Handler();

    Boolean oneTime = false;
    Boolean connectedMain = false;
    Boolean connectedFeatured = false;
    Boolean testLayout = false;

    Intent startTestLayout;


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
        setContentView(R.layout.activity_connecting);

        mQueue = Volley.newRequestQueue(this);

        //Constraint Layout
        connectingDialog = findViewById(R.id.connectingDialog);
        notification = findViewById(R.id.notification);
        background = findViewById(R.id.background);

        //Dialogs
        noConnectionDialog = new Dialog(ActivityConnecting.this);
        cellularDataWarningDialog = new Dialog(ActivityConnecting.this);

        //TextView
        notificationText = findViewById(R.id.notificationText);
        connectionStatusText = findViewById(R.id.connectionStatusText);
        connectingDialogBody = findViewById(R.id.connectingDialogBody);

        String[] connectingArray = ActivityConnecting.this.getResources().getStringArray(R.array.connecting_array);
        connectingDialogBody.setText(connectingArray[new Random().nextInt(connectingArray.length)]);

        notification.setTranslationY(-45 * getResources().getDisplayMetrics().density);

        startTestLayout = new Intent(ActivityConnecting.this, BrowseActivity.class);

        checkConnection();
        bothGrabbed();

        background.setOnClickListener(view -> {
            connectedMain = true;
            connectedFeatured = true;
        });

    }

    private void checkConnection() {
        if (isInternetConnected()) {
            if (isNetworkTypeCellular()) {
                if (Values.askData) {
                    if (oneTime) {
                        getItems();
                        playConnectingDialog();
                    } else {
                        showCellularWarningDialog();
                    }
                } else {
                    getItems();
                    playConnectingDialog();
                }
            } else {
                getItems();
                playConnectingDialog();
            }
        } else {
            getItems();
            showNoConnectionDialog();
        }
    }

    private void bothGrabbed() {
        handler4.postDelayed(() -> {
            if (connectedMain && connectedFeatured) {
                handler1.removeCallbacksAndMessages(null);
                handler2.removeCallbacksAndMessages(null);
                handler3.removeCallbacksAndMessages(null);
                handler4.removeCallbacksAndMessages(null);
                startActivity(startTestLayout);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                finish();
            } else {
                bothGrabbed();
            }
        }, 100);
    }

    private void getItems() {
        String url = "https://script.google.com/macros/s/AKfycbwFkoSBTbmeB6l9iIiZWGczp9sDEjqX0jiYeglczbLKFAXsmtB1/exec?action=getItems";
        String urlFeatured = "https://script.google.com/macros/s/AKfycbxBCTFbNajBCakcj90cFSEhKdoFza2y2IrSNBPC/exec?action=getItems";

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                response -> {
                    try {
                        connectionStatusText.setText("Status: Downloading All");
                        JSONArray mainArray = response.getJSONArray("items");
                        ArrayList<HashMap<String, String>> list = new ArrayList<>();

                        for (int i = mainArray.length() - 1; i >= 0; i--) {
                            JSONObject items = mainArray.getJSONObject(i);

                            String backgroundName = items.getString("backgroundName");
                            String startColour = items.getString("leftColour");
                            String endColour = items.getString("rightColour");
                            String description = items.getString("description");

                            HashMap<String, String> item = new HashMap<>();
                            item.put("backgroundName", backgroundName);
                            item.put("startColour", startColour);
                            item.put("endColour", endColour);
                            item.put("description", description);

                            list.add(item);
                            startTestLayout.putExtra("items", list);
                        }
                        connectedMain = true;
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }, Throwable::printStackTrace);

        JsonObjectRequest requestFeatured = new JsonObjectRequest(Request.Method.GET, urlFeatured, null,
                response -> {
                    try {
                        connectionStatusText.setText("Status: Downloading Featured");
                        JSONArray featuredArray = response.getJSONArray("items");
                        ArrayList<HashMap<String, String>> featuredList = new ArrayList<>();

                        for (int i = featuredArray.length() - 1; i >= 0; i--) {
                            JSONObject featuredItems = featuredArray.getJSONObject(i);

                            String backgroundName = featuredItems.getString("backgroundName");
                            String startColour = featuredItems.getString("leftColour");
                            String endColour = featuredItems.getString("rightColour");
                            String description = featuredItems.getString("description");

                            HashMap<String, String> item = new HashMap<>();
                            item.put("backgroundName", backgroundName);
                            item.put("startColour", startColour);
                            item.put("endColour", endColour);
                            item.put("description", description);

                            featuredList.add(item);
                            startTestLayout.putExtra("featured", featuredList);
                        }
                        connectedFeatured = true;
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }, Throwable::printStackTrace);

        mQueue.add(request);
        mQueue.add(requestFeatured);
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
            checkConnection();
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

        notificationText.setText(R.string.noWifi);
        playAnimation(500);

        WindowManager.LayoutParams lp = Objects.requireNonNull(cellularDataWarningDialog.getWindow()).getAttributes();
        Window window = cellularDataWarningDialog.getWindow();
        lp.dimAmount = 0f;
        cellularDataWarningDialog.getWindow().setAttributes(lp);
        lp.gravity = Gravity.CENTER;
        window.setAttributes(lp);

        useButton.setOnClickListener(v -> {
            cellularDataWarningDialog.dismiss();
            oneTime = true;
            checkConnection();
        });
        dontAskAgain.setOnClickListener(v -> {
            cellularDataWarningDialog.dismiss();
            Values.askData = false;
            checkConnection();
        });
        tryWifi.setOnClickListener(v -> {
            cellularDataWarningDialog.dismiss();
            checkConnection();
        });

        cellularDataWarningDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        cellularDataWarningDialog.setCancelable(false);
        cellularDataWarningDialog.show();
    }

    public void playConnectingDialog() {
        UIAnimations.constraintLayoutObjectAnimator(connectingDialog, "alpha", 1, 300, 0, new LinearInterpolator());
        ImageView connectingAnimation = findViewById(R.id.animationView);

        if (Values.peppaPink) {
            connectingAnimation.setBackgroundResource(R.drawable.peppa);
        } else {
            connectingAnimation.setBackgroundResource(R.drawable.loading_animation);
            AnimationDrawable animationDrawable = (AnimationDrawable) connectingAnimation.getBackground();
            animationDrawable.start();
        }

        notification.setAlpha(1);

        handler1.postDelayed(() -> {
            UIAnimations.textViewChanger(notificationText, "It seems that something is wrong", 0);
            playAnimation(0);
            UIAnimations.textViewChanger(notificationText, "Repairing", 6000);
            playAnimation(6000);
        }, 8000);

        handler2.postDelayed(() -> {
            Intent reload = new Intent(ActivityConnecting.this, ActivityConnecting.class);
            startActivity(reload);
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            handler1.removeCallbacksAndMessages(null);
            handler2.removeCallbacksAndMessages(null);
            handler3.removeCallbacksAndMessages(null);
            handler4.removeCallbacksAndMessages(null);
            finish();
        }, 19000);


    }

    public void playAnimation(int delay) {
        handler3.postDelayed(() -> {
            notification.setAlpha(1);
            Vibration.INSTANCE.notification(ActivityConnecting.this);
            UIAnimations.constraintLayoutObjectAnimator(notification, "translationY",
                    0, 500,
                    0, new DecelerateInterpolator(3));
            UIAnimations.constraintLayoutObjectAnimator(notification, "translationY",
                    Math.round(-45 * getResources().getDisplayMetrics().density), 500,
                    3000, new DecelerateInterpolator(3));
            UIAnimations.constraintLayoutAlpha(notification, 0, 3500);
        }, delay);
    }

    public boolean isInternetConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() != null;
    }

    public boolean isNetworkTypeCellular() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = cm.getActiveNetworkInfo();
        return networkInfo.getType() == ConnectivityManager.TYPE_MOBILE;
    }
}

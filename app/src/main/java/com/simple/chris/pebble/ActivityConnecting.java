package com.simple.chris.pebble;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;
import java.util.Random;

public class ActivityConnecting extends AppCompatActivity {

    RequestQueue mQueue;

    ConstraintLayout connectingDialog, notification;
    Dialog noConnectionDialog, cellularDataWarningDialog;
    Button retry, openSystemSettingsNoConnection, useButton, dontAskAgain, tryWifi;
    TextView notificationText, connectionStatusText, connectingDialogBody;
    ImageView background;
    ImageView animationView;

    Handler handler1 = new Handler();
    Handler handler2 = new Handler();
    Handler handler3 = new Handler();
    Handler handler4 = new Handler();

    Boolean oneTime = false;
    Boolean connectedMain = false;
    Boolean connectedFeatured = false;

    Intent beta5Layout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        UIElements.INSTANCE.setTheme(ActivityConnecting.this);
        Values.INSTANCE.saveValues(ActivityConnecting.this);
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

        //ImageView
        animationView = findViewById(R.id.animationView);

        String[] connectingArray = ActivityConnecting.this.getResources().getStringArray(R.array.connecting_array);
        connectingDialogBody.setText(connectingArray[new Random().nextInt(connectingArray.length)]);

        notification.setTranslationY(-45 * getResources().getDisplayMetrics().density);

        beta5Layout = new Intent(ActivityConnecting.this, ActivityBrowse.class);

        checkConnection();
        bothGrabbed();


        animationView.setOnClickListener(view -> {
            switch (Values.INSTANCE.getTheme()) {
                case "light":
                    Values.INSTANCE.setTheme("dark");
                    break;
                case "dark":
                    Values.INSTANCE.setTheme("black");
                    break;
                case "black":
                    Values.INSTANCE.setTheme("light");
                    break;

            }
        });

        background.setOnClickListener(view -> {
            startActivity(new Intent(ActivityConnecting.this, Create.class));
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            finish();
        });

    }

    private void checkConnection() {
        if (isInternetConnected()) {
            if (isNetworkTypeCellular()) {
                if (Values.INSTANCE.getAskMobileData()) {
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
            if (connectedMain) {
                handler1.removeCallbacksAndMessages(null);
                handler2.removeCallbacksAndMessages(null);
                handler3.removeCallbacksAndMessages(null);
                handler4.removeCallbacksAndMessages(null);
                startActivity(beta5Layout);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                finish();
            } else {
                bothGrabbed();
            }
        }, 100);
    }

    private void getItems() {
        String url = "https://script.google.com/macros/s/AKfycbwFkoSBTbmeB6l9iIiZWGczp9sDEjqX0jiYeglczbLKFAXsmtB1/exec?action=getItems";
        //String urlFeatured = "https://script.google.com/macros/s/AKfycbxBCTFbNajBCakcj90cFSEhKdoFza2y2IrSNBPC/exec?action=getItems";

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                response -> {
                    try {
                        connectionStatusText.setText("Status: Downloading Gradient");
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
                            Values.INSTANCE.setBrowse(list);
                        }
                        connectedMain = true;
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }, Throwable::printStackTrace);

        mQueue.add(request);
    }

    public void showNoConnectionDialog() {
        connectionStatusText.setVisibility(View.INVISIBLE);
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
            connectionStatusText.setVisibility(View.VISIBLE);
            noConnectionDialog.dismiss();
            checkConnection();
        });
        openSystemSettingsNoConnection.setOnClickListener(v -> {
            //noConnectionDialog.dismiss();
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
            Values.INSTANCE.setAskMobileData(false);
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
        UIElements.INSTANCE.constraintLayoutObjectAnimator(connectingDialog, "alpha", 1, 300, 0, new LinearInterpolator());
        ImageView connectingAnimation = findViewById(R.id.animationView);

        connectingAnimation.setBackgroundResource(R.drawable.animation_loading);
        AnimationDrawable animationDrawable = (AnimationDrawable) connectingAnimation.getBackground();
        animationDrawable.start();

        notification.setAlpha(1);

        handler1.postDelayed(() -> {
            UIElements.INSTANCE.textViewTextChanger(notificationText, "It seems that something is wrong", 0);
            playAnimation(0);
            UIElements.INSTANCE.textViewTextChanger(notificationText, "Attempting Repair", 6000);
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
            UIElements.INSTANCE.constraintLayoutObjectAnimator(notification, "translationY",
                    0, 500,
                    0, new DecelerateInterpolator(3));
            UIElements.INSTANCE.constraintLayoutObjectAnimator(notification, "translationY",
                    Math.round(-45 * getResources().getDisplayMetrics().density), 500,
                    3000, new DecelerateInterpolator(3));
            UIElements.INSTANCE.constraintLayoutVisibility(notification, View.INVISIBLE, 3500);
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

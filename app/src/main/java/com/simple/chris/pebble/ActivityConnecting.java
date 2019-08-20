package com.simple.chris.pebble;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.animation.ObjectAnimator;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

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

import eightbitlab.com.blurview.RenderScriptBlur;

public class ActivityConnecting extends AppCompatActivity {

    ConstraintLayout connectingDialog, notification;
    Dialog noConnectionDialog, cellularDataWarningDialog;
    Button retry, openSystemSettingsNoConnection, useButton, dontAskAgain, tryWifi;
    TextView notificationText;
    ImageView background;

    Handler handler1 = new Handler();
    Handler handler2 = new Handler();
    Handler handler3 = new Handler();
    Handler handler4 = new Handler();

    Boolean oneTime = false;
    Boolean connectedMain = false;
    Boolean connectedFeatured = false;

    Intent startGradientScreen;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Values.darkMode) {
            setTheme(R.style.ThemeDark);
        } else {
            setTheme(R.style.ThemeLight);
        }
        setContentView(R.layout.activity_connecting);
        background = findViewById(R.id.background);
        if (Values.darkMode) {
            background.setBackgroundResource(R.drawable.placeholder_gradient_dark);
        } else {
            background.setBackgroundResource(R.drawable.placeholder_gradient_light);
        }

        /** Declare UI Elements */
        //Constraint Layout
        connectingDialog = findViewById(R.id.connectingDialog);
        notification = findViewById(R.id.notification);

        //Dialogs
        noConnectionDialog = new Dialog(ActivityConnecting.this);
        cellularDataWarningDialog = new Dialog(ActivityConnecting.this);

        //TextView
        notificationText = findViewById(R.id.notificationText);

        notification.setTranslationY(-45 * getResources().getDisplayMetrics().density);

        startGradientScreen = new Intent(ActivityConnecting.this, GradientsScreen.class);

        checkConnection();
        bothGrabbed();

        background.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                connectedFeatured = true;
            }
        });
    }

    private void checkConnection() {
        if (isInternetConnected()) {
            if (isNetworkTypeCellular()) {
                if (Values.askData) {
                    if (oneTime) {
                        getItems();
                        playConnectingDialog();
                    }else {
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
            //getItems();
            showNoConnectionDialog();
        }
    }

    private void bothGrabbed() {
        handler4.postDelayed(() -> {
            if (connectedMain && connectedFeatured) {
                startActivity(startGradientScreen);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                //Log.e("TAG", list.toString());
                //noConnectionDialog.dismiss();
                //cellularDataWarningDialog.dismiss();
                handler1.removeCallbacksAndMessages(null);
                handler2.removeCallbacksAndMessages(null);
                handler3.removeCallbacksAndMessages(null);
                finish();
            } else {
                bothGrabbed();
            }
        }, 100);
    }

    private void getItems() {
        playConnectingDialog();

        StringRequest gradientGridItems = new StringRequest(Request.Method.GET, "https://script.google.com/macros/s/AKfycbwFkoSBTbmeB6l9iIiZWGczp9sDEjqX0jiYeglczbLKFAXsmtB1/exec?action=getItems",
                this::parseItems,

                error -> {

                }
        );
        StringRequest featuredGradients = new StringRequest(Request.Method.GET, "https://script.google.com/macros/s/AKfycbxBCTFbNajBCakcj90cFSEhKdoFza2y2IrSNBPC/exec?action=getItems",
                this::parseFeatured,

                error -> {

                }
        );

        int socketTimeOut = 10000;
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeOut, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);

        gradientGridItems.setRetryPolicy(policy);
        featuredGradients.setRetryPolicy(policy);

        RequestQueue mainQueue = Volley.newRequestQueue(this);
        mainQueue.add(gradientGridItems);

        RequestQueue featuredQueue = Volley.newRequestQueue(this);
        featuredQueue.add(featuredGradients);

    }

    private void parseItems(String jsonResponse) {
        ArrayList<HashMap<String, String>> list = new ArrayList<>();

        try {
            JSONObject jobj = new JSONObject(jsonResponse);
            JSONArray jarray = jobj.getJSONArray("items");

            //Top to bottom: int i = 0; i < jarray.length(); i++
            //Bottom to top: int i = jarray.length() - 1; i >= 0; i--
            for (int i = jarray.length() - 1; i >= 0; i--) {

                JSONObject jo = jarray.getJSONObject(i);

                String backgroundName = jo.getString("backgroundName").replace(" ", "\n");
                String leftColour = jo.getString("leftColour");
                String rightColour = jo.getString("rightColour");
                String description = jo.getString("description");
                //String featured = jo.getString("featured");

                HashMap<String, String> item = new HashMap<>();
                item.put("backgroundName", backgroundName);
                item.put("leftColour", leftColour);
                item.put("rightColour", rightColour);
                item.put("description", description);

                list.add(item);
                startGradientScreen.putExtra("items", list);
                //Log.e("INFO", ""+featuredList);
            }
            connectedMain = true;
        } catch (JSONException e) {
            e.printStackTrace();
            Log.e("Info", "Failed " + e.getLocalizedMessage());
        } catch (Exception ex) {
            Log.e("Info", "Failed " + ex.getLocalizedMessage());
        }

    }

    private void parseFeatured(String jsonResponse) {
        ArrayList<HashMap<String, String>> flist = new ArrayList<>();

        try {
            JSONObject fjobj = new JSONObject(jsonResponse);
            JSONArray fjarray = fjobj.getJSONArray("items");

            //Top to bottom: int i = 0; i < fjarray.length(); i++
            //Bottom to top: int i = fjarray.length() - 1; i >= 0; i--
            for (int i = 0; i < fjarray.length(); i++) {

                JSONObject jo = fjarray.getJSONObject(i);

                String backgroundName = jo.getString("backgroundName");
                String leftColour = jo.getString("leftColour");
                String rightColour = jo.getString("rightColour");
                String description = jo.getString("description");
                //String featured = jo.getString("featured");

                HashMap<String, String> item = new HashMap<>();
                item.put("backgroundName", backgroundName);
                item.put("leftColour", leftColour);
                item.put("rightColour", rightColour);
                item.put("description", description);

                flist.add(item);
                startGradientScreen.putExtra("featured", flist);
                //Log.e("INFO", ""+flist);
            }
            connectedFeatured = true;
        } catch (JSONException e) {
            e.printStackTrace();
            Log.e("Info", "Failed " + e.getLocalizedMessage());
        } catch (Exception ex) {
            Log.e("Info", "Failed " + ex.getLocalizedMessage());
        }

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

        notificationText.setText("Not connected to Wi-Fi");
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

        connectingAnimation.setBackgroundResource(R.drawable.loading_animation);
        AnimationDrawable animationDrawable = (AnimationDrawable) connectingAnimation.getBackground();
        animationDrawable.start();
        notification.setAlpha(1);

        handler1.postDelayed(() -> {
            playAnimation(0);
            UIAnimations.textViewChanger(notificationText, "Repairing", 7500);
            playAnimation(6000);
        }, 8000);

        handler2.postDelayed(() -> {
            Intent reload = new Intent(ActivityConnecting.this, ActivityConnecting.class);
            startActivity(reload);
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            finish();
        }, 17000);


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

package com.simple.chris.pebble;

import android.animation.ObjectAnimator;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.Pair;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.LinearInterpolator;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

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

public class GradientsList extends AppCompatActivity implements AdapterView.OnItemClickListener{

    public ArrayList<Integer> colours;
    ImageView top, title, bottom;
    GridView gridView;
    Dialog noConnectionDialog, cellularDataWarningDialog;
    Button openSystemSettingsNoConnection, dontAskAgain, tryWifi;
    ConstraintLayout titleHolder;
    LinearLayout connectingDialog, retry, useButton;
    SwipeRefreshLayout swipeToRefresh;

    GridAdapterUserFriendly US;

    int screenHeight;



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
        Values.saveValues(GradientsList.this);

        titleHolder = findViewById(R.id.titleHolder);
        connectingDialog = findViewById(R.id.connectingDialog);


        //Create Dialogs
        noConnectionDialog = new Dialog(this);
        cellularDataWarningDialog = new Dialog(this);
        top = findViewById(R.id.imageView9);
        bottom = findViewById(R.id.imageView8);
        title = findViewById(R.id.title);

        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        colours = new ArrayList<>();

        gridView = findViewById(R.id.gv_items);
        gridView.setOnItemClickListener((parent, view, position, id) -> {
            HashMap<String, String> map = (HashMap<String, String>)parent.getItemAtPosition(position);
            Toast.makeText(this, ""+map.get("backgroundName"), Toast.LENGTH_SHORT).show();
        });
        gridView.postDelayed(() -> {
            DisplayMetrics displayMetrics = new DisplayMetrics();
            getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
            screenHeight = displayMetrics.heightPixels;
            gridView.setTranslationY(screenHeight);
        }, 10);
        gridView.setAlpha(1);
        titleHolder.setAlpha(0);

        swipeToRefresh = findViewById(R.id.swipeToRefresh);
        swipeToRefresh.setOnRefreshListener(() -> {
            Intent GL = new Intent(GradientsList.this, GradientsList.class);
            startActivity(GL);
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            finish();
        });
        if (isInterenetConnected()) {
            if (isNetworkTypeCellular()) {
                if (Values.askData){
                    showCellularWarningDialog();
                }else {
                    getItems();
                }
            } else {
                getItems();
            }
        } else {
            showNoConnectionDialog();
        }

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
        //Log.e("Info", "Got to 'parseItems' with response: " + jsonResponse);

        ArrayList<HashMap<String, String>> list = new ArrayList<>();

        try {
            JSONObject jobj = new JSONObject(jsonResponse);
            JSONArray jarray = jobj.getJSONArray("items");

            for (int i = jarray.length() - 1; i >= 0; i--) { //int i = 0; i < jarray.length(); i++

                JSONObject jo = jarray.getJSONObject(i);

                String backgroundName = jo.getString("backgroundName").replace(" ", "\n");
                String leftColour = jo.getString("leftColour");
                String rightColour = jo.getString("rightColour");
                String description = jo.getString("description");

                HashMap<String, String> item = new HashMap<>();
                item.put("backgroundName", backgroundName);
                item.put("leftColour", leftColour);
                item.put("rightColour", rightColour);
                item.put("description", description);

                list.add(item);
            }
        } catch (JSONException e) {
            e.printStackTrace();
            Log.e("Info", "Failed " + e.getLocalizedMessage());
        } catch (Exception ex){
            Log.e("Info", "Failed " + ex.getLocalizedMessage());
        }

        if (Values.uiDesignerMode) {
            try {
                GridAdapterUIDesigner gridAdapterUIDesigner = new GridAdapterUIDesigner(GradientsList.this, list);
                gridView.setAdapter(gridAdapterUIDesigner);
            }catch (Exception e){
                Log.e("Err", ""+e.getLocalizedMessage());
            }

        } else {
            try {
                GridAdapterUserFriendly gridAdapterUserFriendly = new GridAdapterUserFriendly(GradientsList.this, list);
                gridView.setAdapter(gridAdapterUserFriendly);
            }catch (Exception e){
                Log.e("Err", ""+e.getLocalizedMessage());
            }

        }

        //FAB.setVisibility(View.VISIBLE);
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

        }, 150);

    }

    private void getServerStatus() {
        StringRequest stringRequest = new StringRequest(Request.Method.GET, "https://script.google.com/macros/s/AKfycbwAP_xOtxMg25Pi7kqqSkjRJtz8B_VHcJRdiTYXqEWd02yJUGg/exec?action=getStatus",
                this::parseStatus,

                error -> {

                });
        int socketTimeOut = 15000;
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeOut, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        stringRequest.setRetryPolicy(policy);
        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(stringRequest);
    }

    private void parseStatus(String jsonResponse) {
        ArrayList<HashMap<String, String>> list = new ArrayList<>();
        try {
            JSONObject jobj = new JSONObject(jsonResponse);
            JSONArray jarray = jobj.getJSONArray("items");
            for (int i = 0; i < jarray.length(); i++) {
                JSONObject jo = jarray.getJSONObject(i);
                String serverStatus = jo.getString("serverStatus");
                String serverMessage = jo.getString("serverMessage");
            }
        } catch (Exception ignored) {
        }

        Toast.makeText(this, "Status: " + list + "Message: ", Toast.LENGTH_SHORT).show();
    }

    public boolean isInterenetConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        return cm.getActiveNetworkInfo() != null;
    }

    public boolean isNetworkTypeCellular() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = cm.getActiveNetworkInfo();
        boolean isData = networkInfo.getType() == ConnectivityManager.TYPE_MOBILE;
        //Log.e("Connection", "" + isData);
        return isData;
    }

    public void showNoConnectionDialog() {
        noConnectionDialog.setContentView(R.layout.dialog_no_connection);
        retry = noConnectionDialog.findViewById(R.id.retryButton);
        openSystemSettingsNoConnection = noConnectionDialog.findViewById(R.id.openSystemSettingsButton);

        WindowManager.LayoutParams lp = noConnectionDialog.getWindow().getAttributes();
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

        WindowManager.LayoutParams lp = cellularDataWarningDialog.getWindow().getAttributes();
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
    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id){

        Log.e("INFO", "Parent: "+parent+" View: "+view+" Position: "+position+" ID: "+id);
        //Intent intent = new Intent(this, GradientDetails.class);
        //@SuppressWarnings("unchecked")

        try{
            //Pair<String,String> map = new Pair<>();
            //map = parent.getItemAtPosition(position);
        }catch (Exception e){
            Log.e("ERR", ""+e.getLocalizedMessage());
        }



        /*String backgroundName = map.get("backgroundName");
        String leftColour = map.get("leftColour");
        String rightColour = map.get("rightColour");
        String description = map.get("description");

        intent.putExtra("backgroundName", backgroundName);
        intent.putExtra("leftColour", leftColour);
        intent.putExtra("rightColour", rightColour);
        intent.putExtra("description", description);

        startActivity(intent);*/

        Toast.makeText(this, "Clicked: ", Toast.LENGTH_SHORT).show();


    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        gridView.setNumColumns(newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE ? 4 : 2);
        super.onConfigurationChanged(newConfig);
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }

    public static void appCrashReceiver(){
        GradientsList gl = new GradientsList();
        gl.appCrashHandler();
    }
    public void appCrashHandler(){
        try {
            titleHolder.setVisibility(View.INVISIBLE);
        }catch (Exception e){
            Log.e("TAG", ""+e.getLocalizedMessage());
        }
    }

}

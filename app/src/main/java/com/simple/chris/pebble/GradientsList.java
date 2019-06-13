package com.simple.chris.pebble;

import android.animation.ObjectAnimator;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
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
import android.widget.TextView;
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
import java.util.List;

public class GradientsList extends AppCompatActivity {

    public ArrayList<Integer> colours;
    ImageView top, title, bottom;
    GridView gridView;
    Dialog noConnectionDialog, cellularDataDialog;
    Button openSystemSettingsNoConnection, openSystemSettingsCellularData, continueButton, retry;
    ConstraintLayout titleHolder;
    LinearLayout connectingDialog;
    SwipeRefreshLayout swipeToRefresh;
    List<String> topLeftHex = new ArrayList<>();
    List<String> bottomRightHex = new ArrayList<>();
    List<String> backgroundNames = new ArrayList<>();
    List<Integer> leftColours = new ArrayList<>();
    List<Integer> rightColours = new ArrayList<>();
    List<String> descriptions = new ArrayList<>();
    String[] topLeftHexx;
    String[] bottomRightHexx;
    String[] backgroundNamess;
    int[] leftColourss;
    int[] rightColourss;
    String[] descriptionss;

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
        cellularDataDialog = new Dialog(this);
        top = findViewById(R.id.imageView9);
        bottom = findViewById(R.id.imageView8);
        title = findViewById(R.id.title);

        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        colours = new ArrayList<>();

        gridView = findViewById(R.id.gv_items);
        gridView.postDelayed(() -> {
            DisplayMetrics displayMetrics = new DisplayMetrics();
            getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
            screenHeight = displayMetrics.heightPixels;
            gridView.setTranslationY(screenHeight);
        }, 10);
        gridView.setAlpha(1);
        titleHolder.setAlpha(0);
        // gradientsFound = (TextView) connectingDialog.findViewById(R.id.gradientsFound);
        //setBlurView();
        //setAddGradientBlur();
        swipeToRefresh = (SwipeRefreshLayout) findViewById(R.id.swipeToRefresh);
        //swipeToRefresh.setVisibility(View.GONE);
        swipeToRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                //Arrays.fill(backgroundNamess, null);
                //Arrays.fill(leftColourss, null);
                //Arrays.fill(rightColourss, null);
                //Arrays.fill(descriptionss, null);
                //getItems();
                //swipeToRefresh.setRefreshing(true);
                Intent GL = new Intent(GradientsList.this, GradientsList.class);
                startActivity(GL);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                finish();
            }
        });
        if (isInterenetConnected()) {
            if (isNetworkTypeCellular()) {
                showCellularWarningDialog();
            } else {
                getItems();
            }
        } else {
            showNoConnectionDialog();
        }

    }

    private void getItems() {
        playConnectingDialog();
        new Handler().postDelayed(() -> {
            //swipeToRefresh.setRefreshing(false);
        }, 50);

        StringRequest stringRequest = new StringRequest(Request.Method.GET, "https://script.google.com/macros/s/AKfycbwFkoSBTbmeB6l9iIiZWGczp9sDEjqX0jiYeglczbLKFAXsmtB1/exec?action=getItems",
                this::parseItems,

                error -> {

                }
        );

        int socketTimeOut = 30000;
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeOut, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);

        stringRequest.setRetryPolicy(policy);

        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(stringRequest);

    }

    private void parseItems(String jsonResponse) {
        //Log.e("Info", "Got to 'parseItems' with response: " + jsonResponse);

        try {
            JSONObject jobj = new JSONObject(jsonResponse);
            JSONArray jarray = jobj.getJSONArray("items");

            for (int i = jarray.length() - 1; i >= 0; i--) { //int i = 0; i < jarray.length(); i++

                // Gets and parses topLeftHex
                topLeftHex.add(jarray.getJSONObject(i).getString("leftColour"));
                topLeftHexx = topLeftHex.toArray(new String[0]);

                // Gets and parses bottomRightHex
                bottomRightHex.add(jarray.getJSONObject(i).getString("rightColour"));
                bottomRightHexx = bottomRightHex.toArray(new String[0]);

                // Gets and parses backgroundName
                backgroundNames.add(jarray.getJSONObject(i).getString("backgroundName").replace(" ", "\n"));
                backgroundNamess = backgroundNames.toArray(new String[0]);

                // Gets and parses leftColour
                String left = jarray.getJSONObject(i).getString("leftColour");
                leftColours.add(Color.parseColor(left));
                leftColourss = leftColours.stream().mapToInt(Integer::intValue).toArray();

                // Gets and parses rightColour
                String right = jarray.getJSONObject(i).getString("rightColour");
                rightColours.add(Color.parseColor(right));
                rightColourss = rightColours.stream().mapToInt(Integer::intValue).toArray();

                // Gets and parses description
                descriptions.add(jarray.getJSONObject(i).getString("description"));
                descriptionss = descriptions.toArray(new String[0]);
            }
        } catch (JSONException e) {
            e.printStackTrace();
            Log.e("Info", "Failed " + e.getLocalizedMessage());
        } catch (Exception ex){
            Log.e("Info", "Failed " + ex.getLocalizedMessage());
        }

        if (Values.uiDesignerMode) {
            GridAdapterUIDesigner gridAdapterUIDesigner = new GridAdapterUIDesigner(GradientsList.this, topLeftHexx, bottomRightHexx, backgroundNamess, leftColourss, rightColourss);
            gridView.setAdapter(gridAdapterUIDesigner);
        } else {
            GridAdapterUserFriendly gridAdapterUserFriendly = new GridAdapterUserFriendly(GradientsList.this, backgroundNamess, leftColourss, rightColourss);
            gridView.setAdapter(gridAdapterUserFriendly);
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
        lp.dimAmount = 0.8f;
        noConnectionDialog.getWindow().setAttributes(lp);
        lp.gravity = Gravity.BOTTOM;
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
        cellularDataDialog.setContentView(R.layout.dialog_cell_wifi_used);
        continueButton = cellularDataDialog.findViewById(R.id.continueButton);
        openSystemSettingsCellularData = cellularDataDialog.findViewById(R.id.openSystemSettingsButton);

        WindowManager.LayoutParams lp = cellularDataDialog.getWindow().getAttributes();
        Window window = cellularDataDialog.getWindow();
        lp.dimAmount = 0.8f;
        cellularDataDialog.getWindow().setAttributes(lp);
        lp.gravity = Gravity.BOTTOM;
        window.setAttributes(lp);

        continueButton.setOnClickListener(v -> {
            cellularDataDialog.dismiss();
            if (isInterenetConnected()) {
                getItems();
            } else {
                showNoConnectionDialog();
            }
        });
        openSystemSettingsCellularData.setOnClickListener(v -> {
            noConnectionDialog.dismiss();
            startActivityForResult(new Intent(Settings.ACTION_SETTINGS), 0);
        });

        cellularDataDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        cellularDataDialog.setCancelable(false);
        cellularDataDialog.show();
    }

    public void playConnectingDialog() {
        ImageView connectingAnimation = findViewById(R.id.animationView);

        connectingAnimation.setBackgroundResource(R.drawable.loading_animation);
        AnimationDrawable animationDrawable = (AnimationDrawable) connectingAnimation.getBackground();
        animationDrawable.start();
    }

    /*private void setBlurView() {
        float radius = 25f;

        View decorView = getWindow().getDecorView();

        ViewGroup rootView = (ViewGroup) decorView.findViewById(android.R.id.content);
        Drawable windowBackground = decorView.getBackground();
        title.setupWith(rootView)
                .setFrameClearDrawable(windowBackground)
                .setBlurAlgorithm(new RenderScriptBlur(this))
                .setBlurRadius(radius)
                .setHasFixedTransformationMatrix(true);
        FAB.setupWith(rootView)
                .setFrameClearDrawable(windowBackground)
                .setBlurAlgorithm(new RenderScriptBlur(this))
                .setBlurRadius(radius)
                .setHasFixedTransformationMatrix(true);
    }*/

    public void onModuleClick(AdapterView<?> parent, View view, int position, long id){
        Intent intent = new Intent(this, GradientDetails.class);
        HashMap<String, String> map = (HashMap) parent.getItemAtPosition(position);
        String backgroundName = map.get("backgroundName").toString();
        String leftColour = map.get("leftColour").toString();
        String rightColour = map.get("rightColour").toString();
        String description = map.get("description").toString();

        intent.putExtra("backgroundName", backgroundName);
        intent.putExtra("leftColour", leftColour);
        intent.putExtra("rightColour", rightColour);
        intent.putExtra("description", description);

        startActivity(intent);

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

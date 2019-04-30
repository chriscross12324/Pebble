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
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

public class GradientsList extends AppCompatActivity {

    public ArrayList<Integer> colours;
    TextView gradientsFound;
    GridView gridView;
    Dialog connectingDialog, noConnectionDialog, cellularDataDialog;
    Button openSystemSettingsNoConnection, openSystemSettingsCellularData, continueButton, retry;
    SwipeRefreshLayout swipeToRefresh;
    List<String> backgroundNames = new ArrayList<String>();
    List<Integer> leftColours = new ArrayList<Integer>();
    List<Integer> rightColours = new ArrayList<Integer>();
    List<String> descriptions = new ArrayList<String>();
    String[] backgroundNamess;
    int[] leftColourss;
    int[] rightColourss;
    String[] descriptionss;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gradients_grid);

        //Create Dialogs
        noConnectionDialog = new Dialog(this);
        cellularDataDialog = new Dialog(this);

        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        colours = new ArrayList<Integer>();

        gridView = (GridView) findViewById(R.id.gv_items);
        swipeToRefresh = (SwipeRefreshLayout) findViewById(R.id.swipeToRefresh);
        connectingDialog = new Dialog(this);
        connectingDialog.setContentView(R.layout.dialog_connecting);
        gridView.setAlpha(0);
        // gradientsFound = (TextView) connectingDialog.findViewById(R.id.gradientsFound);

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
        showConnectingDialog();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                swipeToRefresh.setRefreshing(false);
            }
        }, 50);

        StringRequest stringRequest = new StringRequest(Request.Method.GET, "https://script.google.com/macros/s/AKfycbwFkoSBTbmeB6l9iIiZWGczp9sDEjqX0jiYeglczbLKFAXsmtB1/exec?action=getItems",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        parseItems(response);
                    }
                },

                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                }
        );

        int socketTimeOut = 30000;
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeOut, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);

        stringRequest.setRetryPolicy(policy);

        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(stringRequest);

    }

    private void parseItems(String jsonResponse) {
        Log.e("Info", "Got to 'parseItems' with response: " + jsonResponse);

        try {
            JSONObject jobj = new JSONObject(jsonResponse);
            JSONArray jarray = jobj.getJSONArray("items");

            for (int i = jarray.length() - 1; i >= 0; i--) { //int i = 0; i < jarray.length(); i++

                /** Gets and parses backgroundName **/
                backgroundNames.add(jarray.getJSONObject(i).getString("backgroundName").replace(" ", "\n"));
                backgroundNamess = backgroundNames.toArray(new String[backgroundNames.size()]);

                /** Gets and parses leftColour **/
                String left = jarray.getJSONObject(i).getString("leftColour");
                leftColours.add(Color.parseColor(left));
                leftColourss = leftColours.stream().mapToInt(Integer::intValue).toArray();

                /** Gets and parses rightColour **/
                String right = jarray.getJSONObject(i).getString("rightColour");
                rightColours.add(Color.parseColor(right));
                rightColourss = rightColours.stream().mapToInt(Integer::intValue).toArray();

                /** Gets and parses description **/
                descriptions.add(jarray.getJSONObject(i).getString("description"));
                descriptionss = descriptions.toArray(new String[descriptions.size()]);
                Log.e("Info", ""+backgroundNames);
            }
        } catch (JSONException e) {
            e.printStackTrace();
            Log.e("Info", "Failed " + e.getLocalizedMessage());
        }

        GridAdapter gridAdapter = new GridAdapter(GradientsList.this, backgroundNamess, leftColourss, rightColourss);
        gridView.setAdapter(gridAdapter);

        connectingDialog.dismiss();
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(gridView, "alpha", 1);
                objectAnimator.setDuration(500);
                objectAnimator.setInterpolator(new LinearInterpolator());
                objectAnimator.start();
            }
        }, 150);

    }

    private void getServerStatus() {
        StringRequest stringRequest = new StringRequest(Request.Method.GET, "https://script.google.com/macros/s/AKfycbwAP_xOtxMg25Pi7kqqSkjRJtz8B_VHcJRdiTYXqEWd02yJUGg/exec?action=getStatus",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        parseStatus(response);
                    }
                },

                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
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
        } catch (Exception e) {
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
        Log.e("Connection", "" + isData);
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

        retry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
            }
        });
        openSystemSettingsNoConnection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                noConnectionDialog.dismiss();
                startActivityForResult(new Intent(Settings.ACTION_SETTINGS), 0);
            }
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

        continueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cellularDataDialog.dismiss();
                if (isInterenetConnected()) {
                    getItems();
                } else {
                    showNoConnectionDialog();
                }
            }
        });
        openSystemSettingsCellularData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                noConnectionDialog.dismiss();
                startActivityForResult(new Intent(Settings.ACTION_SETTINGS), 0);
            }
        });

        cellularDataDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        cellularDataDialog.setCancelable(false);
        cellularDataDialog.show();
    }

    public void showConnectingDialog() {
        ImageView animationView = connectingDialog.findViewById(R.id.animationView);

        WindowManager.LayoutParams lp = connectingDialog.getWindow().getAttributes();
        Window window = connectingDialog.getWindow();
        lp.dimAmount = 0.8f;
        connectingDialog.getWindow().setAttributes(lp);
        lp.gravity = Gravity.BOTTOM;
        window.setAttributes(lp);

        animationView.setBackgroundResource(R.drawable.loading_animation);
        AnimationDrawable animationDrawable = (AnimationDrawable) animationView.getBackground();
        animationDrawable.start();

        connectingDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        connectingDialog.show();

    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        gridView.setNumColumns(newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE ? 3 : 2);
        super.onConfigurationChanged(newConfig);
    }
}

package com.simple.chris.pebble;

import android.animation.ObjectAnimator;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.util.JsonReader;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.LinearInterpolator;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.SimpleAdapter;
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
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

public class GradientsList extends AppCompatActivity {

    TextView gradientsFound;
    GridView gridView;
    SimpleAdapter adapter, backgroundAdapter;
    GridAdapter gridAdapter;
    public ArrayList<Integer> colours;
    ProgressDialog loading;
    Dialog connectingDialog;
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

        //gridView.setColumnWidth(size.x / 2 - 30);
        getItems();
    }

    private void getItems() {
        showConnectingDialog();
        //loading = ProgressDialog.show(this, "Loading Gradients", "Please wait...", false, true);
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

        int socketTimeOut = 50000;
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeOut, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);

        stringRequest.setRetryPolicy(policy);

        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(stringRequest);

    }

    private void parseItems(String jsonResponse) {
        Log.e("Info", "Got here");
        ArrayList<HashMap<String, String>> list = new ArrayList<>();

        try {
            ArrayList<String> listData = new ArrayList<String>();
            JSONObject jobj = new JSONObject(jsonResponse);
            JSONArray jarray = jobj.getJSONArray("items");


            for (int i = jarray.length() - 1; i >= 0; i--) { //int i = 0; i < jarray.length(); i++



                backgroundNames.add(jarray.getJSONObject(i).getString("backgroundName").replace(" ", "\n"));
                backgroundNamess = backgroundNames.toArray(new String[backgroundNames.size()]);
                String left = jarray.getJSONObject(i).getString("leftColour");
                //left.replace("#", "");
                //leftColours.add(Integer.parseInt(left, 16));
                leftColours.add(Color.parseColor(left));
                leftColourss = leftColours.stream().mapToInt(Integer::intValue).toArray();
                String right = jarray.getJSONObject(i).getString("rightColour");
                //right.replace("#", "");
                //rightColours.add(Integer.parseInt(right, 16));
                rightColours.add(Color.parseColor(right));
                rightColourss = rightColours.stream().mapToInt(Integer::intValue).toArray();
                descriptions.add(jarray.getJSONObject(i).getString("description"));
                descriptionss = descriptions.toArray(new String[descriptions.size()]);
                //names[i] = items.getString("backgroundName");




                /*String backgroundName = jo.getString("backgroundName");
                String leftColour = jo.getString("leftColour");
                String rightColour = jo.getString("rightColour");
                String description = jo.getString("description");

                backgroundName = backgroundName.replace(" ", "\n");
                leftColour = leftColour.replace("#", "");
                rightColour = rightColour.replace("#", "");
                hexL = Integer.parseInt(leftColour, 16);
                hexR = Integer.parseInt(rightColour, 16);
                colours.add(hexL);

                HashMap<String, String> item = new HashMap<>();
                item.put("backgroundName", backgroundName);
                item.put("leftColour", leftColour);
                item.put("rightColour", rightColour);
                item.put("description", description);

                Log.e("NOTE:", "Item got");

                list.add(item);

                //gradientsFound.setText("Gradients found: " + jarray);*/


            }
        } catch (JSONException e) {
            e.printStackTrace();
            Log.e("Info", "Failed "+e.getLocalizedMessage());
        }
        Log.e("Background Names", ""+ backgroundNamess);
        Log.e("Left Colours", ""+ leftColours);
        Log.e("Right Colours", ""+ rightColours);
        Log.e("Description", ""+ descriptions);

        /*adapter = new SimpleAdapter(this, list, R.layout.gridview_module,
                new String[]{"backgroundName"},
                new int[]{R.id.backgroundName});*/
        //gridAdapter = new GridAdapter(this,colours);
        GridAdapter gridAdapter = new GridAdapter(GradientsList.this, backgroundNamess, leftColourss, rightColourss);
        gridView.setAdapter(gridAdapter);

        /*adapter = new SimpleAdapter(this, list, R.layout.gridview_module,
                new String[]{"backgroundName", "leftColour", "rightColour", "description"},
                new int[]{R.id.backgroundName, R.id.leftColour, R.id.rightColour, R.id.description});*/
        SimpleAdapter.ViewBinder binder = new SimpleAdapter.ViewBinder() {
            @Override
            public boolean setViewValue(View view, Object data, String textRepresentation) {
                Log.e("ERR:", ""+view);
                if (view.equals((ImageView)view.findViewById(R.id.backgroundGradient))){
                    ImageView gradient = (ImageView)view.findViewById(R.id.backgroundGradient);
                    /*GradientDrawable gradientDrawable = new GradientDrawable(
                            GradientDrawable.Orientation.LEFT_RIGHT,
                            new int[]{1867488, 70}
                    );
                    gradientDrawable.setShape(GradientDrawable.RECTANGLE);
                    gradient.setBackground(gradientDrawable);*/
                    //gradient.setBackgroundColor(Color.parseColor(Integer.toHexString(hexL)));
                    Log.e("ERR:", "First if ran");
                }
                if (view instanceof ImageView){
                    ImageView gradient = (ImageView)view.findViewById(R.id.backgroundGradient);
                    GradientDrawable gradientDrawable = new GradientDrawable(
                            GradientDrawable.Orientation.LEFT_RIGHT,
                            new int[]{1867488, 70}
                    );
                    gradientDrawable.setShape(GradientDrawable.RECTANGLE);
                    gradient.setBackground(gradientDrawable);
                    Log.e("ERR:", "Second if ran");
                    return true;
                }
                Log.e("ERR:", "No if ran");
                return false;
            }
        };
        //gridView.setAdapter(gridAdapter);
        //Toast.makeText(this, "L: " + hexL + " R: " + hexR, Toast.LENGTH_SHORT).show();
        //loading.dismiss();
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
            for (int i = 0; i < jarray.length(); i++){
                JSONObject jo = jarray.getJSONObject(i);
                String serverStatus = jo.getString("serverStatus");
                String serverMessage = jo.getString("serverMessage");
            }
        }catch (Exception e){}

        Toast.makeText(this, "Status: " + list + "Message: ", Toast.LENGTH_SHORT).show();
    }

    public void showConnectingDialog() {

        //connectingDialog.setContentView(R.layout.dialog_connecting);
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


        /*LayoutInflater layoutInflater = LayoutInflater.from(DialogTestActivity.this);
        View view = layoutInflater.inflate(R.layout.dialog_connecting, null);
        view.setLayerType(View.LAYER_TYPE_HARDWARE, null);*/
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        gridView.setNumColumns(newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE ? 3 : 2);
        super.onConfigurationChanged(newConfig);
    }
}

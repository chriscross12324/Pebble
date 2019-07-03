package com.simple.chris.pebble;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class Check {

    public boolean isInterenetConnected(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() != null;
    }

    public boolean isNetworkTypeCellular(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = cm.getActiveNetworkInfo();
        boolean isData = networkInfo.getType() == ConnectivityManager.TYPE_MOBILE;
        return isData;
    }
}

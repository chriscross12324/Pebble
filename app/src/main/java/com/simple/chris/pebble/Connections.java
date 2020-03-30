package com.simple.chris.pebble;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import androidx.annotation.NonNull;

import java.util.Objects;

public class Connections {

    public static boolean isNetworkConnected(@NonNull Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        return Objects.requireNonNull(cm).getActiveNetwork() != null;
    }

    public static boolean isNetworkTypeData(@NonNull Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = Objects.requireNonNull(cm).getActiveNetworkInfo();
        return Objects.requireNonNull(networkInfo).getType() == ConnectivityManager.TYPE_MOBILE;
    }

}

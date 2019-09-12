package com.simple.chris.pebble;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Splash extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        Values.loadValues(Splash.this);

        Date date = new Date();
        SimpleDateFormat format = new SimpleDateFormat("MM-dd-yyyy");
        String formatedDate = format.format(date);
        Log.e("Date", formatedDate);

        if (formatedDate.equals("04-12-2020")) {
            Values.peppaPink = true;
        } else {
            Values.peppaPink = false;
        }


        if (!Values.firstStart) {
            startActivity(new Intent(Splash.this, ActivityConnecting.class));
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            finish();
        } else {
            startActivity(new Intent(Splash.this, Permissions.class));
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_in);
            finish();
        }

    }
}

package com.simple.chris.pebble;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.github.javiersantos.appupdater.AppUpdater;

import org.jsoup.Jsoup;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Splash extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        Values.INSTANCE.loadValues(Splash.this);

        if (!Values.INSTANCE.getFirstStart()) {
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

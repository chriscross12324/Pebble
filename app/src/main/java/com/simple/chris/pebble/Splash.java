package com.simple.chris.pebble;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

public class Splash extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        Values.loadValues(Splash.this);

        if (!Values.firstStart){
            startActivity(new Intent(Splash.this, GradientsScreen.class));
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            finish();
        }else {
            startActivity(new Intent(Splash.this, Permissions.class));
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_in);
            finish();
        }

    }
}

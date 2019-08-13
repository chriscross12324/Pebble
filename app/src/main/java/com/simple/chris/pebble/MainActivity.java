package com.simple.chris.pebble;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    Dialog connectingDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Values.loadValues(MainActivity.this);
        if (Values.firstStart) {
            Intent P = new Intent(MainActivity.this, Permissions.class);
            startActivity(P);
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            finish();
        } else {
            Intent GL = new Intent(MainActivity.this, GradientsScreen.class);
            startActivity(GL);
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            finish();
        }

        connectingDialog = new Dialog(this);

        Button loginButton = findViewById(R.id.button);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //showConnectingDialog();
                Intent gradients = new Intent(MainActivity.this, GradientsScreen.class);
                startActivity(gradients);
                //MainActivity.this.finish();
            }
        });

    }

}
package com.simple.chris.pebble;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;

public class GradientDetails extends AppCompatActivity {
    ImageView backButton;
    String backgroundName, leftColour, rightColour, description;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gradient_details);

        Intent intent = getIntent();
        backgroundName = intent.getStringExtra("backgroundName");
        leftColour = intent.getStringExtra("leftColour");
        rightColour = intent.getStringExtra("rightColour");
        description = intent.getStringExtra("description");

    }
}

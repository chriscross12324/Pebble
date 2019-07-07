package com.simple.chris.pebble;

import androidx.appcompat.app.AppCompatActivity;

import android.animation.ObjectAnimator;
import android.graphics.Point;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.Toast;

public class Setup extends AppCompatActivity {

    ImageView pebbleLogo;

    int screenHeight;
    int logoSize;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup);

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        screenHeight = displayMetrics.heightPixels;

        pebbleLogo = findViewById(R.id.pebbleLogo);
        Log.e("INFO", "Made it");

        while (logoSize == 0){
            logoSize = pebbleLogo.getHeight()/2;
            if (logoSize != 0){
                Log.e("Info", ""+logoSize);
                ObjectAnimator scaleX = ObjectAnimator.ofFloat(pebbleLogo, "scaleX", logoSize);
                //ObjectAnimator scaleY = ObjectAnimator.ofFloat(pebbleLogo, "scaleY", logoSize);
                scaleX.setDuration(2000);
                //scaleY.setDuration(800);
                scaleX.setInterpolator(new DecelerateInterpolator(3));
                //scaleY.setInterpolator(new DecelerateInterpolator(3));
                scaleX.start();
                //scaleY.start();
            }
        }







        //Toast.makeText(this, ""+pebbleLogo.getHeight(), Toast.LENGTH_SHORT).show();
        //Toast.makeText(this, ""+logoSize, Toast.LENGTH_SHORT).show();
    }
}

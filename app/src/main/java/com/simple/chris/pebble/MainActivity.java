package com.simple.chris.pebble;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    Dialog connectingDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Intent GL = new Intent(MainActivity.this, GradientsList.class);
        startActivity(GL);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        finish();
        connectingDialog = new Dialog(this);

        ImageView pebbleLogo = findViewById(R.id.pebbleLogo);
        pebbleLogo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent menuTest = new Intent(MainActivity.this, DialogTestActivity.class);
                startActivity(menuTest);
                MainActivity.this.finish();
                //showConnectingDialog();
            }
        });

        Button loginButton = findViewById(R.id.button);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //showConnectingDialog();
                Intent gradients = new Intent(MainActivity.this, GradientsList.class);
                startActivity(gradients);
                //MainActivity.this.finish();
            }
        });

    }

    public void showConnectingDialog() {
        connectingDialog.setContentView(R.layout.dialog_connecting);
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
}

/*class MysqlCon{
    public static void main(String args[]){
        try {
            Class.forName("com.simple.chris.pebble");
            Connection connection = DriverManager.getConnection(
                    "chris:mysql://"
            )
        }catch (Exception e){
            System.out.println(e);
        }
    }
}*/

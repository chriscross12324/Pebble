package com.simple.chris.pebble;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class Permissions extends AppCompatActivity {

    Dialog wifiPermission, dataWarning;
    Button understandButtonWifi, understandButtonData;
    ConstraintLayout warningNotification;
    int dialogWidth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        UIElements.INSTANCE.setTheme(Permissions.this);
        setContentView(R.layout.activity_permissions);

        wifiPermission = new Dialog(this);
        dataWarning = new Dialog(this);

        wifiPermission.setContentView(R.layout.dialog_wifi);
        dataWarning.setContentView(R.layout.dialog_data);

        understandButtonWifi = wifiPermission.findViewById(R.id.understandButton);
        understandButtonData = dataWarning.findViewById(R.id.understandButton);
        warningNotification = findViewById(R.id.warningNotification);

        warningNotification.setTranslationY(-90 * getResources().getDisplayMetrics().density);

        WindowManager.LayoutParams layoutParams = wifiPermission.getWindow().getAttributes();
        Window window = wifiPermission.getWindow();
        layoutParams.dimAmount = 0f;
        wifiPermission.getWindow().setAttributes(layoutParams);
        layoutParams.gravity = Gravity.CENTER;
        window.setAttributes(layoutParams);

        understandButtonWifi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                wifiPermission.dismiss();
                showDataWarningDialog();
                warningNotification.setAlpha(1);
            }
        });

        wifiPermission.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        wifiPermission.setCancelable(false);
        wifiPermission.show();
        wifiPermission.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                View view = wifiPermission.findViewById(R.id.rootLayout);
                dialogWidth = view.getWidth();
                Log.e("TAG", ""+dialogWidth);
            }
        });
        //dialogWidth = getWindow().getDecorView().getWidth();
        //Toast.makeText(this, ""+dialogWidth, Toast.LENGTH_SHORT).show();
    }

    public void showDataWarningDialog(){
        //asterisk.setVisibility(View.VISIBLE);
        WindowManager.LayoutParams layoutParams = dataWarning.getWindow().getAttributes();
        Window window = dataWarning.getWindow();
        layoutParams.dimAmount = 0f;
        //layoutParams.width = dialogWidth;
        dataWarning.getWindow().setAttributes(layoutParams);
        layoutParams.gravity = Gravity.CENTER;
        window.setAttributes(layoutParams);

        UIElements.INSTANCE.constraintLayoutObjectAnimator(warningNotification, "translationY",
                0, 500,
                200, new DecelerateInterpolator(3));

        understandButtonData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UIElements.INSTANCE.constraintLayoutObjectAnimator(warningNotification, "translationY",
                        Math.round(-90 * getResources().getDisplayMetrics().density), 500,
                        0, new DecelerateInterpolator(3));
                Values.INSTANCE.setFirstStart(false);
                dataWarning.dismiss();
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Intent GL = new Intent(Permissions.this, ActivityConnecting.class);
                        startActivity(GL);
                        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                        finish();
                    }
                },700);

            }
        });


        dataWarning.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dataWarning.setCancelable(false);
        dataWarning.show();
    }
}

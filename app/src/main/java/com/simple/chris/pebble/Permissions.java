package com.simple.chris.pebble;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.Image;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

public class Permissions extends AppCompatActivity {

    Dialog wifiPermission, dataWarning;
    LinearLayout understandButtonWifi, understandButtonData;
    int dialogWidth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Values.darkMode) {
            setTheme(R.style.ThemeDark);
        } else {
            setTheme(R.style.ThemeLight);
        }
        setContentView(R.layout.activity_permissions);
        ImageView background = (ImageView) findViewById(R.id.background);
        if (Values.darkMode) {
            background.setBackgroundResource(R.drawable.placeholder_gradient_dark);
        } else {
            background.setBackgroundResource(R.drawable.placeholder_gradient_light);
        }

        wifiPermission = new Dialog(this);
        dataWarning = new Dialog(this);

        wifiPermission.setContentView(R.layout.dialog_permission_wifi);
        dataWarning.setContentView(R.layout.dialog_warning_data);

        understandButtonWifi = wifiPermission.findViewById(R.id.understandButton);
        understandButtonData = dataWarning.findViewById(R.id.understandButton);

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
        WindowManager.LayoutParams layoutParams = dataWarning.getWindow().getAttributes();
        Window window = dataWarning.getWindow();
        layoutParams.dimAmount = 0f;
        //layoutParams.width = dialogWidth;
        dataWarning.getWindow().setAttributes(layoutParams);
        layoutParams.gravity = Gravity.CENTER;
        window.setAttributes(layoutParams);

        understandButtonData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dataWarning.dismiss();
                Intent GL = new Intent(Permissions.this, GradientsList.class);
                startActivity(GL);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                finish();
            }
        });

        dataWarning.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dataWarning.setCancelable(false);
        dataWarning.show();
    }
}

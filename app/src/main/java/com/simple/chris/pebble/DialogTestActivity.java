package com.simple.chris.pebble;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

public class DialogTestActivity extends AppCompatActivity {

    Dialog signInDialog, signOutDialog, registerDialog, createGradientDialog, connectingDialog, createGroupDialog;
    Button signInButton, signOutButton, registerButton, createGradientButton, connectingButton, createGroupButton;
    ImageView closeButton, darkButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Values.currentActivity == "Debug") {
            setTheme(R.style.ThemeDark);
        }else {
            setTheme(R.style.ThemeLight);
        }

        setContentView(R.layout.activity_dialog_test);

        signInDialog = new Dialog(this);
        signOutDialog = new Dialog(this);
        registerDialog = new Dialog(this);
        connectingDialog = new Dialog(this);
        createGroupDialog = new Dialog(this);


        signInButton = findViewById(R.id.signInButton);
        signOutButton = findViewById(R.id.signOutButton);
        registerButton = findViewById(R.id.registerButton);
        connectingButton = findViewById(R.id.connectingPopupButton);
        createGroupButton = findViewById(R.id.createGroupPopupButton);
        darkButton = findViewById(R.id.darkButton);

        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showSignInDialog();
            }
        });
        signOutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showSignOutDialog();
            }
        });
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showRegisterDialog();
            }
        });
        connectingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showConnectingDialog();
            }
        });
        createGroupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showCreateGroupDialog();
            }
        });
        darkButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setTheme(R.style.ThemeDark);
                Intent intent = getIntent();
                finish();
                startActivity(intent);
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        Values.currentActivity = "Debug";
    }

    public void showSignInDialog() {
        signInDialog.setContentView(R.layout.dialog_signin);
        closeButton = signInDialog.findViewById(R.id.closeButton);

        WindowManager.LayoutParams lp = signInDialog.getWindow().getAttributes();
        Window window = signInDialog.getWindow();
        lp.dimAmount = 0.8f;
        signInDialog.getWindow().setAttributes(lp);
        lp.gravity = Gravity.BOTTOM;
        window.setAttributes(lp);

        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signInDialog.dismiss();
            }
        });

        signInDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        signInDialog.setCancelable(false);
        signInDialog.show();
    }
    public void showSignOutDialog() {
        signOutDialog.setContentView(R.layout.dialog_signout);
        closeButton = signOutDialog.findViewById(R.id.closeButton);

        WindowManager.LayoutParams lp = signOutDialog.getWindow().getAttributes();
        Window window = signOutDialog.getWindow();
        lp.dimAmount = 0.8f;
        signOutDialog.getWindow().setAttributes(lp);
        lp.gravity = Gravity.BOTTOM;
        window.setAttributes(lp);

        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signOutDialog.dismiss();
            }
        });

        signOutDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        signOutDialog.setCancelable(false);
        signOutDialog.show();
    }
    public void showRegisterDialog() {
        registerDialog.setContentView(R.layout.dialog_register);
        closeButton = registerDialog.findViewById(R.id.closeButton);

        WindowManager.LayoutParams lp = registerDialog.getWindow().getAttributes();
        Window window = registerDialog.getWindow();
        lp.dimAmount = 0.8f;
        registerDialog.getWindow().setAttributes(lp);
        lp.gravity = Gravity.BOTTOM;
        window.setAttributes(lp);

        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerDialog.dismiss();
            }
        });

        registerDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        registerDialog.setCancelable(false);
        registerDialog.show();
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

    public void showCreateGroupDialog() {
        createGroupDialog.setContentView(R.layout.dialog_create_group);
        closeButton = createGroupDialog.findViewById(R.id.closeButton);

        WindowManager.LayoutParams lp = createGroupDialog.getWindow().getAttributes();
        Window window = createGroupDialog.getWindow();
        lp.dimAmount = 0.8f;
        createGroupDialog.getWindow().setAttributes(lp);
        lp.gravity = Gravity.BOTTOM;
        window.setAttributes(lp);

        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createGroupDialog.dismiss();
            }
        });

        createGroupDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        createGroupDialog.setCancelable(false);
        createGroupDialog.show();
    }
}

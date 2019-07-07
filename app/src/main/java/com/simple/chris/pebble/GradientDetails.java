package com.simple.chris.pebble;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.animation.ObjectAnimator;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class GradientDetails extends AppCompatActivity {
    CardView cardView;
    ConstraintLayout detailsHolder;
    ImageView backButton, gradientViewer, topColourCircle, bottomColourCircle;
    TextView detailsTitle, detailsDescription, topColourHex, bottomColourHex;
    String backgroundName, leftColour, rightColour, description;
    int leftColourInt, rightColourInt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        isDarkTheme();
        setContentView(R.layout.activity_gradient_details);
        postponeEnterTransition();
        Values.currentActivity = "GradientDetails";

        /** Declare UI Elements */
        //ConstraintLayout
        detailsHolder = findViewById(R.id.detailsHolder);

        //CardView
        cardView = findViewById(R.id.cardView);
        //ImageView
        backButton = findViewById(R.id.backButton);
        gradientViewer = findViewById(R.id.gradientViewer);
        topColourCircle = findViewById(R.id.topColourCircle);
        bottomColourCircle = findViewById(R.id.bottomColourCircle);

        //TextView
        detailsTitle = findViewById(R.id.detailsTitle);
        detailsDescription = findViewById(R.id.detailsDescription);
        topColourHex = findViewById(R.id.topColourHex);
        bottomColourHex = findViewById(R.id.bottomColourHex);


        Intent intent = getIntent();
        backgroundName = intent.getStringExtra("backgroundName");
        Log.e("INFO", backgroundName);
        leftColour = intent.getStringExtra("leftColour");
        rightColour = intent.getStringExtra("rightColour");
        description = intent.getStringExtra("description");
        rightColourInt = Color.parseColor(rightColour);
        leftColourInt = Color.parseColor(leftColour);
        //Toast.makeText(this, ""+backgroundName, Toast.LENGTH_SHORT).show();
        //Log.e("INFO", backgroundName);

        int left = Color.parseColor(leftColour);
        int right = Color.parseColor(rightColour);

        GradientDrawable gradientDrawable = new GradientDrawable(
                GradientDrawable.Orientation.TL_BR,
                new int[]{left, right}
        );
        gradientViewer.setBackgroundDrawable(gradientDrawable);
        //cardView.setCardBackgroundColor(right);
        cardView.setTransitionName(backgroundName);
        gradientViewer.setTransitionName(backgroundName+"1");

        backButton.setOnClickListener(v -> {
            ObjectAnimator OA2 = ObjectAnimator.ofFloat(backButton, "alpha", 0);
            OA2.setDuration(200);
            OA2.setInterpolator(new LinearInterpolator());
            OA2.start();
            ObjectAnimator OA3 = ObjectAnimator.ofFloat(detailsHolder, "alpha", 0);
            OA3.setDuration(300);
            OA3.setInterpolator(new LinearInterpolator());
            OA3.start();
            this.onBackPressed();

        });

        gradientViewer.post(() -> {
            scheduledStartPostponedTransition(cardView);
            /*ObjectAnimator OA1 = ObjectAnimator.ofFloat(backButton, "alpha", 1);
            OA1.setDuration(200);
            OA1.setInterpolator(new LinearInterpolator());
            OA1.start();*/
            ObjectAnimator OA3 = ObjectAnimator.ofFloat(detailsHolder, "alpha", 1);
            OA3.setDuration(300);
            OA3.setInterpolator(new LinearInterpolator());
            OA3.start();
        });

        detailsTitle.setText(backgroundName.replace("\n", " "));
        detailsDescription.setText(description);
        topColourHex.setText(leftColour);
        bottomColourHex.setText(rightColour);
        GradientDrawable topColourCircleDrawable = new GradientDrawable();
        topColourCircleDrawable.setShape(GradientDrawable.OVAL);
        topColourCircleDrawable.setStroke(5, leftColourInt);
        GradientDrawable bottomColourCircleDrawable = new GradientDrawable();
        bottomColourCircleDrawable.setShape(GradientDrawable.OVAL);
        bottomColourCircleDrawable.setStroke(5, rightColourInt);
        topColourCircle.setBackgroundDrawable(topColourCircleDrawable);
        bottomColourCircle.setBackgroundDrawable(bottomColourCircleDrawable);
    }

    public boolean isDarkTheme(){
        if (Values.darkMode) {
            setTheme(R.style.ThemeDark);
            return true;
        } else {
            setTheme(R.style.ThemeLight);
            return false;
        }
    }

    private void scheduledStartPostponedTransition(final View sharedElement){
        sharedElement.getViewTreeObserver().addOnPreDrawListener(
                new ViewTreeObserver.OnPreDrawListener() {
                    @Override
                    public boolean onPreDraw() {
                        sharedElement.getViewTreeObserver().removeOnPreDrawListener(this);
                        startPostponedEnterTransition();
                        return true;
                    }
                }
        );
    }

    /*@Override
    public void onBackPressed() {
        finish();
        return;
    }*/
}

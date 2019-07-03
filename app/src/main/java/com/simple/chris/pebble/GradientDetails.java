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
    ImageView backButton, gradientViewer;
    TextView backgroundNameTextView, descriptionTextView;
    String backgroundName, leftColour, rightColour, description;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        isDarkTheme();
        setContentView(R.layout.activity_gradient_details);
        postponeEnterTransition();
        Values.currentActivity = "GradientDetails";

        /** Declare UI Elements */
        //ConstraintLayout
        //CardView
        cardView = findViewById(R.id.cardView);
        //ImageView
        backButton = findViewById(R.id.backButton);
        gradientViewer = findViewById(R.id.gradientViewer);
        //TextView
        backgroundNameTextView = findViewById(R.id.backgroundNameTextView);
        descriptionTextView = findViewById(R.id.descriptionTextView);

        Intent intent = getIntent();
        backgroundName = intent.getStringExtra("backgroundName");
        leftColour = intent.getStringExtra("leftColour");
        rightColour = intent.getStringExtra("rightColour");
        description = intent.getStringExtra("description");
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
        backgroundNameTextView.setText(backgroundName);
        descriptionTextView.setText(description);
        cardView.setTransitionName(backgroundName);
        gradientViewer.setTransitionName(backgroundName+"1");

        gradientViewer.post(new Runnable() {
            @Override
            public void run() {
                scheduledStartPostponedTransition(cardView);
                ObjectAnimator OA1 = ObjectAnimator.ofFloat(backButton, "alpha", 1);
                OA1.setDuration(200);
                OA1.setInterpolator(new LinearInterpolator());
                OA1.start();
            }
        });

        backButton.setOnClickListener(v -> {
            ObjectAnimator OA2 = ObjectAnimator.ofFloat(backButton, "alpha", 0);
            OA2.setDuration(200);
            OA2.setInterpolator(new LinearInterpolator());
            OA2.start();
            this.onBackPressed();

        });
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

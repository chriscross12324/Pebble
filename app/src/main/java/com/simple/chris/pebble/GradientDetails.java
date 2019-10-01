package com.simple.chris.pebble;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;

public class GradientDetails extends AppCompatActivity {
    CardView corners;
    ConstraintLayout detailsHolder, actionsHolder, copiedNotification;
    LinearLayout backButton, hideButton, startHex, endHex;
    ImageView gradientViewer, topColourCircle, bottomColourCircle, arrow;
    TextView detailsTitle, detailsDescription, topColourHex, bottomColourHex, copiedText;
    String backgroundName, leftColour, rightColour, description;
    int leftColourInt, rightColourInt, detailsDefaultHeight;
    boolean expanded = false;
    boolean playingCopiedAnimation = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        switch (Values.theme) {
            case "light":
                setTheme(R.style.ThemeLight);
                break;
            case "dark":
                setTheme(R.style.ThemeDark);
                break;
            case "black":
                setTheme(R.style.ThemeBlack);
                break;
        }
        setContentView(R.layout.activity_gradient_details);
        postponeEnterTransition();
        Values.currentActivity = "GradientDetails";

        /** Declare UI Elements */
        //ConstraintLayout
        detailsHolder = findViewById(R.id.detailsHolder);
        actionsHolder = findViewById(R.id.actionsHolder);
        copiedNotification = findViewById(R.id.copiedNotification);

        //LinearLayout
        backButton = findViewById(R.id.backButton);
        hideButton = findViewById(R.id.hideButton);
        startHex = findViewById(R.id.startHex);
        endHex = findViewById(R.id.endHex);

        //CardView
        corners = findViewById(R.id.corners);

        //ImageView
        gradientViewer = findViewById(R.id.gradientViewer);
        topColourCircle = findViewById(R.id.topColourCircle);
        bottomColourCircle = findViewById(R.id.bottomColourCircle);
        arrow = findViewById(R.id.arrow);

        //TextView
        detailsTitle = findViewById(R.id.detailsTitle);
        detailsDescription = findViewById(R.id.detailsDescription);
        topColourHex = findViewById(R.id.topColourHex);
        bottomColourHex = findViewById(R.id.bottomColourHex);
        copiedText = findViewById(R.id.copiedText);

        //Get-Set values from previous activity
        Intent intent = getIntent();
        backgroundName = intent.getStringExtra("gradientName");

        //Determines if uppercase HEX value
        if (Values.uppercaseHEX) {
            leftColour = intent.getStringExtra("startColour").toUpperCase();
            rightColour = intent.getStringExtra("endColour").toUpperCase();
        } else {
            leftColour = intent.getStringExtra("startColour").toLowerCase();
            rightColour = intent.getStringExtra("endColour").toLowerCase();
        }
        description = intent.getStringExtra("description");
        rightColourInt = Color.parseColor(rightColour);
        leftColourInt = Color.parseColor(leftColour);
        int left = Color.parseColor(leftColour);
        int right = Color.parseColor(rightColour);

        //Sets background gradient
        GradientDrawable gradientDrawable = new GradientDrawable(
                GradientDrawable.Orientation.TL_BR,
                new int[]{left, right}
        );
        gradientViewer.setBackgroundDrawable(gradientDrawable);
        corners.setTransitionName(backgroundName);
        gradientViewer.setTransitionName(backgroundName + "1");

        if (description.equals("")) {
            detailsDescription.setVisibility(View.GONE);
        }

        startHex.setOnLongClickListener(view -> {
            ClipboardManager clipboardManager = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData clipData = ClipData.newPlainText("startHex", leftColour);
            clipboardManager.setPrimaryClip(clipData);
            copiedNotification.setAlpha(1);
            if (!playingCopiedAnimation) {
                playingCopiedAnimation = true;
                Vibration.INSTANCE.hFeedack(GradientDetails.this);
                UIAnimations.constraintLayoutObjectAnimator(copiedNotification, "translationY",
                        0, 500,
                        0, new DecelerateInterpolator(3));
                UIAnimations.constraintLayoutObjectAnimator(copiedNotification, "translationY",
                        Math.round(-45 * getResources().getDisplayMetrics().density), 500,
                        2000, new DecelerateInterpolator(3));
                UIAnimations.constraintLayoutAlpha(copiedNotification, 0, 2500);
                boolean handler = new Handler().postDelayed(() -> {
                    playingCopiedAnimation = false;
                }, 2500);
            }
            return false;
        });

        endHex.setOnLongClickListener(view -> {
            ClipboardManager clipboardManager = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData clipData = ClipData.newPlainText("endHex", rightColour);
            clipboardManager.setPrimaryClip(clipData);
            copiedNotification.setAlpha(1);
            if (!playingCopiedAnimation) {
                playingCopiedAnimation = true;
                Vibration.INSTANCE.hFeedack(GradientDetails.this);
                UIAnimations.constraintLayoutObjectAnimator(copiedNotification, "translationY",
                        0, 500,
                        0, new DecelerateInterpolator(3));
                UIAnimations.constraintLayoutObjectAnimator(copiedNotification, "translationY",
                        Math.round(-45 * getResources().getDisplayMetrics().density), 500,
                        2000, new DecelerateInterpolator(3));
                UIAnimations.constraintLayoutAlpha(copiedNotification, 0, 2500);
                boolean handler = new Handler().postDelayed(() -> {
                    playingCopiedAnimation = false;
                }, 2500);
            }
            return false;
        });

        detailsHolder.setOnClickListener(v -> {
            if (expanded) {
                expanded = false;
                UIAnimations.constraintLayoutValueAnimator(detailsHolder, 50 * getResources().getDisplayMetrics().density,
                        detailsDefaultHeight, 700,
                        0, new DecelerateInterpolator(3));
                UIAnimations.constraintLayoutObjectAnimator(actionsHolder, "translationY",
                        0, 400,
                        50, new DecelerateInterpolator());
                UIAnimations.constraintLayoutObjectAnimator(detailsHolder, "translationY",
                        0, 400,
                        0, new DecelerateInterpolator());
                UIAnimations.imageViewObjectAnimator(arrow, "alpha",
                        0, 200,
                        0, new DecelerateInterpolator());
                UIAnimations.textViewObjectAnimator(detailsTitle, "alpha",
                        1, 200,
                        100, new DecelerateInterpolator());
                expanded = false;
            }
        });

        backButton.setOnClickListener(v -> {
            UIAnimations.constraintLayoutObjectAnimator(detailsHolder, "translationY",
                    Math.round((90 * getResources().getDisplayMetrics().density) + detailsHolder.getHeight()), 250,
                    50, new DecelerateInterpolator()
            );
            UIAnimations.constraintLayoutObjectAnimator(actionsHolder, "translationY",
                    Math.round((74 * getResources().getDisplayMetrics().density) + detailsHolder.getHeight()), 250,
                    0, new DecelerateInterpolator()
            );
            UIAnimations.constraintLayoutAlpha(detailsHolder, 0, 300);
            UIAnimations.constraintLayoutAlpha(actionsHolder, 0, 300);
            UIAnimations.constraintLayoutAlpha(copiedNotification, 0, 300);
            Handler handler1 = new Handler();
            handler1.postDelayed(() -> GradientDetails.super.onBackPressed(), 250);

        });
        hideButton.setOnClickListener(v -> {
            UIAnimations.constraintLayoutValueAnimator(detailsHolder, detailsHolder.getMeasuredHeight(),
                    50 * getResources().getDisplayMetrics().density, 700,
                    0, new DecelerateInterpolator(3));
            UIAnimations.constraintLayoutObjectAnimator(detailsHolder, "translationY",
                    74 * getResources().getDisplayMetrics().density, 400,
                    0, new DecelerateInterpolator());
            UIAnimations.constraintLayoutObjectAnimator(actionsHolder, "translationY",
                    74 * getResources().getDisplayMetrics().density, 400,
                    25, new DecelerateInterpolator());
            UIAnimations.textViewObjectAnimator(detailsTitle, "alpha",
                    0, 200,
                    0, new DecelerateInterpolator());
            UIAnimations.imageViewObjectAnimator(arrow, "alpha",
                    1, 200,
                    200, new DecelerateInterpolator());
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    expanded = true;
                }
            }, 400);

        });

        gradientViewer.post(() -> {
            //Start sharedElement transition
            scheduledStartPostponedTransition(corners);

            //Set UI Elements position
            detailsHolder.setTranslationY((90 * getResources().getDisplayMetrics().density) + detailsHolder.getHeight());
            actionsHolder.setTranslationY((74 * getResources().getDisplayMetrics().density) + detailsHolder.getHeight());
            copiedNotification.setTranslationY(-45 * getResources().getDisplayMetrics().density);

            //Animate UI Elements
            UIAnimations.constraintLayoutAlpha(detailsHolder, 1, 500);
            UIAnimations.constraintLayoutAlpha(actionsHolder, 1, 500);
            UIAnimations.constraintLayoutObjectAnimator(actionsHolder, "translationY",
                    0, 700,
                    550, new DecelerateInterpolator(3));
            UIAnimations.constraintLayoutObjectAnimator(detailsHolder, "translationY",
                    0, 700,
                    500, new DecelerateInterpolator(3));

            //Store detailsHolder height
            detailsDefaultHeight = detailsHolder.getHeight();
        });

        detailsTitle.setText(backgroundName.replace("\n", " "));
        detailsDescription.setText(description);
        if (description == null) {
            detailsDescription.setVisibility(View.GONE);
        }
        topColourHex.setText(leftColour);
        bottomColourHex.setText(rightColour);
        GradientDrawable topColourCircleDrawable = new GradientDrawable();
        topColourCircleDrawable.setShape(GradientDrawable.OVAL);
        topColourCircleDrawable.setStroke(6, leftColourInt);
        GradientDrawable bottomColourCircleDrawable = new GradientDrawable();
        bottomColourCircleDrawable.setShape(GradientDrawable.OVAL);
        bottomColourCircleDrawable.setStroke(6, rightColourInt);
        topColourCircle.setBackgroundDrawable(topColourCircleDrawable);
        bottomColourCircle.setBackgroundDrawable(bottomColourCircleDrawable);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            gradientViewer.setOutlineSpotShadowColor(rightColourInt);
            corners.setOutlineSpotShadowColor(rightColourInt);
        }
    }

    private void scheduledStartPostponedTransition(final View sharedElement) {
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


    @Override
    public void onBackPressed() {
        UIAnimations.constraintLayoutObjectAnimator(detailsHolder, "translationY",
                Math.round((90 * getResources().getDisplayMetrics().density) + detailsHolder.getHeight()), 250,
                50, new DecelerateInterpolator()
        );
        UIAnimations.constraintLayoutObjectAnimator(actionsHolder, "translationY",
                Math.round((74 * getResources().getDisplayMetrics().density) + detailsHolder.getHeight()), 250,
                0, new DecelerateInterpolator()
        );
        UIAnimations.constraintLayoutAlpha(detailsHolder, 0, 300);
        UIAnimations.constraintLayoutAlpha(actionsHolder, 0, 300);
        UIAnimations.constraintLayoutAlpha(copiedNotification, 0, 300);
        Handler handler1 = new Handler();
        handler1.postDelayed(() -> GradientDetails.super.onBackPressed(), 250);
        return;
    }
}

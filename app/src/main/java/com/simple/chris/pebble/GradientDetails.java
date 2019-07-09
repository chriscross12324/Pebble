package com.simple.chris.pebble;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class GradientDetails extends AppCompatActivity {
    CardView corners;
    ConstraintLayout detailsHolder, actionsHolder, copiedNotification;
    LinearLayout backButton, hideButton, startHex, endHex;
    ImageView gradientViewer, topColourCircle, bottomColourCircle, arrow;
    TextView detailsTitle, detailsDescription, topColourHex, bottomColourHex;
    String backgroundName, leftColour, rightColour, description;
    int leftColourInt, rightColourInt, detailsDefaultHeight;
    boolean expanded = false;
    boolean playingCopiedAnimation = false;

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


        Intent intent = getIntent();
        backgroundName = intent.getStringExtra("backgroundName");
        Log.e("INFO", backgroundName);
        if (Values.uppercaseHEX){
            leftColour = intent.getStringExtra("leftColour").toUpperCase();
            rightColour = intent.getStringExtra("rightColour").toUpperCase();
        }else {
            leftColour = intent.getStringExtra("leftColour").toLowerCase();
            rightColour = intent.getStringExtra("rightColour").toLowerCase();
        }
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
        corners.setTransitionName(backgroundName);
        gradientViewer.setTransitionName(backgroundName+"1");

        startHex.setOnClickListener(v -> {
            ClipboardManager clipboardManager = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData clipData = ClipData.newPlainText("startHex", leftColour);
            clipboardManager.setPrimaryClip(clipData);
            copiedNotification.setAlpha(1);
            if (!playingCopiedAnimation){
                UIAnimations.constraintLayoutObjectAnimator(copiedNotification,
                        "translationY",
                        0,
                        500,
                        new DecelerateInterpolator(3));
                Handler copiedUp =  new Handler();
                copiedUp.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        UIAnimations.constraintLayoutObjectAnimator(copiedNotification,
                                "translationY",
                                Math.round(-45 * getResources().getDisplayMetrics().density),
                                500,
                                new DecelerateInterpolator(3));
                        Handler copiedHide = new Handler();
                        copiedHide.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                copiedNotification.setAlpha(0);
                            }
                        }, 500);
                    }
                }, 2000);
            }
        });
        endHex.setOnClickListener(v -> {
            ClipboardManager clipboardManager = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData clipData = ClipData.newPlainText("endHex", rightColour);
            clipboardManager.setPrimaryClip(clipData);
            copiedNotification.setAlpha(1);
            if (!playingCopiedAnimation){
                UIAnimations.constraintLayoutObjectAnimator(copiedNotification,
                        "translationY",
                        0,
                        500,
                        new DecelerateInterpolator(3));
                Handler copiedUp =  new Handler();
                copiedUp.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        UIAnimations.constraintLayoutObjectAnimator(copiedNotification,
                                "translationY",
                                Math.round(-45 * getResources().getDisplayMetrics().density),
                                500,
                                new DecelerateInterpolator(3));
                        Handler copiedHide = new Handler();
                        copiedHide.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                copiedNotification.setAlpha(0);
                            }
                        }, 500);
                    }
                }, 2000);
            }
        });

        detailsHolder.setOnClickListener(v -> {
            if (expanded){
                expanded = false;
                UIAnimations.constraintLayoutValueAnimator(detailsHolder,
                        50 * getResources().getDisplayMetrics().density,
                        detailsDefaultHeight,
                        700,
                        new DecelerateInterpolator(3));
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        ObjectAnimator shiftUp = ObjectAnimator.ofFloat(actionsHolder, "translationY", 0);
                        shiftUp.setInterpolator(new DecelerateInterpolator());
                        shiftUp.setDuration(400);
                        shiftUp.start();
                    }
                }, 50);
                ObjectAnimator shiftUp2 = ObjectAnimator.ofFloat(detailsHolder, "translationY", 0);
                shiftUp2.setInterpolator(new DecelerateInterpolator());
                shiftUp2.setDuration(400);
                shiftUp2.start();
                expanded = false;
                ObjectAnimator OA1 = ObjectAnimator.ofFloat(arrow, "alpha", 0);
                OA1.setDuration(200);
                OA1.setInterpolator(new DecelerateInterpolator());
                OA1.start();
                Handler handler3 = new Handler();
                handler3.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        ObjectAnimator OA2 = ObjectAnimator.ofFloat(detailsTitle, "alpha", 1);
                        OA2.setDuration(200);
                        OA2.setInterpolator(new DecelerateInterpolator());
                        OA2.start();
                    }
                }, 100);
            }else {
                Toast.makeText(this, "Not expanded", Toast.LENGTH_SHORT).show();
            }
        });

        backButton.setOnClickListener(v -> {
            /*ObjectAnimator OA3 = ObjectAnimator.ofFloat(detailsHolder, "alpha", 0);
            OA3.setDuration(2000);
            OA3.setInterpolator(new LinearInterpolator());
            OA3.start();
            ObjectAnimator OA4 = ObjectAnimator.ofFloat(actionsHolder, "alpha", 0);
            OA4.setDuration(2000);
            OA4.setInterpolator(new LinearInterpolator());
            OA4.start();*/
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    ObjectAnimator shiftUp2 = ObjectAnimator.ofFloat(detailsHolder, "translationY", (90 * getResources().getDisplayMetrics().density)+detailsHolder.getHeight());
                    shiftUp2.setInterpolator(new DecelerateInterpolator());
                    shiftUp2.setDuration(300);
                    shiftUp2.start();
                }
            }, 50);
            ObjectAnimator shiftUp = ObjectAnimator.ofFloat(actionsHolder, "translationY", (74 * getResources().getDisplayMetrics().density)+detailsHolder.getHeight());
            shiftUp.setInterpolator(new DecelerateInterpolator());
            shiftUp.setDuration(300);
            shiftUp.start();
            Handler handler1 = new Handler();
            handler1.postDelayed(new Runnable() {
                @Override
                public void run() {
                    detailsHolder.setAlpha(0);
                    actionsHolder.setAlpha(0);
                    copiedNotification.setAlpha(1);
                    GradientDetails.super.onBackPressed();
                }
            }, 350);
            //this.onBackPressed();

        });
        hideButton.setOnClickListener(v -> {
            /*float newHeight = 50 * getResources().getDisplayMetrics().density;
            Toast.makeText(this, ""+newHeight, Toast.LENGTH_SHORT).show();
            ObjectAnimator hide = ObjectAnimator.ofInt(detailsHolder, detailsTitle.getHeight(), detailsHolder.getHeight(), 50);
            hide.setInterpolator(new DecelerateInterpolator());
            hide.setDuration(5000);
            hide.start();*/
            ValueAnimator hide = ValueAnimator.ofInt(detailsHolder.getMeasuredHeight(), Math.round(50 * getResources().getDisplayMetrics().density));
            hide.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    int val = (Integer) animation.getAnimatedValue();
                    ViewGroup.LayoutParams layoutParams = detailsHolder.getLayoutParams();
                    layoutParams.height = val;
                    detailsHolder.setLayoutParams(layoutParams);
                }
            });
            hide.setInterpolator(new DecelerateInterpolator(3));
            hide.setDuration(700);
            hide.start();
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    ObjectAnimator shiftDown = ObjectAnimator.ofFloat(detailsHolder, "translationY", 74 * getResources().getDisplayMetrics().density);
                    shiftDown.setInterpolator(new DecelerateInterpolator());
                    shiftDown.setDuration(400);
                    shiftDown.start();
                }
            }, 50);
            ObjectAnimator shiftDown2 = ObjectAnimator.ofFloat(actionsHolder, "translationY", 74 * getResources().getDisplayMetrics().density);
            shiftDown2.setInterpolator(new DecelerateInterpolator());
            shiftDown2.setDuration(400);
            shiftDown2.start();
            expanded = true;
            ObjectAnimator OA1 = ObjectAnimator.ofFloat(detailsTitle, "alpha", 0);
            OA1.setDuration(200);
            OA1.setInterpolator(new DecelerateInterpolator());
            OA1.start();
            Handler handler2 = new Handler();
            handler2.postDelayed(new Runnable() {
                @Override
                public void run() {
                    ObjectAnimator OA2 = ObjectAnimator.ofFloat(arrow, "alpha", 1);
                    OA2.setDuration(200);
                    OA2.setInterpolator(new DecelerateInterpolator());
                    OA2.start();
                }
            }, 200);
            /*ViewGroup.LayoutParams params = detailsHolder.getLayoutParams();
            params.height = Math.round(50*getResources().getDisplayMetrics().density);
            detailsHolder.setLayoutParams(params); */
        });

        gradientViewer.post(() -> {
            scheduledStartPostponedTransition(corners);
            /*ObjectAnimator OA1 = ObjectAnimator.ofFloat(backButton, "alpha", 1);
            OA1.setDuration(200);
            OA1.setInterpolator(new LinearInterpolator());
            OA1.start();*/

            detailsHolder.setTranslationY((90 * getResources().getDisplayMetrics().density)+detailsHolder.getHeight());
            actionsHolder.setTranslationY((74 * getResources().getDisplayMetrics().density)+detailsHolder.getHeight());
            copiedNotification.setTranslationY(-45 * getResources().getDisplayMetrics().density);
            Handler handlerMain = new Handler();
            handlerMain.postDelayed(new Runnable() {
                @Override
                public void run() {
                    detailsHolder.setAlpha(1);
                    actionsHolder.setAlpha(1);
                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            ObjectAnimator shiftUp = ObjectAnimator.ofFloat(actionsHolder, "translationY", 0);
                            shiftUp.setInterpolator(new DecelerateInterpolator(3));
                            shiftUp.setDuration(700);
                            shiftUp.start();
                        }
                    }, 50);
                    ObjectAnimator shiftUp2 = ObjectAnimator.ofFloat(detailsHolder, "translationY", 0);
                    shiftUp2.setInterpolator(new DecelerateInterpolator(3));
                    shiftUp2.setDuration(700);
                    shiftUp2.start();
                }
            }, 500);
            detailsDefaultHeight = detailsHolder.getHeight();
        });

        detailsTitle.setText(backgroundName.replace("\n", " "));
        detailsDescription.setText(description);
        if (description == null){
            detailsDescription.setVisibility(View.GONE);
        }
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

    @Override
    public void onBackPressed() {
        return;
    }
}

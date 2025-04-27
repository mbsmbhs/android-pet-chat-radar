package com.petplore.app.radarItems;

import android.app.Activity;
import android.os.Build;
import android.os.Handler;
import android.view.View;
import android.widget.ImageView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;

public class PulsingImage {


    // passed elements
    Activity activity;
    int constraintLayoutForPulseId;
    int selectedPulseImageRID;
    int howManyPulses;
    int width;
    int height;
    float maxSizeMultiply;
    int pulseDuration;

    // local elements
    Handler handlerAnimation;
    Runnable[] pulseRunnableSeriesAnimations;
    ImageView[] pulseImages;

    public PulsingImage() {
    }

    public PulsingImage(Activity activity,
                        int constraintLayoutForPulseId,
                        int selectedPulseImageRID,
                        int howManyPulses,
                        int width,
                        int height,
                        float maxSizeMultiply,
                        int pulseDuration) {
        this.activity = activity;
        this.constraintLayoutForPulseId = constraintLayoutForPulseId;
        this.selectedPulseImageRID = selectedPulseImageRID;
        this.howManyPulses = howManyPulses;
        this.width = width;
        this.height = height;
        this.maxSizeMultiply = maxSizeMultiply;
        this.pulseDuration = pulseDuration;


        pulseRunnableSeriesAnimations = new Runnable[howManyPulses];
        pulseImages = new ImageView[howManyPulses];

        for (int i = 0; i < howManyPulses; i++) {

            // Start Create An ImageView From Passed Pulse Picture

            // first to get main constraint layout
            ConstraintLayout mainPulseAnimationHolder = activity.findViewById(constraintLayoutForPulseId);

            // TODO set pulse images
            pulseImages[i] = new ImageView(activity);
            pulseImages[i].setImageResource(this.selectedPulseImageRID);
            pulseImages[i].setId(22800 + i);

            // setting constraint layout params for each image
            //  to dp converter
            float toDp = activity.getResources().getDisplayMetrics().density;
            int widthInDp = (int) (width * toDp);
            int heightInDp = (int) (height * toDp);
            ConstraintLayout.LayoutParams imageConstarintLayoutParams = new ConstraintLayout.LayoutParams(widthInDp, heightInDp);

            pulseImages[i].setLayoutParams(imageConstarintLayoutParams);
            mainPulseAnimationHolder.addView(pulseImages[i]);

            ConstraintSet constraintSet = new ConstraintSet();
            constraintSet.clone(mainPulseAnimationHolder);

            constraintSet.connect(pulseImages[i].getId(), ConstraintSet.TOP, mainPulseAnimationHolder.getId(), ConstraintSet.TOP, 0);
            constraintSet.connect(pulseImages[i].getId(), ConstraintSet.BOTTOM, mainPulseAnimationHolder.getId(), ConstraintSet.BOTTOM, 0);
            constraintSet.connect(pulseImages[i].getId(), ConstraintSet.START, mainPulseAnimationHolder.getId(), ConstraintSet.START, 0);
            constraintSet.connect(pulseImages[i].getId(), ConstraintSet.END, mainPulseAnimationHolder.getId(), ConstraintSet.END, 0);

            constraintSet.applyTo(mainPulseAnimationHolder);

            pulseImages[i].setVisibility(View.GONE);

            // end Create An ImageView From Passed Pulse Picture


        }

        // set animations for each pulseImage
        //  set animation handler
        handlerAnimation = new Handler();

        setAllPulseAnimations(maxSizeMultiply, pulseDuration, selectedPulseImageRID);

    }

    // Start Setting Animations
    void setAllPulseAnimations(final float maxSizeMultiply, final int pulseDuration, final int selectedPulseImagePic) {

        for (int i = 0; i < pulseImages.length; i++) {

            // pass i to lower functions
            final int finalI = i;

            pulseRunnableSeriesAnimations[i] = new Runnable() {
                @Override
                public void run() {

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                        pulseImages[finalI].setVisibility(View.VISIBLE);
                        pulseImages[finalI].setScaleX(1f);
                        pulseImages[finalI].setScaleY(1f);
                        pulseImages[finalI].setAlpha(1f);
                        pulseImages[finalI].setImageResource(selectedPulseImagePic);
                        pulseImages[finalI].animate().scaleX(maxSizeMultiply).scaleY(maxSizeMultiply).alpha(0f).setDuration(pulseDuration).withEndAction(new Runnable() {
                            @Override
                            public void run() {

                                pulseImages[finalI].setScaleX(1f);
                                pulseImages[finalI].setScaleY(1f);
                                pulseImages[finalI].setAlpha(1f);
                                pulseImages[finalI].setVisibility(View.GONE);

                            }
                        });
                    }


                    // set delay between pulses
                    int eachPulseDelayTillNext = (int) pulseDuration / (pulseImages.length);

                    // check if it's final pulse and resend first pulse or continue to next
                    if (finalI + 1 < pulseImages.length) {

                        handlerAnimation.postDelayed(pulseRunnableSeriesAnimations[finalI + 1], eachPulseDelayTillNext);

                    } else {

                        handlerAnimation.postDelayed(pulseRunnableSeriesAnimations[0], eachPulseDelayTillNext);

                    }

                }
            };
        }
    }
    // End Setting Animations

    public void startAnimation() {

        pulseRunnableSeriesAnimations[0].run();

    }

    public void stopAnimation() {

        for (int k = 0; k < pulseRunnableSeriesAnimations.length; k++) {

            handlerAnimation.removeCallbacks(pulseRunnableSeriesAnimations[k]);

        }
    }

    public void disablePulse(int disabledPulseImageRID) {

        setAllPulseAnimations(maxSizeMultiply, pulseDuration, disabledPulseImageRID);

    }

    public void enablePulse() {

        setAllPulseAnimations(maxSizeMultiply, pulseDuration, selectedPulseImageRID);

    }


}


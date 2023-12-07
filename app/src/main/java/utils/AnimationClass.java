package utils;

import android.view.View;
import android.view.animation.AnimationUtils;

import com.ventura.venturawealth.R;

/**
 * Created by XtremsoftTechnologies on 03/03/17.
 */

public class AnimationClass {

    public static void showChart(final View view){
        android.view.animation.Animation animation = AnimationUtils.loadAnimation(GlobalClass.latestContext, R.anim.chart_top_in);
        //use this to make it longer:  animation.setDuration(1000);
        animation.setAnimationListener(new android.view.animation.Animation.AnimationListener() {
            @Override
            public void onAnimationStart(android.view.animation.Animation animation) {}

            @Override
            public void onAnimationRepeat(android.view.animation.Animation animation) {}

            @Override
            public void onAnimationEnd(android.view.animation.Animation animation) {
                view.setVisibility(View.VISIBLE);
            }
        });

        view.startAnimation(animation);
    }
    public static void hideChart(final View view){
        android.view.animation.Animation animation = AnimationUtils.loadAnimation(GlobalClass.latestContext,
                R.anim.slide_down);
        //use this to make it longer:  animation.setDuration(1000);
        animation.setAnimationListener(new android.view.animation.Animation.AnimationListener() {
            @Override
            public void onAnimationStart(android.view.animation.Animation animation) {}

            @Override
            public void onAnimationRepeat(android.view.animation.Animation animation) {}

            @Override
            public void onAnimationEnd(android.view.animation.Animation animation) {
                view.setVisibility(View.GONE);
            }
        });

        view.startAnimation(animation);
    }
}

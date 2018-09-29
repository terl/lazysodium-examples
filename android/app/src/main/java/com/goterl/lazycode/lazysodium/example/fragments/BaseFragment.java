package com.goterl.lazycode.lazysodium.example.fragments;

import android.animation.Animator;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.View;
import io.codetail.animation.ViewAnimationUtils;

public class BaseFragment extends Fragment {

    protected View overlay;

    protected  <T extends AppCompatActivity> void openActivity(final View view, final Class<T> activityClass) {
        int[] coords = getCenterOfView(view);

        int startRadius = 0;

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int height = displayMetrics.heightPixels;
        int endRadius = height;

        Animator anim =
                ViewAnimationUtils.createCircularReveal(overlay, coords[0], coords[1], startRadius, endRadius);
        anim.setDuration(200L);
        anim.setStartDelay(0L);
        overlay.setVisibility(View.VISIBLE);
        anim.start();

        // Start activity with a fade in
        fadeInActivity(activityClass);
    }

    protected <T extends AppCompatActivity> void fadeInActivity(Class<T> activityClass) {
        ActivityOptionsCompat anim = ActivityOptionsCompat.makeCustomAnimation(getActivity(), android.R.anim.fade_in, android.R.anim.fade_out);
        Bundle bundle = anim.toBundle();
        Intent intent = new Intent(getActivity(), activityClass);
        startActivity(intent, bundle);
    }

    protected int[] getCenterOfView(final View view) {
        int [] location = new int[2];
        view.getLocationOnScreen(location);
        int x = location[0];
        int y = location[1];
        float centreX = x + view.getWidth() / 2;
        float centreY = y + view.getHeight() / 2;
        return new int[] {(int) centreX, (int) centreY};
    }

}

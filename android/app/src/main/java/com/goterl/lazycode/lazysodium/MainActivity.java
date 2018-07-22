package com.goterl.lazycode.lazysodium;

import android.animation.Animator;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import com.goterl.lazycode.lazysodium.adapters.MultiAdapter;
import com.goterl.lazycode.lazysodium.models.Operation;
import com.goterl.lazycode.lazysodium.operation_acts.*;
import io.codetail.animation.ViewAnimationUtils;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity  {

    private static final String TAG = "MainActivity";
    private MultiAdapter adapter;
    private View overlay;
    private MultiAdapter creditsAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        overlay = findViewById(R.id.overlay);

        adapter = new MultiAdapter(this, getListOfOps(), false);
        adapter.setClickListener(new MultiAdapter.ItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                if (position == 0) {
                    openActivity(view, SymmetricEncryptionActivity.class);
                }
                if (position == 1) {
                    openActivity(view, AsymmetricEncryptionActivity.class);
                }
                if (position == 2) {
                    openActivity(view, GenericHashActivity.class);
                }
                if (position == 3) {
                    openActivity(view, PasswordHashActivity.class);
                }
                if (position == 4) {
                    openActivity(view, KeyDerivationActivity.class);
                }
            }
        });

        createRecycler(R.id.list, adapter);

        creditsAdapter = new MultiAdapter(this, getListOfCredits(), true);
        creditsAdapter.setClickListener(new MultiAdapter.ItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                if (position == 0) {
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/terl/lazysodium-android"));
                    startActivity(browserIntent);
                }
                if (position == 1) {
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/jedisct1/libsodium"));
                    startActivity(browserIntent);
                }
                if (position == 2) {
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https:/terl.co"));
                    startActivity(browserIntent);
                }
            }
        });
        createRecycler(R.id.credits_list, creditsAdapter);
    }

    private void createRecycler(int list, MultiAdapter adapter) {
        RecyclerView recyclerView = findViewById(list);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setNestedScrollingEnabled(false);
        recyclerView.setAdapter(adapter);
    }

    private List<Operation> getListOfOps() {
        List<Operation> operationList = new ArrayList<>();
        Operation symmetricEnc = new Operation(
                "Symmetric encryption",
                "Encryption using a symmetric key."
        );
        Operation asymmetricEnc = new Operation(
                "Asymmetric encryption",
                "Encryption using an asymmetric (public-private) key."
        );
        Operation genericHashing = new Operation(
                "Generic hashing",
                "Hash something with the Blake2B algorithm."
        );
        Operation passwordHashing = new Operation(
                "Password hashing",
                "Securely hash passwords."
        );
        Operation keyDerivation = new Operation(
                "Key derivation",
                "Derive keys from a master key."
        );
        operationList.add(symmetricEnc);
        operationList.add(asymmetricEnc);
        operationList.add(genericHashing);
        operationList.add(passwordHashing);
        operationList.add(keyDerivation);
        return operationList;
    }


    private List<Operation> getListOfCredits() {
        List<Operation> creditsList = new ArrayList<>();
        Operation credit0 = new Operation(
                "Lazysodium",
                "Visit the Lazysodium project page."
        );
        Operation credit1 = new Operation(
                "Libsodium",
                "Many thanks to the Libsodium project which provides a great C library for wrapping around."
        );
        Operation credit2 = new Operation(
                "Terl",
                "We're the creators of Lazysodium. You should check out our other apps and services!"
        );
        creditsList.add(credit0);
        creditsList.add(credit1);
        creditsList.add(credit2);
        return creditsList;
    }


    private <T extends AppCompatActivity> void openActivity(final View view, final Class<T> activityClass) {
        int[] coords = getCenterOfView(view);

        int startRadius = 0;

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
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

    private <T extends AppCompatActivity> void fadeInActivity(Class<T> activityClass) {
        ActivityOptionsCompat anim = ActivityOptionsCompat.makeCustomAnimation(this, android.R.anim.fade_in, android.R.anim.fade_out);
        Bundle bundle = anim.toBundle();
        Intent intent = new Intent(this, activityClass);
        startActivity(intent, bundle);
    }

    private int[] getCenterOfView(final View view) {
        int [] location = new int[2];
        view.getLocationOnScreen(location);
        int x = location[0];
        int y = location[1];
        float centreX = x + view.getWidth() / 2;
        float centreY = y + view.getHeight() / 2;
        return new int[] {(int) centreX, (int) centreY};
    }


    @Override
    protected void onResume() {
        super.onResume();
        if (overlay != null && overlay.getVisibility() == View.VISIBLE) {
            Animation fadeOut = new AlphaAnimation(1, 0);
            fadeOut.setInterpolator(new AccelerateInterpolator());
            overlay.startAnimation(fadeOut);
            fadeOut.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {

                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    if (overlay != null) {
                        overlay.setVisibility(View.GONE);
                    }
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });
        }
    }
}

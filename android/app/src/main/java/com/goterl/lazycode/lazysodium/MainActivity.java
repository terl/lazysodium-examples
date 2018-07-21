package com.goterl.lazycode.lazysodium;

import android.animation.Animator;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import com.goterl.lazycode.lazysodium.adapters.OperationAdapter;
import com.goterl.lazycode.lazysodium.models.Operation;
import com.goterl.lazycode.lazysodium.operation_acts.SymmetricEncryptionActivity;
import io.codetail.animation.ViewAnimationUtils;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements OperationAdapter.ItemClickListener {

    private static final String TAG = "MainActivity";
    private OperationAdapter adapter;
    private View overlay;
    private MotionEvent lastTouch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        overlay = findViewById(R.id.overlay);

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
        Operation keyDerivation = new Operation(
                "Key derivation",
                "Derive keys from a master key."
        );
        operationList.add(symmetricEnc);
        operationList.add(asymmetricEnc);
        operationList.add(genericHashing);
        operationList.add(keyDerivation);

        RecyclerView recyclerView = findViewById(R.id.list);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setNestedScrollingEnabled(false);
        adapter = new OperationAdapter(this, operationList);
        adapter.setClickListener(this);
        recyclerView.setAdapter(adapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement

        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onItemClick(View view, int position) {
        if (position == 0) {
            Log.e(TAG, "Clicked!");
            openActivity(view, SymmetricEncryptionActivity.class);
        }
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

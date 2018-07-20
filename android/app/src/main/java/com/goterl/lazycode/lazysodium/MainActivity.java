package com.goterl.lazycode.lazysodium;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import com.goterl.lazycode.lazysodium.adapters.OperationAdapter;
import com.goterl.lazycode.lazysodium.models.Operation;
import com.goterl.lazycode.lazysodium.operation_acts.SymmetricEncryptionActivity;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements OperationAdapter.ItemClickListener {

    private static final String TAG = "MainActivity";
    private OperationAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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
            openActivity(SymmetricEncryptionActivity.class);
        }
    }

    private <T extends AppCompatActivity> void openActivity(final Class<T> activityClass) {
        Intent intent = new Intent(this, activityClass);
        startActivity(intent);
    }
}

package com.goterl.lazycode.lazysodium.operation_acts;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import com.goterl.lazycode.lazysodium.R;

public class SymmetricEncryptionActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_symmetric_enc);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setTitle("Symmetric key encryption");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

}

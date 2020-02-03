package com.skysam.hchirinos.elmejorprecio;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

public class AcercadeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_acercade);

    }

    @Override
    public void onBackPressed() {
        finish();
    }
}

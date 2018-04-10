package com.patelheggere.muralidharahalappa.activities;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import com.patelheggere.muralidharahalappa.R;
public class SplashScreenActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
              startActivity(new Intent(getApplicationContext(), MainActivity.class));
              finish();
            }
        }, 3000);
    }
}

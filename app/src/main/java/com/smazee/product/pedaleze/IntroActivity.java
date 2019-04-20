package com.smazee.product.pedaleze;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;

public class IntroActivity extends AppCompatActivity {

    private static int SPLASH_TIME_OUT = 3000;
    private PrefManager prefManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro);
        //ImageView logo = findViewById(R.id.logo);
        //logo.setAlpha(1.0f);
        //logo.animate().rotationBy(360).alphaBy(0.0f).setDuration(3000);
        prefManager = new PrefManager(this);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if(prefManager.isFirstTimeLaunch()){
                    startActivity(new Intent(IntroActivity.this,SplashActivity.class));
                }
                else if(prefManager.isFirstTimeLogin()){
                    startActivity(new Intent(IntroActivity.this, LoginActivity.class));
                }
                else {
                    startActivity(new Intent(IntroActivity.this, ProfileActivity.class));
                }
                finish();
            }
        }, SPLASH_TIME_OUT);
    }
}

package com.smazee.product.pedaleze;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class IntroActivity extends AppCompatActivity {

    private static int SPLASH_TIME_OUT = 3000;
    private PrefManager prefManager;
    TextView name;
    Button btn;
    LinearLayout linearLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro);
        //ImageView logo = findViewById(R.id.logo);
        //logo.setAlpha(1.0f);
        //logo.animate().rotationBy(360).alphaBy(0.0f).setDuration(3000);
        linearLayout = findViewById(R.id.nameLayout);
        name = findViewById(R.id.nameText);
        btn = findViewById(R.id.login);
        linearLayout.setVisibility(View.INVISIBLE);
        prefManager = new PrefManager(this);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                /*
                if(prefManager.getName().equals("PedalEze User"))
                    linearLayout.setVisibility(View.VISIBLE);
                else
                    startActivity(new Intent(IntroActivity.this, ProfileActivity.class));
                */
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

    public void login(View view){
        String str = name.getText().toString();
        prefManager.setName(str);
        startActivity(new Intent(IntroActivity.this, ProfileActivity.class));
    }

}

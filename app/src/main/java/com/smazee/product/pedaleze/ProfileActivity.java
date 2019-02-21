package com.smazee.product.pedaleze;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.ncorti.slidetoact.SlideToActView;

public class ProfileActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        SlideToActView swipeBtn = findViewById(R.id.swipe_btn);
        swipeBtn.setOnSlideCompleteListener(new SlideToActView.OnSlideCompleteListener() {
            @Override
            public void onSlideComplete(SlideToActView slideToActView) {
                Intent toMap = new Intent(ProfileActivity.this,MapsActivity.class);
                startActivity(toMap);

            }
        });
    }
}

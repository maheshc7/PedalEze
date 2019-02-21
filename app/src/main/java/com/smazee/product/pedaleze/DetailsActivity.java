package com.smazee.product.pedaleze;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class DetailsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
    }

    void onProceed(View view){
        Intent toProfile = new Intent(DetailsActivity.this,ProfileActivity.class);
        startActivity(toProfile);
    }
}

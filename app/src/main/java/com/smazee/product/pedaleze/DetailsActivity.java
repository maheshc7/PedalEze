package com.smazee.product.pedaleze;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.smazee.product.pedaleze.R;
import com.smazee.product.pedaleze.model.MessageSender;
import com.smazee.product.pedaleze.model.ProfileDetails;

public class DetailsActivity extends AppCompatActivity {

    public static ProfileDetails profile;
    PrefManager prefManager;
    RadioGroup radioGroup;
    TextView height_txt, weight_txt, hip_txt, waist_txt, sos_txt, dob_txt, wrist_txt,email_txt;
    String height, weight, dob, wrist_size, hip_size, sos_number;
    int gender=-1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        height_txt = findViewById(R.id.height_txt);
        weight_txt = findViewById(R.id.weight_txt);
        hip_txt = findViewById(R.id.hip_txt);
        waist_txt = findViewById(R.id.waist_txt);
        wrist_txt = findViewById(R.id.wrist_size_txt);
        sos_txt = findViewById(R.id.sos_txt);
        dob_txt = findViewById(R.id.dob_txt);
        radioGroup = findViewById(R.id.radioGroup);
        prefManager = new PrefManager(this);
        MessageSender messageSender = new MessageSender(DetailsActivity.this);
        messageSender.getLogin(DetailsActivity.this, prefManager.getPhoneNumber(), "test");
        if(profile!=null){
            setProfile(profile);
        }
    }

    public void setProfile(ProfileDetails profile){
        height_txt.setText(profile.getHeigh());
        weight_txt.setText(profile.getWeight());
        wrist_txt.setText(profile.getWrist_size());
        sos_txt.setText(profile.getSos_number());
        dob_txt.setText(profile.getDob());


    }

    public  void onProceed(View view){
        boolean noError=true;
        height = height_txt.getText().toString();
        weight = weight_txt.getText().toString();
        hip_size = hip_txt.getText().toString()+":"+waist_txt.getText().toString();
        sos_number = sos_txt.getText().toString();
        dob = dob_txt.getText().toString();
        wrist_size = wrist_txt.getText().toString();
        gender = radioGroup.getCheckedRadioButtonId();
        if(gender==-1){
            noError = false;
            Toast.makeText(this,"Please Choose Your Gender.",Toast.LENGTH_SHORT).show();
        }
        if(dob.isEmpty()|| dob.trim().equals("")){
            noError = false;
            dob_txt.setError("Enter Date of Birth.");
        }
        Log.d("DetailsAct--->","DOB: "+dob+"\nGender: "+gender+wrist_size);

        if(noError) {
            profile.setHeigh(height);
            profile.setWeight(weight);
            profile.setGender(gender);
            profile.setHip_size(hip_size);
            profile.setWrist_size(wrist_size);
            profile.setSos_number(sos_number);
            profile.setDob(dob);
            Log.d("DetailsAct--->", profile.toString());
            MessageSender messageSender = new MessageSender(DetailsActivity.this);
            messageSender.updateDetails(profile);

            Intent toProfile = new Intent(DetailsActivity.this, ProfileActivity.class);
            startActivity(toProfile);
        }
    }
}

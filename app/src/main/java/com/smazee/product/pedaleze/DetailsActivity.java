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
import android.widget.RadioButton;
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
    RadioButton m,f,o;
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
        m = findViewById(R.id.male_radio);
        f = findViewById(R.id.female_radio);
        o = findViewById(R.id.other_radio);
        prefManager = new PrefManager(this);
        //MessageSender messageSender = new MessageSender(DetailsActivity.this);
        //messageSender.getLogin(DetailsActivity.this, prefManager.getPhoneNumber(), "test");
        if(profile!=null){
            Log.d("Check--->",profile.toString());
            setProfile(profile);
        }
    }

    public void setProfile(ProfileDetails profile){
        Log.d("Profile--->",profile.toString());
        height_txt.setText(profile.getHeigh());
        weight_txt.setText(profile.getWeight());
        wrist_txt.setText(profile.getWrist_size());
        sos_txt.setText(profile.getSos_number());
        dob_txt.setText(profile.getDob());
        if(profile.getGender()==1)
            m.setSelected(true);
        else if(profile.getGender()==2)
            f.setSelected(true);
        else
            o.setSelected(true);
        String hw[] = profile.getHip_size().split(":");
        hip_txt.setText(hw[0]);
        waist_txt.setText(hw[1]);

    }

    public  void onProceed(View view){
        boolean noError=true;
        height = height_txt.getText().toString();
        weight = weight_txt.getText().toString();
        hip_size = hip_txt.getText().toString()+":"+waist_txt.getText().toString();
        sos_number = sos_txt.getText().toString();
        dob = dob_txt.getText().toString();
        wrist_size = wrist_txt.getText().toString();
        int id = radioGroup.getCheckedRadioButtonId();
        if(id==-1){
            noError = false;
            Toast.makeText(this,"Please Choose Your Gender.",Toast.LENGTH_SHORT).show();
        }
        else{
            RadioButton rb = findViewById(id);
            gender = Integer.parseInt(rb.getTag().toString());
        }
        if(dob.isEmpty()|| dob.trim().equals("")){
            noError = false;
            dob_txt.setError("Enter Date of Birth.");
        }
        Log.d("DetailsAct--->","SOS: "+sos_number+"\nGender: "+gender+"    "+wrist_size);

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

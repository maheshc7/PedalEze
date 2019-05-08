package com.smazee.product.pedaleze;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.smazee.product.pedaleze.R;
import com.smazee.product.pedaleze.model.MessageSender;
import com.smazee.product.pedaleze.model.ProfileDetails;

import java.util.Calendar;

public class DetailsActivity extends AppCompatActivity {

    public static ProfileDetails profile;
    PrefManager prefManager;
    RadioGroup radioGroup;
    TextView height_txt, weight_txt, hip_txt, waist_txt, sos_txt, dob_txt, wrist_txt,email_txt;
    String height, weight, dob, wrist_size, hip_size, sos_number;
    private Calendar calendar;
    int gender=-1;
    RadioButton m,f,o;
    Button proceed, save, cancel;
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
        proceed = findViewById(R.id.proceed_btn);
        save = findViewById(R.id.save_btn);
        cancel = findViewById(R.id.cancel_btn);
        calendar = Calendar.getInstance();
        prefManager = new PrefManager(this);
        //MessageSender messageSender = new MessageSender(DetailsActivity.this);
        //messageSender.getLogin(DetailsActivity.this, prefManager.getPhoneNumber(), "test");
        profile = ProfileActivity.profile;
        if(profile!=null){
            Log.d("Check--->",profile.toString());
            setProfile(profile);
        }
        Intent intent = getIntent();
        if(intent.getStringExtra("dest").equals("profile")){
            save.setVisibility(View.VISIBLE);
            cancel.setVisibility(View.VISIBLE);
            proceed.setVisibility(View.INVISIBLE);

        }
        else{
            save.setVisibility(View.INVISIBLE);
            cancel.setVisibility(View.INVISIBLE);
            proceed.setVisibility(View.VISIBLE);
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
        String[] hw = profile.getHip_size().split(":");
        hip_txt.setText(hw[0]);
        waist_txt.setText(hw[1]);

    }

    public  void onProceed(View view){
        boolean noError=true;
        int w;
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

        if(weight.isEmpty())
            weight_txt.setError("Field empty");
        else {
            w = Integer.parseInt(weight);
            if (w < 40 || w > 110) {
                weight_txt.setError("Weight should be between 40 & 110 kgs");
                noError = false;
            }
        }
        if(height.isEmpty())
            height_txt.setError("Field empty");
        else {
            w = Integer.parseInt(height);
            if (w < 120 || w > 200) {
                height_txt.setError("Height should be between 120 & 200 cms");
                noError = false;
            }
        }

        if(hip_size.equals(":"))
            waist_txt.setError("Field empty");
        if(sos_number.isEmpty())
            sos_txt.setError("Field empty");
        if(wrist_size.isEmpty())
            wrist_txt.setError("Field empty");

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

    public void onCancel(View view){
        super.onBackPressed();
    }

    public void selectDate(View view){
        //datePicker.setVisibility(View.VISIBLE);
        DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                // TODO Auto-generated method stub
                showDate(year, monthOfYear, dayOfMonth);
            }
        };
        DatePickerDialog datePickerDialog = new DatePickerDialog(this,date, calendar
                .get(Calendar.YEAR)-8, calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH));
        Calendar cal =Calendar.getInstance();
        cal.add(Calendar.YEAR,-8);
        datePickerDialog.getDatePicker().setMaxDate(cal.getTimeInMillis());
        cal.add(Calendar.YEAR,-82);
        datePickerDialog.getDatePicker().setMinDate(cal.getTimeInMillis());
        datePickerDialog.show();

    }

    private void showDate(int year, int month, int day) {
        dob_txt.setText(new StringBuilder().append(year).append("/")
                .append(month+1).append("/").append(day));
    }

}

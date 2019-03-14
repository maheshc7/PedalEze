package com.smazee.product.pedaleze.model.rest.message;

import com.google.gson.annotations.SerializedName;
import com.smazee.product.pedaleze.model.ProfileDetails;

public class LoginRequest {

    public LoginRequest(String mobile,String password){

        this.mobile=mobile;
        this.password=password;
    }

    public LoginRequest(ProfileDetails prof){

        this.mobile=prof.getMobile();
        this.password="test";
        this.dob=prof.getDob();
        this.heigh=prof.getHeigh();
        this.weight=prof.getWeight();
        this.hip_size=prof.getHip_size();
        this.wrist_size=prof.getWrist_size();
        this.sos_number=prof.getSos_number();
        this.name=prof.getName();
        this.token=prof.getToken();


    }

    @SerializedName("name")
    private String name;

    @SerializedName("heigh")
    private String heigh;

    @SerializedName("weight")
    private String weight;

    @SerializedName("dob")
    private String dob;

    @SerializedName("wrist_size")
    private String wrist_size;

    @SerializedName("hip_size")
    private String hip_size;

    @SerializedName("sos_number")
    private String sos_number;

    @SerializedName("token")
    private int token;

    @SerializedName("mobile")
    private String mobile;

    @SerializedName("password")
    private String password;
}

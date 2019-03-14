package com.smazee.product.pedaleze.model;

public class ProfileDetails {

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getGender() {
        return gender;
    }

    public void setGender(int gender) {
        this.gender = gender;
    }

    public int getApp_token() {
        return app_token;
    }

    public void setApp_token(int app_token) {
        this.app_token = app_token;
    }

    public int getVehicle_id() {
        return vehicle_id;
    }

    public void setVehicle_id(int vehicle_id) {
        this.vehicle_id = vehicle_id;
    }

    public int getToken() {
        return token;
    }

    public void setToken(int token) {
        this.token = token;
    }

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    public String getUpdated_at() {
        return updated_at;
    }

    public void setUpdated_at(String updated_at) {
        this.updated_at = updated_at;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPincode() {
        return pincode;
    }

    public void setPincode(String pincode) {
        this.pincode = pincode;
    }

    public String getBooking_id() {
        return booking_id;
    }

    public void setBooking_id(String booking_id) {
        this.booking_id = booking_id;
    }

    public String getHeigh() {
        return heigh;
    }

    public void setHeigh(String heigh) {
        this.heigh = heigh;
    }

    public String getWeight() {
        return weight;
    }

    public void setWeight(String weight) {
        this.weight = weight;
    }

    public String getDob() {
        return dob;
    }

    public void setDob(String dob) {
        this.dob = dob;
    }

    public String getWrist_size() {
        return wrist_size;
    }

    public void setWrist_size(String wrist_size) {
        this.wrist_size = wrist_size;
    }

    public String getHip_size() {
        return hip_size;
    }

    public void setHip_size(String hip_size) {
        this.hip_size = hip_size;
    }

    public String getSos_number() {
        return sos_number;
    }

    public void setSos_number(String sos_number) {
        this.sos_number = sos_number;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getParent_id() {
        return parent_id;
    }

    public void setParent_id(String parent_id) {
        this.parent_id = parent_id;
    }

    int id;
    int gender;
    int app_token;
    int vehicle_id;

    @Override
    public String toString() {
        return "{" +
                "id=" + id +
                ", gender=" + gender +
                ", app_token=" + app_token +
                ", vehicle_id=" + vehicle_id +
                ", token=" + token +
                ", created_at='" + created_at + '\'' +
                ", updated_at='" + updated_at + '\'' +
                ", name='" + name + '\'' +
                ", mobile='" + mobile + '\'' +
                ", email='" + email + '\'' +
                ", address='" + address + '\'' +
                ", pincode='" + pincode + '\'' +
                ", booking_id='" + booking_id + '\'' +
                ", heigh='" + heigh + '\'' +
                ", weight='" + weight + '\'' +
                ", dob='" + dob + '\'' +
                ", wrist_size='" + wrist_size + '\'' +
                ", hip_size='" + hip_size + '\'' +
                ", sos_number='" + sos_number + '\'' +
                ", password='" + password + '\'' +
                ", parent_id='" + parent_id + '\'' +
                '}';
    }

    int token;
    String created_at,updated_at,name ,mobile
            ,email,address,pincode,booking_id,
            heigh,weight,dob,wrist_size,hip_size,
            sos_number,password,parent_id;
}

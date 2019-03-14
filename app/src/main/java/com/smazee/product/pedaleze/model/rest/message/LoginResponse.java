package com.smazee.product.pedaleze.model.rest.message;

import com.google.gson.JsonObject;
import com.google.gson.annotations.SerializedName;
import com.smazee.product.pedaleze.model.ProfileDetails;

import org.json.JSONException;
import org.json.JSONObject;

public class LoginResponse extends MessengerResponse{
    @SerializedName("success")
    private JsonObject successList;

    public ProfileDetails getProfileDetails() throws JSONException {
        ProfileDetails profileDetail = new ProfileDetails();
        int token = successList.get("token").getAsInt();
        JSONObject user = new JSONObject(successList.get("user").toString());
        int id =user.getInt("id");
        String created_at = user.getString("created_at");
        String name = user.getString("name");
        String mobile = user.getString("mobile");
        int app_token = user.getInt("app_token");
        String height = user.getString("heigh");
        String weight = user.getString("weight");

        profileDetail.setToken(token);
        profileDetail.setId(id);
        profileDetail.setApp_token(app_token);
        profileDetail.setName(name);
        profileDetail.setMobile(mobile);
        profileDetail.setCreated_at(created_at);
        profileDetail.setWeight(weight);
        profileDetail.setHeigh(height);

        return profileDetail;
    }
}

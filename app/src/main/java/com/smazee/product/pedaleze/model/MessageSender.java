package com.smazee.product.pedaleze.model;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.smazee.product.pedaleze.DetailsActivity;
import com.smazee.product.pedaleze.LoginActivity;
import com.smazee.product.pedaleze.ProfileActivity;
import com.smazee.product.pedaleze.model.rest.MessengerApiInterface;
import com.smazee.product.pedaleze.model.rest.MessengerRestClient;
import com.smazee.product.pedaleze.model.rest.message.LoginRequest;
import com.smazee.product.pedaleze.model.rest.message.LoginResponse;


import org.json.JSONException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


/**
 * Created by N.Mahesh on 09/03/2019.
 */

public class MessageSender {


    private static final String TAG = MessageSender.class.getSimpleName() + "--->";

    Context context=null;

    public MessageSender(Context ctx){
        context = ctx;
    }

    public void getLogin(final LoginActivity loginActivity, String mobile, String password){
        MessengerApiInterface apiService = MessengerRestClient.Get().createService(MessengerApiInterface.class);

        LoginRequest loginRequest = new LoginRequest(mobile, password);
        Call<LoginResponse> call = apiService.getLogin(loginRequest);
        call.enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                Log.d(TAG, "Response Code is " + response.code());
                Log.d(TAG, "GetLogin method called");

                boolean isSuccess = response.isSuccessful();

                if(isSuccess) {
                    try {
                        Log.d(TAG, "Success response:\n"+response.body().getProfileDetails().toString());
                        DetailsActivity.profile=response.body().getProfileDetails();
                        Log.d("check--->",DetailsActivity.profile.toString());
                        if(response.body().getProfileDetails().getDob().isEmpty()){
                            loginActivity.intent(false);
                        }
                        else {
                            loginActivity.intent(true);
                            ProfileActivity.profile = response.body().getProfileDetails();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Log.e(TAG,"getLogin LoginActivity error");
                    }
                }
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                Log.d(TAG, "GetLogin On Failure");
                Log.d(TAG,"failed to connect: "+t.getMessage());
                Toast.makeText(context, "connection failed", Toast.LENGTH_LONG).show();

            }

        });
    }

    /*public void getLogin(final DetailsActivity detailsActivity, String mobile, String password){
        MessengerApiInterface apiService = MessengerRestClient.Get().createService(MessengerApiInterface.class);

        LoginRequest loginRequest = new LoginRequest(mobile, password);
        Call<LoginResponse> call = apiService.getLogin(loginRequest);
        call.enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                Log.d(TAG, "Response Code is " + String.valueOf(response.code()));
                Log.d(TAG, "GetLogin method called");

                boolean isSuccess = response.isSuccessful();

                if(isSuccess) {
                    try {
                        Log.d(TAG, "Success response:\n"+response.body().getProfileDetails().toString());
                        detailsActivity.profile = response.body().getProfileDetails();
                        detailsActivity.setProfile(response.body().getProfileDetails());

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                Log.d(TAG, "GetLogin On Failure");
                Log.d(TAG,"failed to connect: "+t.getMessage());
                Toast.makeText(context, "connection failed", Toast.LENGTH_LONG).show();

            }

        });
    }*/

    public void getLogin(final ProfileActivity profileActivity, String mobile, String password){
        MessengerApiInterface apiService = MessengerRestClient.Get().createService(MessengerApiInterface.class);

        LoginRequest loginRequest = new LoginRequest(mobile, password);
        Call<LoginResponse> call = apiService.getLogin(loginRequest);
        call.enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                Log.d(TAG, "Response Code is " + response.code());
                Log.d(TAG, "GetLogin method called");

                boolean isSuccess = response.isSuccessful();

                if(isSuccess) {
                    try {
                        Log.d(TAG, "Success response:\n"+response.body().getProfileDetails().toString());
                        profileActivity.updateView(response.body().getProfileDetails());

                    } catch (JSONException e) {
                        e.printStackTrace();
                        Log.e(TAG,"getLogin ProfileActivity error");
                    }
                }
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                Log.d(TAG, "GetLogin On Failure");
                Log.d(TAG,"failed to connect: "+t.getMessage());
                Toast.makeText(context, "connection failed", Toast.LENGTH_LONG).show();

            }

        });
    }

    public void updateDetails(ProfileDetails prof){
        MessengerApiInterface apiService = MessengerRestClient.Get().createService(MessengerApiInterface.class);
        Log.d(TAG,prof.toString());
        LoginRequest loginRequest = new LoginRequest(prof);
        Call<LoginResponse> call = apiService.updateUser(loginRequest);
        call.enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                Log.d(TAG, "Response Code is " + response.code());
                Log.d(TAG, "updateDetails method called");

                boolean isSuccess = response.isSuccessful();

                if(isSuccess) {
                    Log.d(TAG, "Success response:\n"+response.body().toString());
                    try {
                        ProfileActivity.profile = response.body().getProfileDetails();
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Log.e(TAG,"updateDetails Profile error");
                    }
                }
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                Log.d(TAG, "Update User On Failure");
                Log.d(TAG,"failed to connect: "+t.getMessage());
                Toast.makeText(context, "connection failed", Toast.LENGTH_LONG).show();

            }

        });
    }


}
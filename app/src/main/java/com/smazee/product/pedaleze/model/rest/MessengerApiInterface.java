package com.smazee.product.pedaleze.model.rest;

import com.smazee.product.pedaleze.model.rest.message.LoginRequest;
import com.smazee.product.pedaleze.model.rest.message.LoginResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

/**
 * Created by N.Mahesh on 09/03/2019.
 */

public interface MessengerApiInterface {

    @POST("login")
    Call<LoginResponse> getLogin(@Body LoginRequest request);

    @POST("update")
    Call<LoginResponse> updateUser(@Body LoginRequest request);
}

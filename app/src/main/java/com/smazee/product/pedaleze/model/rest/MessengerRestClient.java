package com.smazee.product.pedaleze.model.rest;


import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * Created by S.Ananya on 15/12/2018.
 */

public class MessengerRestClient extends RestClient {
    private static final String TAG = MessengerRestClient.class.getSimpleName() + "--->";
    public static final String BASE_URL_REST = "http://www.pedaleze.com/demo/customer/"; //
    public static Gson gson = new GsonBuilder()
            //       .setDateFormat("yyyy-MM-dd'T'HH:mm:ssZ") //faced problem with TZ 05:30
            //.registerTypeAdapter(Date.class, new Iso8601DateAdapter())
            .create();
    private MessengerRestClient(){}
    private MessengerRestClient(String url, Gson gsonBuilder) {
        initRetrofit(url, gsonBuilder);
    }
    public static MessengerRestClient Get() {
        clientObj = null;
            clientObj = new MessengerRestClient(BASE_URL_REST, gson);

        return clientObj;
    }
    private static MessengerRestClient clientObj = null;




}

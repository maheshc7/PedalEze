package com.smazee.product.pedaleze.model.rest;

import com.google.gson.Gson;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


/**
 * Created by N.Mahesh on 09/03/2019.
 */

public class RestClient {
    private static final String TAG = RestClient.class.getSimpleName() + "--->";
    protected void initRetrofit(String url, Gson gsonBuilder) {
        if (builder == null) {
            builder = new Retrofit.Builder()
                    .baseUrl(url)
                    .addConverterFactory(GsonConverterFactory.create(gsonBuilder));
        }
        if (retrofit == null) {
            if (!httpClient.interceptors().contains(logging)) {
                httpClient.addInterceptor(logging);
                /*httpClient.connectTimeout(60, TimeUnit.SECONDS)//new
                        .readTimeout(60, TimeUnit.SECONDS)//new
                        .writeTimeout(60, TimeUnit.SECONDS);//new*/
                builder.client(httpClient.build());
            }
            retrofit = builder.build();
        }
    }

    Retrofit.Builder builder = null;
    Retrofit retrofit = null;

    private HttpLoggingInterceptor logging =
            new HttpLoggingInterceptor()
                    .setLevel(HttpLoggingInterceptor.Level.BODY);

    private OkHttpClient.Builder httpClient =
            new OkHttpClient.Builder();

    public <S> S createService(
            Class<S> serviceClass) {
        if (retrofit == null) {
            retrofit = builder.build();
        }
        if (!httpClient.interceptors().contains(logging)) {
            httpClient.addInterceptor(logging);
            builder.client(httpClient.build());
            retrofit = builder.build();
        }

        return retrofit.create(serviceClass);
    }
}
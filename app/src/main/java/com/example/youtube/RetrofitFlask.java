package com.example.youtube;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitFlask  {

    private static final String API_URL = "http://54.180.8.33:5000";



    private static Gson gson = new GsonBuilder()
            .setLenient()
            .create();

    private static Retrofit retrofit = new Retrofit.Builder()
            .baseUrl(API_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build();

    public FlaskApiService getRetrofit() {
        return this.retrofit.create(FlaskApiService.class);
    }
}

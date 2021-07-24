package com.example.youtube;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface FlaskApiService {

    //flask와 통신
    @FormUrlEncoded
    @POST("/")
    Call<result> request(@Field("request") String request);


}

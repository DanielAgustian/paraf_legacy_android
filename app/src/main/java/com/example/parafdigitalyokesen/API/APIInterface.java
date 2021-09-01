package com.example.parafdigitalyokesen.API;

import com.example.parafdigitalyokesen.model.AuthModel;
import com.example.parafdigitalyokesen.model.LoginModel;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface APIInterface {
    @POST("/api/v1/register")
    Call<AuthModel> registerUser(@Body AuthModel auth);

    @FormUrlEncoded
    @POST("/api/v1/login")
    Call<LoginModel> loginUser(@Field("email") String email, @Field("password") String password);

}

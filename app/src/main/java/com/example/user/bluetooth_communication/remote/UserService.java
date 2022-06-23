package com.example.user.bluetooth_communication.remote;


import com.example.user.bluetooth_communication.remote.Model.Request.AdduserReq;
import com.example.user.bluetooth_communication.remote.Model.Request.LoginRequest;
import com.example.user.bluetooth_communication.remote.Model.Response.AddUserRes;
import com.example.user.bluetooth_communication.remote.Model.Response.GetAllUser;
import com.example.user.bluetooth_communication.remote.Model.Response.LoginResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface UserService {

    @POST("login")
    Call<LoginResponse> login(@Body LoginRequest.Request loginRequest);


    @GET("sync")
    Call<GetAllUser> syncUsers(@Header("Authorization") String Token);

    @DELETE("delete-user/{id}")
    Call<AddUserRes> deleteUser(@Header("Authorization") String Token, @Path("id") String id);

    @POST("add-user")
    Call<AddUserRes> addUser(@Body AdduserReq.Request adduserReq, @Header("Authorization") String Token);
}

package com.yemeksepeti.interview.rest.service;

import com.yemeksepeti.interview.model.request.LoginRequest;
import com.yemeksepeti.interview.model.request.UserUpdateRequest;
import com.yemeksepeti.interview.model.response.DefaultResponse;
import com.yemeksepeti.interview.model.response.LoginResponse;
import com.yemeksepeti.interview.model.response.UserListResponse;
import com.yemeksepeti.interview.model.response.UserResponse;
import com.yemeksepeti.interview.rest.util.RestCall;
import com.yemeksepeti.interview.util.Constants;

import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Path;

/**
 * Created by farukyavuz on 27/08/2017.
 * Copyright (c) 2017
 * All rights reserved.
 */

public interface RestService {

    @POST(Constants.ENDPOINT_LOGIN)
    RestCall<LoginResponse> login(@Body LoginRequest loginRequest);

    @GET(Constants.ENDPOINT_USERS)
    RestCall<UserListResponse> users(@Header("x-access-token") String accessToken, @Header("x-key") String xKey);

    @GET(Constants.ENDPOINT_USER)
    RestCall<UserResponse> user(@Header("x-access-token") String accessToken, @Header("x-key") String xKey,@Path("userId") String userId);

    @POST(Constants.ENDPOINT_USER_UPDATE)
    RestCall<DefaultResponse> userUpdate(@Header("x-access-token") String accessToken, @Header("x-key") String xKey, @Body UserUpdateRequest userUpdateRequest);

}

package com.yemeksepeti.interview;

import android.app.Application;

import com.yemeksepeti.interview.rest.service.RestService;
import com.yemeksepeti.interview.rest.util.ErrorHandlingCallAdapterFactory;
import com.yemeksepeti.interview.util.Constants;
import com.yemeksepeti.interview.util.Fonty;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by farukyavuz on 26/08/2017.
 * Copyright (c) 2017
 * All rights reserved.
 */

public class YSApplication extends Application {

    //Api Service
    private static RestService restService;
    public static RestService getRestService() {
        return restService;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Fonty.builder(getApplicationContext());

        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .readTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .connectTimeout(30, TimeUnit.SECONDS)
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .client(okHttpClient)
                .baseUrl(Constants.API_BASE_URL)
                .addCallAdapterFactory(new ErrorHandlingCallAdapterFactory())
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        initServices(retrofit);
    }

    private void initServices(Retrofit retrofit) {
        restService = retrofit.create(RestService.class);
    }
}

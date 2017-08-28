package com.yemeksepeti.interview.rest.util;

import android.app.Activity;
//import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.support.v7.app.AlertDialog;

import android.content.DialogInterface;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.yemeksepeti.interview.R;

import org.json.JSONObject;
import okhttp3.MediaType;
import retrofit2.Response;

/**
 * Created by farukyavuz on 14/03/2017.
 * Copyright (c) 2017
 * All rights reserved.
 */

public abstract class RestCallbackImpl<T> implements RestCallback<T> {

    private static final int TOTAL_RETRIES = 3;
    private final RestCall<T> call;
    private int retryCount = 0;
    private Activity activity;

    private Boolean showSpinner;
    private ProgressDialog progressDialog;


    public RestCallbackImpl(RestCall<T> call) {
        this.call = call;
    }

    public RestCallbackImpl(RestCall<T> call, Activity activity) {
        this.call = call;
        this.activity = activity;
    }

    public RestCallbackImpl(RestCall<T> call, Activity activity, Boolean showSpinner) {
        this.call = call;
        this.activity = activity;
        this.showSpinner = showSpinner;

        if(this.showSpinner){
            this.progressDialog = new ProgressDialog(activity);
            setupProgressDialog();
        }

    }

    private void setupProgressDialog(){
        progressDialog.setMessage(activity.getResources().getString(R.string.loading));
        progressDialog.setCancelable(false);
        progressDialog.show();
    }

    @Override
    public void networkError(Throwable t) {

        if (retryCount++ < TOTAL_RETRIES) {
            new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                @Override
                public void run() {
                    retry();
                }
            }, 1000);
        } else {
            serverError(null);
        }
    }

    @Override
    public void onResponse(Response<T> response) {
        if(progressDialog != null && progressDialog.isShowing()){
            progressDialog.dismiss();
        }
        Log.e("Response Test : ", response.toString());
    }

    @Override
    public void serverError(Response<?> response) {

        showDialog("Server Error!");
    }

    public void clientError(Response<?> response) {
        try {
            if (response.errorBody() != null && isContentTypeJson(response)) {

                JSONObject jObject = new JSONObject(response.errorBody().string());
                if (jObject.has("message")) {

                    String error = jObject.getString("message");
                    showDialog(error);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private boolean isContentTypeJson(Response response) {

        if (response.errorBody().contentType() != null) {
            if (response.errorBody().contentType().toString().equalsIgnoreCase(MediaType.parse("application/json;charset=UTF-8").toString())) {
                return true;
            }
        }
        return false;
    }

    private void showDialog(final String msg) {

        new Handler(Looper.getMainLooper()).post(new Runnable() {

            public void run() {
                if (activity != null && !activity.isFinishing()) {
                    new AlertDialog.Builder(activity)
                            .setTitle(R.string.server_error)
                            .setMessage(msg)
                            .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.dismiss();
                                }
                            });
                }
            }
        });
    }

    @Override
    public void unexpectedError(Throwable t) {
        showDialog("Unexpected Error!");
    }

    @Override
    public void unauthenticated(Response<?> response) {
    }

    private void retry() {
        call.clone().enqueue(this);
    }

}

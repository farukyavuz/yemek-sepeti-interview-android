package com.yemeksepeti.interview.rest.util;

import java.io.IOException;
import retrofit2.Response;

/**
 * Created by farukyavuz on 14/03/2017.
 * Copyright (c) 2017
 * All rights reserved.
 */

public interface RestCall<T> {

    void enqueue(RestCallback<T> callback);

    Response<T> execute() throws IOException;

    RestCall<T> clone();

    void cancel();

}

package com.github.siela1915.dishdelish.ApiTests;

import okhttp3.Request;
import okio.Timeout;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SuccessfulCall<T> implements Call<T> {
    private final T response;
    private boolean isExecuted = false;
    private boolean isCanceled = false;

    public SuccessfulCall(T response) {
        this.response = response;
    }

    @Override
    public Response<T> execute() {
        isExecuted = true;
        return Response.success(response);
    }

    @Override
    public void enqueue(Callback<T> callback) {
        isExecuted = true;
        callback.onResponse(this, Response.success(response));
    }

    @Override
    public boolean isExecuted() {
        return isExecuted;
    }

    @Override
    public void cancel() {
        isCanceled = true;
    }

    @Override
    public boolean isCanceled() {
        return isCanceled;
    }

    @Override
    public Call<T> clone() {
        return null;
    }

    @Override
    public Request request() {
        return null;
    }


    @Override
    public Timeout timeout() {
        return null;
    }
}

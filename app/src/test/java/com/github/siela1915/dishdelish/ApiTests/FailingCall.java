package com.github.siela1915.dishdelish.ApiTests;

import java.io.IOException;

import okhttp3.Request;
import okio.Timeout;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FailingCall<T> implements Call<T> {
    private final Exception exception;

    public FailingCall(Exception exception) {
        this.exception = exception;
    }

    @Override
    public Response<T> execute() throws IOException {
        try {
            throw exception;
        } catch (Exception e) {
            throw new IOException(e);
        }
    }

    @Override
    public void enqueue(Callback<T> callback) {

    }

    @Override
    public boolean isExecuted() {
        return false;
    }

    @Override
    public void cancel() {

    }

    @Override
    public boolean isCanceled() {
        return false;
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

    // Implement other methods of the Call interface
}

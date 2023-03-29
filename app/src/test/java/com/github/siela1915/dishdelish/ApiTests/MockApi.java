package com.github.siela1915.dishdelish.ApiTests;

import com.github.siela1915.bootcamp.AutocompleteApi.ApiResponse;
import com.github.siela1915.bootcamp.AutocompleteApi.ApiService;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import retrofit2.Call;

public class MockApi implements ApiService {
    private boolean failingCall;

    public MockApi(boolean failingCall) {
        this.failingCall=failingCall;
    }

    @Override
    public Call<List<ApiResponse>> fetchIngredients(String query, int number, String apiKey) {
        List<ApiResponse> response = new ArrayList<>();
        if(failingCall){
            return new FailingCall<>(new IOException("failed to connect"));
        }else{

            ApiResponse ex = new ApiResponse("apple", "not used", 0, "Not used", new String[]{"piece"});
            response.add(ex);
            return new SuccessfulCall<>(response);
        }
    }
}

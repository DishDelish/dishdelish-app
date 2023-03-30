package com.github.siela1915.bootcamp.AutocompleteApi;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface ApiService {
    @GET("autocomplete")
    Call<List<ApiResponse>> fetchIngredients(@Query("query") String query, @Query("number") int number, @Query("apiKey") String apiKey);
}
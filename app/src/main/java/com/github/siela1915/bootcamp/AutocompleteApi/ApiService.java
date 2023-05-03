package com.github.siela1915.bootcamp.AutocompleteApi;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ApiService {
    @GET("autocomplete")
    Call<List<ApiResponse>> fetchIngredients(@Query("query") String query,
                                             @Query("number") int number,
                                             @Query("apiKey") String apiKey,
                                             @Query("metaInformation") Boolean metaInformation);

    //maybe just do "/" as get, and id as query
    @GET("{id}/information")
    Call<NutrientsResponse> getNutrition(@Path("id") int id,
                                             @Query("amount") int amount,
                                             @Query("unit") String unit,
                                             @Query("apiKey") String apiKey);
}

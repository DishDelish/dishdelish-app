package com.github.siela1915.dishdelish;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;

import com.github.siela1915.bootcamp.AutocompleteApi.ApiResponse;
import com.github.siela1915.bootcamp.AutocompleteApi.ApiService;
import com.github.siela1915.bootcamp.AutocompleteApi.IngredientAutocomplete;
import com.github.siela1915.bootcamp.AutocompleteApi.Nutrient;
import com.github.siela1915.bootcamp.AutocompleteApi.NutrientsResponse;
import com.github.siela1915.bootcamp.BuildConfig;
import com.github.siela1915.bootcamp.Recipes.Ingredient;
import com.github.siela1915.bootcamp.Recipes.Unit;

import org.checkerframework.checker.units.qual.A;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import okhttp3.Request;
import okio.Timeout;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AutocompleteTest {

    @Test
    public void fetcherReturnsNonemptyIngredient(){
        ApiService mockedApi = Mockito.mock(ApiService.class);
        Call<List<ApiResponse>> mockedCall = Mockito.mock(Call.class);
        Mockito.when(mockedApi.fetchIngredients("app", 5, BuildConfig.API_KEY, true)).thenReturn(mockedCall);

        Mockito.doAnswer(invocation -> {
            Callback callback = invocation.getArgument(0, Callback.class);
            callback.onResponse(mockedCall, Response.success(Arrays.asList(new ApiResponse("apple", "not used", 0, "Not used", new String[]{"piece"}))));
            // or callback.onResponse(mockedCall, Response.error(404. ...);
            // or callback.onFailure(mockedCall, new IOException());
            return null;
        }).when(mockedCall).enqueue(any(Callback.class));

        IngredientAutocomplete fetcher = new IngredientAutocomplete();
        fetcher.service = mockedApi;
        List<ApiResponse> ret = new ArrayList<>();
        fetcher.completeSearch("app", ret);
        assertTrue(!ret.isEmpty());
        assertEquals("apple", ret.get(0).name);

    }

    @Test
    public void nullIngredientResponseHandledCorrectly(){
        ApiService mockedApi = Mockito.mock(ApiService.class);
        Call<List<ApiResponse>> mockedCall = Mockito.mock(Call.class);
        Mockito.when(mockedApi.fetchIngredients("app", 5, BuildConfig.API_KEY, true)).thenReturn(mockedCall);

        Mockito.doAnswer(invocation -> {
            Callback callback = invocation.getArgument(0, Callback.class);
            callback.onResponse(mockedCall, Response.success(null));
            return null;
        }).when(mockedCall).enqueue(any(Callback.class));

        IngredientAutocomplete fetcher = new IngredientAutocomplete();
        fetcher.service = mockedApi;
        List<ApiResponse> ret = new ArrayList<>();
        fetcher.completeSearch("app", ret);
        assertTrue(ret.isEmpty());
    }

    @Test
    public void failedCallDoesntOverwritePreviousCall(){
        ApiService mockedApi = Mockito.mock(ApiService.class);
        Call<List<ApiResponse>> mockedCall = Mockito.mock(Call.class);
        Mockito.when(mockedApi.fetchIngredients("app", 5, BuildConfig.API_KEY, true)).thenReturn(mockedCall);

        Mockito.doAnswer(invocation -> {
            Callback callback = invocation.getArgument(0, Callback.class);
            callback.onResponse(mockedCall, Response.success(Arrays.asList(new ApiResponse("apple", "not used", 0, "Not used", new String[]{"piece"}))));
            return null;
        }).when(mockedCall).enqueue(any(Callback.class));

        IngredientAutocomplete fetcher = new IngredientAutocomplete();
        fetcher.service = mockedApi;
        List<ApiResponse> ret = new ArrayList<>();
        fetcher.completeSearch("app", ret);

        Mockito.doAnswer(invocation -> {
            Callback callback = invocation.getArgument(0, Callback.class);
            callback.onFailure(mockedCall, new IOException());
            return null;
        }).when(mockedCall).enqueue(any(Callback.class));

        fetcher.completeSearch("app", ret);

        assertTrue(!ret.isEmpty());
        assertEquals("apple", ret.get(0).name);

    }



    @Test
    public void nutrientFetcherReturnsCorrectNutrients(){
        ApiService mockedApi = Mockito.mock(ApiService.class);
        Call<NutrientsResponse> mockedCall = Mockito.mock(Call.class);
        //9266 = id for pineapple
        Mockito.when(mockedApi.getNutrition(9266, 1, "piece", BuildConfig.API_KEY)).thenReturn(mockedCall);

        Mockito.doAnswer(invocation -> {
            Callback callback = invocation.getArgument(0, Callback.class);
            callback.onResponse(mockedCall, Response.success(new NutrientsResponse(Arrays.asList(
                    new Nutrient("Calories", 452.5, "cal", 22.63),
                    new Nutrient("Fat", 1.09, "g", 1.67),
                    new Nutrient("Carbohydrates", 118.74, "g", 39.58),
                    new Nutrient("Sugar", 89.14, "g", 99.05)))));
            return null;
        }).when(mockedCall).enqueue(any(Callback.class));

        IngredientAutocomplete fetcher = new IngredientAutocomplete();
        fetcher.service = mockedApi;
        Ingredient ing = new Ingredient("pineapple", new Unit(1, "piece"));
        fetcher.getNutritionFromIngredient(9266, ing);


        assertEquals(452.5, ing.getCalories(), 0.01);
        assertEquals(1.09, ing.getFat(), 0.01);
        assertEquals(118.74, ing.getCarbs(), 0.01);
        assertEquals(89.14, ing.getSugar(), 0.01);
    }

}

package com.github.siela1915.dishdelish;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;

import com.github.siela1915.bootcamp.AutocompleteApi.ApiResponse;
import com.github.siela1915.bootcamp.AutocompleteApi.ApiService;
import com.github.siela1915.bootcamp.AutocompleteApi.IngredientAutocomplete;

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
    private final String apiKey = "44a82829c64d4202a18b887e47e76bdd";

    @Test
    public void fetcherReturnsNonemptyIngredient(){
        ApiService mockedApi = Mockito.mock(ApiService.class);
        Call<List<ApiResponse>> mockedCall = Mockito.mock(Call.class);
        Mockito.when(mockedApi.fetchIngredients("app", 5, apiKey, true)).thenReturn(mockedCall);

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
        Mockito.when(mockedApi.fetchIngredients("app", 5, apiKey, true)).thenReturn(mockedCall);

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
        Mockito.when(mockedApi.fetchIngredients("app", 5, apiKey, true)).thenReturn(mockedCall);

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

}

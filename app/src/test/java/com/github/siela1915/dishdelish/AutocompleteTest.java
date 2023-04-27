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

    //I can write real tests just for the service class, the others can be mocked??
    @Test
    public void testing() throws IOException {
        IngredientAutocomplete fetcher = new IngredientAutocomplete();
        Call<List<ApiResponse>> call = fetcher.service.fetchIngredients("app", 5, BuildConfig.API_KEY, true);

        Response<List<ApiResponse>> response = call.execute();

        assertEquals(200, response.code());
    }

    @Test
    public void fetcherReturnsNonemptyIngredient() throws InterruptedException {

        IngredientAutocomplete fetcher = new IngredientAutocomplete();
        List<ApiResponse> ret = new ArrayList<>();
        fetcher.completeSearch("app", ret);

        //I know this is bad, just here till I find another solution
        Thread.sleep(300);

        assertTrue(!ret.isEmpty());
        assertEquals("apple", ret.get(0).name);

    }

    @Test
    public void nullIngredientResponseHandledCorrectly() throws InterruptedException {
        IngredientAutocomplete fetcher = new IngredientAutocomplete();
        List<ApiResponse> ret = new ArrayList<>();
        fetcher.completeSearch("this ingredient doesn't exist", ret);

        //I know this is bad, just here till I find another solution
        Thread.sleep(1000);

        assertTrue(ret.isEmpty());
    }

    @Test
    public void failedCallDoesntOverwritePreviousCall() throws InterruptedException {

        IngredientAutocomplete fetcher = new IngredientAutocomplete();
        List<ApiResponse> ret = new ArrayList<>();
        fetcher.completeSearch("app", ret);

        fetcher.completeSearch("app", ret);
        //I know this is bad, just here till I find another solution
        Thread.sleep(1000);

        assertTrue(!ret.isEmpty());
        assertEquals("apple", ret.get(0).name);

    }
    @Test
    public void nutrientFetcherReturnsCorrectNutrients() throws InterruptedException {
        IngredientAutocomplete fetcher = new IngredientAutocomplete();
        Ingredient ing = new Ingredient("pineapple", new Unit(1, "piece"));
        fetcher.getNutritionFromIngredient(9266, ing);

        //I know this is bad, just here till I find another solution
        Thread.sleep(1000);

        assertEquals(452.5, ing.getCalories(), 1.);
        assertEquals(1.09, ing.getFat(), 0.05);
        assertEquals(118.74, ing.getCarbs(), 1.);
        assertEquals(89.14, ing.getSugar(), 1.);
    }

}

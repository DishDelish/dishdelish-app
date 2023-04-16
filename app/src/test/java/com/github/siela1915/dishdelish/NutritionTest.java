package com.github.siela1915.dishdelish;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import com.github.siela1915.bootcamp.AutocompleteApi.ApiResponse;
import com.github.siela1915.bootcamp.AutocompleteApi.IngredientAutocomplete;
import com.github.siela1915.bootcamp.Recipes.Ingredient;

import org.checkerframework.checker.units.qual.A;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import okhttp3.Request;
import okio.Timeout;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NutritionTest {

    @Test
    public void tempTest() throws InterruptedException {
        IngredientAutocomplete fetcher = new IngredientAutocomplete();
        String toFetch = "lemo";
        List<ApiResponse> ingredients = new ArrayList<>();
        fetcher.completeSearch(toFetch, ingredients);
        Thread.sleep(100);
        ingredients.forEach(r -> System.out.println(r.id));
        assertEquals(new ArrayList<>(), ingredients.stream().map(i -> i.name).collect(Collectors.toList()));
    }
}

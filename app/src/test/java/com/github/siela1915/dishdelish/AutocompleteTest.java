package com.github.siela1915.dishdelish;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;

import com.github.siela1915.bootcamp.AutocompleteApi.ApiResponse;
import com.github.siela1915.bootcamp.AutocompleteApi.ApiService;
import com.github.siela1915.bootcamp.AutocompleteApi.EmptyUploadCallback;
import com.github.siela1915.bootcamp.AutocompleteApi.IngredientAutocomplete;
import com.github.siela1915.bootcamp.AutocompleteApi.Nutrient;
import com.github.siela1915.bootcamp.AutocompleteApi.NutrientsResponse;
import com.github.siela1915.bootcamp.AutocompleteApi.UploadCallback;
import com.github.siela1915.bootcamp.BuildConfig;
import com.github.siela1915.bootcamp.Recipes.ExampleRecipes;
import com.github.siela1915.bootcamp.Recipes.Ingredient;
import com.github.siela1915.bootcamp.Recipes.Recipe;
import com.github.siela1915.bootcamp.Recipes.Unit;

import org.checkerframework.checker.units.qual.A;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Request;
import okio.Timeout;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AutocompleteTest {

    //For these tests, only the ApiService class uses real calls, the other functions can be mocked
    //--------------------Real api calls----------------------------
    @Test
    public void ingredientAutocompleteCallWorks() throws IOException {
        IngredientAutocomplete fetcher = new IngredientAutocomplete();
        Call<List<ApiResponse>> call = fetcher.service.fetchIngredients("app", 5, BuildConfig.API_KEY, true);
        Response<List<ApiResponse>> response = call.execute();
        assertEquals(200, response.code());
        assertTrue(response.body().stream().filter(a -> a.name.equals("apple")).findFirst().isPresent());
    }

    @Test
    public void nutrientCallWorks() throws IOException {
        IngredientAutocomplete fetcher = new IngredientAutocomplete();
        Call<NutrientsResponse> call = fetcher.service.getNutrition(11165, 3, "g", BuildConfig.API_KEY);
        Response<NutrientsResponse> response = call.execute();
        assertEquals(200, response.code());
        assertTrue(response.body().nutrition.mapFromNutrients().containsKey("Protein"));
        assertEquals(response.body().nutrition.mapFromNutrients().get("Protein").doubleValue(), 0.06, 0.01);
    }


    //-----------------Mocked api calls------------------------

    ApiService mockedApi;
    Call<List<ApiResponse>> mockedIngCall;
    Call<NutrientsResponse> mockedNutCall;

    @Before
    public void setup() throws IOException {
        mockedApi = Mockito.mock(ApiService.class);
        mockedIngCall = Mockito.mock(Call.class);
        mockedNutCall = Mockito.mock(Call.class);
    }

    @Test
    public void fetcherReturnsNonemptyIngredient() throws InterruptedException {

        Mockito.when(mockedApi.fetchIngredients("app", 5, BuildConfig.API_KEY, true)).thenReturn(mockedIngCall);

        Mockito.doAnswer(invocation -> {
            Callback callback = invocation.getArgument(0, Callback.class);
            callback.onResponse(mockedIngCall, Response.success(Arrays.asList(new ApiResponse("apple", "not used", 0, "Not used", new String[]{"piece"}))));
            // or callback.onResponse(mockedCall, Response.error(404. ...);
            // or callback.onFailure(mockedCall, new IOException());
            return null;
        }).when(mockedIngCall).enqueue(any(Callback.class));

        IngredientAutocomplete fetcher = new IngredientAutocomplete();
        fetcher.service = mockedApi;
        List<ApiResponse> ret = new ArrayList<>();
        fetcher.completeSearch("app", ret);
        assertTrue(!ret.isEmpty());
        assertEquals("apple", ret.get(0).name);

    }

    @Test
    public void nullIngredientResponseHandledCorrectly() throws InterruptedException {
        Mockito.when(mockedApi.fetchIngredients("app", 5, BuildConfig.API_KEY, true)).thenReturn(mockedIngCall);

        Mockito.doAnswer(invocation -> {
            Callback callback = invocation.getArgument(0, Callback.class);
            callback.onResponse(mockedIngCall, Response.success(null));
            return null;
        }).when(mockedIngCall).enqueue(any(Callback.class));

        IngredientAutocomplete fetcher = new IngredientAutocomplete();
        fetcher.service = mockedApi;
        List<ApiResponse> ret = new ArrayList<>();
        fetcher.completeSearch("app", ret);
        assertTrue(ret.isEmpty());
    }

    @Test
    public void failedCallDoesntOverwritePreviousCall() throws InterruptedException {

        Mockito.when(mockedApi.fetchIngredients("app", 5, BuildConfig.API_KEY, true)).thenReturn(mockedIngCall);

        Mockito.doAnswer(invocation -> {
            Callback callback = invocation.getArgument(0, Callback.class);
            callback.onResponse(mockedIngCall, Response.success(Arrays.asList(new ApiResponse("apple", "not used", 0, "Not used", new String[]{"piece"}))));
            return null;
        }).when(mockedIngCall).enqueue(any(Callback.class));

        IngredientAutocomplete fetcher = new IngredientAutocomplete();
        fetcher.service = mockedApi;
        List<ApiResponse> ret = new ArrayList<>();
        fetcher.completeSearch("app", ret);

        Mockito.doAnswer(invocation -> {
            Callback callback = invocation.getArgument(0, Callback.class);
            callback.onFailure(mockedIngCall, new IOException());
            return null;
        }).when(mockedIngCall).enqueue(any(Callback.class));

        fetcher.completeSearch("app", ret);

        assertTrue(!ret.isEmpty());
        assertEquals("apple", ret.get(0).name);

    }
    @Test
    public void nutrientFetcherReturnsCorrectNutrients() throws InterruptedException {
        IngredientAutocomplete fetcher = new IngredientAutocomplete();
        Ingredient ing = new Ingredient("pineapple", new Unit(1, "piece"));
        Map<Integer, Boolean> finishedMap = new HashMap<>();
        finishedMap.put(9266, false);
        fetcher.getNutritionFromIngredient(9266, ing, finishedMap, new EmptyUploadCallback());

        //I know this is bad, just here till I find another solution
        Thread.sleep(1000);

        assertEquals(452.5, ing.getCalories(), 1.);
        assertEquals(1.09, ing.getFat(), 0.05);
        assertEquals(118.74, ing.getCarbs(), 1.);
        assertEquals(89.14, ing.getSugar(), 1.);
    }

    @Test
    public void test() throws InterruptedException {
        IngredientAutocomplete fetcher = new IngredientAutocomplete();
        //cauliflower rice recipe
        Recipe recipe = ExampleRecipes.recipes.get(1);
        int cilantroId = 11165;
        int cauliflowerId = 11135;
        Map<String, Integer> idMap = new HashMap<>();
        idMap.put(recipe.ingredientList.get(0).getIngredient(), cauliflowerId);
        idMap.put(recipe.ingredientList.get(1).getIngredient(), cilantroId);
        fetcher.getNutritionFromRecipe(recipe, idMap, new EmptyUploadCallback());
        Thread.sleep(1000);

        recipe.ingredientList.forEach(i -> {
            System.out.println(i.getProtein());
        });
        assertEquals(11.04, recipe.ingredientList.get(0).getProtein(), 0.01);
        assertEquals(0.06, recipe.ingredientList.get(1).getProtein(), 0.01);
    }

}

package com.github.siela1915.dishdelish;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;

import com.github.siela1915.bootcamp.AutocompleteApi.ApiResponse;
import com.github.siela1915.bootcamp.AutocompleteApi.ApiService;
import com.github.siela1915.bootcamp.AutocompleteApi.IngredientAutocomplete;
import com.github.siela1915.bootcamp.AutocompleteApi.Nutrient;
import com.github.siela1915.bootcamp.AutocompleteApi.NutrientsResponse;
import com.github.siela1915.bootcamp.AutocompleteApi.Nutrition;
import com.github.siela1915.bootcamp.BuildConfig;
import com.github.siela1915.bootcamp.Recipes.ExampleRecipes;
import com.github.siela1915.bootcamp.Recipes.Ingredient;
import com.github.siela1915.bootcamp.Recipes.Recipe;
import com.github.siela1915.bootcamp.Recipes.Unit;
import com.github.siela1915.dishdelish.Recipes.EmptyUploadCallback;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.MediaType;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.HttpException;
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
        //9266 = id for pineapple
        Mockito.when(mockedApi.getNutrition(9266, 1, "piece", BuildConfig.API_KEY)).thenReturn(mockedNutCall);

        Mockito.doAnswer(invocation -> {
            Callback callback = invocation.getArgument(0, Callback.class);
            callback.onResponse(mockedNutCall, Response.success(new NutrientsResponse(new Nutrition(Arrays.asList(
                    new Nutrient("Calories", 452.5, "cal", 22.63),
                    new Nutrient("Fat", 1.09, "g", 1.67),
                    new Nutrient("Carbohydrates", 118.74, "g", 39.58),
                    new Nutrient("Sugar", 89.14, "g", 99.05))))));
            return null;
        }).when(mockedNutCall).enqueue(any(Callback.class));

        IngredientAutocomplete fetcher = new IngredientAutocomplete();
        fetcher.service = mockedApi;
        Ingredient ing = new Ingredient("pineapple", new Unit(1, "piece"));
        Map<Integer, Boolean> finishedMap = new HashMap<>();
        finishedMap.put(9266, false);
        fetcher.getNutritionFromIngredient(9266, ing, finishedMap, new EmptyUploadCallback());


        assertEquals(452.5, ing.getCalories(), 0.01);
        assertEquals(1.09, ing.getFat(), 0.01);
        assertEquals(118.74, ing.getCarbs(), 0.01);
        assertEquals(89.14, ing.getSugar(), 0.01);
    }


    //if only some ingredients are valid (selected from the autocomplete dropdown), their nutrients still get fetched
    @Test
    public void certainInvalidIngredientsWontStopFetching() throws InterruptedException {
        //9266 = id for pineapple
        Mockito.when(mockedApi.getNutrition(9266, 1, "piece", BuildConfig.API_KEY)).thenReturn(mockedNutCall);

        Mockito.doAnswer(invocation -> {
            Callback callback = invocation.getArgument(0, Callback.class);
            callback.onResponse(mockedNutCall, Response.success(new NutrientsResponse(new Nutrition(Arrays.asList(
                    new Nutrient("Calories", 452.5, "cal", 22.63),
                    new Nutrient("Fat", 1.09, "g", 1.67),
                    new Nutrient("Carbohydrates", 118.74, "g", 39.58),
                    new Nutrient("Sugar", 89.14, "g", 99.05))))));
            return null;
        }).when(mockedNutCall).enqueue(any(Callback.class));

        IngredientAutocomplete fetcher = new IngredientAutocomplete();
        fetcher.service = mockedApi;
        Ingredient ing1 = new Ingredient("pineapple", new Unit(1, "piece"));
        Ingredient ing2 = new Ingredient("not valid", new Unit(1, "piece"));
        Map<String, Integer> idMap = new HashMap<>();
        idMap.put("pineapple", 9266);
        Recipe recipe = new Recipe();
        recipe.ingredientList = Arrays.asList(ing1, ing2);
        fetcher.getNutritionFromRecipe(recipe, idMap, new EmptyUploadCallback());

        assertEquals(452.5, ing1.getCalories(), 0.01);
        assertEquals(1.09, ing1.getFat(), 0.01);
        assertEquals(118.74, ing1.getCarbs(), 0.01);
        assertEquals(89.14, ing1.getSugar(), 0.01);

        assertEquals(0.0, ing2.getCalories(), 0.01);
        assertEquals(0.0, ing2.getFat(), 0.01);
        assertEquals(0.0, ing2.getCarbs(), 0.01);
        assertEquals(0.0, ing2.getSugar(), 0.01);
    }

    @Test
    public void cauliflowerRiceNutrientsAddedCorrectly() throws InterruptedException {
        int cilantroId = 11165;
        int cauliflowerId = 11135;

        Call<NutrientsResponse> mockedCilCall = Mockito.mock(Call.class);
        Call<NutrientsResponse> mockedCauCall = Mockito.mock(Call.class);
        Mockito.when(mockedApi.getNutrition(cilantroId, 3, "g", BuildConfig.API_KEY)).thenReturn(mockedCilCall);
        Mockito.when(mockedApi.getNutrition(cauliflowerId, 1, "piece", BuildConfig.API_KEY)).thenReturn(mockedCauCall);


        Mockito.doAnswer(invocation -> {
            Callback callback = invocation.getArgument(0, Callback.class);
            callback.onResponse(mockedNutCall, Response.success(new NutrientsResponse(new Nutrition(Arrays.asList(
                    new Nutrient("Calories", 0.69, "kcal", 0.03),
                    new Nutrient("Fat", 0.02, "g", 0.02),
                    new Nutrient("Carbohydrates", 0.03, "g", 0.01),
                    new Nutrient("Sugar", 0.03, "g", 0.03),
                    new Nutrient("Protein", 0.06, "g", 0.13))))));
            return null;
        }).when(mockedCilCall).enqueue(any(Callback.class));

        Mockito.doAnswer(invocation -> {
            Callback callback = invocation.getArgument(0, Callback.class);
            callback.onResponse(mockedNutCall, Response.success(new NutrientsResponse(new Nutrition(Arrays.asList(
                    new Nutrient("Calories", 143.75, "kcal", 7.19),
                    new Nutrient("Fat", 1.61, "g", 2.48),
                    new Nutrient("Carbohydrates", 17.08, "g", 6.21),
                    new Nutrient("Sugar", 10.98, "g", 12.2),
                    new Nutrient("Protein", 11.04, "g", 22.08))))));
            return null;
        }).when(mockedCauCall).enqueue(any(Callback.class));

        IngredientAutocomplete fetcher = new IngredientAutocomplete();
        fetcher.service = mockedApi;
        //cauliflower rice recipe
        Recipe recipe = ExampleRecipes.recipes.get(1);
        Map<String, Integer> idMap = new HashMap<>();
        idMap.put(recipe.ingredientList.get(0).getIngredient(), cauliflowerId);
        idMap.put(recipe.ingredientList.get(1).getIngredient(), cilantroId);
        fetcher.getNutritionFromRecipe(recipe, idMap, new EmptyUploadCallback());

        assertEquals(11.04, recipe.ingredientList.get(0).getProtein(), 0.01);
        assertEquals(0.06, recipe.ingredientList.get(1).getProtein(), 0.01);
    }


//-----------------------Failing API calls tests------------------------

    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private final ByteArrayOutputStream errContent = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;
    private final PrintStream originalErr = System.err;

    @Before
    public void setUpStreams() {
        System.setOut(new PrintStream(outContent));
    }

    @After
    public void restoreStreams() {
        System.setOut(originalOut);
    }


    @Test
    public void unsuccessfulNutrientApiCallHandledCorrectly(){
        //9266 = id for pineapple
        Mockito.when(mockedApi.getNutrition(9266, 1, "piece", BuildConfig.API_KEY)).thenReturn(mockedNutCall);


        String responseBodyString = "{\"1\": \"apple\"}"; // create a JSON string as an example response body
        ResponseBody responseBody = ResponseBody.create(MediaType.parse("application/json"), responseBodyString);
        Response<ResponseBody> response = Response.error(404, responseBody);

        Mockito.doAnswer(invocation -> {
            Callback callback = invocation.getArgument(0, Callback.class);
            callback.onResponse(mockedNutCall, response);
            return null;
            // or callback.onResponse(mockedCall, Response.error(404. ...);
            // or callback.onFailure(mockedCall, new IOException());
        }).when(mockedNutCall).enqueue(any(Callback.class));

        IngredientAutocomplete fetcher = new IngredientAutocomplete();
        fetcher.service = mockedApi;
        Ingredient ing = new Ingredient("pineapple", new Unit(1, "piece"));
        Map<Integer, Boolean> finishedMap = new HashMap<>();
        finishedMap.put(9266, false);
        EmptyUploadCallback callback = new EmptyUploadCallback();
        fetcher.getNutritionFromIngredient(9266, ing, finishedMap, callback);

        //Ingredient nutrients shouldn't change from the default value.
        assertEquals(ing.getProtein(), 0.0, 0.01);
        assertEquals(ing.getFat(), 0.0, 0.01);
        assertEquals(ing.getCalories(), 0.0, 0.01);
        assertEquals(ing.getCarbs(), 0.0, 0.01);
        assertEquals(ing.getSugar(), 0.0, 0.01);

        //correct error message sent
        String expectedMessage = "Couldn't fetch nutritional values of pineapple";
        assertEquals(expectedMessage, callback.errorMessage);
    }


    @Test
    public void failedNutrientApiCallHandledCorrectly(){
        //9266 = id for pineapple
        Mockito.when(mockedApi.getNutrition(9266, 1, "piece", BuildConfig.API_KEY)).thenReturn(mockedNutCall);


        String responseBodyString = "{\"1\": \"apple\"}"; // create a JSON string as an example response body
        ResponseBody responseBody = ResponseBody.create(MediaType.parse("application/json"), responseBodyString);
        Response<ResponseBody> response = Response.error(404, responseBody);

        Mockito.doAnswer(invocation -> {
            Callback callback = invocation.getArgument(0, Callback.class);
            callback.onFailure(mockedNutCall, new HttpException(response));
            return null;
        }).when(mockedNutCall).enqueue(any(Callback.class));

        IngredientAutocomplete fetcher = new IngredientAutocomplete();
        fetcher.service = mockedApi;
        Ingredient ing = new Ingredient("pineapple", new Unit(1, "piece"));
        Map<Integer, Boolean> finishedMap = new HashMap<>();
        finishedMap.put(9266, false);
        EmptyUploadCallback callback = new EmptyUploadCallback();
        fetcher.getNutritionFromIngredient(9266, ing, finishedMap, callback);

        //Ingredient nutrients shouldn't change from the default value.
        assertEquals(ing.getProtein(), 0.0, 0.01);
        assertEquals(ing.getFat(), 0.0, 0.01);
        assertEquals(ing.getCalories(), 0.0, 0.01);
        assertEquals(ing.getCarbs(), 0.0, 0.01);
        assertEquals(ing.getSugar(), 0.0, 0.01);

        //correct error message sent
        String expectedMessage = "Error while uploading: " + "HTTP 404 Response.error()";
        assertEquals(expectedMessage, callback.errorMessage);
    }

}

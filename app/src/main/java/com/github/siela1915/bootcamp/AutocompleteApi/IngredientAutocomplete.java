package com.github.siela1915.bootcamp.AutocompleteApi;

import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;

import com.github.siela1915.bootcamp.BuildConfig;
import com.github.siela1915.bootcamp.Recipes.Ingredient;
import com.github.siela1915.bootcamp.Recipes.Recipe;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

import kotlin.io.TextStreamsKt;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class IngredientAutocomplete {
    private final int numberOfIngredients = 5;
    private final int numberOfNutrition = 3;
    private final String API_KEY = BuildConfig.API_KEY;
    public ApiService service;

    //atomic map, we want it to be shared across threads for asynchronous api calls
    volatile Map<Integer, Boolean> finishedCallsMap = new ConcurrentHashMap<>();
    AtomicBoolean wasRun = new AtomicBoolean(false);



    //default constructor, no need to specify URL
    public IngredientAutocomplete() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://api.spoonacular.com/food/ingredients/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        service = retrofit.create(ApiService.class);
    }

    //will store the responses inside the ingredients list
    //implementation will have to work even with empty lists, i.e. while the call is still ongoing.
    public List<ApiResponse> completeSearch(String query, List<ApiResponse> ingredients){
        //TODO add retrying, log something in case of failure
        service.fetchIngredients(query, numberOfIngredients, API_KEY, true).enqueue(new Callback<List<ApiResponse>>() {
            @Override
            public void onResponse(Call<List<ApiResponse>> call, Response<List<ApiResponse>> response) {
                if(response.isSuccessful()) {
                    if (response.body() != null) {
                        for (ApiResponse ing : response.body()) {
                            //Adds the ingredients to the passed list
                            ingredients.add(ing);
                        }
                    }
                    //add a case for not successful?
                }

            }

            //Somehow indicate to the user that there was an error while fetching
            //Now just ignores it, since we want the autocomplete option to be empty in this case,
            //without disrupting the user too much.
            @Override
            public void onFailure(Call<List<ApiResponse>> call, Throwable t) {
            }
        });
        return ingredients;
    }

    //Method that only returns the names of the ingredients, used when uploading recipe
    public List<String> completeSearchNames(String query, AutoCompleteTextView view, Map<String, Integer> idMap){
        List<String> ingredients = new ArrayList<>();
        service.fetchIngredients(query, numberOfIngredients, API_KEY, true).enqueue(new Callback<List<ApiResponse>>() {
            @Override
            public void onResponse(Call<List<ApiResponse>> call, Response<List<ApiResponse>> response) {
                if(response.isSuccessful()) {
                    if (response.body() != null) {
                        for (ApiResponse ing : response.body()) {
                            //Adds the ingredients to the passed list
                            ingredients.add(ing.name);
                            idMap.put(ing.name, ing.id);
                            System.out.println(ing.name + ing.id);
                        }
                    }
                    //add a case for not successful?
                }
                ArrayAdapter<String> ingredientAdapter = new ArrayAdapter<String>(view.getContext(), android.R.layout.select_dialog_item, ingredients);
                view.setAdapter(ingredientAdapter);
                view.showDropDown();
            }

            //Somehow indicate to the user that there was an error while fetching
            //Now just ignores it, since we want the autocomplete option to be empty in this case,
            //without disrupting the user too much.
            @Override
            public void onFailure(Call<List<ApiResponse>> call, Throwable t) {
            }
        });
        return ingredients;
    }

    public void getNutritionFromIngredient(int id, Ingredient ingredient, Map<Integer, Boolean> finishedMap, UploadCallback callback){
        //System.out.println(service.getNutrition(id, ingredient.getUnit().getValue(), API_KEY).request().url().toString());

        //To be able to get the nutrition correctly for a given unit, we need a naming convention for the units which I am not sure how to do
        //TODO give the users possible options of units once he chooses an ingredient?
        service.getNutrition(id, ingredient.getUnit().getValue(), ingredient.getUnit().getInfo(), API_KEY).enqueue(new Callback<NutrientsResponse>() {
            @Override
            public void onResponse(Call<NutrientsResponse> call, Response<NutrientsResponse> response) {
                if(response.isSuccessful()) {
                    if (response.body() != null) {
                        Map<String, Double> map = response.body().nutrition.mapFromNutrients();
                        for(String s : Nutrition.NUTRIENT_NAMES){
                            switch (s) {
                                //map.get() won't return null due to how the map is constructed in the Nutrition class
                                case "Calories":
                                    ingredient.setCalories(map.get(s));
                                    break;
                                case "Fat":
                                    ingredient.setFat(map.get(s));
                                    break;
                                case "Sugar":
                                    ingredient.setSugar(map.get(s));
                                    break;
                                case "Protein":
                                    ingredient.setProtein(map.get(s));
                                    break;
                                case "Carbohydrates":
                                    ingredient.setCarbs(map.get(s));
                                    break;
                                default:
                                    break;
                            }
                        }
                    }
                    //if not successful
                }else{
                    String errorMessage = "Error code: " + response.code() + " With message: " + response.message();
                    System.out.println(errorMessage);
                }
                finishedMap.put(id, true);
                //For every finished call, we check if all the calls are done
                //If they are, we call the callback to upload the recipe
                if(!(finishedMap.containsValue(false)) && wasRun.compareAndSet(false, true)) {

                    callback.onSuccess();
                    System.out.println("this should happen once");
                }
            }
            @Override
            public void onFailure(Call<NutrientsResponse> call, Throwable t) {
                //even if the call fails, it is finished so we indicate it in the map
                finishedMap.put(id, true);
            }
        });
    }

    /**
     * Sets the nutrients of the recipe
     * @param recipe
     */
    public void getNutritionFromRecipe(Recipe recipe, Map<String, Integer> idMap, UploadCallback callback){
        //we could use execute() to make the calls synchronously?
        //volatile Map<Integer, Boolean> finishedCallsMap = new ConcurrentHashMap<>();

        recipe.ingredientList.forEach(i -> {
            Integer ingID = idMap.get(i.getIngredient());
            finishedCallsMap.put(ingID, false);
        });

        recipe.ingredientList.forEach(i -> {
            Integer ingID = idMap.get(i.getIngredient());
            getNutritionFromIngredient(ingID, i, finishedCallsMap, callback);
        });
    }


}

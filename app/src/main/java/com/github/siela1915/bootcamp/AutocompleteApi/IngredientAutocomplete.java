package com.github.siela1915.bootcamp.AutocompleteApi;

import com.github.siela1915.bootcamp.Recipes.Ingredient;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class IngredientAutocomplete {
    private final int numberOfIngredients = 5;
    private final int numberOfNutrition = 3;
    private final String apiKey = "44a82829c64d4202a18b887e47e76bdd";
    public ApiService service;



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
        service.fetchIngredients(query, numberOfIngredients, apiKey, true).enqueue(new Callback<List<ApiResponse>>() {
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

    public void getNutritionFromIngredient(int id, Ingredient ingredient){
        service.getNutrition(id, ingredient.getUnit().getValue(), ingredient.getUnit().getInfo(), apiKey).enqueue(new Callback<NutrientsResponse>() {
            @Override
            public void onResponse(Call<NutrientsResponse> call, Response<NutrientsResponse> response) {
                if(response.isSuccessful()) {
                    if (response.body() != null) {
                        for (Nutrient nut : response.body().nutrients) {
                            //Select which nutrient information we want
                            switch(nut.name){
                                case "Calories":
                                    ingredient.setCalories(nut.amount);
                                    break;
                                case "Fat":
                                    ingredient.setFat(nut.amount);
                                case "Carbohydrates":
                                    ingredient.setCarbs(nut.amount);
                                    break;
                                case "Sugar":
                                    ingredient.setSugar(nut.amount);
                                case "Protein":
                                    ingredient.setProtein(nut.amount);
                                    break;
                            }
                        }
                    }
                    //add a case for not successful?
                }
            }
            //Somehow indicate to the user that there was an error while fetching
            //Now just ignores it, since we want the autocomplete option to be empty in this case,
            //without disrupting the user too much.
            @Override
            public void onFailure(Call<NutrientsResponse> call, Throwable t) {
            }
        });
    }

    public Map<String, Double> getNutritionValues(List<Ingredient> ing, Map<String, Double> ret){
        ing.forEach(i -> i.getIngredient());
        ret = new HashMap<>();

        return null;
    }

}

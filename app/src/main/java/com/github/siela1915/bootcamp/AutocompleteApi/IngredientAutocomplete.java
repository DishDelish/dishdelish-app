package com.github.siela1915.bootcamp.AutocompleteApi;

import com.github.siela1915.bootcamp.Recipes.Ingredient;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class IngredientAutocomplete {
    private final int numberOfIngredients = 5;
    private final String apiKey = "44a82829c64d4202a18b887e47e76bdd";

    Retrofit retrofit = new Retrofit.Builder()
            .baseUrl("https://api.spoonacular.com/food/ingredients/")
            .addConverterFactory(GsonConverterFactory.create())
            .build();
    ApiService service = retrofit.create(ApiService.class);


    public List<String> completeSearch(String query){
        List<String> temp = new ArrayList<>();
        service.fetchIngredients(query, numberOfIngredients, apiKey).enqueue(new Callback<List<ApiResponse>>() {
            @Override
            public void onResponse(Call<List<ApiResponse>> call, Response<List<ApiResponse>> response) {
                for(ApiResponse ing: response.body()) {
                    System.out.println(ing.name);
                    temp.add(ing.name);
                }




                if(response.isSuccessful()){
//                    val temp = response.body() as BoredActivity
//                    dataView.setText(temp.activity)
//                    dao.insertAll(temp)
                }else{
                    //dataView.setText("Fetch was unsuccessful :(")
                }
                System.out.println("yay");

            }

            @Override
            public void onFailure(Call<List<ApiResponse>> call, Throwable t) {
                System.out.println("nay");
            }
        });
        return temp;
    }
}

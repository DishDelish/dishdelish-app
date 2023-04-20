package com.github.siela1915.bootcamp;

import android.content.Context;
import android.content.Intent;

import com.github.siela1915.bootcamp.Recipes.Ingredient;
import com.github.siela1915.bootcamp.Recipes.Recipe;

import java.util.ArrayList;
import java.util.List;

public class RecipeConverter {

    public static ArrayList<String> convertIngredientList(List<Ingredient> ingredients){
        ArrayList<String> ingredientList = new ArrayList<>();
        for(int i = 0; i < ingredients.size(); i++){
            ingredientList.add(ingredients.get(i).getUnit().toString() + " "+ingredients.get(i).getIngredient());
        }
        return ingredientList;
    }

    public static Intent convertToIntent(Recipe recipe, Context c){
        Intent i = new Intent(c, RecipeActivity.class);
        i.putExtra("Recipe", recipe);
        return i;
    }
}

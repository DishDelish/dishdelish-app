package com.github.siela1915.bootcamp;

import android.content.Context;
import android.content.Intent;
import android.util.Pair;

import com.github.siela1915.bootcamp.Recipes.Ingredient;
import com.github.siela1915.bootcamp.Recipes.Recipe;
import com.github.siela1915.bootcamp.Recipes.Unit;

import java.util.ArrayList;
import java.util.List;

public class RecipeConverter {

    public static ArrayList<String> convertIngredientList(List<Pair<Unit, Ingredient>> ingredients){
        ArrayList<String> ingredientList = new ArrayList<>();

        for(int i = 0; i < ingredients.size(); i++){
            ingredientList.add(ingredients.get(i).first.toString() + " "+ingredients.get(i).second.toString());
        }
        return ingredientList;
    }

    public static Intent convertToIntent(Recipe recipe, Context c){

        Intent i = new Intent(c, RecipeActivity.class);

        i.putExtra("image", recipe.image);
        i.putExtra("recipeName", recipe.recipeName);
        i.putExtra("userName", recipe.userName);
        i.putExtra("profilePicture", recipe.profilePicture);
        i.putExtra("rating", recipe.rating);
        i.putExtra("prepTime", recipe.prepTime);
        i.putExtra("cookTime", recipe.cookTime);
        i.putExtra("servings", recipe.servings);
        i.putExtra("utensils", recipe.utensils.getUtensils().toArray());
//        i.putStringArrayListExtra("ingredients", convertIngredientList(recipe.ingredientList));
        i.putExtra("steps", recipe.steps.toArray());
        i.putExtra("comments", recipe.comments.toArray());

        return i;

    }
}

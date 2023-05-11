package com.github.siela1915.bootcamp.Tools;

import com.github.siela1915.bootcamp.Recipes.Recipe;
import com.github.siela1915.bootcamp.firebase.Database;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

public class SuggestionCalculator {

    public static Task<List<Recipe>> getSuggestions() {
        return null;
    }

    private static Task<List<Recipe>> getFavouritesFromDatabase() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            Database db = new Database(FirebaseDatabase.getInstance());
            Task<List<String>> favourites = db.getFavorites();
            return favourites.continueWith(ls -> {
                List<Recipe> recipes = new ArrayList<>();
                for (String key : ls.getResult()) {
                    recipes.add(db.getAsync(key).getResult());
                }
                return recipes;
            });
        }
        return null;
    }

}

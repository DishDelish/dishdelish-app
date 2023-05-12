package com.github.siela1915.bootcamp.Tools;

import com.github.siela1915.bootcamp.Recipes.Recipe;
import com.github.siela1915.bootcamp.firebase.Database;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

public class SuggestionCalculator {

    /* Specifies the number of recipes to fetch from the database.
     * Should not be hardcoded.
     */
    private static int N = 20;
    private static int TOP = 10;


    public static Task<List<Recipe>> getSuggestions() {
        Database db = new Database(FirebaseDatabase.getInstance());
        Task<List<Recipe>> favourites = getFavouritesFromDatabase(db);
        Task<List<Recipe>> recipes = db.getNRandomAsync(N);
        Task<List<Recipe>> topRecipes = db.getByMaxLikesAsync(TOP);
        return null;
    }

    private static Task<List<Recipe>> getFavouritesFromDatabase(Database db) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            Task<List<String>> favourites = db.getFavorites();
            return favourites.continueWith(ls -> {
                List<Recipe> recipes = new ArrayList<>();
                for (String key : ls.getResult()) {
                    recipes.add(db.getAsync(key).getResult());
                }
                return recipes;
            });
        }
        return Tasks.forResult(new ArrayList<>());
    }


}

package com.github.siela1915.bootcamp.Tools;

import com.github.siela1915.bootcamp.Labelling.AllergyType;
import com.github.siela1915.bootcamp.Labelling.CuisineType;
import com.github.siela1915.bootcamp.Labelling.DietType;
import com.github.siela1915.bootcamp.Recipes.Recipe;
import com.github.siela1915.bootcamp.firebase.Database;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskCompletionSource;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

public class SuggestionCalculator {

    /* Specifies the number of recipes to fetch from the database.
     * Should not be hardcoded.
     */
    private static int N = 20;
    private static int TOP = 10;


    public static Task<List<Recipe>> getSuggestions(Database db) {

        // Create a list of tasks
        List<Task<List<Recipe>>> tasks = new ArrayList<>();
        tasks.add(getFavouritesFromDatabase(db));
        tasks.add(db.getNRandomAsync(N));
        tasks.add(db.getByMaxLikesAsync(TOP));

        return Tasks.whenAllSuccess(tasks)
                .continueWith(task -> {
                    List<Recipe> favourites = (List<Recipe>) task.getResult().get(0);
                    AllergyType allergy = Utilities.getDominantAllergy(favourites);
                    DietType diet = Utilities.getDominantDiet(favourites);
                    CuisineType cuisine = Utilities.getDominantCuisine(favourites);

                    List<Recipe> random = (List<Recipe>) task.getResult().get(1);
                    Set<Recipe> result = new HashSet<>(); //Use set to avoid duplicates
                    result.addAll(Utilities.getAllergy(allergy, random));
                    result.addAll(Utilities.getCuisine(cuisine, random));
                    result.addAll(Utilities.getDiet(diet, random));

                    result.addAll((Collection<? extends Recipe>) task.getResult().get(2));
                    List<Recipe> ls = result.stream().collect(Collectors.toList());
                    Collections.shuffle(ls);

                    return ls;
                });
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


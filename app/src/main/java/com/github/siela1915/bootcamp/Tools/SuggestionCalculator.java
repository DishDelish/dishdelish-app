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
    private static int N = 100;
    private static int TOP = 10;


    public static Task<List<Recipe>> getSuggestions(Database db) {

        // Create a list of tasks
        List<Task<List<Recipe>>> tasks = new ArrayList<>();
        tasks.add(db.getFavoriteRecipes());
        tasks.add(db.getNRandomAsync(N));
        tasks.add(db.getByMaxLikesAsync(TOP));

        return Tasks.whenAll(tasks)
                .continueWith(task -> {
                    List<Recipe> favourites = new ArrayList<>();
                    if (tasks.get(0).isSuccessful()) {
                        favourites = tasks.get(0).getResult();
                    }
                    AllergyType allergy = Utilities.getDominantAllergy(favourites);
                    DietType diet = Utilities.getDominantDiet(favourites);
                    CuisineType cuisine = Utilities.getDominantCuisine(favourites);

                    List<Recipe> random = tasks.get(1).getResult();
                    Set<Recipe> result = new HashSet<>(); //Use set to avoid duplicates

                    //Add all recipes with dominant cuisine/allergy/diet types
                    result.addAll(Utilities.getAllergy(allergy, random));
                    result.addAll(Utilities.getCuisine(cuisine, random));
                    result.addAll(Utilities.getDiet(diet, random));

                    //Add all top recipes
                    result.addAll(tasks.get(2).getResult());

                    //Add some different recipes too
                    result.addAll(random.stream().filter(r -> !result.contains(r)).limit(N/2).collect(Collectors.toList()));
                    List<Recipe> ls = new ArrayList<>(result);
                    Collections.shuffle(ls);

                    return ls;
                });
    }

}


package com.github.siela1915.bootcamp.firebase;

import com.github.siela1915.bootcamp.Recipes.Recipe;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.internal.api.FirebaseNoSignedInUserException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

/**
 * Java class which connects to the Firebase database to set and retrieve data
 *
 */
public class Database {

    private DatabaseReference db;
    private final static String RECIPES = "recipes";
    private final static String FAVORITES = "favorites";

    /**
     * Constructor instantiates the connection to the database
     * @param database the database instance
     * Pass FirebaseDatabase.getInstance() to get default database
     */
    public Database(FirebaseDatabase database) {
        db = database.getReference();
    }


    /**
     * Retrieves a recipe from the database given its unique id
     * @param uniqueKey the unique id associated to the recipe
     * @return the recipe fetched from the database
     * @throws ExecutionException
     * @throws InterruptedException
     * The exceptions are thrown when the retrieval fails.
     * For instance if the method times-out due to a network connection error.
     */
    public Recipe get(String uniqueKey) throws ExecutionException, InterruptedException {
            Task<DataSnapshot> task = db.child(RECIPES).child(uniqueKey).get();
            try {
                DataSnapshot snapshot = Tasks.await(task);
                return snapshot.getValue(Recipe.class);
            } catch (ExecutionException | InterruptedException e) {
                throw e;
            }
    }

    /**
     * Adds a recipe to the database.
     * @param recipe the recipe to add to the database
     * @return the unique key id associated to this specific recipe in the database
     */
    public String set(Recipe recipe) throws ExecutionException, InterruptedException {
        Map<String, Object> value = recipeToMap(recipe);
        String uniqueKey = db.child(RECIPES).child("new").push().getKey();
        try {
            Tasks.await(db.child(RECIPES).child(uniqueKey).updateChildren(value));
        } catch (ExecutionException | InterruptedException e) {
            throw e;
        }
        return uniqueKey;
    }

    /**
     * Removes a recipe from the database.
     * @param key the unique identifying key of the recipe
     */
    public void remove(String key) {
        db.child(RECIPES).child(key).removeValue();
    }

    /**
     * Retrieve a recipe with a given name (not the unique id).
     * Since the name is not unique, this method may return multiple recipes.
     * @param name the name of the recipe to search for
     * @return a map from the recipe's unique key id to the recipe itself
     * @throws ExecutionException
     * @throws InterruptedException
     * The exceptions are thrown when the retrieval fails.
     * For instance if the method times-out due to a network connection error.
     */
    public Map<String, Recipe> getByName(String name) throws ExecutionException, InterruptedException {
        Query query = db.child(RECIPES).orderByChild("recipeName").equalTo(name);
        Task<DataSnapshot> task = query.get();
        try {
            DataSnapshot snapshot = Tasks.await(task);
            Map<String, Recipe> recipes = new HashMap<>();
            for (DataSnapshot val : snapshot.getChildren()) {
                recipes.put(val.getKey(), val.getValue(Recipe.class));
            }
            return recipes;
        } catch (ExecutionException | InterruptedException e) {
            throw e;
        }

    }

    private Map<String, Object> recipeToMap(Recipe recipe) {
        Map<String, Object> map = new HashMap<>();
        map.put("image", recipe.image);
        map.put("recipeName", recipe.recipeName);
        map.put("userName", recipe.userName);
        map.put("profilePicture", recipe.profilePicture);
        map.put("rating", recipe.rating);
        map.put("prepTime", recipe.prepTime);
        map.put("cookTime", recipe.cookTime);
        map.put("servings", recipe.servings);
        map.put("utensils", recipe.utensils);
        map.put("cuisineTypes", Arrays.asList(recipe.cuisineTypes));
        map.put("allergyTypes", Arrays.asList(recipe.allergyTypes));
        map.put("dietTypes", Arrays.asList(recipe.dietTypes));
        map.put("ingredientList", recipe.ingredientList);
        map.put("steps", recipe.steps);
        map.put("comments", recipe.comments);
        map.put("likes", recipe.likes);
        return map;
    }

    public Task<Void> addFavorite(String recipeId) {
        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            return Tasks.forException(new FirebaseNoSignedInUserException("Sign in to add favorites"));
        }
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        Task<DataSnapshot> existing = db.child(FAVORITES + "/" + userId + "/" + recipeId).get();
        return existing.continueWithTask(t -> {
            if (t.getResult().getValue() != null) {
                return Tasks.forResult(null);
            }
            return db.child(FAVORITES + "/" + userId).updateChildren(Collections.singletonMap(recipeId, System.currentTimeMillis()));
        });
    }

    public Task<Void> removeFavorite(String recipeId) {
        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            return Tasks.forException(new FirebaseNoSignedInUserException("Sign in to remove favorites"));
        }
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        return db.child(FAVORITES + "/" + userId + "/" + recipeId).removeValue();
    }

    public Task<List<String>> getFavorites() {
        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            return Tasks.forException(new FirebaseNoSignedInUserException("Sign in to get favorites"));
        }
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        Query query = db.child(FAVORITES + "/" + userId).orderByValue();
        Task<DataSnapshot> task = query.get();
        return task.continueWith(snapshot -> {
            List<String> favs = new ArrayList<>();
            for (DataSnapshot recipe : snapshot.getResult().getChildren()) {
                favs.add(recipe.getKey());
            }
            return favs;
        });
    }
}

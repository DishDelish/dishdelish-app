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
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

/**
 * Java class which connects to the Firebase database to set and retrieve data
 *
 */
public class Database {

    private final DatabaseReference db;
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
     */
    public Recipe get(String uniqueKey) throws ExecutionException, InterruptedException {
        Task<DataSnapshot> task = db.child(RECIPES).child(uniqueKey).get();
        DataSnapshot snapshot = Tasks.await(task);
        return snapshot.getValue(Recipe.class);
    }

    /**
     * Retrieves asynchronously a recipe from the database given its unique id
     * @param uniqueKey the unique id associated to the recipe
     * @return the recipe fetched from the database embedded in a Task
     */
    public Task<Recipe> getAsync(String uniqueKey) {
        Task<DataSnapshot> task = db.child(RECIPES).child(uniqueKey).get();
        return task.continueWith(snapshot -> {
            DataSnapshot data = snapshot.getResult();
            return data.getValue(Recipe.class);
        });
    }

    /**
     * Adds a recipe to the database.
     * @param recipe the recipe to add to the database
     * @return the unique key id associated to this specific recipe in the database
     */
    public String set(Recipe recipe) throws ExecutionException, InterruptedException {
        String uniqueKey = db.child(RECIPES).child("new").push().getKey();
        recipe.setUniqueKey(uniqueKey);
        Map<String, Object> value = new HashMap<>();
        value.put(uniqueKey, recipe);
        Tasks.await(db.child(RECIPES).updateChildren(value));
        return uniqueKey;
    }

    /**
     * Adds asynchronously a recipe to the database.
     * @param recipe the recipe to add to the database
     * @return the unique key id associated to this specific recipe in the database embedded in a Task
     */
    public Task<String> setAsync(Recipe recipe) {
        String uniqueKey = db.child(RECIPES).child("new").push().getKey();
        recipe.setUniqueKey(uniqueKey);
        Map<String, Object> value = new HashMap<>();
        value.put(uniqueKey, recipe);
        return db.child(RECIPES).updateChildren(value).continueWith(snapshot -> uniqueKey);
    }

    /**
     * Removes a recipe from the database.
     * @param key the unique identifying key of the recipe
     */
    public void remove(String key) {
        db.child(RECIPES).child(key).removeValue();
    }

    /**
     * Removes asynchronously a recipe from the database.
     * @param key the unique identifying key of the recipe
     * @return Void task. No return value from this task.
     */
    public Task<Void> removeAsync(String key) {
        return db.child(RECIPES).child(key).removeValue();
    }

    /**
     * Retrieves recipes with a given name (not the unique id).
     * Since the name is not unique, this method may return multiple recipes.
     * @param name the name of the recipe to search for
     * @return a map from the recipe's unique key id to the recipe itself
     */
    public Map<String, Recipe> getByName(String name) throws ExecutionException, InterruptedException {
        return getByStringAttribute("recipeName", name);
    }

    /**
     * Retrieves asynchronously recipes with a given name (not the unique id).
     * Since the name is not unique, this method may return multiple recipes.
     * @param name the name of the recipe to search for
     * @return a list of recipes with a matching name embedded in a Task
     */
    public Task<List<Recipe>> getByNameAsync(String name) {
        return getByStringAttributeAsync("recipeName", name);
    }

    /**
     * Retrieve a recipe with a given name (not the unique id).
     * Since the name is not unique, this method may return multiple recipes.
     * @param userName the name of the user who uploaded the recipe
     * @return a map from the recipe's unique key id to the recipe itself
     */
    public Map<String, Recipe> getByUserName(String userName) throws ExecutionException, InterruptedException {
        return getByStringAttribute("userName", userName);
    }

    /**
     * Retrieve asynchronously a recipe with a given name (not the unique id).
     * Since the name is not unique, this method may return multiple recipes.
     * @param userName the name of the user who uploaded the recipe
     * @return a list of recipes with a matching name embedded in a Task
     */
    public Task<List<Recipe>> getByUserNameAsync(String userName) {
        return getByStringAttributeAsync("userName", userName);
    }

    /**
     * Retrieve recipes with lower prep time.
     * @param time the upper bound limit to the prep time.
     * @return recipes with lower prep time than the time specified as parameter.
     */
    public Map<String, Recipe> getByUpperLimitOnPrepTime(int time) throws ExecutionException, InterruptedException {
        return getByUpperLimitOnTime("prepTime", time);
    }

    /**
     * Retrieve asynchronously recipes with lower prep time.
     * @param time the upper bound limit to the prep time.
     * @return recipes with lower prep time than the time specified as parameter.
     */
    public Task<List<Recipe>> getByUpperLimitOnPrepTimeAsync(int time) {
        return getByUpperLimitOnTimeAsync("prepTime", time);
    }

    /**
     * Retrieve recipes with lower cook time.
     * @param time the upper bound limit to the prep time.
     * @return recipes with lower prep time than the time specified as parameter.
     */
    public Map<String, Recipe> getByUpperLimitOnCookTime(int time) throws ExecutionException, InterruptedException {
        return getByUpperLimitOnTime("cookTime", time);
    }

    /**
     * Retrieve asynchronously recipes with lower cook time.
     * @param time the upper bound limit to the cook time.
     * @return recipes with lower cook time than the time specified as parameter.
     */
    public Task<List<Recipe>> getByUpperLimitOnCookTimeAsync(int time) {
        return getByUpperLimitOnTimeAsync("cookTime", time);
    }

    /**
     * Retrieve recipes with higher prep time.
     * @param time the lower bound limit to the prep time.
     * @return recipes with higher prep time than the time specified as parameter.
     */
    public Map<String, Recipe> getByLowerLimitOnPrepTime(int time) throws ExecutionException, InterruptedException {
        return getByLowerLimitOnTime("prepTime", time);
    }

    /**
     * Retrieve asynchronously recipes with higher prep time.
     * @param time the lower bound limit to the prep time.
     * @return recipes with higher prep time than the time specified as parameter.
     */
    public Task<List<Recipe>> getByLowerLimitOnPrepTimeAsync(int time) {
        return getByLowerLimitOnTimeAsync("prepTime", time);
    }

    /**
     * Retrieve recipes with higher prep time.
     * @param time the upper bound limit to the prep time.
     * @return recipes with lower prep time than the time specified as parameter.
     */
    public Map<String, Recipe> getByLowerLimitOnCookTime(int time) throws ExecutionException, InterruptedException {
        return getByLowerLimitOnTime("cookTime", time);
    }

    /**
     * Retrieve asynchronously recipes with higher cook time.
     * @param time the lower bound limit to the cook time.
     * @return recipes with higher cook time than the time specified as parameter.
     */
    public Task<List<Recipe>> getByLowerLimitOnCookTimeAsync(int time) {
        return getByLowerLimitOnTimeAsync("cookTime", time);
    }

    /**
     * Retrieve the top n recipes with the maximum number of likes in the database
     * i.e. the most popular recipes.
     * @param n number of top liked recipes to retrieve from the database.
     * @return the top n recipes.
     */
    public Map<String, Recipe> getByMaxLikes(int n) throws ExecutionException, InterruptedException {
        return getByValueAttribute("likes", n);
    }

    /**
     * Retrieve asynchronously the top n recipes with the maximum number of likes in the database
     * i.e. the most popular recipes.
     * @param n number of top liked recipes to retrieve from the database.
     * @return the top n recipes.
     */
    public Task<List<Recipe>> getByMaxLikesAsync(int n) {
        return getByValueAttributeAsync("likes", n);
    }

    /**
     * Retrieve the top n recipes with the maximum number of ratings in the database
     * @param n number of top liked recipes to retrieve from the database
     * @return the top n recipes
     */
    public Map<String, Recipe> getByNumRatings(int n) throws ExecutionException, InterruptedException {
        return getByValueAttribute("numRatings", n);
    }

    /**
     * Retrieve the top n recipes with the maximum number of ratings in the database
     * @param n number of top liked recipes to retrieve from the database
     * @return the top n recipes
     */
    public Task<List<Recipe>> getByNumRatingsAsync(int n) {
        return getByValueAttributeAsync("numRatings", n);
    }

    private Map<String, Recipe> getByValueAttribute(String attribute, int n) throws ExecutionException, InterruptedException {
        if (n < 1) {
            throw new IllegalArgumentException();
        }
        Query query = db.child(RECIPES).orderByChild(attribute).orderByValue().limitToLast(n);
        Task<DataSnapshot> task = query.get();
        DataSnapshot snapshot = Tasks.await(task);
        Map<String, Recipe> recipes = new HashMap<>();
        for (DataSnapshot val : snapshot.getChildren()) {
            recipes.put(val.getKey(), val.getValue(Recipe.class));
        }
        return recipes;
    }

    private Task<List<Recipe>> getByValueAttributeAsync(String attribute, int n) {
        if (n < 1) {
            throw new IllegalArgumentException();
        }
        Query query = db.child(RECIPES).orderByChild(attribute).orderByValue().limitToLast(n);
        Task<DataSnapshot> task = query.get();
        return task.continueWith(snapshot -> {
            List<Recipe> recipes = new ArrayList<>();
            for (DataSnapshot s : snapshot.getResult().getChildren()) {
                recipes.add(s.getValue(Recipe.class));
            }
            return recipes;
        });
    }

    private Map<String, Recipe> getByUpperLimitOnTime(String attribute, int time) throws ExecutionException, InterruptedException {
        Query query = db.child(RECIPES).orderByChild(attribute).endAt(time);
        Task<DataSnapshot> task = query.get();
        DataSnapshot snapshot = Tasks.await(task);
        Map<String, Recipe> recipes = new HashMap<>();
        for (DataSnapshot val : snapshot.getChildren()) {
            recipes.put(val.getKey(), val.getValue(Recipe.class));
        }
        return recipes;
    }

    private Task<List<Recipe>> getByUpperLimitOnTimeAsync(String attribute, int time) {
        Query query = db.child(RECIPES).orderByChild(attribute).endAt(time);
        Task<DataSnapshot> task = query.get();
        return task.continueWith(snapshot -> {
            List<Recipe> recipes = new ArrayList<>();
            for (DataSnapshot s : snapshot.getResult().getChildren()) {
                recipes.add(s.getValue(Recipe.class));
            }
            return recipes;
        });
    }

    private Map<String, Recipe> getByLowerLimitOnTime(String attribute, int time) throws ExecutionException, InterruptedException {
        Query query = db.child(RECIPES).orderByChild(attribute).startAt(time);
        Task<DataSnapshot> task = query.get();
        DataSnapshot snapshot = Tasks.await(task);
        Map<String, Recipe> recipes = new HashMap<>();
        for (DataSnapshot val : snapshot.getChildren()) {
            recipes.put(val.getKey(), val.getValue(Recipe.class));
        }
        return recipes;
    }

    private Task<List<Recipe>> getByLowerLimitOnTimeAsync(String attribute, int time) {
        Query query = db.child(RECIPES).orderByChild(attribute).startAt(time);
        Task<DataSnapshot> task = query.get();
        return task.continueWith(snapshot -> {
            List<Recipe> recipes = new ArrayList<>();
            for (DataSnapshot s : snapshot.getResult().getChildren()) {
                recipes.add(s.getValue(Recipe.class));
            }
            return recipes;
        });
    }

    private Map<String, Recipe> getByStringAttribute(String attribute, String name) throws ExecutionException, InterruptedException {
        Query query = db.child(RECIPES).orderByChild(attribute).equalTo(name);
        Task<DataSnapshot> task = query.get();
        DataSnapshot snapshot = Tasks.await(task);
        Map<String, Recipe> recipes = new HashMap<>();
        for (DataSnapshot val : snapshot.getChildren()) {
            recipes.put(val.getKey(), val.getValue(Recipe.class));
        }
        return recipes;
    }

    private Task<List<Recipe>> getByStringAttributeAsync(String attribute, String name) {
        Query query = db.child(RECIPES).orderByChild(attribute).equalTo(name);
        Task<DataSnapshot> task = query.get();
        return task.continueWith(snapshot -> {
            List<Recipe> recipes = new ArrayList<>();
            for (DataSnapshot s : snapshot.getResult().getChildren()) {
                recipes.add(s.getValue(Recipe.class));
            }
            return recipes;
        });
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

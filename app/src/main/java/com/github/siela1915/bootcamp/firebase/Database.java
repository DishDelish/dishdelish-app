package com.github.siela1915.bootcamp.firebase;

import android.security.keystore.UserNotAuthenticatedException;

import com.github.siela1915.bootcamp.Recipes.Ingredient;
import com.github.siela1915.bootcamp.Recipes.Recipe;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
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
import java.util.stream.Collectors;

/**
 * Java class which connects to the Firebase database to set and retrieve data
 *
 */
public class Database {

    private final DatabaseReference db;
    public final static String RECIPES = "recipes",
                                FAVORITES = "favorites",
                                FRIDGE = "fridge",
                                NEW = "new",
                                USERNAME = "userName",
                                RECIPE_NAME = "recipeName",
                                PREP_TIME = "prepTime",
                                COOK_TIME = "cookTime",
                                LIKES = "likes",
                                NUM_RATINGS = "numRatings";

    /**
     * Constructor instantiates the connection to the database
     * @param database the database instance
     * Pass FirebaseInstanceManager.getDatabase() to get default database
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
        return Tasks.await(getAsync(uniqueKey));
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
     * Get n random recipes from the database.
     * Note: calling this method once will give a pseudo-random list of recipes.
     * However, calling it multiple times with the same argument will return more or less the same
     * recipes.
     * @param n Number of recipes to retrieve from the database.
     * @return A list of those recipes.
     */
    public List<Recipe> getNRandom(int n) throws ExecutionException, InterruptedException {
        return Tasks.await(getNRandomAsync(n));
    }

    /**
     * Get n random recipes from the database asynchronously.
     * Note: calling this method once will give a pseudo-random list of recipes.
     * However, calling it multiple times with the same argument will return more or less the same
     * recipes.
     * @param n Number of recipes to retrieve from the database.
     * @return A list of those recipes.
     */
    public Task<List<Recipe>> getNRandomAsync(int n) {
        if (n < 1) {
            throw new IllegalArgumentException("Number of recipes to retrieve must be strictly positive.");
        }
        Query query = db.child(RECIPES).limitToFirst(n);
        return getValueOfQuery(query);
    }

    /**
     * Adds a recipe to the database.
     * @param recipe the recipe to add to the database
     * @return the unique key id associated to this specific recipe in the database
     */
    public String set(Recipe recipe) throws ExecutionException, InterruptedException {
        return Tasks.await(setAsync(recipe));
    }

    /**
     * Adds asynchronously a recipe to the database.
     * @param recipe the recipe to add to the database
     * @return the unique key id associated to this specific recipe in the database embedded in a Task
     */
    public Task<String> setAsync(Recipe recipe) {
        String uniqueKey = db.child(RECIPES).child(NEW).push().getKey();
        recipe.setUniqueKey(uniqueKey);
        return updateAsync(recipe).continueWith(snapshot -> uniqueKey);
    }

    /**
     * Updates an already existing recipe in the database.
     * Note: update on a recipe that does not yet exist in the database will add that recipe to the
     * database if its uniqueKey attribute is not null and not empty. If the uniqueKey attribute is
     * null update will throw a DatabaseException. If the uniqueKey is empty, update will throw an
     * ExecutionException
     * @param recipe to modify/update
     * @throws ExecutionException when the recipe's uniqueKey attribute is an empty string.
     */
    public void update(Recipe recipe) throws ExecutionException, InterruptedException {
        Tasks.await(updateAsync(recipe));
    }

    /**
     * Updates asynchronously an already existing recipe in the database.
     * Note: update on a recipe that does not yet exist in the database will add that recipe to the
     * database if its uniqueKey attribute is not null and not empty. If the uniqueKey attribute is
     * null update will throw a DatabaseException. If the uniqueKey is empty, update will throw an
     * ExecutionException
     * @param recipe to modify/update
     */
    public Task<Void> updateAsync(Recipe recipe) {
        Map<String, Object> value = new HashMap<>();
        value.put(recipe.getUniqueKey(), recipe);
        return db.child(RECIPES).updateChildren(value);
    }

    /**
     * Removes a recipe from the database.
     * @param key the unique identifying key of the recipe
     */
    public void remove(String key) throws ExecutionException, InterruptedException {
        Tasks.await(removeAsync(key));
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
    public List<Recipe> getByName(String name) throws ExecutionException, InterruptedException {
        return Tasks.await(getByNameAsync(name));
    }

    /**
     * Retrieves asynchronously recipes with a given name (not the unique id).
     * Since the name is not unique, this method may return multiple recipes.
     * @param name the name of the recipe to search for
     * @return a list of recipes with a matching name embedded in a Task
     */
    public Task<List<Recipe>> getByNameAsync(String name) {
        return getByStringAttributeAsync(RECIPE_NAME, name);
    }

    /**
     * Retrieve a recipe with a given username.
     * Since the name is not unique, this method may return multiple recipes.
     * @param userName the name of the user who uploaded the recipe
     * @return a map from the recipe's unique key id to the recipe itself
     */
    public List<Recipe> getByUserName(String userName) throws ExecutionException, InterruptedException {
        return Tasks.await(getByUserNameAsync(userName));
    }

    /**
     * Retrieve asynchronously a recipe with a given username.
     * Since the name is not unique, this method may return multiple recipes.
     * @param userName the name of the user who uploaded the recipe
     * @return a list of recipes with a matching name embedded in a Task
     */
    public Task<List<Recipe>> getByUserNameAsync(String userName) {
        return getByStringAttributeAsync(USERNAME, userName);
    }

    /**
     * Retrieve recipes with lower prep time.
     * @param time the upper bound limit to the prep time.
     * @return recipes with lower prep time than the time specified as parameter.
     */
    public List<Recipe> getByUpperLimitOnPrepTime(int time) throws ExecutionException, InterruptedException {
        return Tasks.await(getByUpperLimitOnPrepTimeAsync(time));
    }

    /**
     * Retrieve asynchronously recipes with lower prep time.
     * @param time the upper bound limit to the prep time.
     * @return recipes with lower prep time than the time specified as parameter.
     */
    public Task<List<Recipe>> getByUpperLimitOnPrepTimeAsync(int time) {
        return getByUpperLimitOnTimeAsync(PREP_TIME, time);
    }

    /**
     * Retrieve recipes with lower cook time.
     * @param time the upper bound limit to the prep time.
     * @return recipes with lower prep time than the time specified as parameter.
     */
    public List<Recipe> getByUpperLimitOnCookTime(int time) throws ExecutionException, InterruptedException {
        return Tasks.await(getByUpperLimitOnCookTimeAsync(time));
    }

    /**
     * Retrieve asynchronously recipes with lower cook time.
     * @param time the upper bound limit to the cook time.
     * @return recipes with lower cook time than the time specified as parameter.
     */
    public Task<List<Recipe>> getByUpperLimitOnCookTimeAsync(int time) {
        return getByUpperLimitOnTimeAsync(COOK_TIME, time);
    }

    /**
     * Retrieve recipes with higher prep time.
     * @param time the lower bound limit to the prep time.
     * @return recipes with higher prep time than the time specified as parameter.
     */
    public List<Recipe> getByLowerLimitOnPrepTime(int time) throws ExecutionException, InterruptedException {
        return Tasks.await(getByLowerLimitOnPrepTimeAsync(time));
    }

    /**
     * Retrieve asynchronously recipes with higher prep time.
     * @param time the lower bound limit to the prep time.
     * @return recipes with higher prep time than the time specified as parameter.
     */
    public Task<List<Recipe>> getByLowerLimitOnPrepTimeAsync(int time) {
        return getByLowerLimitOnTimeAsync(PREP_TIME, time);
    }

    /**
     * Retrieve recipes with higher prep time.
     * @param time the upper bound limit to the cook time.
     * @return recipes with lower prep time than the time specified as parameter.
     */
    public List<Recipe> getByLowerLimitOnCookTime(int time) throws ExecutionException, InterruptedException {
        return Tasks.await(getByLowerLimitOnCookTimeAsync(time));
    }

    /**
     * Retrieve asynchronously recipes with higher cook time.
     * @param time the lower bound limit to the cook time.
     * @return recipes with higher cook time than the time specified as parameter.
     */
    public Task<List<Recipe>> getByLowerLimitOnCookTimeAsync(int time) {
        return getByLowerLimitOnTimeAsync(COOK_TIME, time);
    }

    /**
     * Retrieve the top n recipes with the maximum number of likes in the database
     * i.e. the most popular recipes.
     * @param n number of top liked recipes to retrieve from the database.
     * @return the top n recipes.
     */
    public List<Recipe> getByMaxLikes(int n) throws ExecutionException, InterruptedException {
        return Tasks.await(getByMaxLikesAsync(n));
    }

    /**
     * Retrieve asynchronously the top n recipes with the maximum number of likes in the database
     * i.e. the most popular recipes.
     * @param n number of top liked recipes to retrieve from the database.
     * @return the top n recipes.
     */
    public Task<List<Recipe>> getByMaxLikesAsync(int n) {
        return getByValueAttributeAsync(LIKES, n);
    }

    /**
     * Retrieve the top n recipes with the maximum number of ratings in the database
     * @param n number of top liked recipes to retrieve from the database
     * @return the top n recipes
     */
    public List<Recipe> getByNumRatings(int n) throws ExecutionException, InterruptedException {
        return Tasks.await(getByNumRatingsAsync(n));
    }

    /**
     * Retrieve the top n recipes with the maximum number of ratings in the database
     * @param n number of top liked recipes to retrieve from the database
     * @return the top n recipes
     */
    public Task<List<Recipe>> getByNumRatingsAsync(int n) {
        return getByValueAttributeAsync(NUM_RATINGS, n);
    }

    private Task<List<Recipe>> getByValueAttributeAsync(String attribute, int n) {
        if (n < 1) {
            throw new IllegalArgumentException();
        }
        Query query = db.child(RECIPES).orderByChild(attribute).limitToLast(n);
        return getValueOfQuery(query);
    }

    private Task<List<Recipe>> getByUpperLimitOnTimeAsync(String attribute, int time) {
        Query query = db.child(RECIPES).orderByChild(attribute).endAt(time);
        return getValueOfQuery(query);
    }

    private Task<List<Recipe>> getByLowerLimitOnTimeAsync(String attribute, int time) {
        Query query = db.child(RECIPES).orderByChild(attribute).startAt(time);
        return getValueOfQuery(query);
    }

    private Task<List<Recipe>> getByStringAttributeAsync(String attribute, String name) {
        Query query = db.child(RECIPES).orderByChild(attribute).equalTo(name);
        return getValueOfQuery(query);
    }

    private Task<List<Recipe>> getValueOfQuery(Query query) {
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
        if (FirebaseInstanceManager.getAuth().getCurrentUser() == null) {
            return Tasks.forException(new FirebaseNoSignedInUserException("Sign in to add favorites"));
        }
        String userId = FirebaseInstanceManager.getAuth().getCurrentUser().getUid();
        Task<DataSnapshot> existing = db.child(FAVORITES + "/" + userId + "/" + recipeId).get();
        db.child(RECIPES + "/" + recipeId).keepSynced(true);
        return existing.continueWithTask(t -> {
            if (t.getResult().getValue() != null) {
                return Tasks.forResult(null);
            }
            return db.child(FAVORITES + "/" + userId).updateChildren(Collections.singletonMap(recipeId, System.currentTimeMillis()));
        });
    }

    public Task<Void> removeFavorite(String recipeId) {
        if (FirebaseInstanceManager.getAuth().getCurrentUser() == null) {
            return Tasks.forException(new FirebaseNoSignedInUserException("Sign in to remove favorites"));
        }
        String userId = FirebaseInstanceManager.getAuth().getCurrentUser().getUid();
        db.child(RECIPES + "/" + recipeId).keepSynced(false);
        return db.child(FAVORITES + "/" + userId + "/" + recipeId).removeValue();
    }

    public Task<List<String>> getFavorites() {
        if (FirebaseInstanceManager.getAuth().getCurrentUser() == null) {
            return Tasks.forException(new FirebaseNoSignedInUserException("Sign in to get favorites"));
        }
        String userId = FirebaseInstanceManager.getAuth().getCurrentUser().getUid();
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

    public Task<List<Recipe>> getFavoriteRecipes() {
        if (FirebaseInstanceManager.getAuth().getCurrentUser() == null) {
            return Tasks.forException(new FirebaseNoSignedInUserException("Sign in to get favorites"));
        }
        return getFavorites().continueWithTask(favorites -> {
            List<Task<Recipe>> favListTasks = favorites.getResult().stream()
                    .map(this::getAsync).collect(Collectors.toList());
            return Tasks.whenAll(favListTasks)
                    .continueWith(task -> favListTasks.stream()
                            .map(Task::getResult).collect(Collectors.toList()));
        });
    }

    public void syncFavorites() {
        getFavorites().addOnSuccessListener(favorites ->
                favorites.forEach(fav ->
                        db.child(RECIPES + "/" + fav).keepSynced(true)));
    }

    public Task<Void> updateFridge(List<Ingredient> ing) {
        FirebaseUser user = FirebaseInstanceManager.getAuth().getCurrentUser();
        if (user == null) {
            return Tasks.forException(new UserNotAuthenticatedException("User needs to be authenticated to access his fridge"));
        }

        return db.child(FRIDGE + "/" + user.getUid()).setValue(ing);
    }

    public Task<List<Ingredient>> getFridge() {
        FirebaseUser user = FirebaseInstanceManager.getAuth().getCurrentUser();
        if (user == null) {
            return Tasks.forException(new UserNotAuthenticatedException("User needs to be authenticated to access his fridge"));
        }

        return db.child(FRIDGE + "/" + user.getUid()).get()
                .continueWith(dataTask -> {
                    List<Ingredient> list = new ArrayList<>();
                    for (DataSnapshot ds : dataTask.getResult().getChildren()) {
                        list.add(ds.getValue(Ingredient.class));
                    }

                    return list;
                });
    }
}

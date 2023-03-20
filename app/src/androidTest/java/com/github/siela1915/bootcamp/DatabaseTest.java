package com.github.siela1915.bootcamp;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;

import android.util.Pair;

import com.github.siela1915.bootcamp.Recipes.Ingredient;
import com.github.siela1915.bootcamp.Recipes.Recipe;
import com.github.siela1915.bootcamp.Recipes.Unit;
import com.github.siela1915.bootcamp.Recipes.Utensils;
import com.github.siela1915.bootcamp.firebase.Database;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ExecutionException;

public class DatabaseTest {
    @Before
    public void setupEmulatorUser() {
        FirebaseAuth.getInstance().useEmulator("10.0.2.2", 9099);
        FirebaseDatabase.getInstance().useEmulator("10.0.2.2", 9000);
    }

    private FirebaseDatabase firebaseInstance;

    @Before
    public void connectToEmulator() {
        firebaseInstance = FirebaseDatabase.getInstance();
        firebaseInstance.useEmulator("10.0.2.2",9000);
    }

    @After
    public void clearDatabase() {
        firebaseInstance.getReference().setValue(null);
    }


    @Test
    public void setAfterGetReturnsRecipe() {
        Database db = new Database(firebaseInstance);

        Recipe recipe = createRecipeEggs();

        String key = db.set(recipe);
        Recipe retrieval;
        try {
            retrieval = db.get(key);
        } catch (ExecutionException | InterruptedException e) {
            System.out.println(e.getMessage());
            throw new RuntimeException(e);
        }
        //System.out.println(recipe);
        assertEquals(recipe, retrieval);
    }

    @Test
    public void getFailsWithExceptionOnBogusKey() {
        Database db = new Database(firebaseInstance);
        Recipe recipe = createRecipeEggs(); //add at least one recipe to database
        db.set(recipe);
        Throwable throwable = assertThrows(Exception.class, () -> db.get("bogus"));
        System.out.println(Objects.requireNonNull(throwable.getCause()).getMessage());
    }

    /*
        This test might fail because removal might not finish before retrieval.
        Not too sure how to fix this issue without changing the signature of the remove method
        to return a Future of some kind. 
     */
    @Test
    public void removeMakesRecipeNotRetrievable() {
        Database db = new Database(firebaseInstance);

        Recipe recipe = createRecipeEggs();

        String key = db.set(recipe);
        db.remove(key);
        //Not clear which exception firebase returns when key is absent
        Throwable throwable = assertThrows(Exception.class, () -> db.get(key));
        System.out.println(Objects.requireNonNull(throwable.getCause()).getMessage());

    }

    @Test
    public void searchByNameReturnsSingleRecipe() {
        Database db = new Database(firebaseInstance);
        Recipe recipe = createRecipeEggs();
        String key = db.set(recipe);
        Map<String, Recipe> map;
        try {
            map = db.getByName("testRecipe");
        } catch (ExecutionException | InterruptedException e) {
            System.out.println(e.getMessage());
            throw new RuntimeException(e);
        }
        assertTrue(map.containsKey(key));
        assertEquals(recipe, map.get(key));
    }

    @Test
    public void searchByNameReturnsMultipleRecipes() {
        Database db = new Database(firebaseInstance);
        Recipe recipe1 = createRecipeEggs();
        Recipe recipe2 = createOtherEggsRecipe();
        String key1 = db.set(recipe1);
        String key2 = db.set(recipe2);
        Map<String, Recipe> map;
        try {
            map = db.getByName("testRecipe");
        } catch (ExecutionException | InterruptedException e) {
            System.out.println(e.getMessage());
            throw new RuntimeException(e);
        }
        assertTrue(map.containsKey(key1));
        assertTrue(map.containsKey(key2));
        assertEquals(2, map.size());
        assertEquals(recipe1, map.get(key1));
        assertEquals(recipe2, map.get(key2));

    }

    @Test
    public void searchByNameFailsWhenNameNotFound() {
        //Fill database with at least one recipe
        Database db = new Database(firebaseInstance);
        Recipe recipe = createRecipeEggs();
        db.set(recipe);
        Throwable throwable = assertThrows(Exception.class, () -> db.getByName("bogusName"));
        System.out.println(Objects.requireNonNull(throwable.getCause()).getMessage());
    }

    private Recipe createRecipeEggs() {
        List<String> utensils = new ArrayList<>();
        utensils.add("spoon");
        utensils.add("fork");
        utensils.add("knife");
        List<Pair<Unit, Ingredient>> ingredients = new ArrayList<>();
        ingredients.add(new Pair<>(new Unit(4, "info"), Ingredient.EGGS));
        List<String> steps = new ArrayList<>();
        steps.add("Just mash them up!");
        List<String> comments = new ArrayList<>();
        comments.add("mmm just love it!");
        comments.add("Steps unclear, bad recipe");
        return new Recipe(98, "testRecipe", "randomUser1", 86, 4.5,
                10, 5, 4, new Utensils(utensils), ingredients, steps, comments);
    }

    private Recipe createOtherEggsRecipe() {
        List<String> utensils = new ArrayList<>();
        utensils.add("spoon");
        utensils.add("fork");
        utensils.add("knife");
        List<Pair<Unit, Ingredient>> ingredients = new ArrayList<>();
        ingredients.add(new Pair<>(new Unit(4, "info"), Ingredient.EGGS));
        ingredients.add(new Pair<>(new Unit(4, "info"), Ingredient.PEPPER));
        ingredients.add(new Pair<>(new Unit(4, "info"), Ingredient.SALT));
        List<String> steps = new ArrayList<>();
        steps.add("Crack the eggs open in a frying pan.");
        steps.add("Stir while eggs cook.");
        steps.add("Season with some salt and pepper.");
        List<String> comments = new ArrayList<>();
        comments.add("mmm just love it!");
        comments.add("Steps are clear! Much better than that other recipe I checked out.");
        return new Recipe(97, "testRecipe", "randomUser2", 85, 4.5,
                10, 5, 4, new Utensils(utensils), ingredients, steps, comments);
    }





    @Test
    public void addAndGetSingleFavoriteRecipeTest() {
        FirebaseAuthActivityTest.loginSync("addAndGetSingle@example.com");

        Database db = new Database(firebaseInstance);
        Task<Void> addTask = db.addFavorite("testRecipe1");
        Task<List<String>> resultTask = addTask.continueWithTask(t -> db.getFavorites());
        try {
            List<String> favorites = Tasks.await(resultTask);
            assertThat(favorites, contains("testRecipe1"));
        } catch (ExecutionException | InterruptedException e) {
            throw new RuntimeException(e);
        }

        FirebaseAuthActivityTest.logoutSync();
    }

    @Test
    public void addAndGetMultipleFavoriteRecipesTest() {
        FirebaseAuthActivityTest.loginSync("addAndGetMultiple@example.com");

        Database db = new Database(firebaseInstance);
        Task<Void> addTask = db.addFavorite("testRecipe1");
        addTask = addTask.continueWithTask(t -> db.addFavorite("testRecipe2"));
        addTask = addTask.continueWithTask(t -> db.addFavorite("testRecipe3"));
        addTask = addTask.continueWithTask(t -> db.addFavorite("testRecipe4"));
        Task<List<String>> resultTask = addTask.continueWithTask(t -> db.getFavorites());
        try {
            List<String> favorites = Tasks.await(resultTask);
            assertThat(favorites, contains("testRecipe1", "testRecipe2", "testRecipe3", "testRecipe4"));
        } catch (ExecutionException | InterruptedException e) {
            throw new RuntimeException(e);
        }

        FirebaseAuthActivityTest.logoutSync();
    }

    @Test
    public void addMultipleFavoriteRecipesAndRemoveOneTest() {
        FirebaseAuthActivityTest.loginSync("addMultipleAndRemove@example.com");

        Database db = new Database(firebaseInstance);
        Task<Void> addTask = db.addFavorite("testRecipe1");
        addTask = addTask.continueWithTask(t -> db.addFavorite("testRecipe2"));
        addTask = addTask.continueWithTask(t -> db.addFavorite("testRecipe3"));
        addTask = addTask.continueWithTask(t -> db.addFavorite("testRecipe4"));
        addTask = addTask.continueWithTask(t -> db.removeFavorite("testRecipe3"));
        Task<List<String>> resultTask = addTask.continueWithTask(t -> db.getFavorites());
        try {
            List<String> favorites = Tasks.await(resultTask);
            assertThat(favorites, contains("testRecipe1", "testRecipe2", "testRecipe4"));
        } catch (ExecutionException | InterruptedException e) {
            throw new RuntimeException(e);
        }

        FirebaseAuthActivityTest.logoutSync();
    }

    @Test
    public void addingSameFavoriteRecipeKeepsOrderTest() {
        FirebaseAuthActivityTest.loginSync("addingSameFavoriteKeepsOrder@example.com");

        Database db = new Database(firebaseInstance);
        Task<Void> addTask = db.addFavorite("testRecipe1");
        addTask = addTask.continueWithTask(t -> db.addFavorite("testRecipe2"));
        addTask = addTask.continueWithTask(t -> db.addFavorite("testRecipe3"));
        addTask = addTask.continueWithTask(t -> db.addFavorite("testRecipe4"));
        addTask = addTask.continueWithTask(t -> db.addFavorite("testRecipe2"));
        Task<List<String>> resultTask = addTask.continueWithTask(t -> db.getFavorites());
        try {
            List<String> favorites = Tasks.await(resultTask);
            assertThat(favorites, contains("testRecipe1", "testRecipe2", "testRecipe3", "testRecipe4"));
        } catch (ExecutionException | InterruptedException e) {
            throw new RuntimeException(e);
        }

        FirebaseAuthActivityTest.logoutSync();
    }
}

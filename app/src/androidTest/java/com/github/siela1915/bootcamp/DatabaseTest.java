package com.github.siela1915.bootcamp;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.junit.Assert.assertNotEquals;

import com.github.siela1915.bootcamp.firebase.Database;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import org.junit.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

public class DatabaseTest {

    @Test
    public void setAfterGetReturnsRecipe() {
        Database db = new Database();
        Map<String, Object> map = new HashMap<>();
        map.put("Ingredients", "egg, bacon, toast");
        map.put("rating", "great");
        map.put("servings", "one hungry person");
        String key = db.set(map);
        String recipe = db.get(key);
        System.out.println(recipe);
        assertNotEquals(recipe, null);
        db.remove(key);
    }

    @Test
    public void searchByNameReturnsSingleRecipe() {
        Database db = new Database();
        Map<String, Object> map = new HashMap<>();
        map.put("name", "testRecipe");
        map.put("Ingredients", "egg, bacon, toast");
        map.put("rating", "great");
        map.put("servings", "one hungry person");
        String key = db.set(map);
        String recipe = db.getByName("testRecipe");
        System.out.println(recipe);
        assertNotEquals(recipe, null);
        db.remove(key);
    }


    @Test
    public void addAndGetSingleFavoriteRecipeTest() {
        FirebaseAuth.getInstance().useEmulator("10.0.2.2", 9099);
        FirebaseDatabase.getInstance().useEmulator("10.0.2.2", 9000);
        FirebaseAuthActivityTest.loginSync("addAndGetSingle@example.com");

        Database db = new Database();
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
        FirebaseAuth.getInstance().useEmulator("10.0.2.2", 9099);
        FirebaseDatabase.getInstance().useEmulator("10.0.2.2", 9000);
        FirebaseAuthActivityTest.loginSync("addAndGetMultiple@example.com");

        Database db = new Database();
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
        FirebaseAuth.getInstance().useEmulator("10.0.2.2", 9099);
        FirebaseDatabase.getInstance().useEmulator("10.0.2.2", 9000);
        FirebaseAuthActivityTest.loginSync("addMultipleAndRemove@example.com");

        Database db = new Database();
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
        FirebaseAuth.getInstance().useEmulator("10.0.2.2", 9099);
        FirebaseDatabase.getInstance().useEmulator("10.0.2.2", 9000);
        FirebaseAuthActivityTest.loginSync("addingSameFavoriteKeepsOrder@example.com");

        Database db = new Database();
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

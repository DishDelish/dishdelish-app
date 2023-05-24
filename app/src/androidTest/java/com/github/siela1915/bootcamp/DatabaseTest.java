package com.github.siela1915.bootcamp;

import static androidx.test.core.app.ApplicationProvider.getApplicationContext;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;

import com.github.siela1915.bootcamp.Recipes.Comment;
import com.github.siela1915.bootcamp.Recipes.Ingredient;
import com.github.siela1915.bootcamp.Recipes.Recipe;
import com.github.siela1915.bootcamp.Recipes.Unit;
import com.github.siela1915.bootcamp.Recipes.Utensils;
import com.github.siela1915.bootcamp.firebase.Database;
import com.github.siela1915.bootcamp.firebase.FirebaseInstanceManager;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseException;
import com.google.firebase.database.FirebaseDatabase;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class DatabaseTest {
    private FirebaseDatabase firebaseInstance;

    @Before
    public void connectToEmulator() {
        FirebaseInstanceManager.emulator = true;
        firebaseInstance = FirebaseInstanceManager.getDatabase();
    }

    @After
    public void clearDatabase() {
        if (firebaseInstance != null) {
            try {
                for (DataSnapshot recipe : Tasks.await(firebaseInstance.getReference("recipes").orderByChild("recipeName")
                        .startAt("testRecipe").endAt("testRecipeNew").get()).getChildren()) {
                    Tasks.await(recipe.getRef().removeValue());
                }
            } catch (ExecutionException | InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }


    @Test
    public void setAfterGetReturnsRecipe() {
        Database db = new Database(firebaseInstance);
        Recipe recipe = createRecipeEggs();
        try {
            String key = db.set(recipe);
            Recipe retrieval = db.get(key);
            assertEquals(recipe, retrieval);
        } catch (ExecutionException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void setAfterGetAsyncReturnsRecipe() {
        Database db = new Database(firebaseInstance);
        Recipe recipe = createRecipeEggs();
        Task<String> setTask = db.setAsync(recipe);
        Task<Recipe> getTask = setTask.continueWithTask(t -> db.getAsync(t.getResult()));
        try {
            Recipe r = Tasks.await(getTask);
            assertEquals(recipe, r);
        } catch (ExecutionException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void getFailsOnBogusKey() {
        Database db = new Database(firebaseInstance);
        Recipe recipe = createRecipeEggs(); //add at least one recipe to database
        try {
            db.set(recipe);
            Recipe bogus = db.get("bogus");
            assertNull(bogus);
        } catch (ExecutionException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void getAsyncFailsOnBogusKey() {
        Database db = new Database(firebaseInstance);
        Recipe recipe = createRecipeEggs();
        Task<String> setTask = db.setAsync(recipe);
        Task<Recipe> getTask = setTask.continueWithTask(t -> db.getAsync("bogus"));
        try {
            Recipe r = Tasks.await(getTask);
            assertNull(r);
        } catch (ExecutionException | InterruptedException e) {
            throw new RuntimeException(e);
        }
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
        try {
            String key = db.set(recipe);
            db.remove(key);
            //Not clear which exception firebase returns when key is absent
            Recipe nonExistant = db.get(key);
            assertNull(nonExistant);
        } catch (ExecutionException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void removeAsyncMakesRecipeNotRetrievable() {
        Database db = new Database(firebaseInstance);
        Recipe recipe = createRecipeEggs();
        Task<String> setTask = db.setAsync(recipe);
        Task<Void> remove = setTask.continueWithTask(t -> db.removeAsync(recipe.uniqueKey));
        Task<Recipe> getTask = remove.continueWithTask(t -> db.getAsync(recipe.uniqueKey));
        try {
            Recipe r = Tasks.await(getTask);
            assertNull(r);
        } catch (ExecutionException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void searchByNameReturnsSingleRecipe() {
        Database db = new Database(firebaseInstance);
        Recipe recipe = createRecipeEggs();
        try {
            db.set(recipe);
            List<Recipe> ls = db.getByName("testRecipe");
            assertEquals(ls.size(), 1);
            assertEquals(recipe, ls.get(0));
        } catch (ExecutionException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void searchByNameAsyncReturnsList() {
        Database db = new Database(firebaseInstance);
        Recipe recipe1 = createRecipeEggs();
        Recipe recipe2 = createOtherEggsRecipe();
        Task<String> set1 = db.setAsync(recipe1);
        Task<String> set2 = set1.continueWithTask(t -> db.setAsync(recipe2));
        Task<List<Recipe>> listTask = set2.continueWithTask(t -> db.getByNameAsync("testRecipe"));
        try {
            List<Recipe> ls = Tasks.await(listTask);
            assertEquals(ls.size(), 2);
            assertTrue(ls.contains(recipe1));
            assertTrue(ls.contains(recipe2));
        } catch (ExecutionException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void searchByNameReturnsMultipleRecipes() {
        Database db = new Database(firebaseInstance);
        Recipe recipe1 = createRecipeEggs();
        Recipe recipe2 = createOtherEggsRecipe();
        try {
            db.set(recipe1);
            db.set(recipe2);
            List<Recipe> ls = db.getByName("testRecipe");
            assertTrue(ls.contains(recipe1));
            assertTrue(ls.contains(recipe2));
            assertEquals(2, ls.size());
        } catch (ExecutionException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void searchByNameFailsWhenNameNotFound() {
        //Fill database with at least one recipe
        Database db = new Database(firebaseInstance);
        Recipe recipe = createRecipeEggs();
        try {
            db.set(recipe);
            List<Recipe> bogus = db.getByName("bogusName");
            assertEquals(bogus.size(), 0);
        } catch (ExecutionException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void searchByUserNameReturnsSingleRecipe() {
        Database db = new Database(firebaseInstance);
        Recipe recipe = createRecipeEggs();
        try {
            db.set(recipe);
            List<Recipe> ls = db.getByUserName("randomUser1");
            assertTrue(ls.contains(recipe));
        } catch (ExecutionException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void searchByUserNameReturnsMultipleRecipes() {
        Database db = new Database(firebaseInstance);
        Recipe recipe1 = createRecipeEggs();
        Recipe recipe2 = createOtherEggsRecipe();
        Recipe recipe3 = createRecipeEggs();
        try {
            db.set(recipe1);
            db.set(recipe2);
            db.set(recipe3);
            List<Recipe> ls = db.getByUserName("randomUser1");
            assertEquals(2, ls.size());
            assertTrue(ls.contains(recipe1));
            assertTrue(ls.contains(recipe3));
        } catch (ExecutionException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void searchByUserNameAsyncReturnsList() {
        Database db = new Database(firebaseInstance);
        Recipe recipe1 = createRecipeEggs();
        Recipe recipe2 = createOtherEggsRecipe();
        Recipe recipe3 = createRecipeEggs();
        Task<String> set1 = db.setAsync(recipe1);
        Task<String> set2 = set1.continueWithTask(t -> db.setAsync(recipe2));
        Task<String> set3 = set2.continueWithTask(t -> db.setAsync(recipe3));
        Task<List<Recipe>> listTask = set3.continueWithTask(t -> db.getByUserNameAsync("randomUser1"));
        try {
            List<Recipe> ls = Tasks.await(listTask);
            assertEquals(2, ls.size());
            assertTrue(ls.contains(recipe1));
            assertTrue(ls.contains(recipe3));
        } catch (ExecutionException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void searchByNameAsyncReturnsEmptyListWhenNameNotFound() {
        Database db = new Database(firebaseInstance);
        Recipe recipe1 = createRecipeEggs();
        Task<String> set1 = db.setAsync(recipe1);
        Task<List<Recipe>> listTask = set1.continueWithTask(t -> db.getByNameAsync("bogus"));
        try {
            List<Recipe> ls = Tasks.await(listTask);
            assertEquals(ls.size(), 0);
        } catch (ExecutionException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void searchByUserNameReturnsEmptyListWhenNameNotFound() {
        //Fill database with at least one recipe
        Database db = new Database(firebaseInstance);
        Recipe recipe = createRecipeEggs();
        try {
            db.set(recipe);
            List<Recipe> bogus = db.getByUserName("bogusName");
            assertEquals(bogus.size(), 0);
        } catch (ExecutionException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void getByUpperLimitReturnsNoValuesAboveLimit() {
        Database db = new Database(firebaseInstance);
        try {
            List<Recipe> cook = db.getByUpperLimitOnCookTime(20);
            List<Recipe> prep = db.getByUpperLimitOnPrepTime(20);
            assertEquals(8, cook.size());
            assertEquals(10, prep.size());
            boolean check1 = true;
            for (Recipe r : cook) {
                if (r.getCookTime() > 20) {
                    check1 = false;
                    break;
                }
            }
            assertTrue(check1);
            boolean check2 = true;
            for (Recipe r : prep) {
                if (r.getPrepTime() > 20) {
                    check2 = false;
                    break;
                }
            }
            assertTrue(check2);
        } catch (ExecutionException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void getByUpperLimitAsyncReturnsNoValuesAboveLimit() {
        Database db = new Database(firebaseInstance);
        try {
            List<Recipe> cook = Tasks.await(db.getByUpperLimitOnCookTimeAsync(20));
            List<Recipe> prep = Tasks.await(db.getByUpperLimitOnPrepTimeAsync(20));
            assertEquals(8, cook.size());
            assertEquals(10, prep.size());
            boolean check1 = true;
            for (Recipe r : cook) {
                if (r.getCookTime() > 20) {
                    check1 = false;
                    break;
                }
            }
            assertTrue(check1);
            boolean check2 = true;
            for (Recipe r : prep) {
                if (r.getPrepTime() > 20) {
                    check2 = false;
                    break;
                }
            }
            assertTrue(check2);
        } catch (ExecutionException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void getByLowerLimitReturnsNoValuesUnderLimit() {
        Database db = new Database(firebaseInstance);
        try {
            List<Recipe> cook = db.getByLowerLimitOnCookTime(20);
            List<Recipe> prep = db.getByLowerLimitOnPrepTime(20);
            assertEquals(8, cook.size());
            assertEquals(8, prep.size());
            boolean check1 = true;
            for (Recipe r : cook) {
                if (r.getCookTime() < 20) {
                    check1 = false;
                    break;
                }
            }
            assertTrue(check1);
            boolean check2 = true;
            for (Recipe r : prep) {
                if (r.getPrepTime() < 20) {
                    check2 = false;
                    break;
                }
            }
            assertTrue(check2);
        } catch (ExecutionException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void getByLowerLimitAsyncReturnsNoValuesUnderLimit() {
        Database db = new Database(firebaseInstance);
        try {
            List<Recipe> cook = Tasks.await(db.getByLowerLimitOnCookTimeAsync(20));
            List<Recipe> prep = Tasks.await(db.getByLowerLimitOnPrepTimeAsync(20));
            assertEquals(8, cook.size());
            assertEquals(8, prep.size());
            boolean check1 = true;
            for (Recipe r : cook) {
                if (r.getCookTime() < 20) {
                    check1 = false;
                    break;
                }
            }
            assertTrue(check1);
            boolean check2 = true;
            for (Recipe r : prep) {
                if (r.getPrepTime() < 20) {
                    check2 = false;
                    break;
                }
            }
            assertTrue(check2);
        } catch (ExecutionException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void getByMaxLikesReturnsNoSmallerValue() {
        Database db = new Database(firebaseInstance);
        List<Recipe> recipes = createRecipesDifferentIntegers();
        try {
            for (Recipe r : recipes) {
                db.set(r);
            }
            List<Recipe> ls = db.getByMaxLikes(4);
            assertEquals(4, ls.size());
            boolean check = true;
            for (Recipe r : ls) {
                if (r.getLikes() < 30) {
                    check = false;
                    break;
                }
            }
            assertTrue(check);
        } catch (ExecutionException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void getByMaxLikesAsyncReturnsNoSmallerValue() {
        Database db = new Database(firebaseInstance);
        List<Recipe> recipes = createRecipesDifferentIntegers();
        try {
            for (Recipe r : recipes) {
                db.set(r);
            }
            List<Recipe> ls = Tasks.await(db.getByMaxLikesAsync(4));
            assertEquals(4, ls.size());
            boolean check = true;
            for (Recipe r : ls) {
                if (r.getLikes() < 30) {
                    check = false;
                    break;
                }
            }
            assertTrue(check);
        } catch (ExecutionException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void getByNumRatingsReturnsNoSmallerValue() {
        Database db = new Database(firebaseInstance);
        List<Recipe> recipes = createRecipesDifferentIntegers();
        try {
            for (Recipe r : recipes) {
                db.set(r);
            }
            List<Recipe> ls = db.getByNumRatings(4);
            assertEquals(4, ls.size());
            boolean check = true;
            for (Recipe r : ls) {
                if (r.getLikes() < 30) {
                    check = false;
                    break;
                }
            }
            assertTrue(check);
        } catch (ExecutionException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void getByNumRatingsAsyncReturnsNoSmallerValue() {
        Database db = new Database(firebaseInstance);
        List<Recipe> recipes = createRecipesDifferentIntegers();
        try {
            for (Recipe r : recipes) {
                db.set(r);
            }
            List<Recipe> ls = Tasks.await(db.getByNumRatingsAsync(4));
            assertEquals(4, ls.size());
            boolean check = true;
            for (Recipe r : ls) {
                if (r.getLikes() < 30) {
                    check = false;
                    break;
                }
            }
            assertTrue(check);
        } catch (ExecutionException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void getNRandomReturnsNRecipes() {
        Database db = new Database(firebaseInstance);
        List<Recipe> recipes = createRecipesDifferentIntegers();
        try {
            for (Recipe r : recipes) {
                db.set(r);
            }
            List<Recipe> ls = db.getNRandom(4);
            assertEquals(4, ls.size());
        } catch (ExecutionException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void getNRandomThrowsExceptionOnNegativeArgument() {
        Database db = new Database(firebaseInstance);
        List<Recipe> recipes = createRecipesDifferentIntegers();
        try {
            for (Recipe r : recipes) {
                db.set(r);
            }
            assertThrows(IllegalArgumentException.class, () -> db.getNRandom(-1));
        } catch (ExecutionException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void getNRandomArgumentCanOverflow() {
        Database db = new Database(firebaseInstance);
        try {
            List<Recipe> ls = db.getNRandom(20);
            assertEquals(12, ls.size());
        } catch (ExecutionException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void updateChangesRecipeValue() {
        Database db = new Database(firebaseInstance);
        Recipe recipe = createRecipeEggs();
        try {
            db.set(recipe);
            recipe.setRecipeName("testRecipeNew");
            db.update(recipe);
            Recipe r = db.get(recipe.getUniqueKey());
            assertEquals("testRecipeNew", r.getRecipeName());
        } catch (ExecutionException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void updateOnNonExistingRecipeAddsRecipeToDatabase() {
        Database db = new Database(firebaseInstance);
        Recipe recipe = createRecipeEggs();
        recipe.setUniqueKey("randomKey");
        try {
            db.update(recipe);
            Recipe r = db.get(recipe.getUniqueKey());
            assertEquals(recipe, r);
        } catch (ExecutionException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void updateOnNonExistingRecipeWithEmptyStringKeyThrowsException() {
        Database db = new Database(firebaseInstance);
        Recipe recipe = createRecipeEggs();
        assertThrows(ExecutionException.class, () -> db.update(recipe));
    }

    @Test
    public void updateOnNonExistingRecipeWithNullStringKeyThrowsException() {
        Database db = new Database(firebaseInstance);
        Recipe recipe = createRecipeEggs();
        recipe.setUniqueKey(null);
        assertThrows(DatabaseException.class, () -> db.update(recipe));
    }

    private List<Recipe> createRecipesDifferentIntegers() {
        List<Integer> ls = Arrays.asList(4, 8, 15, 20, 30, 36, 45, 90);
        List<Recipe> recipes = new ArrayList<>();
        for (Integer i : ls) {
            Recipe r = new Recipe();
            r.setCookTime(i);
            r.setPrepTime(i);
            r.setNumRatings(i);
            r.setLikes(i);
            r.setRecipeName("testRecipe");
            recipes.add(r);
        }
        return recipes;
    }

    private Recipe createRecipeEggs() {
        return new Recipe("URL", "testRecipe", "randomUser1", 86, 4.5,
                10, 5, 4, new Utensils(createUtensils()), cuisine, allergy, diet,
                createIngredients().subList(0, 2), createSteps(), createListComments().subList(0, 2), 190);
    }

    private Recipe createOtherEggsRecipe() {
        return new Recipe("URL", "testRecipe", "randomUser2", 85, 4.5,
                10, 5, 4, new Utensils(createUtensils()), cuisine, allergy, diet,
                createIngredients(), createSteps(), createListComments(), 190);
    }

    private List<Comment> createListComments() {
        String[] cs = new String[]{"Love it!", "Nah not me.", "What???"};
        List<Comment> ls = new ArrayList<>();
        for (String s : cs) {
            ls.add(new Comment(s));
        }
        return ls;
    }

    private List<String> createUtensils() {
        String[] us = new String[] {"spoon", "fork", "knife"};
        return Arrays.asList(us);
    }

    private List<Ingredient> createIngredients() {
        List<Ingredient> ingredients = new ArrayList<>();
        ingredients.add(new Ingredient("Eggs", new Unit(4, "some info")));
        ingredients.add(new Ingredient("Pepper", new Unit(4, "some info")));
        ingredients.add(new Ingredient("Salt", new Unit(4, "some info")));
        return ingredients;
    }

    private List<String> createSteps() {
        String[] ss = new String[]{"Crack the eggs open in a frying pan.",
                "Stir while eggs cook.", "Season with some salt and pepper."};
        return Arrays.asList(ss);
    }

    private final List<Integer> cuisine = Arrays.asList(1, 2, 3, 4, 5);
    private final List<Integer> allergy = Arrays.asList(1, 2, 3, 4, 5);
    private final List<Integer> diet = Arrays.asList(1, 2, 3, 4, 5);






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

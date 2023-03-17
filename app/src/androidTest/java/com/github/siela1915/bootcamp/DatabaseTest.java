package com.github.siela1915.bootcamp;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;

import android.util.Pair;

import com.github.siela1915.bootcamp.Recipes.Ingredient;
import com.github.siela1915.bootcamp.Recipes.Recipe;
import com.github.siela1915.bootcamp.Recipes.Unit;
import com.github.siela1915.bootcamp.Recipes.Utensils;
import com.github.siela1915.bootcamp.firebase.Database;
import com.google.firebase.database.FirebaseDatabase;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

public class DatabaseTest {

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
        } catch (ExecutionException e) {
            System.out.println(e.getMessage());
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            System.out.println(e.getMessage());
            throw new RuntimeException(e);
        }
        //System.out.println(recipe);
        assertEquals(recipe, retrieval);
    }

    @Test
    public void removeMakesRecipeNotRetrievable() {
        Database db = new Database(firebaseInstance);

        Recipe recipe = createRecipeEggs();

        String key = db.set(recipe);
        db.remove(key);

        assertThrows(Exception.class, () -> db.get(key));

    }

    /*@Test
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
    }*/

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




}

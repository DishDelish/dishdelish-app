package com.github.siela1915.bootcamp;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

import com.github.siela1915.bootcamp.firebase.Database;

import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

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

}

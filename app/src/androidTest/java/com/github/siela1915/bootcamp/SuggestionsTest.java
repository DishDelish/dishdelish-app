package com.github.siela1915.bootcamp;

import static androidx.test.core.app.ApplicationProvider.getApplicationContext;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import android.app.Activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.github.siela1915.bootcamp.Labelling.AllergyType;
import com.github.siela1915.bootcamp.Labelling.CuisineType;
import com.github.siela1915.bootcamp.Labelling.DietType;
import com.github.siela1915.bootcamp.Recipes.Recipe;
import com.github.siela1915.bootcamp.Tools.SuggestionCalculator;
import com.github.siela1915.bootcamp.firebase.Database;
import com.github.siela1915.bootcamp.firebase.FirebaseInstanceManager;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.FirebaseDatabase;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;

public class SuggestionsTest {

    private FirebaseDatabase firebaseInstance;
    private final int SIZE = 10;

    @Before
    public void useEmulator() {
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
    public void testGetSuggestionsUserNotLoggedIn() {
        Database db = new Database(firebaseInstance);
        try {
            for (Recipe r : favourites()) {
                db.set(r);
            }
            for (Recipe r : popular()) {
                db.set(r);
            }
            List<Recipe> ls = Tasks.await(SuggestionCalculator.getSuggestions(db));
            assertTrue(ls.stream().filter(r -> r.getRecipeName() == "testRecipe").allMatch(r -> r.getLikes() == 1000));
        } catch (ExecutionException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void testGetSuggestionsUserLoggedIn() {
        FirebaseAuthActivityTest.loginSync("testGetSuggestionsUserLoggedIn@example.com");
        Database db = new Database(firebaseInstance);
        try {
            for (Recipe r : favourites()) {
                db.set(r);
            }
            for (Recipe r : popular()) {
                db.set(r);
            }
            List<Recipe> ls = Tasks.await(SuggestionCalculator.getSuggestions(db));
            assertTrue(ls.stream().filter(r -> r.getRecipeName() == "testRecipe" && !r.getDietTypes().isEmpty())
                    .allMatch(r -> r.getDietTypes().contains(DietType.VEGETARIAN.ordinal())));
            assertTrue(ls.stream().filter(r -> r.getRecipeName() == "testRecipe" && !r.getAllergyTypes().isEmpty())
                    .allMatch(r -> r.getAllergyTypes().contains(AllergyType.EGGS.ordinal())));
            assertTrue(ls.stream().filter(r -> r.getRecipeName() == "testRecipe" && !r.getCuisineTypes().isEmpty())
                    .allMatch(r -> r.getCuisineTypes().contains(CuisineType.CHINESE.ordinal())));
        } catch (ExecutionException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private List<Recipe> favourites() {
        List<Recipe> ls = new ArrayList<>();
        for (int i = 0; i < SIZE; ++i) {
            Recipe r = new Recipe();
            r.setRecipeName("testRecipe");
            r.setAllergyTypes(Collections.singletonList(AllergyType.EGGS.ordinal()));
            r.setCuisineTypes(Collections.singletonList(CuisineType.CHINESE.ordinal()));
            r.setDietTypes(Collections.singletonList(DietType.VEGETARIAN.ordinal()));
            ls.add(r);
        }
        return ls;
    }

    private List<Recipe> popular() {
        List<Recipe> ls = new ArrayList<>();
        for (int i = 0; i < SIZE; ++i) {
            Recipe r = new Recipe();
            r.setRecipeName("testRecipe");
            r.setLikes(1000);
            ls.add(r);
        }
        return ls;
    }


}

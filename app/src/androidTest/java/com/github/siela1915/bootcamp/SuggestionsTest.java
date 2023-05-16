package com.github.siela1915.bootcamp;

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
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
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
        firebaseInstance = FirebaseDatabase.getInstance();
        firebaseInstance.useEmulator("10.0.2.2", 9000);
    }

    @After
    public void clearDatabase() {
        if (firebaseInstance != null) {
            firebaseInstance.getReference().setValue(null);
        }
    }


    @Test
    public void testGetSuggestions() {
        Database db = new Database(firebaseInstance);
        try {
            for (Recipe r : favourites()) {
                db.set(r);
            }
            for (Recipe r : popular()) {
                db.set(r);
            }
            List<Recipe> ls = Tasks.await(SuggestionCalculator.getSuggestions(db));
            assertEquals(2*SIZE, ls.size());
            assertTrue(ls.stream().filter(r -> !r.getDietTypes().isEmpty())
                    .allMatch(r -> r.getDietTypes().contains(DietType.VEGETARIAN.ordinal())));
            assertTrue(ls.stream().filter(r -> !r.getAllergyTypes().isEmpty())
                    .allMatch(r -> r.getAllergyTypes().contains(AllergyType.EGGS.ordinal())));
            assertTrue(ls.stream().filter(r -> !r.getCuisineTypes().isEmpty())
                    .allMatch(r -> r.getCuisineTypes().contains(CuisineType.CHINESE.ordinal())));
        } catch (ExecutionException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private List<Recipe> favourites() {
        List<Recipe> ls = new ArrayList<>();
        for (int i = 0; i < SIZE; ++i) {
            Recipe r = new Recipe();
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
            r.setLikes(1000);
            ls.add(r);
        }
        return ls;
    }


}
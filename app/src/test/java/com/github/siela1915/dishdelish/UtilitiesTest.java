package com.github.siela1915.dishdelish;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import com.github.siela1915.bootcamp.Labelling.AllergyType;
import com.github.siela1915.bootcamp.Labelling.CuisineType;
import com.github.siela1915.bootcamp.Labelling.DietType;
import com.github.siela1915.bootcamp.Recipes.Recipe;
import com.github.siela1915.bootcamp.Tools.Utilities;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

public class UtilitiesTest {

    private final int CAPACITY = 10;

    @Test
    public void filteringOnAllergyType() {
        Recipe r = new Recipe();
        r.setAllergyTypes(Collections.singletonList(AllergyType.EGGS.ordinal()));
        List<Recipe> ls = new ArrayList<>(CAPACITY);
        Random random = new Random();
        int check = 0;
        for (int i = 0; i < CAPACITY; ++i) {
            Recipe temp = new Recipe();
            int rand = random.nextInt(AllergyType.values().length);
            if (rand == AllergyType.EGGS.ordinal()) ++check;
            temp.setAllergyTypes(Collections.singletonList(AllergyType.values()[rand].ordinal()));
        }
        List<Recipe> same = Utilities.getSameAllergyTypes(r, ls);
        assertEquals(check, same.size());
        assertTrue(same.stream().allMatch(elem -> elem.getAllergyTypes().contains(AllergyType.EGGS.ordinal())));
    }

    @Test
    public void filteringOnCuisineType() {
        Recipe r = new Recipe();
        r.setCuisineTypes(Collections.singletonList(CuisineType.CHINESE.ordinal()));
        List<Recipe> ls = new ArrayList<>(CAPACITY);
        Random random = new Random();
        int check = 0;
        for (int i = 0; i < CAPACITY; ++i) {
            Recipe temp = new Recipe();
            int rand = random.nextInt(CuisineType.values().length);
            if (rand == CuisineType.CHINESE.ordinal()) ++check;
            temp.setCuisineTypes(Collections.singletonList(CuisineType.values()[rand].ordinal()));
        }
        List<Recipe> same = Utilities.getSameCuisineTypes(r, ls);
        assertEquals(check, same.size());
        assertTrue(same.stream().allMatch(elem -> elem.getCuisineTypes().contains(CuisineType.CHINESE.ordinal())));
    }

    @Test
    public void filteringOnDietType() {
        Recipe r = new Recipe();
        r.setDietTypes(Collections.singletonList(DietType.DAIRY.ordinal()));
        List<Recipe> ls = new ArrayList<>(CAPACITY);
        Random random = new Random();
        int check = 0;
        for (int i = 0; i < CAPACITY; ++i) {
            Recipe temp = new Recipe();
            int rand = random.nextInt(DietType.values().length);
            if (rand == DietType.DAIRY.ordinal()) ++check;
            temp.setDietTypes(Collections.singletonList(DietType.values()[rand].ordinal()));
        }
        List<Recipe> same = Utilities.getSameDietTypes(r, ls);
        assertEquals(check, same.size());
        assertTrue(same.stream().allMatch(elem -> elem.getDietTypes().contains(DietType.DAIRY.ordinal())));
    }

    @Test
    public void dominantAllergy() {
        List<Recipe> ls = new ArrayList<>(CAPACITY * 2);
        for (int i = 0; i < CAPACITY; ++i) {
            Recipe r = new Recipe();
            r.setAllergyTypes(Collections.singletonList(AllergyType.EGGS.ordinal()));
            ls.add(r);
        }
        Random rand = new Random();
        for (int i = 0; i < CAPACITY; ++i) {
            Recipe r = new Recipe();
            r.setAllergyTypes(Collections.singletonList(rand.nextInt(AllergyType.values().length)));
            ls .add(r);
        }
        assertEquals(AllergyType.EGGS, Utilities.getDominantAllergy(ls));
    }

    @Test
    public void dominantDiet() {
        List<Recipe> ls = new ArrayList<>(CAPACITY * 2);
        for (int i = 0; i < CAPACITY; ++i) {
            Recipe r = new Recipe();
            r.setDietTypes(Collections.singletonList(DietType.DAIRY.ordinal()));
            ls.add(r);
        }
        Random rand = new Random();
        for (int i = 0; i < CAPACITY; ++i) {
            Recipe r = new Recipe();
            r.setDietTypes(Collections.singletonList(rand.nextInt(DietType.values().length)));
            ls .add(r);
        }
        assertEquals(DietType.DAIRY, Utilities.getDominantDiet(ls));
    }

    @Test
    public void dominantCuisine() {
        List<Recipe> ls = new ArrayList<>(CAPACITY * 2);
        for (int i = 0; i < CAPACITY; ++i) {
            Recipe r = new Recipe();
            r.setCuisineTypes(Collections.singletonList(CuisineType.CHINESE.ordinal()));
            ls.add(r);
        }
        Random rand = new Random();
        for (int i = 0; i < CAPACITY; ++i) {
            Recipe r = new Recipe();
            r.setCuisineTypes(Collections.singletonList(rand.nextInt(CuisineType.values().length)));
            ls .add(r);
        }
        assertEquals(CuisineType.CHINESE, Utilities.getDominantCuisine(ls));
    }


}

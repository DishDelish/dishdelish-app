package com.github.siela1915.bootcamp;

import static com.github.siela1915.bootcamp.Labelling.AllergyType.EGGS;
import static com.github.siela1915.bootcamp.Recipes.Ingredient.*;

import org.junit.Test;

import static org.junit.Assert.*;

import com.github.siela1915.bootcamp.Labelling.AllergyType;
import com.github.siela1915.bootcamp.Labelling.CuisineType;
import com.github.siela1915.bootcamp.Labelling.DietType;
import com.github.siela1915.bootcamp.Labelling.RecipeFetcher;
import com.github.siela1915.bootcamp.Recipes.ExampleRecipes;
import com.github.siela1915.bootcamp.Recipes.Ingredient;
import com.github.siela1915.bootcamp.Recipes.Recipe;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class RecipeFetcherTest {


    public RecipeFetcher fetcher;


    @Test
    public void fetcherSortsRecipes(){
        List<AllergyType> allergies = Arrays.asList();
        List<CuisineType> cuisines = Arrays.asList(CuisineType.FRENCH);
        List<DietType> diets = Arrays.asList();
        List<Ingredient> favorites = Arrays.asList(LEMON);

        fetcher = new RecipeFetcher(allergies, cuisines, diets, favorites);
        assertEquals(
                Arrays.asList(ExampleRecipes.recipes.get(0).recipeName, ExampleRecipes.recipes.get(2).recipeName, ExampleRecipes.recipes.get(1).recipeName),
                fetcher.fetchRecipeList()
        );

    }

    @Test
    public void fetcherExcludesInappropriateRecipes(){
        List<AllergyType> allergies = Arrays.asList(EGGS);
        List<CuisineType> cuisines = Arrays.asList(CuisineType.FRENCH);
        List<DietType> diets = Arrays.asList();
        List<Ingredient> favorites = Arrays.asList(LEMON);

        fetcher = new RecipeFetcher(allergies, cuisines, diets, favorites);
        assertEquals(
                Arrays.asList(ExampleRecipes.recipes.get(1).recipeName),
                fetcher.fetchRecipeList()
        );

    }

    @Test
    public void emptyPreferencesWork(){
        List<AllergyType> allergies = Arrays.asList();
        List<CuisineType> cuisines = Arrays.asList();
        List<DietType> diets = Arrays.asList();
        List<Ingredient> favorites = Arrays.asList();

        fetcher = new RecipeFetcher(allergies, cuisines, diets, favorites);


        List<String> expectedList = ExampleRecipes.recipes.stream().map(r -> r.recipeName).collect(Collectors.toList());
        List<String> actualList = fetcher.fetchRecipeList();

        //Order doesn't matter since there are no preferences
        assertEquals(expectedList.size(), actualList.size());
        assertTrue(expectedList.containsAll(actualList));
        assertTrue(actualList.containsAll(expectedList));
    }


}

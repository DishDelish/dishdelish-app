package com.github.siela1915.dishdelish;

import static com.github.siela1915.bootcamp.Labelling.AllergyType.EGGS;

import org.junit.Test;

import static org.junit.Assert.*;

import com.github.siela1915.bootcamp.Labelling.CuisineType;
import com.github.siela1915.bootcamp.Labelling.RecipeFetcher;
import com.github.siela1915.bootcamp.Recipes.ExampleRecipes;
import com.github.siela1915.bootcamp.Recipes.Ingredient;
import com.github.siela1915.bootcamp.Recipes.Recipe;
import com.github.siela1915.bootcamp.Recipes.Unit;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class RecipeFetcherTest {


    public RecipeFetcher fetcher;


    //The lists are gonna be fetched from the database, as lists.
    @Test
    public void fetcherSortsRecipes(){
        List<Integer> allergies = new ArrayList<>();
        List<Integer> cuisines = Arrays.asList(CuisineType.FRENCH.ordinal());
        List<Integer> diets = new ArrayList<>();

        fetcher = new RecipeFetcher(allergies, cuisines, diets);
        assertEquals(
                Arrays.asList(ExampleRecipes.recipes.get(0).recipeName, ExampleRecipes.recipes.get(2).recipeName, ExampleRecipes.recipes.get(1).recipeName, ExampleRecipes.recipes.get(3).recipeName),
                fetcher.fetchRecipeList()
        );

    }

    @Test
    public void fetcherExcludesInappropriateRecipes(){
        List<Integer> allergies = Arrays.asList(EGGS.ordinal());
        List<Integer> cuisines = Arrays.asList(CuisineType.FRENCH.ordinal());
        List<Integer> diets = new ArrayList<>();

        fetcher = new RecipeFetcher(allergies, cuisines, diets);
        assertEquals(
                Arrays.asList(ExampleRecipes.recipes.get(1).recipeName),
                fetcher.fetchRecipeList()
        );

    }

    @Test
    public void emptyPreferencesWork(){
        List<Integer> allergies = new ArrayList<>();
        List<Integer> cuisines = new ArrayList<>();
        List<Integer> diets = new ArrayList<>();

        fetcher = new RecipeFetcher(allergies, cuisines, diets);


        List<String> expectedList = ExampleRecipes.recipes.stream().map(r -> r.recipeName).collect(Collectors.toList());
        List<String> actualList = fetcher.fetchRecipeList();

        //Order doesn't matter since there are no preferences
        assertEquals(expectedList.size(), actualList.size());
        assertTrue(expectedList.containsAll(actualList));
        assertTrue(actualList.containsAll(expectedList));
    }

    @Test
    public void sortByPrepTimeSortsCorrectlyDescending(){
        List<Integer> allergies = new ArrayList<>();
        List<Integer> cuisines = new ArrayList<>();
        List<Integer> diets = new ArrayList<>();
        fetcher = new RecipeFetcher(allergies, cuisines, diets);

        //The example recipes are already sorted by prep time
        List<String> expectedList = ExampleRecipes.recipes.stream().map(r -> r.recipeName).collect(Collectors.toList());
        List<String> actualList = fetcher.sortRecipesByPreparationTime(ExampleRecipes.recipes, false);

        assertEquals(expectedList.get(2), actualList.get(3));
        assertTrue(expectedList.get(0).equals(actualList.get(2)) ||
                expectedList.get(0).equals(actualList.get(1)));
        assertEquals(expectedList.get(3), actualList.get(0));
    }

    @Test
    public void sortByPrepTimeSortsCorrectlyAscending(){
        List<Integer> allergies = new ArrayList<>();
        List<Integer> cuisines = new ArrayList<>();
        List<Integer> diets = new ArrayList<>();
        fetcher = new RecipeFetcher(allergies, cuisines, diets);

        //The example recipes are already sorted by prep time
        List<String> expectedList = ExampleRecipes.recipes.stream().map(r -> r.recipeName).collect(Collectors.toList());

        List<String> actualList = fetcher.sortRecipesByPreparationTime(ExampleRecipes.recipes, true);

        assertEquals(expectedList.get(2), actualList.get(0));
        assertTrue(expectedList.get(1).equals(actualList.get(1)) ||
                expectedList.get(1).equals(actualList.get(2)));
        assertEquals(expectedList.get(3), actualList.get(3));
    }

    @Test
    public void filterByIngredientFiltersCorrectly(){
        List<Integer> allergies = new ArrayList<>();
        List<Integer> cuisines = new ArrayList<>();
        List<Integer> diets = new ArrayList<>();
        fetcher = new RecipeFetcher(allergies, cuisines, diets);

        List<String> expectedList = Arrays.asList(ExampleRecipes.recipes.get(2).recipeName);
        List<String> actualList = fetcher.filterByIngredients(ExampleRecipes.recipes, Arrays.asList(new Ingredient("lemon", new Unit(1, ""))));

        assertEquals(expectedList, actualList);
    }

    @Test
    public void ingredientFilteringConvertsToLowerCase(){
        List<Integer> allergies = new ArrayList<>();
        List<Integer> cuisines = new ArrayList<>();
        List<Integer> diets = new ArrayList<>();
        fetcher = new RecipeFetcher(allergies, cuisines, diets);

        List<String> expectedList = Arrays.asList(ExampleRecipes.recipes.get(2).recipeName);
        List<String> actualList = fetcher.filterByIngredients(ExampleRecipes.recipes, Arrays.asList(new Ingredient("LeMON", new Unit(1, ""))));

        assertEquals(expectedList, actualList);
    }

    @Test
    public void sortingByNutritionalValuesSortsCorrectly(){
        RecipeFetcher fetcher = new RecipeFetcher(null, null, null);
        List<Recipe> recipes = new ArrayList<>();
        for (int i = 1; i < 11; i++) {
            Recipe r = new Recipe();
            r.setUniqueKey(String.valueOf(i));
            r.setFat(11-i);
            r.setSugar(i);
            r.setProtein(11-i);
            r.setCalories(i);
            r.setCarbohydrates(11-i);
            recipes.add(r);
        }

        List<String> rangeAsc = IntStream.rangeClosed(1, 10)
                .boxed().map(i -> Integer.toString(i)).collect(Collectors.toList());
        List<String> rangeDesc = rangeAsc.stream().sorted((i1, i2) ->Integer.compare(Integer.parseInt(i2), Integer.parseInt(i1))).collect(Collectors.toList());

        assertEquals(rangeDesc, fetcher.sortByFat(recipes));
        assertEquals(rangeAsc, fetcher.sortBySugar(recipes));
        assertEquals(rangeDesc, fetcher.sortByProtein(recipes));
        assertEquals(rangeAsc, fetcher.sortByCalories(recipes));
        assertEquals(rangeDesc, fetcher.sortByCarbohydrates(recipes));
    }


}

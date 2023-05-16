package com.github.siela1915.bootcamp.Tools;

import com.github.siela1915.bootcamp.Labelling.AllergyType;
import com.github.siela1915.bootcamp.Labelling.CuisineType;
import com.github.siela1915.bootcamp.Labelling.DietType;
import com.github.siela1915.bootcamp.Recipes.Recipe;

import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

public class Utilities {

    /**
     * Retains from the provided list of recipes only the ones with the specified allergy.
     * @param allergy The allergy type.
     * @param ls the list of recipes to be filtered.
     * @return A new list of recipes.
     */
    public static List<Recipe> getAllergy(AllergyType allergy, List<Recipe> ls) {
        return ls.stream().filter(r -> r.getAllergyTypes() != null && r.getAllergyTypes().contains(allergy.ordinal()))
                .collect(Collectors.toList());
    }

    /**
     * Retains from the provided list of recipes only the ones with the specified diet.
     * @param diet The diet type.
     * @param ls the list of recipes to be filtered.
     * @return A new list of recipes.
     */
    public static List<Recipe> getDiet(DietType diet, List<Recipe> ls) {
        return ls.stream().filter(r -> r.getDietTypes() != null && r.getDietTypes().contains(diet.ordinal()))
                .collect(Collectors.toList());
    }

    /**
     * Retains from the provided list of recipes only the ones with the specified cuisine.
     * @param cuisine The cuisine type.
     * @param ls the list of recipes to be filtered.
     * @return A new list of recipes.
     */
    public static List<Recipe> getCuisine(CuisineType cuisine, List<Recipe> ls) {
        return ls.stream().filter(r -> r.getCuisineTypes() != null && r.getCuisineTypes().contains(cuisine.ordinal()))
                .collect(Collectors.toList());
    }

    /**
     * Filters the provided list of recipes to retain only the recipes with the same cuisine types
     * as the recipe provided as first parameter to the function.
     * @param recipe The recipe on which the filtering will be based.
     * @param ls The list of recipes to filter.
     * @return a list of recipes, all containing the same cuisine types as recipe.
     */
    public static List<Recipe> getSameCuisineTypes(Recipe recipe, List<Recipe> ls) {
        return ls.stream().filter(r -> new HashSet<>(r.getCuisineTypes()).containsAll(recipe.getCuisineTypes()))
                .collect(Collectors.toList());
    }

    /**
     * Filters the provided list of recipes to retain only the recipes with the same diet types
     * as the recipe provided as first parameter to the function.
     * @param recipe The recipe on which the filtering will be based.
     * @param ls The list of recipes to filter.
     * @return a list of recipes, all containing the same diet types as recipe.
     */
    public static List<Recipe> getSameDietTypes(Recipe recipe, List<Recipe> ls) {
        return ls.stream().filter(r -> new HashSet<>(r.getDietTypes()).containsAll(recipe.getDietTypes()))
                .collect(Collectors.toList());
    }

    /**
     * Filters the provided list of recipes to retain only the recipes with the same allergy types
     * as the recipe provided as first parameter to the function.
     * @param recipe The recipe on which the filtering will be based.
     * @param ls The list of recipes to filter.
     * @return a list of recipes, all containing the same allergy types as recipe.
     */
    public static List<Recipe> getSameAllergyTypes(Recipe recipe, List<Recipe> ls) {
        return ls.stream().filter(r -> new HashSet<>(r.getAllergyTypes()).containsAll(recipe.getAllergyTypes()))
                .collect(Collectors.toList());
    }

    /**
     * Finds the most popular allergy type among the provided list of recipes.
     * @param ls The list of recipes submitted to the search.
     * @return The most popular allergy type (in case of an equality just returns the first
     * occurrence).
     */
    public static AllergyType getDominantAllergy(List<Recipe> ls) {
        int[] arr = new int[AllergyType.values().length];
        for (Recipe elem : ls) {
            elem.getAllergyTypes().forEach(allergy -> ++arr[allergy]);
        }
        return AllergyType.values()[getMaxIndex(arr)];
    }

    /**
     * Finds the most popular diet type among the provided list of recipes.
     * @param ls The list of recipes submitted to the search.
     * @return The most popular diet type (in case of an equality just returns the first
     * occurrence).
     */
    public static DietType getDominantDiet(List<Recipe> ls) {
        int[] arr = new int[DietType.values().length];
        for (Recipe elem : ls) {
            elem.getDietTypes().forEach(diet -> ++arr[diet]);
        }
        return DietType.values()[getMaxIndex(arr)];
    }

    /**
     * Finds the most popular cuisine type among the provided list of recipes.
     * @param ls The list of recipes submitted to the search.
     * @return The most popular cuisine type (in case of an equality just returns the first
     * occurrence).
     */
    public static CuisineType getDominantCuisine(List<Recipe> ls) {
        int[] arr = new int[CuisineType.values().length];
        for (Recipe elem : ls) {
            elem.getCuisineTypes().forEach(cuisine -> ++arr[cuisine]);
        }
        return CuisineType.values()[getMaxIndex(arr)];
    }

    private static int getMaxIndex(int[] arr) {
        int max = 0, index = 0;
        for (int i = 0; i < arr.length; ++i) {
            if (arr[i] > max) {
                max = arr[i];
                index = i;
            }
        }
        return index;
    }

}

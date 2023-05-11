package com.github.siela1915.bootcamp.Tools;

import com.github.siela1915.bootcamp.Recipes.Recipe;

import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

public class Utilities {

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


}

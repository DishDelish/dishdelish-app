package com.github.siela1915.bootcamp.Labelling;

import static java.util.Collections.reverseOrder;

import android.content.SyncStatusObserver;
import android.util.Pair;

import com.github.siela1915.bootcamp.Recipes.ExampleRecipes;
import com.github.siela1915.bootcamp.Recipes.Ingredient;
import com.github.siela1915.bootcamp.Recipes.Recipe;
import com.github.siela1915.bootcamp.firebase.Database;
import com.google.firebase.database.FirebaseDatabase;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.stream.Collectors;

public class RecipeFetcher{
    //These lists will probably be attributes of the user, TODO refactor later
    private List<Integer> allergies;
    private List<Integer> cuisines;
    private List<Integer> diets;
    private List<Recipe> allRecipes;
    public RecipeFetcher(List<Integer> allergies, List<Integer> cuisines, List<Integer> diets,List<Recipe> fromRecipes) {
        this.allergies = allergies;
        this.cuisines = cuisines;
        this.diets = diets;
        allRecipes=fromRecipes;
    }

    //returns list of IDs of recipes?
    //Not used in searching, just displaying appropriate recipes for a given user
    public List<String> fetchRecipeList(){
        //String will represent the ID of the recipe, for now it's just the name
        Map<String, Float> mapOfRecipes = new HashMap<>();

        for(Recipe r :allRecipes){
            //Base weight, will be lower only if diets or allergies are violated
            float weight = 5;
            weight += (cuisines).stream()
                    .distinct()
                    .filter(x -> (r.cuisineTypes).stream().anyMatch(y -> y.equals(x)))
                    .toArray().length*10;

            //checking if the recipe violates allergy or diet constraint
            if((AllergyType.fromIntList(allergies)).stream()
                    .map(a -> a.toString())
                    .distinct()
                    .filter(x -> (r.ingredientList).stream().anyMatch(y -> y.getIngredient().contains(x.toLowerCase())))
                    .toArray().length > 0
            || (AllergyType.fromIntList(allergies)).stream()
                    .distinct()
                    .filter(x -> (AllergyType.fromIntList(r.allergyTypes)).stream().anyMatch(y -> y.equals(x)))
                    .toArray().length > 0
            || (!diets.isEmpty() && (DietType.fromIntList(diets)).stream()
                    .distinct()
                    .filter(x -> (DietType.fromIntList(r.dietTypes)).stream().anyMatch(y -> y.equals(x)))
                    .toArray().length == 0)
            || (!cuisines.isEmpty() && (CuisineType.fromIntList(cuisines)).stream()
                    .distinct()
                    .filter(x -> (CuisineType.fromIntList(r.cuisineTypes)).stream().anyMatch(y -> y.equals(x)))
                    .toArray().length == 0)
            || (diets).stream()
                    .map(d -> DietType.getViolatingIngredients(d))
                    .filter(x -> (r.ingredientList).stream().anyMatch(y -> x.contains(y.getIngredient())))
                    .toArray().length > 0
            ){

                weight=0;
            }
            if(weight > 1){
                mapOfRecipes.put(r.recipeName, weight);
            }
        }

        //return the appropriate recipes in descending order by weight
        //Order in case of equal weights is not specified
        return mapOfRecipes.entrySet().stream().sorted(reverseOrder(Map.Entry.comparingByValue())).map(Map.Entry::getKey).collect(Collectors.toList());
    }

    //We can fetch a certain number of recipes from a list of the "rank" of the recipes in the database,
    //which is calculated using the above preferences of the user, assigning weights to each recipe
    //Do this algorithm on a certain number of recipes at a time not the whole database, so the user
    //doesn't have to wait a long time for the best recipes to be shown to him


    /**
     * Sorts a list of recipes by cook + preparation time.
     * @param recipes list of recipes
     * @return sorted recipe names
     */
    public List<String> sortRecipesByPreparationTime(List<Recipe> recipes, Boolean ascending){
        Objects.requireNonNull(recipes);
        List<Recipe> ret = new ArrayList<>(recipes);
        //default sort is ascending
        ret.sort(Comparator.comparing(r -> r.prepTime + r.cookTime));
        if(!ascending)
            Collections.reverse(ret);
        return ret.stream().map(r -> r.recipeName).collect(Collectors.toList());
    }

    /**
     * Filters recipes to only contain recipes using all ingredients in the ingredients list
     * @param ingredients to consider for filtering
     * @return filtered recipe names
     */
    public List<String> filterByIngredients(List<Ingredient> ingredients){
        Objects.requireNonNull(allRecipes);
        List<String> filteredRecipes = new ArrayList<>();

        for (Recipe recipe : allRecipes) {
            boolean containsOnlyIngredients = recipe.getIngredientList().stream()
                    .map(i -> i.getIngredient())
                    .allMatch(ingredient ->
                            ingredients.stream()
                                    .map(i -> i.getIngredient()).collect(Collectors.toList())
                            .contains(ingredient));

            if (containsOnlyIngredients) {
                filteredRecipes.add(recipe.recipeName);
            }
        }

        return filteredRecipes;
    }


    /**
     * Sort a list of recipes by their ascending protein value
     * @param recipes to sort
     * @return recipes sorted by protein value
     */
    public List<String> sortByProtein(List<Recipe> recipes) {
        Objects.requireNonNull(recipes);
        return recipes.stream().sorted(Comparator.comparingDouble(Recipe::getProtein)).map(r -> r.uniqueKey).collect(Collectors.toList());
    }

    /**
     * Sort a list of recipes by their ascending fat value
     * @param recipes to sort
     * @return recipes sorted by fat value
     */
    public List<String> sortByFat(List<Recipe> recipes) {
        Objects.requireNonNull(recipes);
        return recipes.stream().sorted(Comparator.comparingDouble(Recipe::getFat)).map(r -> r.uniqueKey).collect(Collectors.toList());
    }

    /**
     * Sort a list of recipes by their ascending carbohydrate value
     * @param recipes to sort
     * @return recipes sorted by carbohydrate value
     */
    public List<String> sortByCarbohydrates(List<Recipe> recipes) {
        Objects.requireNonNull(recipes);
        return recipes.stream().sorted(Comparator.comparingDouble(Recipe::getCarbohydrates)).map(r -> r.uniqueKey).collect(Collectors.toList());
    }

    /**
     * Sort a list of recipes by their ascending calorie value
     * @param recipes to sort
     * @return recipes sorted by calorie value
     */
    public List<String> sortByCalories(List<Recipe> recipes) {
        Objects.requireNonNull(recipes);
        return recipes.stream().sorted(Comparator.comparingDouble(Recipe::getCalories)).map(r -> r.uniqueKey).collect(Collectors.toList());
    }

    /**
     * Sort a list of recipes by their ascending sugar value
     * @param recipes to sort
     * @return recipes sorted by sugar value
     */
    public List<String> sortBySugar(List<Recipe> recipes) {
        Objects.requireNonNull(recipes);
        return recipes.stream().sorted(Comparator.comparingDouble(Recipe::getSugar)).map(r -> r.uniqueKey).collect(Collectors.toList());
    }

}

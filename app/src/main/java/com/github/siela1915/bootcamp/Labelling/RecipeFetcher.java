package com.github.siela1915.bootcamp.Labelling;

import static java.util.Collections.reverseOrder;

import android.util.Pair;

import com.github.siela1915.bootcamp.Recipes.ExampleRecipes;
import com.github.siela1915.bootcamp.Recipes.Ingredient;
import com.github.siela1915.bootcamp.Recipes.Recipe;

import java.util.ArrayList;
import java.util.Arrays;
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
    private int[] allergies;
    private int[] cuisines;
    private int[] diets;

    public RecipeFetcher(int[] allergies, int[] cuisines, int[] diets) {
        this.allergies = allergies;
        this.cuisines = cuisines;
        this.diets = diets;
    }

    //returns list of IDs of recipes?
    //Not used in searching, just displaying appropriate recipes for a given user
    public List<String> fetchRecipeList(){
        //String will represent the ID of the recipe, for now it's just the name
        Map<String, Float> mapOfRecipes = new HashMap<>();
        for(Recipe r :ExampleRecipes.recipes){
            //Base weight, will be lower only if diets or allergies are violated
            float weight = 5;
//            weight += Arrays.stream(cuisines).map(i -> Arrays.stream(r.cuisineTypes).equals(i)).count() * 10;
//
//            //checking if the recipe violates allergy or diet constraint
//            //List<Boolean> temp= allergies.stream().map(a -> r.allergyTypes.contains(a)).collect(Collectors.toList());
//            if(allergies.stream().map(a -> r.allergyTypes.contains(a)).filter(b -> b).count()>0
//            || diets.stream().map(d -> r.dietTypes.contains(d)).filter(b -> b).count()>0){
//                weight=0;
//            }
//            if(weight > 1){
//                mapOfRecipes.put(r.recipeName, weight);
//            }
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
     * @return sorted recipes
     */
    public List<Recipe> sortRecipesByPreparationTime(List<Recipe> recipes){
        Objects.requireNonNull(recipes);
        List<Recipe> ret = new ArrayList<>(recipes);
        ret.sort(Comparator.comparing(r -> r.prepTime + r.cookTime));
        return ret;
    }

    /**
     * Filters recipes to only contain recipes using all ingredients in the ingredients list
     * @param recipes to be filtered
     * @param ingredients to consider for filtering
     * @return filtered recipes
     */
    public List<Recipe> filterByIngredients(List<Recipe> recipes, List<Ingredient> ingredients){
        Objects.requireNonNull(recipes);
        List<Recipe> ret = new ArrayList<>(recipes);
        return ret.stream()
                .filter(r ->
                        new HashSet<>(r.ingredientList.stream().map(Ingredient::getIngredient).collect(Collectors.toList()))
                        .containsAll(ingredients))
                .collect(Collectors.toList());
    }
}

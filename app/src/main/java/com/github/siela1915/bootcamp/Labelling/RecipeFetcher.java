package com.github.siela1915.bootcamp.Labelling;

import static java.util.Collections.reverseOrder;

import android.util.Pair;

import com.github.siela1915.bootcamp.Recipes.ExampleRecipes;
import com.github.siela1915.bootcamp.Recipes.Ingredient;
import com.github.siela1915.bootcamp.Recipes.Recipe;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.stream.Collectors;

public class RecipeFetcher {
    //These lists will probably be attributes of the user, TODO refactor later
    private List<AllergyType> allergies;
    private List<CuisineType> cuisines;
    private List<DietType> diets;
    private List<Ingredient> favorites;

    public RecipeFetcher(List<AllergyType> allergies, List<CuisineType> cuisines, List<DietType> diets, List<Ingredient> favorites) {
        this.allergies = allergies;
        this.cuisines = cuisines;
        this.diets = diets;
        this.favorites = favorites;
    }

    //returns list of IDs of recipes?
    //Not used in searching, just displaying appropriate recipes for a given user
    public List<String> fetchRecipeList(){
        //String will represent the ID of the recipe, for now it's just the name
        Map<String, Float> mapOfRecipes = new HashMap<>();
        for(Recipe r :ExampleRecipes.recipes){
            //Base weight, will be lower only if diets or allergies are violated
            float weight = 5;
            weight += favorites.stream().map(i -> r.ingredientList.stream().map(p -> p.second.equals(i)).filter(b -> b).collect(Collectors.toList())).count() *5;
            weight += cuisines.stream().map(i -> r.cuisineTypes.contains(i)).filter(b -> b).count() * 10;
            //System.out.println(r.recipeName + weight);

            //checking if the recipe violates allergy or diet constraint
            //List<Boolean> temp= allergies.stream().map(a -> r.allergyTypes.contains(a)).collect(Collectors.toList());
            if(allergies.stream().map(a -> r.allergyTypes.contains(a)).filter(b -> b).count()>0
            || diets.stream().map(d -> r.dietTypes.contains(d)).filter(b -> b).count()>0){
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
}

package com.github.siela1915.bootcamp.Labelling;

import java.util.List;

public class RecipeFetcher {
    //These lists will probably be attributes of the user, TODO refactor later
    private List<AllergyType> allergies;
    private List<CuisineType> cuisines;
    private List<DietType> diets;

    public RecipeFetcher(List<AllergyType> allergies, List<CuisineType> cuisines, List<DietType> diets) {
        this.allergies = allergies;
        this.cuisines = cuisines;
        this.diets = diets;
    }

    //returns list of IDs of recipes?
    public List<String> FetchRecipes(){
        return null;
    }

    //We can fetch a certain number of recipes from a list of the "rank" of the recipes in the database,
    //which is calculated using the above preferences of the user, assigning weights to each recipe
    //Do this algorithm on a certain number of recipes at a time not the whole database, so the user
    //doesn't have to wait a long time for the best recipes to be shown to him
}

package com.github.siela1915.bootcamp.AutocompleteApi;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Nutrition {
    public List<Nutrient> nutrients;
    public Nutrition(List<Nutrient> nutrients) {
        this.nutrients = nutrients;
    }

    //List of all the nutrient names that we will be using
    public static final List<String> NUTRIENT_NAMES = Arrays.asList("Calories", "Fat", "Carbohydrates", "Sugar", "Protein");


    public Map<String, Double> mapFromNutrients(){
        Map<String, Double> ret = new HashMap<>();
        for(String s : NUTRIENT_NAMES){
            ret.put(s, nutrients.stream().filter(n -> n.name.equals(s)).map(n -> n.amount).findFirst().orElse(0.));
        }
        return ret;
    }
}

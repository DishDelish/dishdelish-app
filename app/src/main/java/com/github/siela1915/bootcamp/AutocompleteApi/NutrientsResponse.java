package com.github.siela1915.bootcamp.AutocompleteApi;

import java.util.List;
import java.util.Map;

public class NutrientsResponse {
    public NutrientsResponse(Nutrition nutrition) {
        this.nutrition = nutrition;
    }

    public Nutrition nutrition;


    public boolean equals(NutrientsResponse other){
        for (int i = 0; i < nutrition.nutrients.size(); i++) {
            if(!nutrition.nutrients.get(i).equals(other.nutrition.nutrients.get(i))){
                return false;
            }
        }
        return true;
    }
}

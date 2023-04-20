package com.github.siela1915.bootcamp.AutocompleteApi;

import java.util.List;

public class NutrientsResponse {
    public NutrientsResponse(List<Nutrient> nutrients) {
        this.nutrients = nutrients;
    }

    public List<Nutrient> nutrients;

    public boolean equals(NutrientsResponse other){
        for (int i = 0; i < nutrients.size(); i++) {
            if(!nutrients.get(i).equals(other.nutrients.get(i))){
                return false;
            }
        }
        return true;
    }
}

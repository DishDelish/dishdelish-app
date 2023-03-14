package com.github.siela1915.bootcamp.Recipes;


import java.util.List;

//list of strings
public class Utensils {
    private final List<String> utensils;

    public Utensils(List<String> utensils) {
        this.utensils = utensils;
    }

    public List<String> getUtensils() {
        return utensils;
    }
}

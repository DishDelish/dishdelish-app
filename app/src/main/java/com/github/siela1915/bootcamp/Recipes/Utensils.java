package com.github.siela1915.bootcamp.Recipes;


import java.util.List;

//list of strings
public class Utensils {
    private List<String> utensils;

    public Utensils() {}

    public Utensils(List<String> utensils) {
        this.utensils = utensils;
    }

    public List<String> getUtensils() {
        return utensils;
    }

    public void setUtensils(List<String> utensils) {
        this.utensils = utensils;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Utensils) {
            return utensils.equals(((Utensils) obj).utensils);
        }
        return false;
    }
}

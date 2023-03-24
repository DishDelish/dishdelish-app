package com.github.siela1915.bootcamp.Recipes;

import androidx.annotation.NonNull;

import java.util.List;

public enum Ingredient {
    //TODO have different classes of ingredients: countable, measurable...
    //Maybe add optional additional text in addition to ingredient, to specify how to prepare/form of ingredient
    //For example lemon zest
    EGGS("Eggs"), FISH("Fish"), OIL("Oil"), MILK("Milk"),
    BUTTER("Butter"), FLOUR("Flour"), SALT("Salt"), PEPPER("Pepper"),
    CARROTS("Carrots"), CHICKEN("Chicken"), LEMON("Lemon"), HUMMUS("Hummus"),
    CAULIFLOWER("Cauliflower"), CORIANDER("Coriander"), SUGAR("Sugar");

    private final String display_string;


    // constructor to set the string
    Ingredient(String name){display_string = name;}

    // toString returns the display string
    @NonNull
    @Override
    public String toString() {
        return display_string;
    }
    public static String[] getAll(){
        String[] res= {EGGS.toString(), FISH.display_string, OIL.display_string, MILK.display_string,
                BUTTER.display_string, FLOUR.display_string, CARROTS.display_string, CHICKEN.display_string, HUMMUS.display_string,
                CAULIFLOWER.display_string, LEMON.display_string,"Meat","Cheese","Tea"};
        return res;
    }
}

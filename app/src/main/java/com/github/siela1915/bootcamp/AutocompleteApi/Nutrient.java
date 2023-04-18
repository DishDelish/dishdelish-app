package com.github.siela1915.bootcamp.AutocompleteApi;

public class Nutrient {
    public String name;

    public double amount;

    public String unit;
    public double percentOfDailyNeeds;


    public Nutrient(String name, double amount, String unit, double percentOfDailyNeeds) {
        this.name = name;
        this.amount = amount;
        this.unit = unit;
        this.percentOfDailyNeeds = percentOfDailyNeeds;
    }
}

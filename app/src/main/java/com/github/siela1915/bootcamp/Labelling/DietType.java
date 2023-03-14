package com.github.siela1915.bootcamp.Labelling;

//Diet types, also includes food intolerances
public enum DietType {
    KETO("Keto"), VEGAN("Vegan"), VEGETARIAN("Vegetarian"), PALEO("Paleo"),
    GLUTEN("Gluten-free"), HISTAMINE("Histamine-free"), DAIRY("Dairy-free"), SULFITE("Sulfite-free");

    private final String display_string;

    // constructor to set the string
    DietType(String name){display_string = name;}

    // toString returns the display string
    @Override
    public String toString() {
        return display_string;
    }
}

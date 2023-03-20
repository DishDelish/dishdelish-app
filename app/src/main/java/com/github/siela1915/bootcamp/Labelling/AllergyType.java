package com.github.siela1915.bootcamp.Labelling;

//Might be better to implement using ingredients
public enum AllergyType {
    EGGS("Eggs"), FISH("Fish"), CRUSTACEANS("Crustaceans"), TREE_NUTS("Tree nuts"),
    PEANUTS("Peanuts"), WHEAT("Wheat"), SOYBEANS("Soybeans"), SESAME("Sesame"), MILK("Milk");

    private final String display_string;

    // constructor to set the string
    AllergyType(String name){display_string = name;}

    // toString returns the display string
    @Override
    public String toString() {
        return display_string;
    }
}

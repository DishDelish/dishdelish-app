package com.github.siela1915.bootcamp.Labelling;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

//Diet types, also includes food intolerances
public enum DietType {
    NONE("None", Arrays.asList()),
    KETO("Keto", Arrays.asList("rice", "wheat", "oats", "potato", "corn")),
    VEGAN("Vegan", Arrays.asList("meat", "lamb", "beef", "chicken", "fish", "eggs", "egg", "milk", "cheese", "yogurt", "honey", "gelatin")),
    VEGETARIAN("Vegetarian", Arrays.asList("meat", "lamb", "beef", "chicken", "fish")),
    PALEO("Paleo", Arrays.asList("wheat", "rice", "beans", "lentils", "milk", "cheese", "yogurt", "refined sugar", "sunflower oil", "palm oil")),
    GLUTEN("Gluten-free", Arrays.asList("bread", "pasta", "cereal", "beer", "wheat")),
    HISTAMINE("Histamine-free", Arrays.asList("cheese", "sauerkraut", "pickles", "cured meat", "shellfish", "alcohol", "citrus", "strawberries")),
    DAIRY("Dairy-free", Arrays.asList("milk", "cheese", "yogurt", "butter", "cream")),
    SULFITE("Sulfite-free", Arrays.asList("wine", "grape", "tomato", "bread", "crab", "lobster", "shrimp"));

    private final String display_string;
    //The ingredient names that violate the diet
    private final List<String> violatingIngredients;

    // constructor to set the string
    DietType(String name, List<String> l){
        display_string = name;
        violatingIngredients = l;
    }

    @Override
    public String toString() {
        return display_string;
    }

    public static String[] getAll(){
        List<DietType> allDiets= Arrays.asList(DietType.values());
        String[] all= new String[allDiets.size()];
        for(int i= 0; i<allDiets.size();i++){
            all[i]=allDiets.get(i).display_string;
        }
        return all;
    }
    public static DietType fromString(String text) {
        for (DietType b : DietType.values()) {
            if (b.display_string.equalsIgnoreCase(text)) {
                return b;
            }
        }
        return null;
    }

    public static DietType fromInt(Integer i){
        return DietType.values()[i];
    }

    public static List<DietType> fromIntList(List<Integer> l){
        return l.stream().map(i -> DietType.values()[i]).collect(Collectors.toList());
    }

    public static List<String> getViolatingIngredients(Integer d){
        return fromInt(d).violatingIngredients;
    }
}

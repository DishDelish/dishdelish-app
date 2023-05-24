package com.github.siela1915.bootcamp.Labelling;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

//Diet types, also includes food intolerances
public enum DietType {
    NONE("None"), KETO("Keto"), VEGAN("Vegan"), VEGETARIAN("Vegetarian"), PALEO("Paleo"),
    GLUTEN("Gluten-free"), HISTAMINE("Histamine-free"), DAIRY("Dairy-free"), SULFITE("Sulfite-free");

    private final String display_string;

    // constructor to set the string
    DietType(String name){display_string = name;}

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
}

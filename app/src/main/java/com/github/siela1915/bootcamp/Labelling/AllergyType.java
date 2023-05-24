package com.github.siela1915.bootcamp.Labelling;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

//Might be better to implement using ingredients
public enum AllergyType {
    NONE("None"), EGGS("Eggs"), FISH("Fish"), CRUSTACEANS("Crustaceans"), TREE_NUTS("Tree nuts"),
    PEANUTS("Peanuts"), WHEAT("Wheat"), SOYBEANS("Soybeans"), SESAME("Sesame"), MILK("Milk");

    private final String display_string;

    // constructor to set the string
    AllergyType(String name){display_string = name;}

    public static String[] getAll(){
        List<AllergyType> allAllergies= Arrays.asList(AllergyType.values());
        String[] all= new String[allAllergies.size()];
        for(int i= 0; i<allAllergies.size();i++){
            all[i]=allAllergies.get(i).display_string;
        }
        return all;
    }
    public static AllergyType fromString(String text) {
        for (AllergyType b : AllergyType.values()) {
            if (b.display_string.equalsIgnoreCase(text)) {
                return b;
            }
        }
        return null;
    }

    public static AllergyType fromInt(Integer i){
        return AllergyType.values()[i];
    }

    public static List<AllergyType> fromIntList(List<Integer> l){
        return l.stream().map(i -> AllergyType.values()[i]).collect(Collectors.toList());
    }


    @Override
    public String toString() {
        return display_string;
    }

}

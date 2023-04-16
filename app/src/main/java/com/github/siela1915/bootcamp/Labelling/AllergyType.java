package com.github.siela1915.bootcamp.Labelling;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

//Might be better to implement using ingredients
public enum AllergyType {
    EGGS("Eggs"), FISH("Fish"), CRUSTACEANS("Crustaceans"), TREE_NUTS("Tree nuts"),
    PEANUTS("Peanuts"), WHEAT("Wheat"), SOYBEANS("Soybeans"), SESAME("Sesame"), MILK("Milk");

    private final String display_string;

    // constructor to set the string
    AllergyType(String name){display_string = name;}

    @Override
    public String toString() {
        return display_string;
    }

    /**
     * returns the enum matching the display string or null if no such enum type exists
     * @param str to find the enum of
     * @return the corresponding enum, or null if no such enum found
     */
    public static AllergyType fromString(String str){
        for (AllergyType t : AllergyType.values()) {
            if (t.toString().equalsIgnoreCase(str)) {
                return t;
            }
        }
        return null;
    }


}

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
}

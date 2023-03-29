package com.github.siela1915.bootcamp.Recipes;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import java.util.List;

public class Ingredient implements Parcelable {
    String ingredient;
    Unit unit;

    public Ingredient(){}

    public void setIngredient(String ingredient) {
        this.ingredient = ingredient.toLowerCase();
    }

    public void setUnit(Unit unit) {
        this.unit = unit;
    }

    public Ingredient(String ingredient, Unit unit) {
        this.ingredient = ingredient.toLowerCase();
        this.unit = unit;
    }

    protected Ingredient(Parcel in) {
        ingredient = in.readString();
        unit = in.readParcelable(Unit.class.getClassLoader());
    }

    public static final Creator<Ingredient> CREATOR = new Creator<Ingredient>() {
        @Override
        public Ingredient createFromParcel(Parcel in) {
            return new Ingredient(in);
        }

        @Override
        public Ingredient[] newArray(int size) {
            return new Ingredient[size];
        }
    };

    public String getIngredient() {
        return ingredient;
    }

    public Unit getUnit() {
        return unit;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeString(ingredient);
        dest.writeParcelable(unit, flags);
    }

    // toString returns the display string

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Ingredient) {
            return ingredient.equals(((Ingredient) obj).ingredient)
                    && unit.equals(((Ingredient) obj).unit);
        }
        return false;
    }
}

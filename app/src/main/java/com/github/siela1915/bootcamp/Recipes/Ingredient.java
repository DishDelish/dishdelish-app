package com.github.siela1915.bootcamp.Recipes;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import java.util.List;

public class Ingredient implements Parcelable {
    String ingredient;
    Unit unit;
    private double calories = 0;
    private double fat = 0;
    private double carbs = 0;
    private double protein = 0;
    private double sugar = 0;

    public Ingredient(){}

    // constructor to set the string
    public void setIngredient(String ingredient) {
        this.ingredient = ingredient.toLowerCase();
    }

    public void setUnit(Unit unit) {
        this.unit = unit;
    }

    public Ingredient(String ingredient, Unit unit){
        this.ingredient = ingredient.toLowerCase();
        this.unit = unit;
    }

    //constructor for parcels
    public Ingredient(String ingredient, Unit unit, double calories, double fat, double carbs, double protein, double sugar) {
        this.ingredient = ingredient.toLowerCase();
        this.unit = unit;
        this.calories = calories;
        this.fat = fat;
        this.carbs = carbs;
        this.protein = protein;
        this.sugar = sugar;
    }

    protected Ingredient(Parcel in) {
        ingredient = in.readString();
        unit = in.readParcelable(Unit.class.getClassLoader());

        calories = in.readDouble();
        fat = in.readDouble();
        carbs = in.readDouble();
        protein = in.readDouble();
        sugar = in.readDouble();
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
        dest.writeDouble(calories);
        dest.writeDouble(fat);
        dest.writeDouble(carbs);
        dest.writeDouble(protein);
        dest.writeDouble(sugar);
    }

    @Override
    public String toString() {
        return ingredient + " " + unit.toString();
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

    public double getCalories() {
        return calories;
    }

    public void setCalories(double calories) {
        this.calories = calories;
    }

    public double getFat() {
        return fat;
    }

    public void setFat(double fat) {
        this.fat = fat;
    }

    public double getCarbs() {
        return carbs;
    }

    public void setCarbs(double carbs) {
        this.carbs = carbs;
    }

    public double getProtein() {
        return protein;
    }

    public void setProtein(double protein) {
        this.protein = protein;
    }

    public double getSugar() {
        return sugar;
    }

    public void setSugar(double sugar) {
        this.sugar = sugar;
    }
}

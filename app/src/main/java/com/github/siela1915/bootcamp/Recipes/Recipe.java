package com.github.siela1915.bootcamp.Recipes;

import android.media.Image;
import android.util.Pair;

import androidx.annotation.NonNull;

import java.util.List;

public class Recipe {
    //Images should be usable in activity classes like this:
    //Bitmap image = BitmapFactory.decodeResource(this.getResources(), image);
    public int image;
    public String recipeName;
    public String userName;
    public int profilePicture;
    public double rating;
    public int prepTime;
    public int cookTime;
    public int servings;
    public Utensils utensils;
    public List<Pair<Unit, Ingredient>> ingredientList;
    //Every step and comment will be a separate String in the list
    public List<String> steps;
    public List<String> comments;

    public Recipe(int image, String recipeName, String userName, int profilePicture, double rating,
                  int prepTime, int cookTime, int servings, Utensils utensils, List<Pair<Unit, Ingredient>> ingredientList,
                  List<String> steps, List<String> comments) {


        this.image = image;
        this.recipeName = recipeName;
        this.userName = userName;
        this.profilePicture = profilePicture;
        this.rating = rating;
        this.prepTime = prepTime;
        this.cookTime = cookTime;
        this.servings = servings;
        this.utensils = utensils;
        this.ingredientList = ingredientList;
        this.steps = steps;
        this.comments = comments;
    }

    @NonNull
    @Override
    public String toString(){
        return recipeName;
    }
}



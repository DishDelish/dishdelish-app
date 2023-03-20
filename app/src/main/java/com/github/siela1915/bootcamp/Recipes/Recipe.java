package com.github.siela1915.bootcamp.Recipes;

import android.media.Image;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Pair;

import androidx.annotation.NonNull;

import com.github.siela1915.bootcamp.Labelling.AllergyType;
import com.github.siela1915.bootcamp.Labelling.CuisineType;
import com.github.siela1915.bootcamp.Labelling.DietType;

import java.util.List;

public class Recipe implements Parcelable {
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
    public int[] cuisineTypes;
    public int[] allergyTypes;


    public int[] dietTypes;
    public List<Ingredient> ingredientList;
    //Every step and comment will be a separate String in the list
    public List<String> steps;
    public List<String> comments;
    public int likes;

    public int getImage() {
        return image;
    }

    public void setImage(int image) {
        this.image = image;
    }

    public String getRecipeName() {
        return recipeName;
    }

    public void setRecipeName(String recipeName) {
        this.recipeName = recipeName;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public int getProfilePicture() {
        return profilePicture;
    }

    public void setProfilePicture(int profilePicture) {
        this.profilePicture = profilePicture;
    }

    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }

    public int getPrepTime() {
        return prepTime;
    }

    public void setPrepTime(int prepTime) {
        this.prepTime = prepTime;
    }

    public int getCookTime() {
        return cookTime;
    }

    public void setCookTime(int cookTime) {
        this.cookTime = cookTime;
    }

    public int getServings() {
        return servings;
    }

    public void setServings(int servings) {
        this.servings = servings;
    }

    public Utensils getUtensils() {
        return utensils;
    }

    public void setUtensils(Utensils utensils) {
        this.utensils = utensils;
    }

    public int[] getCuisineTypes() {
        return cuisineTypes;
    }

    public void setCuisineTypes(int[] cuisineTypes) {
        this.cuisineTypes = cuisineTypes;
    }

    public int[] getAllergyTypes() {
        return allergyTypes;
    }

    public void setAllergyTypes(int[] allergyTypes) {
        this.allergyTypes = allergyTypes;
    }

    public int[] getDietTypes() {
        return dietTypes;
    }

    public void setDietTypes(int[] dietTypes) {
        this.dietTypes = dietTypes;
    }

    public List<Ingredient> getIngredientList() {
        return ingredientList;
    }

    public void setIngredientList(List<Ingredient> ingredientList) {
        this.ingredientList = ingredientList;
    }

    public List<String> getSteps() {
        return steps;
    }

    public void setSteps(List<String> steps) {
        this.steps = steps;
    }

    public List<String> getComments() {
        return comments;
    }

    public void setComments(List<String> comments) {
        this.comments = comments;
    }

    public int getLikes() {
        return likes;
    }

    public void setLikes(int likes) {
        this.likes = likes;
    }


    public Recipe() {}

    public Recipe(int image, String recipeName, String userName, int profilePicture, double rating,
                  int prepTime, int cookTime, int servings, Utensils utensils, int[] cuisineTypes,
                  int[] allergyTypes, int[] dietTypes, List<Ingredient> ingredientList,
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
        this.cuisineTypes = cuisineTypes;
        this.allergyTypes = allergyTypes;
        this.dietTypes = dietTypes;
        this.ingredientList = ingredientList;
        this.steps = steps;
        this.comments = comments;
    }


    protected Recipe(Parcel in) {
        image = in.readInt();
        recipeName = in.readString();
        userName = in.readString();
        profilePicture = in.readInt();
        rating = in.readDouble();
        prepTime = in.readInt();
        cookTime = in.readInt();
        servings = in.readInt();
        utensils = in.readParcelable(Utensils.class.getClassLoader());
        cuisineTypes = in.createIntArray();
        allergyTypes = in.createIntArray();
        dietTypes = in.createIntArray();
        ingredientList = in.createTypedArrayList(Ingredient.CREATOR);
        steps = in.createStringArrayList();
        comments = in.createStringArrayList();
    }

    public static final Creator<Recipe> CREATOR = new Creator<Recipe>() {
        @Override
        public Recipe createFromParcel(Parcel in) {
            return new Recipe(in);
        }

        @Override
        public Recipe[] newArray(int size) {
            return new Recipe[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeInt(image);
        dest.writeString(recipeName);
        dest.writeString(userName);
        dest.writeInt(profilePicture);
        dest.writeDouble(rating);
        dest.writeInt(prepTime);
        dest.writeInt(cookTime);
        dest.writeInt(servings);
        dest.writeParcelable(utensils, flags);
        dest.writeIntArray(cuisineTypes);
        dest.writeIntArray(allergyTypes);
        dest.writeIntArray(dietTypes);
        dest.writeTypedList(ingredientList);
        dest.writeStringList(steps);
        dest.writeStringList(comments);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Recipe) {
            Recipe recipe = (Recipe) obj;
            return image == recipe.image
                    && recipeName.equals(recipe.recipeName)
                    && userName.equals(recipe.userName)
                    && profilePicture == recipe.profilePicture
                    && Math.abs(rating - recipe.rating) < 1e-6      //Never compare double types with strict equality
                    && prepTime == recipe.prepTime
                    && cookTime == recipe.cookTime
                    && servings == recipe.servings
                    && utensils.equals(recipe.utensils)
                    && ingredientList.equals(recipe.ingredientList)
                    && steps.equals(recipe.steps)
                    && comments.equals(recipe.comments);
        }
        return false;
    }

}



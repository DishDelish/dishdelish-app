package com.github.siela1915.bootcamp.Recipes;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Recipe implements Parcelable {
    public String image, recipeName, userName, uniqueKey = "";
    public int profilePicture, prepTime, cookTime, servings, likes, numRatings = 0;
    public double rating;

    public Utensils utensils;
    public List<Integer> cuisineTypes, allergyTypes, dietTypes;
    public List<Ingredient> ingredientList;
    public List<String> steps;
    public List<Comment> comments= new ArrayList<>();

    //Nutritional values are all in grams, except calories which are in kCal
    public double calories, fat, carbohydrates, sugar, protein = 0;

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
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

    public List<Integer> getCuisineTypes() {
        return cuisineTypes;
    }

    public void setCuisineTypes(List<Integer> cuisineTypes) {
        this.cuisineTypes = cuisineTypes;
    }

    public List<Integer> getAllergyTypes() {
        return allergyTypes;
    }

    public void setAllergyTypes(List<Integer> allergyTypes) {
        this.allergyTypes = allergyTypes;
    }

    public List<Integer> getDietTypes() {
        return dietTypes;
    }

    public void setDietTypes(List<Integer> dietTypes) {
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

    public List<Comment> getComments() {
        return comments;
    }

    public void setComments(List<Comment> comments) {
        this.comments = comments;
    }

    public int getLikes() {
        return likes;
    }

    public void setLikes(int likes) {
        this.likes = likes;
    }

    public String getUniqueKey() {return uniqueKey;}

    public void setUniqueKey(String key) {this.uniqueKey = key;}

    public int getNumRatings() {return numRatings;}

    public void setNumRatings(int numRatings) {this.numRatings = numRatings;}

    public Recipe() {}

    public Recipe(String image, String recipeName, String userName, int profilePicture, double rating,
                  int prepTime, int cookTime, int servings, Utensils utensils, List<Integer> cuisineTypes,
                  List<Integer> allergyTypes, List<Integer> dietTypes, List<Ingredient> ingredientList,
                  List<String> steps, List<Comment> comments, int likes) {
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
        this.likes = likes;
    }


    protected Recipe(Parcel in) {
        image = in.readString();
        recipeName = in.readString();
        userName = in.readString();
        profilePicture = in.readInt();
        rating = in.readDouble();
        prepTime = in.readInt();
        cookTime = in.readInt();
        servings = in.readInt();
        utensils = in.readParcelable(Utensils.class.getClassLoader());
        cuisineTypes = IntStream.of(in.createIntArray()).boxed().collect(Collectors.toCollection(ArrayList::new));
        allergyTypes = IntStream.of(in.createIntArray()).boxed().collect(Collectors.toCollection(ArrayList::new));
        dietTypes = IntStream.of(in.createIntArray()).boxed().collect(Collectors.toCollection(ArrayList::new));
        ingredientList = in.createTypedArrayList(Ingredient.CREATOR);
        steps = in.createStringArrayList();
        comments = in.createTypedArrayList(Comment.CREATOR);
        calories = in.readDouble();
        fat = in.readDouble();
        carbohydrates = in.readDouble();
        sugar = in.readDouble();
        protein = in.readDouble();
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
        dest.writeString(image);
        dest.writeString(recipeName);
        dest.writeString(userName);
        dest.writeInt(profilePicture);
        dest.writeDouble(rating);
        dest.writeInt(prepTime);
        dest.writeInt(cookTime);
        dest.writeInt(servings);
        dest.writeParcelable(utensils, flags);
        dest.writeIntArray(cuisineTypes.stream().mapToInt(x -> x).toArray());
        dest.writeIntArray(allergyTypes.stream().mapToInt(x -> x).toArray());
        dest.writeIntArray(dietTypes.stream().mapToInt(x -> x).toArray());
        dest.writeTypedList(ingredientList);
        dest.writeStringList(steps);
        dest.writeTypedList(comments);
        dest.writeDouble(calories);
        dest.writeDouble(fat);
        dest.writeDouble(carbohydrates);
        dest.writeDouble(sugar);
        dest.writeDouble(protein);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Recipe) {
            Recipe recipe = (Recipe) obj;
            boolean temp0 = Objects.equals(image, recipe.image)
                    && recipeName.equals(recipe.recipeName)
                    && userName.equals(recipe.userName);
            boolean temp1 = profilePicture == recipe.profilePicture
                    && Math.abs(rating - recipe.rating) < 1e-6      //Never compare double types with strict equality
                    && prepTime == recipe.prepTime;
            boolean temp2 = cookTime == recipe.cookTime
                    && servings == recipe.servings
                    && utensils.equals(recipe.utensils);
            boolean temp3 = ingredientList.equals(recipe.ingredientList)
                    && steps.equals(recipe.steps)
                    && comments.equals(recipe.comments);
            boolean temp4 = likes == recipe.likes
                    && allergyTypes.equals(recipe.allergyTypes)
                    && cuisineTypes.equals(recipe.cuisineTypes);
            boolean temp5 = dietTypes.equals(recipe.dietTypes)
                    && uniqueKey.equals(recipe.uniqueKey);
            return  temp0 && temp1 && temp2 && temp3 && temp4 && temp5;
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

    public double getCarbohydrates() {
        return carbohydrates;
    }

    public void setCarbohydrates(double carbohydrates) {
        this.carbohydrates = carbohydrates;
    }

    public double getSugar() {
        return sugar;
    }

    public void setSugar(double sugar) {
        this.sugar = sugar;
    }

    public double getProtein() {
        return protein;
    }

    public void setProtein(double protein) {
        this.protein = protein;
    }

    @NonNull
    @Override
    public String toString() {
        return recipeName;
    }

}



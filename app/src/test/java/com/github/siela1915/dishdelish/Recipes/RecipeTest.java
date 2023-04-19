package com.github.siela1915.dishdelish.Recipes;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

import com.github.siela1915.bootcamp.Recipes.Ingredient;
import com.github.siela1915.bootcamp.Recipes.Recipe;
import com.github.siela1915.bootcamp.Recipes.Unit;
import com.github.siela1915.bootcamp.Recipes.Utensils;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class RecipeTest {

    @Test
    public void listOfStringsAreEqual() {
        List<String> ls1 = new ArrayList<>();
        ls1.add("string1");
        ls1.add("string2");
        ls1.add("string3");
        List<String> ls2 = new ArrayList<>();
        ls2.add("string1");
        ls2.add("string2");
        ls2.add("string3");
        assertEquals(ls1, ls2);
    }

    @Test
    public void toStringReturnsCorrectRecipeName() {
        Recipe recipe = createRecipe();
        assertEquals("randomRecipe", recipe.toString());
    }

    @Test
    public void equalsReturnsTrueOnSameRecipe() {
        Recipe recipe1 = createRecipe();
        Recipe recipe2 = createRecipe();
        assertEquals(recipe1, recipe2);
    }

    @Test
    public void equalsReturnsTrueOnMoreElaborateRecipes() {
        Recipe recipe1 = createOtherRecipe();
        Recipe recipe2 = createOtherRecipe();
        assertEquals(recipe1, recipe2);
    }


    @Test
    public void equalsFailsWhenRecipesDoNotHaveSameName() {
        Recipe recipe1 = createRecipe();
        Recipe recipe2 = createRecipeDifferentName();
        assertNotEquals(recipe1, recipe2);
    }

    @Test
    public void equalsFailsWhenRecipesDoNotHaveSameIngredients() {
        Recipe recipe1 = createOtherRecipe();
        Recipe recipe2 = createOtherRecipeDifferentIngredients();
        assertNotEquals(recipe1, recipe2);
    }

    @Test
    public void equalsFailsWhenRecipesDoNotHaveSameSteps() {
        Recipe recipe1 = createOtherRecipe();
        Recipe recipe2 = createOtherRecipeDifferentSteps();
        assertNotEquals(recipe1, recipe2);
    }

    @Test
    public void equalsFailsWhenRecipesDoNotHaveSameDietTypes() {
        Recipe recipe1 = createOtherRecipe();
        Recipe recipe2 = createOtherRecipeDifferentDiet();
        assertNotEquals(recipe1, recipe2);

    }

    private Recipe createRecipe() {
        List<String> utensils = new ArrayList<>();
        utensils.add("spoon");
        utensils.add("fork");
        utensils.add("knife");
        List<Ingredient> ingredients = new ArrayList<>();
        ingredients.add(new Ingredient("Eggs", new Unit(4, "some info")));
        List<String> steps = new ArrayList<>();
        steps.add("Just mash them up!");
        List<String> comments = new ArrayList<>();
        comments.add("mmm just love it!");
        comments.add("Steps unclear, bad recipe");
        List<Integer> cuisine = Arrays.asList(1, 2, 3, 4, 5);
        List<Integer> allergy = Arrays.asList(1, 2, 3, 4, 5);
        List<Integer> diet = Arrays.asList(1, 2, 3, 4, 5);
        return new Recipe(98, "randomRecipe", "randomUser1", 86, 4.5,
                10, 5, 4, new Utensils(utensils), cuisine, allergy, diet, ingredients, steps, comments, 190);
    }

    private Recipe createRecipeDifferentName() {
        List<String> utensils = new ArrayList<>();
        utensils.add("spoon");
        utensils.add("fork");
        utensils.add("knife");
        List<Ingredient> ingredients = new ArrayList<>();
        ingredients.add(new Ingredient("Eggs", new Unit(4, "some info")));
        List<String> steps = new ArrayList<>();
        steps.add("Just mash them up!");
        List<String> comments = new ArrayList<>();
        comments.add("mmm just love it!");
        comments.add("Steps unclear, bad recipe");
        List<Integer> cuisine = Arrays.asList(1, 2, 3, 4, 5);
        List<Integer> allergy = Arrays.asList(1, 2, 3, 4, 5);
        List<Integer> diet = Arrays.asList(1, 2, 3, 4, 5);
        return new Recipe(98, "randomRecipe2", "randomUser1", 86, 4.5,
                10, 5, 4, new Utensils(utensils), cuisine, allergy, diet, ingredients, steps, comments, 190);
    }

    private Recipe createOtherRecipe() {
        List<String> utensils = new ArrayList<>();
        utensils.add("spoon");
        utensils.add("fork");
        utensils.add("knife");
        List<Ingredient> ingredients = new ArrayList<>();
        ingredients.add(new Ingredient("Eggs", new Unit(4, "some info")));
        ingredients.add(new Ingredient("Pepper", new Unit(4, "some info")));
        ingredients.add(new Ingredient("Salt", new Unit(4, "some info")));
        List<String> steps = new ArrayList<>();
        steps.add("Crack the eggs open in a frying pan.");
        steps.add("Stir while eggs cook.");
        steps.add("Season with some salt and pepper.");
        List<String> comments = new ArrayList<>();
        comments.add("mmm just love it!");
        comments.add("Steps are clear! Much better than that other recipe I checked out.");
        List<Integer> cuisine = Arrays.asList(1, 2, 3, 4, 5);
        List<Integer> allergy = Arrays.asList(1, 2, 3, 4, 5);
        List<Integer> diet = Arrays.asList(1, 2, 3, 4, 5);
        return new Recipe(97, "randomRecipe", "randomUser2", 85, 4.5,
                10, 5, 4, new Utensils(utensils), cuisine, allergy, diet, ingredients, steps, comments, 190);
    }

    private Recipe createOtherRecipeDifferentDiet() {
        List<String> utensils = new ArrayList<>();
        utensils.add("spoon");
        utensils.add("fork");
        utensils.add("knife");
        List<Ingredient> ingredients = new ArrayList<>();
        ingredients.add(new Ingredient("Eggs", new Unit(4, "some info")));
        ingredients.add(new Ingredient("Pepper", new Unit(4, "some info")));
        ingredients.add(new Ingredient("Salt", new Unit(4, "some info")));
        List<String> steps = new ArrayList<>();
        steps.add("Crack the eggs open in a frying pan.");
        steps.add("Stir while eggs cook.");
        steps.add("Season with some salt and pepper.");
        List<String> comments = new ArrayList<>();
        comments.add("mmm just love it!");
        comments.add("Steps are clear! Much better than that other recipe I checked out.");
        List<Integer> cuisine = Arrays.asList(1, 2, 3, 4, 5);
        List<Integer> allergy = Arrays.asList(1, 2, 3, 4, 5);
        List<Integer> diet = Arrays.asList(1, 2, 3, 4, 6);
        return new Recipe(97, "randomRecipe", "randomUser2", 85, 4.5,
                10, 5, 4, new Utensils(utensils), cuisine, allergy, diet, ingredients, steps, comments, 190);
    }

    private Recipe createOtherRecipeDifferentIngredients() {
        List<String> utensils = new ArrayList<>();
        utensils.add("spoon");
        utensils.add("fork");
        utensils.add("knife");
        List<Ingredient> ingredients = new ArrayList<>();
        ingredients.add(new Ingredient("Eggs", new Unit(4, "some info")));
        ingredients.add(new Ingredient("Pepper", new Unit(4, "some info")));
        ingredients.add(new Ingredient("Carrots", new Unit(4, "some info")));
        List<String> steps = new ArrayList<>();
        steps.add("Crack the eggs open in a frying pan.");
        steps.add("Stir while eggs cook.");
        steps.add("Season with some salt and pepper.");
        List<String> comments = new ArrayList<>();
        comments.add("mmm just love it!");
        comments.add("Steps are clear! Much better than that other recipe I checked out.");
        List<Integer> cuisine = Arrays.asList(1, 2, 3, 4, 5);
        List<Integer> allergy = Arrays.asList(1, 2, 3, 4, 5);
        List<Integer> diet = Arrays.asList(1, 2, 3, 4, 5);
        return new Recipe(97, "randomRecipe", "randomUser2", 85, 4.5,
                10, 5, 4, new Utensils(utensils), cuisine, allergy, diet, ingredients, steps, comments, 190);
    }

    private Recipe createOtherRecipeDifferentSteps() {
        List<String> utensils = new ArrayList<>();
        utensils.add("spoon");
        utensils.add("fork");
        utensils.add("knife");
        List<Ingredient> ingredients = new ArrayList<>();
        ingredients.add(new Ingredient("Eggs", new Unit(4, "some info")));
        ingredients.add(new Ingredient("Pepper", new Unit(4, "some info")));
        ingredients.add(new Ingredient("Salt", new Unit(4, "some info")));
        List<String> steps = new ArrayList<>();
        steps.add("Crack the eggs open in a frying pan.");
        steps.add("Stir while eggs cook.");
        steps.add("Add the salt THEN the pepper!!!");
        List<String> comments = new ArrayList<>();
        comments.add("mmm just love it!");
        comments.add("Steps are clear! Much better than that other recipe I checked out.");
        List<Integer> cuisine = Arrays.asList(1, 2, 3, 4, 5);
        List<Integer> allergy = Arrays.asList(1, 2, 3, 4, 5);
        List<Integer> diet = Arrays.asList(1, 2, 3, 4, 5);
        return new Recipe(97, "randomRecipe", "randomUser2", 85, 4.5,
                10, 5, 4, new Utensils(utensils), cuisine, allergy, diet, ingredients, steps, comments, 190);
    }

    @Test
    public void getAfterSetReturnsImage() {
        Recipe recipe = new Recipe();
        recipe.setImage(45);
        assertEquals(recipe.getImage(), 45);
    }

    @Test
    public void getAfterSetReturnsRecipeName() {
        Recipe recipe = new Recipe();
        recipe.setRecipeName("some name");
        assertEquals(recipe.getRecipeName(), "some name");
    }

    @Test
    public void getAfterSetReturnsUserName() {
        Recipe recipe = new Recipe();
        recipe.setUserName("user name");
        assertEquals(recipe.getUserName(), "user name");
    }

    @Test
    public void getAfterSetReturnsProfilePicture() {
        Recipe recipe = new Recipe();
        recipe.setProfilePicture(89);
        assertEquals(recipe.getProfilePicture(), 89);
    }

    @Test
    public void getAfterSetReturnsRating() {
        Recipe recipe = new Recipe();
        recipe.setRating(4.5);
        assertTrue(Math.abs(recipe.getRating() - 4.5) < 1e-6);
    }

    @Test
    public void getAfterSetReturnsPrepTime() {
        Recipe recipe = new Recipe();
        recipe.setPrepTime(10);
        assertEquals(recipe.getPrepTime(), 10);
    }

    @Test
    public void getAfterSetReturnsCookTime() {
        Recipe recipe = new Recipe();
        recipe.setCookTime(20);
        assertEquals(recipe.getCookTime(), 20);
    }

    @Test
    public void getAfterSetReturnsServings() {
        Recipe recipe = new Recipe();
        recipe.setServings(4);
        assertEquals(recipe.getServings(), 4);
    }

    @Test
    public void getAfterSetReturnsUtensils() {
        Recipe recipe = new Recipe();
        Utensils utensils = new Utensils(Arrays.asList("fork", "spoon", "knife"));
        recipe.setUtensils(utensils);
        assertEquals(recipe.getUtensils(), utensils);
    }

    @Test
    public void getAfterSetReturnsCuisineTypes() {
        List<Integer> array = Arrays.asList(1, 2, 3, 4, 5);
        Recipe recipe = new Recipe();
        recipe.setCuisineTypes(array);
        assertEquals(array, recipe.getCuisineTypes());
    }

    @Test
    public void getAfterSetReturnsAllergyTypes() {
        List<Integer> array = Arrays.asList(1, 2, 3, 4, 5);
        Recipe recipe = new Recipe();
        recipe.setAllergyTypes(array);
        assertEquals(array, recipe.getAllergyTypes());
    }

    @Test
    public void getAfterSetReturnsDietTypes() {
        List<Integer> array = Arrays.asList(1, 2, 3, 4, 5);
        Recipe recipe = new Recipe();
        recipe.setDietTypes(array);
        assertEquals(array, recipe.getDietTypes());
    }

    @Test
    public void getAfterSetReturnsIngredientList() {
        Recipe recipe = new Recipe();
        List<Ingredient> ls = Arrays.asList(new Ingredient("bread", new Unit(4, "info")),
                new Ingredient("eggs", new Unit(2, "info")),
                new Ingredient("butter", new Unit(200, "grams")));
        recipe.setIngredientList(ls);
        assertEquals(recipe.getIngredientList(), ls);
    }

    @Test
    public void getAfterSetReturnsStepsList() {
        Recipe recipe = new Recipe();
        List<String> ls = Arrays.asList("first step", "second step", "third step");
        recipe.setSteps(ls);
        assertEquals(recipe.getSteps(), ls);
    }

    @Test
    public void getAfterSetReturnsCommentsList() {
        Recipe recipe = new Recipe();
        List<String> ls = Arrays.asList("first comment", "second comment", "third comment");
        recipe.setComments(ls);
        assertEquals(recipe.getComments(), ls);
    }

    @Test
    public void getAfterSetReturnsLikes() {
        Recipe recipe = new Recipe();
        recipe.setLikes(450);
        assertEquals(recipe.getLikes(), 450);
    }

    @Test
    public void getAfterSetReturnsUniqueKey() {
        Recipe recipe = new Recipe();
        recipe.setUniqueKey("unique");
        assertEquals(recipe.getUniqueKey(), "unique");
    }



}

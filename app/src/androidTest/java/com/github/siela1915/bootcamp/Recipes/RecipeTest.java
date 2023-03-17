package com.github.siela1915.bootcamp.Recipes;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import android.util.Pair;

import org.junit.Test;

import java.util.ArrayList;
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
        assertTrue(ls1.equals(ls2));
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
        assertTrue(recipe1.equals(recipe2));
    }

    @Test
    public void equalsReturnsTrueOnMoreElaborateRecipes() {
        Recipe recipe1 = createOtherRecipe();
        Recipe recipe2 = createOtherRecipe();
        assertTrue(recipe1.equals(recipe2));
    }


    @Test
    public void equalsFailsWhenRecipesDoNotHaveSameName() {
        Recipe recipe1 = createRecipe();
        Recipe recipe2 = createRecipeDifferentName();
        assertFalse(recipe1.equals(recipe2));
    }

    @Test
    public void equalsFailsWhenRecipesDoNotHaveSameIngredients() {
        Recipe recipe1 = createOtherRecipe();
        Recipe recipe2 = createOtherRecipeDifferentIngredients();
        assertFalse(recipe1.equals(recipe2));
    }

    @Test
    public void equalsFailsWhenRecipesDoNotHaveSameSteps() {
        Recipe recipe1 = createOtherRecipe();
        Recipe recipe2 = createOtherRecipeDifferentSteps();
        assertFalse(recipe1.equals(recipe2));
    }

    private Recipe createRecipe() {
        List<String> utensils = new ArrayList<>();
        utensils.add("spoon");
        utensils.add("fork");
        utensils.add("knife");
        List<Pair<Unit, Ingredient>> ingredients = new ArrayList<>();
        ingredients.add(new Pair<>(new Unit(4, "info"), Ingredient.EGGS));
        List<String> steps = new ArrayList<>();
        steps.add("Just mash them up!");
        List<String> comments = new ArrayList<>();
        comments.add("mmm just love it!");
        comments.add("Steps unclear, bad recipe");
        return new Recipe(98, "randomRecipe", "randomUser1", 86, 4.5,
                10, 5, 4, new Utensils(utensils), ingredients, steps, comments);
    }

    private Recipe createRecipeDifferentName() {
        List<String> utensils = new ArrayList<>();
        utensils.add("spoon");
        utensils.add("fork");
        utensils.add("knife");
        List<Pair<Unit, Ingredient>> ingredients = new ArrayList<>();
        ingredients.add(new Pair<>(new Unit(4, "info"), Ingredient.EGGS));
        List<String> steps = new ArrayList<>();
        steps.add("Just mash them up!");
        List<String> comments = new ArrayList<>();
        comments.add("mmm just love it!");
        comments.add("Steps unclear, bad recipe");
        return new Recipe(98, "randomRecipe2", "randomUser1", 86, 4.5,
                10, 5, 4, new Utensils(utensils), ingredients, steps, comments);
    }

    private Recipe createOtherRecipe() {
        List<String> utensils = new ArrayList<>();
        utensils.add("spoon");
        utensils.add("fork");
        utensils.add("knife");
        List<Pair<Unit, Ingredient>> ingredients = new ArrayList<>();
        ingredients.add(new Pair<>(new Unit(4, "info"), Ingredient.EGGS));
        ingredients.add(new Pair<>(new Unit(4, "info"), Ingredient.PEPPER));
        ingredients.add(new Pair<>(new Unit(4, "info"), Ingredient.SALT));
        List<String> steps = new ArrayList<>();
        steps.add("Crack the eggs open in a frying pan.");
        steps.add("Stir while eggs cook.");
        steps.add("Season with some salt and pepper.");
        List<String> comments = new ArrayList<>();
        comments.add("mmm just love it!");
        comments.add("Steps are clear! Much better than that other recipe I checked out.");
        return new Recipe(97, "randomRecipe", "randomUser2", 85, 4.5,
                10, 5, 4, new Utensils(utensils), ingredients, steps, comments);
    }

    private Recipe createOtherRecipeDifferentIngredients() {
        List<String> utensils = new ArrayList<>();
        utensils.add("spoon");
        utensils.add("fork");
        utensils.add("knife");
        List<Pair<Unit, Ingredient>> ingredients = new ArrayList<>();
        ingredients.add(new Pair<>(new Unit(4, "info"), Ingredient.EGGS));
        ingredients.add(new Pair<>(new Unit(4, "info"), Ingredient.PEPPER));
        ingredients.add(new Pair<>(new Unit(4, "info"), Ingredient.CARROTS));
        List<String> steps = new ArrayList<>();
        steps.add("Crack the eggs open in a frying pan.");
        steps.add("Stir while eggs cook.");
        steps.add("Season with some salt and pepper.");
        List<String> comments = new ArrayList<>();
        comments.add("mmm just love it!");
        comments.add("Steps are clear! Much better than that other recipe I checked out.");
        return new Recipe(97, "randomRecipe", "randomUser2", 85, 4.5,
                10, 5, 4, new Utensils(utensils), ingredients, steps, comments);
    }

    private Recipe createOtherRecipeDifferentSteps() {
        List<String> utensils = new ArrayList<>();
        utensils.add("spoon");
        utensils.add("fork");
        utensils.add("knife");
        List<Pair<Unit, Ingredient>> ingredients = new ArrayList<>();
        ingredients.add(new Pair<>(new Unit(4, "info"), Ingredient.EGGS));
        ingredients.add(new Pair<>(new Unit(4, "info"), Ingredient.PEPPER));
        ingredients.add(new Pair<>(new Unit(4, "info"), Ingredient.SALT));
        List<String> steps = new ArrayList<>();
        steps.add("Crack the eggs open in a frying pan.");
        steps.add("Stir while eggs cook.");
        steps.add("Add the salt THEN the pepper!!!");
        List<String> comments = new ArrayList<>();
        comments.add("mmm just love it!");
        comments.add("Steps are clear! Much better than that other recipe I checked out.");
        return new Recipe(97, "randomRecipe", "randomUser2", 85, 4.5,
                10, 5, 4, new Utensils(utensils), ingredients, steps, comments);
    }
}

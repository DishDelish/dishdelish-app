package com.github.siela1915.dishdelish.Recipes;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

import com.github.siela1915.bootcamp.Recipes.Comment;
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
        return new Recipe("URL", "randomRecipe", "randomUser1", 86, 4.5,
                10, 5, 4, new Utensils(createUtensils()), cuisine, allergy,
                diet, createIngredients(), createSteps(), createListComments(), 190);
    }

    private Recipe createRecipeDifferentName() {
        return new Recipe("URL", "randomRecipe2", "randomUser1", 86, 4.5,
                10, 5, 4, new Utensils(createUtensils()), cuisine, allergy,
                diet, createIngredients(), createSteps(), createListComments(), 190);
    }

    private Recipe createOtherRecipe() {
        return new Recipe("URL", "randomRecipe", "randomUser2", 85, 4.5,
                10, 5, 4, new Utensils(createUtensils()), cuisine, allergy,
                diet, createIngredients(), createSteps(), createListComments(), 190);
    }

    private Recipe createOtherRecipeDifferentDiet() {
        List<Integer> diet = Arrays.asList(1, 2, 3, 4, 6);
        return new Recipe("URL", "randomRecipe", "randomUser2", 85, 4.5,
                10, 5, 4, new Utensils(createUtensils()), cuisine, allergy,
                diet, createIngredients(), createSteps(), createListComments(), 190);
    }

    private Recipe createOtherRecipeDifferentIngredients() {
        List<Ingredient> ingredients = new ArrayList<>();
        ingredients.add(new Ingredient("Eggs", new Unit(4, "some info")));
        ingredients.add(new Ingredient("Pepper", new Unit(4, "some info")));
        ingredients.add(new Ingredient("Carrots", new Unit(4, "some info")));
        return new Recipe("URL", "randomRecipe", "randomUser2", 85, 4.5,
                10, 5, 4, new Utensils(createUtensils()), cuisine, allergy,
                diet, ingredients, createSteps(), createListComments(), 190);
    }

    private Recipe createOtherRecipeDifferentSteps() {
        List<String> steps = new ArrayList<>();
        steps.add("Crack the eggs open in a frying pan.");
        steps.add("Stir while eggs cook.");
        steps.add("Add the salt THEN the pepper!!!");
        return new Recipe("URL", "randomRecipe", "randomUser2", 85, 4.5,
                10, 5, 4, new Utensils(createUtensils()), cuisine, allergy, diet,
                createIngredients(), steps, createListComments(), 190);
    }

    private List<Comment> createListComments() {
        String[] cs = new String[]{"Love it!", "Nah not me.", "What???"};
        List<Comment> ls = new ArrayList<>();
        for (String s : cs) {
            ls.add(new Comment(s));
        }
        return ls;
    }

    private List<String> createUtensils() {
        String[] us = new String[] {"spoon", "fork", "knife"};
        return Arrays.asList(us);
    }

    private List<Ingredient> createIngredients() {
        List<Ingredient> ingredients = new ArrayList<>();
        ingredients.add(new Ingredient("Eggs", new Unit(4, "some info")));
        ingredients.add(new Ingredient("Pepper", new Unit(4, "some info")));
        ingredients.add(new Ingredient("Salt", new Unit(4, "some info")));
        return ingredients;
    }

    private List<String> createSteps() {
        String[] ss = new String[]{"Crack the eggs open in a frying pan.",
                "Stir while eggs cook.", "Season with some salt and pepper."};
        return Arrays.asList(ss);
    }

    private final List<Integer> cuisine = Arrays.asList(1, 2, 3, 4, 5);
    private final List<Integer> allergy = Arrays.asList(1, 2, 3, 4, 5);
    private final List<Integer> diet = Arrays.asList(1, 2, 3, 4, 5);


    @Test
    public void getAfterSetReturnsImage() {
        Recipe recipe = new Recipe();
        recipe.setImage("URL");
        assertEquals(recipe.getImage(), "URL");
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
        List<Comment> cls = createListComments();
        recipe.setComments(cls);
        assertEquals(recipe.getComments(), cls);
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

    @Test
    public void getAfterSetReturnsNumRatings() {
        Recipe recipe = new Recipe();
        recipe.setNumRatings(100);
        assertEquals(recipe.getNumRatings(), 100);
    }


}

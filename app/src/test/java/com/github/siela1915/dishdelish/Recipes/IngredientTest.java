package com.github.siela1915.dishdelish.Recipes;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import com.github.siela1915.bootcamp.Recipes.Ingredient;
import com.github.siela1915.bootcamp.Recipes.Unit;

import org.junit.Test;

public class IngredientTest {

    @Test
    public void getAfterSetReturnsCorrectStringIngredient() {
        Ingredient ingredient = new Ingredient();
        ingredient.setIngredient("corn");
        assertEquals(ingredient.getIngredient(), "corn");
    }

    @Test
    public void getAfterSetReturnsCorrectUnit() {
        Ingredient ingredient = new Ingredient();
        Unit unit = new Unit(45, "information");
        ingredient.setUnit(unit);
        assertEquals(ingredient.getUnit(), unit);
    }

    @Test
    public void equalsReturnsTrueForIdenticalIngredients() {
        Ingredient ingredient1 = new Ingredient("corn", new Unit(45, "information"));
        Ingredient ingredient2 = new Ingredient("corn", new Unit(45, "information"));
        assertTrue(ingredient1.equals(ingredient2));
    }

    @Test
    public void equalsReturnsFalseForDifferentUnit() {
        Ingredient ingredient1 = new Ingredient("corn", new Unit(45, "information"));
        Ingredient ingredient2 = new Ingredient("corn", new Unit(46, "information"));
        assertFalse(ingredient1.equals(ingredient2));
    }

    @Test
    public void equalsReturnsFalseForDifferentString() {
        Ingredient ingredient1 = new Ingredient("eggs", new Unit(45, "information"));
        Ingredient ingredient2 = new Ingredient("corn", new Unit(45, "information"));
        assertFalse(ingredient1.equals(ingredient2));
    }



}

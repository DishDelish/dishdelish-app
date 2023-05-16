package com.github.siela1915.bootcamp;

import static androidx.test.espresso.Espresso.onView;

import androidx.fragment.app.testing.FragmentScenario;

import com.github.siela1915.bootcamp.Recipes.ExampleRecipes;
import com.github.siela1915.bootcamp.Recipes.Ingredient;
import com.github.siela1915.bootcamp.Recipes.Recipe;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.matcher.ViewMatchers.isClickable;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import android.os.Bundle;

import androidx.fragment.app.testing.FragmentScenario;
import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.assertion.ViewAssertions;
import androidx.test.espresso.contrib.RecyclerViewActions;
import androidx.test.espresso.matcher.ViewMatchers;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class IngredientCheckTest {
    FragmentScenario<FragmentIngredientCheck> scenario;//=FragmentScenario.launchInContainer(FragmentIngredientCheck.class);

    @Test
    public void test(){
        Bundle bundle = new Bundle();
        List<Ingredient> ingredients = ExampleRecipes.recipes.get(1).getIngredientList();
        bundle.putParcelableArrayList(FragmentIngredientCheck.ARG_INGREDIENT_LIST, new ArrayList<>(ingredients));
        scenario = FragmentScenario.launchInContainer(FragmentIngredientCheck.class, bundle);
        onView(withId(R.id.neededIngredientsRV)).check(ViewAssertions.matches(isDisplayed()));
        onView(withText(ingredients.get(0).toString())).check(ViewAssertions.matches(isDisplayed()));
        onView(withId(R.id.neededIngredientsRV))
                .perform(RecyclerViewActions.actionOnItemAtPosition(0,click()));
        onView(withId(R.id.addToShoppingListBtn)).check(ViewAssertions.matches(isClickable()));
    }
}

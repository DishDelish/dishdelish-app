package com.github.siela1915.bootcamp;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.Espresso.pressBack;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withEffectiveVisibility;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import static org.hamcrest.Matchers.not;

import android.os.Bundle;

import androidx.fragment.app.testing.FragmentScenario;
import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.assertion.ViewAssertions;
import androidx.test.espresso.contrib.RecyclerViewActions;
import androidx.test.espresso.matcher.ViewMatchers;

import com.github.siela1915.bootcamp.Recipes.ExampleRecipes;
import com.github.siela1915.bootcamp.Recipes.Ingredient;

import org.junit.Test;
import org.mockito.internal.matchers.Not;

import java.util.ArrayList;
import java.util.List;

public class IngredientCheckFragmentContainerTest {
    FragmentScenario<FragmentIngredientCheckContainer> scenario;

    @Test
    public void ingredientCheckFirstInteractionTest(){
        scenario =FragmentScenario.launchInContainer(FragmentIngredientCheckContainer.class);
        onView(withId(R.id.neededIngredientsRV)).check(ViewAssertions.matches(isDisplayed()));
        onView(withId(R.id.addToShoppingListBtn)).perform(click());
        onView(withId(R.id.shoppingCartFragment)).check(ViewAssertions.matches(isDisplayed()));
    }
    /*
    @Test
    public void onClickOnNearbyBtnNavigatesToNearbyHelpFragmentTest(){
        scenario =FragmentScenario.launchInContainer(FragmentIngredientCheckContainer.class);
        onView(withId(R.id.nearByBtn)).perform(click());
        onView(withId(R.id.helpFragConstraintLayout)).check(ViewAssertions.matches(isDisplayed()));
    }

     */
    @Test
    public void containerContentChangesCorrectlyTest(){
        scenario =FragmentScenario.launchInContainer(FragmentIngredientCheckContainer.class);
        FragmentIngredientCheck fragment= FragmentIngredientCheck.newInstance(ExampleRecipes.recipes.get(1).getIngredientList());
        scenario.onFragment(f->{
           f.getParentFragmentManager().beginTransaction()
                   .add(R.id.ingredientContainer,fragment)
                   .commit();
        });
        onView(withText(ExampleRecipes.recipes.get(1).getIngredientList().get(0).toString()))
                .check(ViewAssertions.matches(isDisplayed()));
        onView(withText(ExampleRecipes.recipes.get(1).getIngredientList().get(1).toString()))
                .check(ViewAssertions.matches(isDisplayed()));
    }
}
package com.github.siela1915.bootcamp;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.Espresso.pressBack;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.Visibility.VISIBLE;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withContentDescription;
import static androidx.test.espresso.matcher.ViewMatchers.withEffectiveVisibility;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import static org.hamcrest.Matchers.not;

import android.Manifest;
import android.os.Bundle;
import android.widget.Button;

import androidx.fragment.app.testing.FragmentScenario;
import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.assertion.ViewAssertions;
import androidx.test.espresso.contrib.RecyclerViewActions;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.rule.GrantPermissionRule;

import com.github.siela1915.bootcamp.Recipes.ExampleRecipes;
import com.github.siela1915.bootcamp.Recipes.Ingredient;
import com.github.siela1915.bootcamp.Recipes.Recipe;

import org.junit.Rule;
import org.junit.Test;
import org.mockito.internal.matchers.Not;

import java.util.ArrayList;
import java.util.List;

public class IngredientCheckFragmentContainerTest {
    FragmentScenario<FragmentIngredientCheckContainer> scenario;
    Recipe recipe = ExampleRecipes.recipes.get(0);

    @Rule
    public GrantPermissionRule mRuntimePermissionRule = GrantPermissionRule.grant(
            Manifest.permission.ACCESS_FINE_LOCATION);

    @Test
    public void ingredientCheckFirstInteractionTest(){
        Bundle args = new Bundle();
        ArrayList<Ingredient> l = new ArrayList<>(recipe.getIngredientList());
        args.putParcelableArrayList("ingredients", l);
        scenario =FragmentScenario.launchInContainer(FragmentIngredientCheckContainer.class, args);
        onView(withId(R.id.neededIngredientsRV)).check(ViewAssertions.matches(isDisplayed()));
        scenario.onFragment(f -> {
            Button shoppingListButton = f.getView().findViewById(R.id.addToShoppingListBtn);
            shoppingListButton.performClick();
        });
        //onView(withId(R.id.addToShoppingListBtn)).perform(click());
        onView(withId(R.id.shoppingCartFragment)).check(ViewAssertions.matches(isDisplayed()));
        scenario.close();
    }

    @Test
    public void onClickOnNearbyBtnNavigatesToNearbyHelpFragmentTest(){
        Bundle args = new Bundle();
        ArrayList<Ingredient> l = new ArrayList<>(recipe.getIngredientList());
        args.putParcelableArrayList("ingredients", l);
        scenario =FragmentScenario.launchInContainer(FragmentIngredientCheckContainer.class, args);
        onView(withId(R.id.nearByBtn)).perform(click());
        onView(withContentDescription("Google Map")).check(matches(withEffectiveVisibility(VISIBLE)));
    }

    @Test
    public void containerContentChangesCorrectlyTest(){
        Bundle args = new Bundle();
        ArrayList<Ingredient> l = new ArrayList<>(recipe.getIngredientList());
        args.putParcelableArrayList("ingredients", l);
        scenario =FragmentScenario.launchInContainer(FragmentIngredientCheckContainer.class, args);
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
        scenario.close();
    }
}

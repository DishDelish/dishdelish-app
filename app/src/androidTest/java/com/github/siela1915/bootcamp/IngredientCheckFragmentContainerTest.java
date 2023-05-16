package com.github.siela1915.bootcamp;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import androidx.fragment.app.testing.FragmentScenario;
import androidx.test.espresso.assertion.ViewAssertions;

import org.junit.Test;

public class IngredientCheckFragmentContainerTest {
    FragmentScenario<FragmentIngredientCheckContainer> scenario=FragmentScenario.launchInContainer(FragmentIngredientCheckContainer.class);

    @Test
    public void ingredientCheckFirstInteractionTest(){
        onView(withId(R.id.neededIngredientsRV)).check(ViewAssertions.matches(isDisplayed()));
        onView(withId(R.id.addToShoppingListBtn)).perform(click());
        onView(withId(R.id.shoppingCartFragment)).check(ViewAssertions.matches(isDisplayed()));
    }
    @Test
    public void onClickOnNearbyBtnNavigatesToNearbyHelpFragmentTest(){
        onView(withId(R.id.nearByBtn)).perform(click());
        onView(withId(R.id.helpFragConstraintLayout)).check(ViewAssertions.matches(isDisplayed()));
    }
}

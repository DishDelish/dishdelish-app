package com.github.siela1915.bootcamp;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.Visibility.VISIBLE;
import static androidx.test.espresso.matcher.ViewMatchers.isAssignableFrom;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withEffectiveVisibility;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.containsString;

import android.widget.EditText;

import androidx.fragment.app.testing.FragmentScenario;
import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.matcher.ViewMatchers;

import com.google.android.material.chip.Chip;

import org.junit.After;
import org.junit.Test;

public class UploadingRecipeFragmentTest {
    FragmentScenario<UploadingRecipeFragment> scenario;

    @After
    public void cleanUp() {
        scenario.close();
    }

    @Test
    public void initialDisplayTest() {
        scenario = FragmentScenario.launchInContainer(UploadingRecipeFragment.class);

        onView(withId(R.id.uploadRecipeTitle)).check(matches(withEffectiveVisibility(VISIBLE)));
        onView(withId(R.id.recipeName)).check(matches(withEffectiveVisibility(VISIBLE)));
        onView(withId(R.id.prepTime)).check(matches(withEffectiveVisibility(VISIBLE)));
        onView(withId(R.id.cookTime)).check(matches(withEffectiveVisibility(VISIBLE)));
        onView(withId(R.id.servings)).check(matches(withEffectiveVisibility(VISIBLE)));
        onView(withId(R.id.utensils)).check(matches(withEffectiveVisibility(VISIBLE)));
        onView(withId(R.id.ingredientsGroup)).check(matches(withEffectiveVisibility(VISIBLE)));
        onView(withId(R.id.ingredientsAmount)).check(matches(withEffectiveVisibility(VISIBLE)));
        onView(withId(R.id.ingredientsUnit)).check(matches(withEffectiveVisibility(VISIBLE)));
        onView(withId(R.id.ingredientsName)).check(matches(withEffectiveVisibility(VISIBLE)));
        onView(withId(R.id.addIngredient)).check(matches(withEffectiveVisibility(VISIBLE)));
        onView(withId(R.id.stepGroup)).check(matches(withEffectiveVisibility(VISIBLE)));
        onView(withId(R.id.step)).check(matches(withEffectiveVisibility(VISIBLE)));
        onView(withId(R.id.addStep)).check(matches(withEffectiveVisibility(VISIBLE)));
        onView(withId(R.id.chooseImg)).check(matches(withEffectiveVisibility(VISIBLE)));
        onView(withId(R.id.imgView)).check(matches(withEffectiveVisibility(VISIBLE)));
        onView(withId(R.id.uploadButton)).check(matches(withEffectiveVisibility(VISIBLE)));
    }

    @Test
    public void editRecipeNameTest() {
        scenario = FragmentScenario.launchInContainer(UploadingRecipeFragment.class);

        onView(withId(R.id.recipeName)).perform(ViewActions.replaceText("recipe-test"));

        onView(withId(R.id.recipeName)).check(matches(ViewMatchers.withText("recipe-test")));
    }

    @Test
    public void editRecipePrepTimeTest() {
        scenario = FragmentScenario.launchInContainer(UploadingRecipeFragment.class);

        onView(withId(R.id.prepTime)).perform(ViewActions.replaceText("10"));

        onView(withId(R.id.prepTime)).check(matches(ViewMatchers.withText("10")));
    }

    @Test
    public void editRecipeCookTimeTest() {
        scenario = FragmentScenario.launchInContainer(UploadingRecipeFragment.class);

        onView(withId(R.id.cookTime)).perform(ViewActions.replaceText("10"));

        onView(withId(R.id.cookTime)).check(matches(ViewMatchers.withText("10")));
    }

    @Test
    public void editRecipeServingsTest() {
        scenario = FragmentScenario.launchInContainer(UploadingRecipeFragment.class);

        onView(withId(R.id.servings)).perform(ViewActions.replaceText("10"));

        onView(withId(R.id.servings)).check(matches(ViewMatchers.withText("10")));
    }

    @Test
    public void editRecipeIngredientTest() {
        scenario = FragmentScenario.launchInContainer(UploadingRecipeFragment.class);

        onView(withId(R.id.ingredientsAmount)).perform(ViewActions.replaceText("10"));
        onView(withId(R.id.ingredientsName)).perform(ViewActions.replaceText("ingredient-name-test"));
        onView(withId(R.id.ingredientsUnit)).perform(ViewActions.replaceText("ingredient-unit-test"));

        onView(withId(R.id.ingredientsAmount)).check(matches(ViewMatchers.withText("10")));
        onView(withId(R.id.ingredientsName)).check(matches(ViewMatchers.withText("ingredient-name-test")));
        onView(withId(R.id.ingredientsUnit)).check(matches(ViewMatchers.withText("ingredient-unit-test")));
    }

    @Test
    public void addRecipeIngredientTest() {
        scenario = FragmentScenario.launchInContainer(UploadingRecipeFragment.class);

        String amountTest = "10";
        String nameTest = "ingredient-name-test";
        String unitTest = "ingredient-unit-test";

        onView(withId(R.id.ingredientsAmount)).perform(ViewActions.replaceText(amountTest));
        onView(withId(R.id.ingredientsName)).perform(ViewActions.replaceText(nameTest));
        onView(withId(R.id.ingredientsUnit)).perform(ViewActions.replaceText(unitTest)).perform(ViewActions.closeSoftKeyboard());

        onView(withId(R.id.addIngredient)).perform(ViewActions.click());

        String expectedDisplay = amountTest + " " + unitTest + " " + nameTest;
        onView(allOf(withText(containsString(expectedDisplay)), isAssignableFrom(Chip.class))).check((matches(isDisplayed())));
    }

    @Test
    public void editRecipeUtensilsTest() {
        scenario = FragmentScenario.launchInContainer(UploadingRecipeFragment.class);

        onView(withId(R.id.utensils)).perform(ViewActions.replaceText("utensils-test"));

        onView(withId(R.id.utensils)).check(matches(ViewMatchers.withText("utensils-test")));
    }

    @Test
    public void editRecipeStepTest() {
        scenario = FragmentScenario.launchInContainer(UploadingRecipeFragment.class);

        onView(withId(R.id.step)).perform(ViewActions.replaceText("step-test"));

        onView(withId(R.id.step)).check(matches(ViewMatchers.withText("step-test")));
    }

    @Test
    public void addRecipeStepTest() {
        scenario = FragmentScenario.launchInContainer(UploadingRecipeFragment.class);

        onView(withId(R.id.step)).perform(ViewActions.replaceText("step-test"));

        onView(withId(R.id.addStep)).perform(ViewActions.click());

        onView(allOf(withText(containsString("step-test")), isAssignableFrom(EditText.class))).check((matches(isDisplayed())));
    }
}
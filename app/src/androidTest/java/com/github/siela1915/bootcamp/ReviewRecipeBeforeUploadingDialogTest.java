package com.github.siela1915.bootcamp;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.RootMatchers.isDialog;
import static androidx.test.espresso.matcher.ViewMatchers.Visibility.VISIBLE;
import static androidx.test.espresso.matcher.ViewMatchers.withEffectiveVisibility;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

import android.os.Bundle;

import androidx.fragment.app.testing.FragmentScenario;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.github.siela1915.bootcamp.Labelling.AllergyType;
import com.github.siela1915.bootcamp.Labelling.CuisineType;
import com.github.siela1915.bootcamp.Labelling.DietType;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class ReviewRecipeBeforeUploadingDialogTest {
    FragmentScenario<ReviewRecipeBeforeUploadingDialog> scenario;

    private Bundle createMockRecipeBundle() {
        Bundle bundle = new Bundle();

        bundle.putString("recipeName", "test-recipe");
        bundle.putString("image", null);
        bundle.putString("cookTime", "10");
        bundle.putString("prepTime", "10");
        bundle.putString("servings", "10");
        bundle.putString("utensils", "test-utensils");
        bundle.putString("ingredientList", "test-ingredients");
        bundle.putString("cuisineTypes", CuisineType.AMERICAN.toString());
        bundle.putString("allergyTypes", AllergyType.EGGS.toString());
        bundle.putString("dietTypes", DietType.DAIRY.toString());
        bundle.putString("steps", "test-steps");

        return bundle;
    }

    @Before
    public void launchFragment() {
        scenario = FragmentScenario.launch(ReviewRecipeBeforeUploadingDialog.class, createMockRecipeBundle());
    }

    @After
    public void cleanUp() {
        scenario.close();
    }

    @Test
    public void displayTest() {
        onView(withId(R.id.reviewRecipeTitle)).inRoot(isDialog()).check(matches(withEffectiveVisibility(VISIBLE)));
        onView(withId(R.id.reviewRecipeTitle)).check(matches(ViewMatchers.withText("Review Recipe")));
        onView(withId(R.id.reviewRecipeNameContainer)).check(matches(withEffectiveVisibility(VISIBLE)));
        onView(withId(R.id.reviewRecipeName)).check(matches(ViewMatchers.withText("test-recipe")));
        onView(withId(R.id.reviewRecipeCookTimeContainer)).check(matches(withEffectiveVisibility(VISIBLE)));
        onView(withId(R.id.reviewRecipeCookTime)).check(matches(ViewMatchers.withText("10")));
        onView(withId(R.id.reviewRecipePrepTimeContainer)).check(matches(withEffectiveVisibility(VISIBLE)));
        onView(withId(R.id.reviewRecipePrepTime)).check(matches(ViewMatchers.withText("10")));
        onView(withId(R.id.reviewRecipeServingsContainer)).check(matches(withEffectiveVisibility(VISIBLE)));
        onView(withId(R.id.reviewRecipeServings)).check(matches(ViewMatchers.withText("10")));
        onView(withId(R.id.reviewRecipeUtensilsContainer)).check(matches(withEffectiveVisibility(VISIBLE)));
        onView(withId(R.id.reviewRecipeUtensils)).check(matches(ViewMatchers.withText("test-utensils")));
        onView(withId(R.id.reviewRecipeIngredientsContainer)).check(matches(withEffectiveVisibility(VISIBLE)));
        onView(withId(R.id.reviewRecipeIngredients)).check(matches(ViewMatchers.withText("test-ingredients")));
        onView(withId(R.id.reviewRecipeCuisineTypesContainer)).check(matches(withEffectiveVisibility(VISIBLE)));
        onView(withId(R.id.reviewRecipeCuisineTypes)).check(matches(ViewMatchers.withText(CuisineType.AMERICAN.toString())));
        onView(withId(R.id.reviewRecipeAllergyTypesContainer)).check(matches(withEffectiveVisibility(VISIBLE)));
        onView(withId(R.id.reviewRecipeAllergyTypes)).check(matches(ViewMatchers.withText(AllergyType.EGGS.toString())));
        onView(withId(R.id.reviewRecipeDietTypesContainer)).check(matches(withEffectiveVisibility(VISIBLE)));
        onView(withId(R.id.reviewRecipeDietTypes)).check(matches(ViewMatchers.withText(DietType.DAIRY.toString())));
        onView(withId(R.id.reviewRecipeStepsContainer)).check(matches(withEffectiveVisibility(VISIBLE)));
        onView(withId(R.id.reviewRecipeSteps)).check(matches(ViewMatchers.withText("test-steps")));
        onView(withId(R.id.reviewRecipeImageContainer)).check(matches(withEffectiveVisibility(VISIBLE)));
    }
}

package com.github.siela1915.bootcamp;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.Visibility.VISIBLE;
import static androidx.test.espresso.matcher.ViewMatchers.isDescendantOfA;
import static androidx.test.espresso.matcher.ViewMatchers.withClassName;
import static androidx.test.espresso.matcher.ViewMatchers.withEffectiveVisibility;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.platform.app.InstrumentationRegistry.getInstrumentation;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.endsWith;
import static org.junit.Assert.assertTrue;

import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.testing.FragmentScenario;
import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.matcher.BoundedMatcher;
import androidx.test.espresso.matcher.ViewMatchers;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.junit.After;
import org.junit.Test;

public class UploadingRecipeFragmentTest {
    FragmentScenario<UploadingRecipeFragment> scenario;

    // create a matcher to check the number of children elements which have the same id
    public static Matcher<View> withChildViewCount(final int count, final Matcher<View> childMatcher) {
        return new BoundedMatcher<View, ViewGroup>(ViewGroup.class) {
            @Override
            protected boolean matchesSafely(ViewGroup viewGroup) {
                int matchCount = 0;
                for (int i = 0; i < viewGroup.getChildCount(); i++) {
                    if (childMatcher.matches(viewGroup.getChildAt(i))) {
                        matchCount++;
                    }
                }

                return matchCount == count;
            }

            @Override
            public void describeTo(Description description) {
                description.appendText("ViewGroup with child-count=" + count + " and");
                childMatcher.describeTo(description);
            }
        };
    }

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
        onView(withId(R.id.addIngredientButton)).check(matches(withEffectiveVisibility(VISIBLE)));
        onView(withId(R.id.stepGroup)).check(matches(withEffectiveVisibility(VISIBLE)));
        onView(withId(R.id.step)).check(matches(withEffectiveVisibility(VISIBLE)));
        onView(withId(R.id.addStepButton)).check(matches(withEffectiveVisibility(VISIBLE)));
        onView(withId(R.id.recipeImage)).check(matches(withEffectiveVisibility(VISIBLE)));
        onView(withId(R.id.recipeUploadButton)).check(matches(withEffectiveVisibility(VISIBLE)));
    }

    @Test
    public void editRecipeNameTest() {
        scenario = FragmentScenario.launchInContainer(UploadingRecipeFragment.class);

        onView(allOf(
                isDescendantOfA(withId(R.id.recipeNameContent)),
                withClassName(endsWith("EditText"))
        )).perform(typeText("recipe-test"));

        onView(allOf(
                isDescendantOfA(withId(R.id.recipeNameContent)),
                withClassName(endsWith("EditText"))
        )).check(matches(ViewMatchers.withText("recipe-test")));
    }

    @Test
    public void editRecipePrepTimeTest() {
        scenario = FragmentScenario.launchInContainer(UploadingRecipeFragment.class);

        onView(allOf(
                isDescendantOfA(withId(R.id.prepTimeContent)),
                withClassName(endsWith("EditText"))
        )).perform(typeText("10"));

        onView(allOf(
                isDescendantOfA(withId(R.id.prepTimeContent)),
                withClassName(endsWith("EditText"))
        )).check(matches(ViewMatchers.withText("10")));
    }

    @Test
    public void editRecipeCookTimeTest() {
        scenario = FragmentScenario.launchInContainer(UploadingRecipeFragment.class);

        onView(allOf(
                isDescendantOfA(withId(R.id.cookTimeContent)),
                withClassName(endsWith("EditText"))
        )).perform(typeText("10"));

        onView(allOf(
                isDescendantOfA(withId(R.id.cookTimeContent)),
                withClassName(endsWith("EditText"))
        )).check(matches(ViewMatchers.withText("10")));
    }

    @Test
    public void editRecipeServingsTest() {
        scenario = FragmentScenario.launchInContainer(UploadingRecipeFragment.class);

        onView(allOf(
                isDescendantOfA(withId(R.id.servingsContent)),
                withClassName(endsWith("EditText"))
        )).perform(typeText("10"));

        onView(allOf(
                isDescendantOfA(withId(R.id.servingsContent)),
                withClassName(endsWith("EditText"))
        )).check(matches(ViewMatchers.withText("10")));
    }

    @Test
    public void editRecipeIngredientTest() {
        scenario = FragmentScenario.launchInContainer(UploadingRecipeFragment.class);

        onView(allOf(
                isDescendantOfA(withId(R.id.ingredientsAmount)),
                withClassName(endsWith("EditText"))
        )).perform(ViewActions.scrollTo(), typeText("10"));
        onView(allOf(
                isDescendantOfA(withId(R.id.ingredientsName)),
                withClassName(endsWith("EditText"))
        )).perform(typeText("ingredient-name-test"));
        onView(allOf(
                isDescendantOfA(withId(R.id.ingredientsUnit)),
                withClassName(endsWith("EditText"))
        )).perform(typeText("ingredient-unit-test"));


        onView(allOf(
                isDescendantOfA(withId(R.id.ingredientsAmount)),
                withClassName(endsWith("EditText"))
        )).check(matches(ViewMatchers.withText("10")));
        onView(allOf(
                isDescendantOfA(withId(R.id.ingredientsName)),
                withClassName(endsWith("EditText"))
        )).check(matches(ViewMatchers.withText("ingredient-name-test")));
        onView(allOf(
                isDescendantOfA(withId(R.id.ingredientsUnit)),
                withClassName(endsWith("EditText"))
        )).check(matches(ViewMatchers.withText("ingredient-unit-test")));
    }

    @Test
    public void addRecipeIngredientTest() {
        scenario = FragmentScenario.launchInContainer(UploadingRecipeFragment.class);

        onView(withId(R.id.addIngredientButton)).perform(ViewActions.scrollTo(), ViewActions.click());

        onView(withId(R.id.ingredientsGroup)).check((matches(withChildViewCount(2, withId(R.id.ingredients)))));
    }

    @Test
    public void deleteRecipeIngredientTest() {
        scenario = FragmentScenario.launchInContainer(UploadingRecipeFragment.class);

        onView(withId(R.id.addIngredientButton)).perform(ViewActions.scrollTo(), ViewActions.click());
        onView(withId(R.id.removeIngredient)).perform(ViewActions.scrollTo(), ViewActions.click());

        onView(withId(R.id.ingredientsGroup)).check((matches(withChildViewCount(1, withId(R.id.ingredients)))));
    }

    @Test
    public void editRecipeUtensilsTest() {
        scenario = FragmentScenario.launchInContainer(UploadingRecipeFragment.class);

        onView(allOf(
                isDescendantOfA(withId(R.id.utensilsContent)),
                withClassName(endsWith("EditText"))
        )).perform(ViewActions.scrollTo(), typeText("utensils-test"));

        onView(allOf(
                isDescendantOfA(withId(R.id.utensilsContent)),
                withClassName(endsWith("EditText"))
        )).check(matches(ViewMatchers.withText("utensils-test")));
    }

    @Test
    public void editRecipeStepTest() {
        scenario = FragmentScenario.launchInContainer(UploadingRecipeFragment.class);

        onView(allOf(
                isDescendantOfA(withId(R.id.stepContent)),
                withClassName(endsWith("EditText"))
        )).perform(ViewActions.scrollTo(), typeText("step-test"));

        onView(allOf(
                isDescendantOfA(withId(R.id.stepContent)),
                withClassName(endsWith("EditText"))
        )).check(matches(ViewMatchers.withText("step-test")));
    }

    @Test
    public void addRecipeStepTest() {
        scenario = FragmentScenario.launchInContainer(UploadingRecipeFragment.class);

        onView(withId(R.id.addStepButton)).perform(ViewActions.scrollTo(), ViewActions.click());

        onView(withId(R.id.stepGroup)).check((matches(withChildViewCount(2, withId(R.id.step)))));
    }

    @Test
    public void deleteRecipeStepTest() {
        scenario = FragmentScenario.launchInContainer(UploadingRecipeFragment.class);

        onView(withId(R.id.addStepButton)).perform(ViewActions.scrollTo(), ViewActions.click());
        onView(withId(R.id.removeStep)).perform(ViewActions.scrollTo(), ViewActions.click());

        onView(withId(R.id.stepGroup)).check((matches(withChildViewCount(1, withId(R.id.step)))));
    }

    @Test
    public void openReviewDialog() {
        scenario = FragmentScenario.launchInContainer(UploadingRecipeFragment.class);

        // simulate user input
        onView(allOf(
                isDescendantOfA(withId(R.id.recipeNameContent)),
                withClassName(endsWith("EditText"))
        )).perform(ViewActions.scrollTo(), typeText("recipe-test"));
        onView(allOf(
                isDescendantOfA(withId(R.id.prepTimeContent)),
                withClassName(endsWith("EditText"))
        )).perform(ViewActions.scrollTo(), typeText("10"));
        onView(allOf(
                isDescendantOfA(withId(R.id.cookTimeContent)),
                withClassName(endsWith("EditText"))
        )).perform(ViewActions.scrollTo(), typeText("10"));
        onView(allOf(
                isDescendantOfA(withId(R.id.servingsContent)),
                withClassName(endsWith("EditText"))
        )).perform(ViewActions.scrollTo(), typeText("10"));
        onView(allOf(
                isDescendantOfA(withId(R.id.ingredientsAmount)),
                withClassName(endsWith("EditText"))
        )).perform(ViewActions.scrollTo(), typeText("10"));
        onView(allOf(
                isDescendantOfA(withId(R.id.ingredientsName)),
                withClassName(endsWith("EditText"))
        )).perform(ViewActions.scrollTo(), typeText("ingredient-name-test"));
        onView(allOf(
                isDescendantOfA(withId(R.id.ingredientsUnit)),
                withClassName(endsWith("EditText"))
        )).perform(ViewActions.scrollTo(), typeText("ingredient-unit-test"));
        onView(allOf(
                isDescendantOfA(withId(R.id.utensilsContent)),
                withClassName(endsWith("EditText"))
        )).perform(ViewActions.scrollTo(), typeText("utensils-test"));
        onView(allOf(
                isDescendantOfA(withId(R.id.stepContent)),
                withClassName(endsWith("EditText"))
        )).perform(ViewActions.scrollTo(), typeText("step-test"));

        onView(withId(R.id.recipeUploadButton)).perform(ViewActions.scrollTo(), ViewActions.click());

        getInstrumentation().waitForIdleSync();
        scenario.onFragment(fragment -> {
            Fragment dialog = fragment.getActivity().getSupportFragmentManager().findFragmentByTag("review_recipe_dialog");
            assertTrue(dialog instanceof DialogFragment);
            assertTrue(((DialogFragment) dialog).getShowsDialog());
        });
    }
}
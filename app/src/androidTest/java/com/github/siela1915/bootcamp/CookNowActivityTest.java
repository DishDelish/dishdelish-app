package com.github.siela1915.bootcamp;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.test.core.app.ActivityScenario;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.espresso.Espresso;
import static androidx.test.espresso.Espresso.onView;

import androidx.test.espresso.ViewInteraction;
import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.matcher.BoundedMatcher;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.testing.FragmentScenario;
import androidx.test.espresso.matcher.ViewMatchers;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.junit.Before;
import org.junit.Test;

import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.scrollTo;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.Visibility.VISIBLE;
import static androidx.test.espresso.matcher.ViewMatchers.isCompletelyDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withEffectiveVisibility;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withParent;
import static androidx.test.espresso.matcher.ViewMatchers.withParentIndex;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import static net.bytebuddy.matcher.ElementMatchers.is;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.anyOf;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.instanceOf;
import static org.junit.Assert.assertTrue;

import com.github.siela1915.bootcamp.Recipes.ExampleRecipes;
import com.github.siela1915.bootcamp.Recipes.Recipe;

public class CookNowActivityTest {
    private Recipe recipe = ExampleRecipes.recipes.get(0);

    private String getExpectedStringFromStep(String step){
        return "\u25CF " + step.replaceAll("\n", "\n\u25CF ");
    }
    @Before
    public void setUp() {
        // Launch the activity
        ActivityScenario.launch(CookNowActivity.class);

        Intent intent = new Intent(ApplicationProvider.getApplicationContext(), CookNowActivity.class);
        intent.putExtra("recipe", recipe);
        ActivityScenario scenario = ActivityScenario.launch(intent);
    }


    @Test
    public void viewPagerSwipeShouldNavigateToNextStepFragment() throws InterruptedException {
        // Click the next button on the ViewPager2

        onView(withId(R.id.btnForward))
                .perform(click());


        //testing swiping and content of the fragments
        onView(withId(R.id.cookNowStepContent))
                .check(matches(withText(getExpectedStringFromStep(recipe.steps.get(0)))));
        onView(withId(R.id.btnForward))
                .perform(click());



        onView(withId(R.id.cookNowStepContent))
                .check(matches(withText(getExpectedStringFromStep(recipe.steps.get(1)))));
        onView(withId(R.id.btnBackward))
                .perform(click());


        onView(withId(R.id.cookNowStepContent))
                .check(matches(withText(getExpectedStringFromStep(recipe.steps.get(0)))));
    }

    public static Matcher<View> withPositionInParent(int parentViewId, int position) {
        Matcher<View> parentMatcher = ViewMatchers.withId(parentViewId);

        return allOf(withParent(parentMatcher), withParentIndex(position));
    }


    private final int TIMEOUT_MILLISECONDS = 1000;
    private final int SLEEP_MILLISECONDS = 100;
    private int time = 0;
    private boolean wasDisplayed = false;

    public Boolean isVisible(ViewInteraction interaction) throws InterruptedException {
        interaction.withFailureHandler((error, viewMatcher) -> wasDisplayed = false);
        if (wasDisplayed) {
            time = 0;
            wasDisplayed = false;
            return true;
        }
        if (time >= TIMEOUT_MILLISECONDS) {
            time = 0;
            wasDisplayed = false;
            return false;
        }

        //set it to true if failing handle should set it to false again.
        wasDisplayed = true;
        Thread.sleep(SLEEP_MILLISECONDS);
        time += SLEEP_MILLISECONDS;

        interaction.check(matches(isDisplayed()));
        return isVisible(interaction);
    }

    @Test
    public void displayTest(){

        onView(withId(R.id.container_step))
                .check(matches(withEffectiveVisibility(VISIBLE)));
        onView(withId(R.id.container_timer))
                .check(matches(withEffectiveVisibility(VISIBLE)));

    }

}


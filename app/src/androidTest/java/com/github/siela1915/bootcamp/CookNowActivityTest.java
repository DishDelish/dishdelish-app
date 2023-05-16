package com.github.siela1915.bootcamp;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.test.core.app.ActivityScenario;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.espresso.Espresso;
import static androidx.test.espresso.Espresso.onView;
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
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.Visibility.VISIBLE;
import static androidx.test.espresso.matcher.ViewMatchers.withEffectiveVisibility;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withParent;
import static androidx.test.espresso.matcher.ViewMatchers.withParentIndex;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.instanceOf;

import com.github.siela1915.bootcamp.Recipes.ExampleRecipes;
import com.github.siela1915.bootcamp.Recipes.Recipe;

public class CookNowActivityTest {

    // create a matcher to check the number of children elements which have the same id

    public static Matcher<View> withCount(final int expectedCount, final int viewId) {
        return new BoundedMatcher<View, View>(View.class) {
            int count = 0;

            @Override
            protected boolean matchesSafely(View item) {
                count = item.getRootView().findViewById(viewId).getTag() == null ? 0 : (int) item.getRootView().findViewById(viewId).getTag();
                return count == expectedCount;
            }

            @Override
            public void describeTo(Description description) {
                description.appendText("Expected count: " + expectedCount + ", Actual count: " + count);
            }
        };
    }


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
        onView(withId(R.id.container_step))
                .perform(ViewActions.swipeLeft());
        Thread.sleep(100);

        //testing swiping and content of the fragments
        onView(withId(R.id.cookNowStepContent))
                .check(matches(withText(getExpectedStringFromStep(recipe.steps.get(0)))));

        onView(withId(R.id.container_step))
                .perform(ViewActions.swipeLeft());
        Thread.sleep(100);
        onView(withId(R.id.cookNowStepContent))
                .check(matches(withText(getExpectedStringFromStep(recipe.steps.get(1)))));

        onView(withId(R.id.container_step))
                .perform(ViewActions.swipeRight());
        Thread.sleep(100);
        onView(withId(R.id.cookNowStepContent))
                .check(matches(withText(getExpectedStringFromStep(recipe.steps.get(0)))));
    }

    public static Matcher<View> withPositionInParent(int parentViewId, int position) {
        Matcher<View> parentMatcher = ViewMatchers.withId(parentViewId);

        return allOf(withParent(parentMatcher), withParentIndex(position));
    }

























//    @Test
//    public void viewPagerBackButtonClick_ShouldNavigateToPreviousStepFragment() {
//        // First navigate to the second step
//        onView(withId(R.id.container_step))
//                .perform(ViewActions.swipeLeft());
//
//        // Click the back button on the ViewPager2
//        onView(withId(R.id.container_step))
//                .perform(ViewActions.swipeRight());
//
//        // Verify that the expected fragment is displayed
//        onView(withId(R.id.container_step))
//                .check(matches(ViewMatchers.hasDescendant(withText("Timer"))));
//    }
//
//    @Test
//    public void viewPagerTimerFragment_ShouldMatchStepFragmentPosition() {
//        // Click the next button on the ViewPager2
//        onView(withId(R.id.container_step))
//                .perform(ViewActions.swipeLeft());
//
//        // Verify that the corresponding timer fragment is displayed
//        onView(withId(R.id.container_timer))
//                .check(matches(ViewMatchers.hasDescendant(withText("Step 1 Timer"))));
//    }

    @Test
    public void displayTest(){
        onView(withId(R.id.container_step))
                .check(matches(instanceOf(ViewPager2.class)));
        onView(withId(R.id.container_timer))
                .check(matches(instanceOf(ViewPager2.class)));
        onView(withId(R.id.container_step))
                .check(matches(withEffectiveVisibility(VISIBLE)));
        onView(withId(R.id.container_timer))
                .check(matches(withEffectiveVisibility(VISIBLE)));

    }

    private CookNowActivity getActivityInstance() {
        // Retrieve the current activity instance
        ActivityScenario<CookNowActivity> scenario = ActivityScenario
                .launch(CookNowActivity.class);
        final CookNowActivity[] activity = new CookNowActivity[1];
        scenario.onActivity(new ActivityScenario.ActivityAction<CookNowActivity>() {
            @Override
            public void perform(CookNowActivity value) {
                activity[0] = value;
            }
        });
        return activity[0];
    }

}


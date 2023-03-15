package com.github.siela1915.bootcamp;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.scrollTo;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static org.hamcrest.Matchers.allOf;

import android.content.Intent;
import android.view.View;
import android.widget.RatingBar;

import androidx.test.core.app.ActivityScenario;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.espresso.UiController;
import androidx.test.espresso.ViewAction;
import androidx.test.espresso.intent.Intents;
import androidx.test.espresso.matcher.ViewMatchers;

import org.hamcrest.Matcher;
import org.junit.Test;

public class RatingActivityTest {

    @Test
    public void hasRatingChangedWhenClickedOnTheBar() {
        Intent intent = new Intent(ApplicationProvider.getApplicationContext(), RatingActivity.class);
        ActivityScenario scenario = ActivityScenario.launch(intent);
        onView(withId(R.id.ratingActivityBar)).perform(clickRating(3.5f))
                .check(matches(RecipeActivityTest.withRating(3.5f)));
        scenario.close();
    }

    @Test
    public void submitButtonFiresANewIntent() {
        Intent intent = new Intent(ApplicationProvider.getApplicationContext(), RatingActivity.class);
        ActivityScenario scenario = ActivityScenario.launch(intent);
        Intents.release();

        Intents.init();
        onView(withId(R.id.submitRatingButton))
                .perform(click());

        intended(allOf(hasComponent(MainActivity.class.getName())));

        Intents.release();
        scenario.close();
    }

    public static ViewAction clickRating(final float rating) {
        return new ViewAction() {
            @Override
            public Matcher<View> getConstraints() {
                return ViewMatchers.isAssignableFrom(RatingBar.class);
            }

            @Override
            public String getDescription() {
                return "Click rating " + rating;
            }

            @Override
            public void perform(UiController uiController, View view) {
                ((RatingBar) view).setRating(rating);
            }
        };
    }

}

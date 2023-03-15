package com.github.siela1915.bootcamp;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.scrollTo;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;

import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.ImageView;
import android.widget.RatingBar;

import androidx.test.core.app.ActivityScenario;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.espresso.intent.Intents;
import androidx.test.espresso.matcher.BoundedMatcher;

import com.github.siela1915.bootcamp.Recipes.ExampleRecipes;
import com.github.siela1915.bootcamp.Recipes.Recipe;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.junit.Test;

public class RecipeActivityTest {

    Recipe omelette = ExampleRecipes.recipes.get(0);
    Intent i = RecipeConverter.convertToIntent(omelette, ApplicationProvider.getApplicationContext());

    @Test
    public void isRecipePictureOnDisplay() {
        ActivityScenario scenario = ActivityScenario.launch(i);
        onView(withId(R.id.recipePicture)).check(matches(isDisplayed()));
        scenario.close();
    }

    @Test
    public void isCorrectRecipeNameOnDisplay() {
        ActivityScenario scenario = ActivityScenario.launch(i);
        onView(withId(R.id.recipeNameText)).check(matches(withText(omelette.recipeName)));
        scenario.close();

    }

    @Test
    public void isCorrectUserNameOnDisplay() {
        ActivityScenario scenario = ActivityScenario.launch(i);
        onView(withId(R.id.userNameText)).check(matches(withText(omelette.userName)));
        scenario.close();
    }

    @Test
    public void isCorrectCookTimeDisplayed() {
        ActivityScenario scenario = ActivityScenario.launch(i);
        onView(withId(R.id.cookTimeNbMins)).check(matches(withText(String.valueOf(omelette.cookTime))));
        scenario.close();
    }

    @Test
    public void isCorrectServingsOnDisplay() {
        ActivityScenario scenario = ActivityScenario.launch(i);
        onView(withId(R.id.nbServings)).check(matches(withText(String.valueOf(omelette.servings))));
        scenario.close();
    }

    @Test
    public void isCorrectIngredientsListOnDisplay() {
        ActivityScenario scenario = ActivityScenario.launch(i);
        onView(withId(R.id.ingredientsList)).check(matches(withText(String.join("\n", RecipeConverter.convertIngredientList(omelette.ingredientList)))));
        scenario.close();
    }

    @Test
    public void isCorrectUtensilsListOnDisplay() {
        ActivityScenario scenario = ActivityScenario.launch(i);
        onView(withId(R.id.utensilsList)).check(matches(withText(String.join(", ", omelette.utensils.getUtensils()))));
        scenario.close();
    }

    @Test
    public void areCorrectsStepsDisplayed() {
        ActivityScenario scenario = ActivityScenario.launch(i);
        onView(withId(R.id.stepsText)).check(matches(withText(String.join("\n\n", omelette.steps))));
        scenario.close();
    }

    @Test
    public void areCorrectCommentsOnDisplay() {
        ActivityScenario scenario = ActivityScenario.launch(i);
        onView(withId(R.id.commentsText)).check(matches(withText(String.join("\n", omelette.comments))));
        scenario.close();
    }

    @Test
    public void isRatingCorrect() {
        ActivityScenario scenario = ActivityScenario.launch(i);
        onView(withId(R.id.ratingBar)).check(matches(withRating((float) omelette.rating)));
        scenario.close();

    }

    @Test
    public void isRatingActivityStarted() {
        ActivityScenario scenario = ActivityScenario.launch(i);
        Intents.release();
        Intents.init();
        onView(withId(R.id.rateButton))
                .perform(scrollTo(), click());

        intended(allOf(hasComponent(RatingActivity.class.getName())));

        Intents.release();
        scenario.close();
    }

    @Test
    public void isCorrectRecipePictureDisplayed() {
        ActivityScenario scenario = ActivityScenario.launch(i);
        onView(withId(R.id.recipePicture))
                .check(matches(withDrawable(omelette.image)));
        scenario.close();
    }

    public static Matcher<View> withRating(final float rating) {
        return new TypeSafeMatcher<View>() {
            @Override
            public void describeTo(org.hamcrest.Description description) {
                description.appendText("with rating: " + rating);
            }

            @Override
            public boolean matchesSafely(View view) {
                if (!(view instanceof RatingBar)) {
                    return false;
                }
                RatingBar ratingBar = (RatingBar) view;
                return ratingBar.getRating() == rating;
            }
        };
    }

    public static Matcher<View> withDrawable(final int expectedResourceId) {
        return new BoundedMatcher<View, ImageView>(ImageView.class) {
            private Bitmap expectedBitmap;
            ;

            @Override
            public void describeTo(Description description) {
                description.appendText("with drawable resource id: ");
                description.appendValue(expectedResourceId);
            }

            @Override
            public boolean matchesSafely(ImageView imageView) {
                Resources resources = imageView.getContext().getResources();
                expectedBitmap = BitmapFactory.decodeResource(resources, expectedResourceId);
                Drawable actualDrawable = imageView.getDrawable();
                if (actualDrawable instanceof BitmapDrawable) {
                    Bitmap actualBitmap = ((BitmapDrawable) actualDrawable).getBitmap();
                    return expectedBitmap.sameAs(actualBitmap);
                } else {
                    return false;
                }
            }
        };
    }

}

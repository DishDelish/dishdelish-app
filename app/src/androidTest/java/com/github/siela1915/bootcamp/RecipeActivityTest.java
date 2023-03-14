package com.github.siela1915.bootcamp;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

import androidx.test.ext.junit.rules.ActivityScenarioRule;

import org.junit.Rule;
import org.junit.Test;

public class RecipeActivityTest {

    @Rule
    public ActivityScenarioRule<RecipeActivity> testRule = new ActivityScenarioRule<>(RecipeActivity.class);

    @Test
    public void isRecipePictureOnDisplay(){
        onView(withId(R.id.recipePicture)).check(matches(isDisplayed()));
    }

    @Test
    public void isRecipeTitleOnDisplay(){
        onView(withId(R.id.recipeNameText)).check(matches(isDisplayed()));
    }

    @Test
    public void isRatingOnDisplay(){
        onView(withId(R.id.ratingBar)).check(matches(isDisplayed()));
    }

    @Test
    public void isUserNameOnDisplay(){
        onView(withId(R.id.userNameText)).check(matches(isDisplayed()));
    }

    

    @Test
    public void isFavoritesButtonOnDisplay(){
        onView(withId(R.id.favoriteImage)).check(matches(isDisplayed()));
    }

    @Test
    public void areInfoBoxesOnDisplay(){
        onView(withId(R.id.infoBoxes)).check(matches(isDisplayed()));
    }


}

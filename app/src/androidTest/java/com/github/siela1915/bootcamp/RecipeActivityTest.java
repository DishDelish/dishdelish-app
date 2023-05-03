package com.github.siela1915.bootcamp;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.scrollTo;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static androidx.test.espresso.matcher.ViewMatchers.assertThat;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.ToggleButton;

import androidx.annotation.DrawableRes;
import androidx.recyclerview.widget.RecyclerView;
import androidx.test.core.app.ActivityScenario;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.espresso.intent.Intents;
import androidx.test.espresso.matcher.BoundedMatcher;

import com.github.siela1915.bootcamp.Recipes.Comment;
import com.github.siela1915.bootcamp.Recipes.ExampleRecipes;
import com.github.siela1915.bootcamp.Recipes.Ingredient;
import com.github.siela1915.bootcamp.Recipes.Recipe;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class RecipeActivityTest {

    Recipe omelette = ExampleRecipes.recipes.get(0);
    Intent i = RecipeConverter.convertToIntent(omelette, ApplicationProvider.getApplicationContext());

    /*
    @Test
    public void isRecipePictureOnDisplay() {
        ActivityScenario scenario = ActivityScenario.launch(i);
        onView(withId(R.id.recipePicture)).check(matches(isDisplayed()));
        scenario.close();
    }

     */

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
    public void areCorrectCommentsOnDisplay() {
        ActivityScenario scenario = ActivityScenario.launch(i);

        scenario.onActivity(activity -> {

            RecyclerView commentsList = activity.findViewById(R.id.commentsList);
            CommentAdapter commentAdapter = (CommentAdapter) commentsList.getAdapter();

            // Iterate through the list and compare each element with the adapter's data set
            for (int i = 0; i < omelette.comments.size(); i++) {
                Comment expectedData = omelette.comments.get(i);
                Comment actualData = commentAdapter.getData().get(i);
                assertEquals(expectedData.getContent(), actualData.getContent());
            }

        });
        scenario.close();
    }

    @Test
    public void commentsListStaysTheSameAfterEmptyStringIsSent() {
        ActivityScenario scenario = ActivityScenario.launch(i);

        onView(withId(R.id.sendCommentButton))
                .perform(scrollTo(), click());

        scenario.onActivity(activity -> {
            RecyclerView commentsList = activity.findViewById(R.id.commentsList);
            CommentAdapter commentAdapter = (CommentAdapter) commentsList.getAdapter();

            for (int i = 0; i < omelette.comments.size(); i++) {
                Comment expectedData = omelette.comments.get(i);
                Comment actualData = commentAdapter.getData().get(i);
                assertEquals(expectedData.getContent(), actualData.getContent());
            }
        });
        scenario.close();


    }



    @Test
    public void commentsListUpdatesAfterNonEmptyStringIsSent() {
        ActivityScenario scenario = ActivityScenario.launch(i);

        String test = "test";

        onView(withId(R.id.enterComment)).perform(scrollTo(), typeText(test));

        onView(withId(R.id.sendCommentButton))
                .perform(scrollTo(), click());

        List<Comment> newCommentsList = new ArrayList<>(omelette.comments);
        newCommentsList.add(new Comment(test));

        scenario.onActivity(activity -> {
            RecyclerView commentsList = activity.findViewById(R.id.commentsList);
            CommentAdapter commentAdapter = (CommentAdapter) commentsList.getAdapter();

            for (int i = 0; i < newCommentsList.size(); i++) {
                Comment expectedData = newCommentsList.get(i);
                Comment actualData = commentAdapter.getData().get(i);
                assertEquals(expectedData.getContent(), actualData.getContent());
            }
        });
        scenario.close();

    }

    @Test
    public void backgroundOfLikeButtonChangesWhenItIsClicked(){

        ActivityScenario scenario = ActivityScenario.launch(i);

        int commentIndex = 0;

        scenario.onActivity(activity -> {
            RecyclerView commentsList = activity.findViewById(R.id.commentsList);
            CommentViewHolder viewHolder = (CommentViewHolder) commentsList.findViewHolderForAdapterPosition(commentIndex);

            // Check that the tag value of the button changes when clicked
            ToggleButton thumb = viewHolder.itemView.findViewById(R.id.thumbButton);
            thumb.performClick();
            String actual = (String) thumb.getTag();
            String expected = "liked";
            assertTrue(actual.equals(expected));

            thumb.performClick();
            actual = (String) thumb.getTag();
            expected = "unliked";
            assertTrue(actual.equals(expected));
        });

        scenario.close();

    }

    @Test
    public void likeCounterIncreasesWhenCommentIsLiked(){
        ActivityScenario scenario = ActivityScenario.launch(i);

        int commentIndex = 0;

        scenario.onActivity(activity -> {
            RecyclerView commentsList = activity.findViewById(R.id.commentsList);
            CommentViewHolder viewHolder = (CommentViewHolder) commentsList.findViewHolderForAdapterPosition(commentIndex);

            // Check that the tag value of the button changes when clicked
            ToggleButton thumb = viewHolder.itemView.findViewById(R.id.thumbButton);

            TextView likeCount = viewHolder.itemView.findViewById(R.id.likeCount);
            int likes = Integer.valueOf(likeCount.getText().toString());

            thumb.performClick();
            int actual = Integer.valueOf(likeCount.getText().toString());
            int expected = likes+1;
            assertThat(actual, is(expected));

            thumb.performClick();
            actual = Integer.valueOf(likeCount.getText().toString());;
            expected = likes;
            assertThat(actual, is(expected));
        });

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
    public void isCorrectIngredientsListOnDisplay() {
        ActivityScenario scenario = ActivityScenario.launch(i);

        scenario.onActivity(activity -> {

            RecyclerView ingredientsList = activity.findViewById(R.id.ingredientsList);
            IngredientAdapter ingredientAdapter = (IngredientAdapter) ingredientsList.getAdapter();

            // Iterate through the list and compare each element with the adapter's data set
            for (int i = 0; i < omelette.getIngredientList().size(); i++) {
                Ingredient expectedData = omelette.getIngredientList().get(i);
                Ingredient actualData = ingredientAdapter.getData().get(i);
                assertEquals(expectedData.getIngredient(), actualData.getIngredient());
                assertEquals(expectedData.getUnit().getInfo(), actualData.getUnit().getInfo());
                assertEquals(expectedData.getUnit().getValue(), actualData.getUnit().getValue());
            }

        });
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

                .check(matches(withDrawable(Integer.parseInt(omelette.image))));

        scenario.close();
    }


    @Test
    public void plusButtonIncreasesNumberOfServings() {
        ActivityScenario scenario = ActivityScenario.launch(i);
        onView(withId(R.id.plusButton))
                .perform( scrollTo(), click());

        onView(withId(R.id.nbServings)).check(matches(withText(String.valueOf(omelette.servings + 1))));
        onView(withId(R.id.servings)).check(matches(withText(String.valueOf(omelette.servings + 1))));

        scenario.close();

    }

    @Test
    public void minusButtonDecreasesNumberOfServings() {

        ActivityScenario scenario = ActivityScenario.launch(i);

        onView(withId(R.id.plusButton))
                .perform(scrollTo(), click());

        onView(withId(R.id.minusButton))
                .perform(scrollTo(), click());

        onView(withId(R.id.nbServings)).check(matches(withText(String.valueOf(omelette.servings))));
        onView(withId(R.id.servings)).check(matches(withText(String.valueOf(omelette.servings))));

        scenario.close();

    }


    @Test
    public void numberOfServingsCannotGoBelowOne() {

        ActivityScenario scenario = ActivityScenario.launch(i);

        for (int i = 0; i < omelette.servings; i++) {
            onView(withId(R.id.minusButton))
                    .perform(scrollTo(), click());
        }

        onView(withId(R.id.nbServings)).check(matches(withText(String.valueOf(1))));
        onView(withId(R.id.servings)).check(matches(withText(String.valueOf(1))));

        scenario.close();

    }

    @Test
    public void ingredientAmountsAreCorrectlyUpdatedAfterPlusButtonIsClicked() {
        ActivityScenario scenario = ActivityScenario.launch(i);
        onView(withId(R.id.plusButton))
                .perform(scrollTo(), click());
        int newServings = omelette.getServings() + 1;

        scenario.onActivity(activity -> {
            RecyclerView ingList = activity.findViewById(R.id.ingredientsList);
            IngredientAdapter ingAdapter = (IngredientAdapter) ingList.getAdapter();
            List<Ingredient> data = ingAdapter.getData();
            for (int i = 0; i < data.size(); i++) {
                Ingredient original = omelette.getIngredientList().get(i);
                int oldValue = original.getUnit().getValue();
                double ratio = ((double) newServings / omelette.getServings());
                int expectedValue = (int) Math.ceil(ratio * oldValue);
                int actualValue = data.get(i).getUnit().getValue();

                assertEquals(expectedValue, actualValue);
            }
        });
        scenario.close();
    }

    @Test
    public void ingredientAmountsAreCorrectlyUpdatedAfterMinusButtonIsClicked() {

        ActivityScenario scenario = ActivityScenario.launch(i);
        onView(withId(R.id.plusButton))
                .perform(scrollTo(), click());
        onView(withId(R.id.minusButton))
                .perform(scrollTo(), click());

        scenario.onActivity(activity -> {
            RecyclerView ingList = activity.findViewById(R.id.ingredientsList);
            IngredientAdapter ingAdapter = (IngredientAdapter) ingList.getAdapter();
            List<Ingredient> data = ingAdapter.getData();

            for (int i = 0; i < data.size(); i++) {
                Ingredient original = omelette.getIngredientList().get(i);
                int oldValue = original.getUnit().getValue();
                int expectedValue = (int) Math.ceil(1 * oldValue);
                int actualValue = data.get(i).getUnit().getValue();

                assertEquals(expectedValue, actualValue);
            }
        });

        scenario.close();
    }

    @Test
    public void heartButtonBecomesFullWhenClicked(){
        ActivityScenario scenario = ActivityScenario.launch(i);

        scenario.onActivity(activity -> {
            // Check that the background drawable has changed to the checked state drawable
            ToggleButton heart = (ToggleButton) activity.findViewById(R.id.favoriteButton);
            heart.performClick();
            String actual = (String) heart.getTag();
            String expected = "full";
            assertTrue(actual.equals(expected));

        });
        scenario.close();
    }


    @Test
    public void heartButtonBecomesEmptyWhenClicked2Times() {
        ActivityScenario scenario = ActivityScenario.launch(i);
        scenario.onActivity(activity -> {
            ToggleButton heart = activity.findViewById(R.id.favoriteButton);

            heart.performClick();
            heart.performClick();

            String actual = (String) heart.getTag();
            String expected = "empty";
            assertTrue(actual.equals(expected));

        });
        scenario.close();
    }

    @Test
    public void addToListButtonChangesStateOnClick() {
        ActivityScenario scenario = ActivityScenario.launch(i);

        int ingredientIndex = 0;

        scenario.onActivity(activity -> {
            RecyclerView ingredientsList = activity.findViewById(R.id.ingredientsList);
            IngredientViewHolder viewHolder = (IngredientViewHolder) ingredientsList.findViewHolderForAdapterPosition(ingredientIndex);

            // Check that the tag value of the button changes when clicked
            ToggleButton addButton = viewHolder.itemView.findViewById(R.id.AddToListButton);
            addButton.performClick();
            String actual = (String) addButton.getTag();
            String expected = "added";
            assertTrue(actual.equals(expected));

            addButton.performClick();
            actual = (String) addButton.getTag();
            expected = "removed";
            assertTrue(actual.equals(expected));
        });

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

    public static Matcher<View> withToggleButtonBackgroundDrawable(final int expectedResourceId) {
        return new BoundedMatcher<View, ToggleButton>(ToggleButton.class) {
            @Override
            public void describeTo(Description description) {
                description.appendText("with toggle button background drawable resource id: ");
                description.appendValue(expectedResourceId);
            }

            @Override
            public boolean matchesSafely(ToggleButton toggleButton) {
                Drawable expectedDrawable = toggleButton.getResources().getDrawable(expectedResourceId);
                Drawable toggleButtonDrawable = toggleButton.getBackground();
                if (expectedDrawable == null && toggleButtonDrawable == null) {
                    return true;
                }
                if (expectedDrawable == null || toggleButtonDrawable == null) {
                    return false;
                }
                return expectedDrawable.getConstantState().equals(toggleButtonDrawable.getConstantState());
            }
        };
    }

    public static Matcher<View> withDrawableId(@DrawableRes final int id) {
        return new DrawableMatcher(id);
    }


    public static class DrawableMatcher extends TypeSafeMatcher<View> {

        private final int expectedId;
        private String resourceName;

        public DrawableMatcher(@DrawableRes int expectedId) {
            super(View.class);
            this.expectedId = expectedId;
        }

        @Override
        protected boolean matchesSafely(View target) {
            if (!(target instanceof ImageView)) {
                return false;
            }
            ImageView imageView = (ImageView) target;
            if (expectedId < 0) {
                return imageView.getDrawable() == null;
            }
            Resources resources = target.getContext().getResources();
            Drawable expectedDrawable = resources.getDrawable(expectedId);
            resourceName = resources.getResourceEntryName(expectedId);
            if (expectedDrawable != null && expectedDrawable.getConstantState() != null) {
                return expectedDrawable.getConstantState().equals(
                        imageView.getDrawable().getConstantState()
                );
            } else {
                return false;
            }
        }

        @Override
        public void describeTo(Description description) {
            description.appendText("with drawable from resource id: ");
            description.appendValue(expectedId);
            if (resourceName != null) {
                description.appendText("[");
                description.appendText(resourceName);
                description.appendText("]");
            }
        }


}
}


package com.github.siela1915.bootcamp;

import static androidx.test.core.app.ApplicationProvider.getApplicationContext;
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

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.VectorDrawable;
import android.os.AsyncTask;
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
import com.github.siela1915.bootcamp.firebase.Database;
import com.github.siela1915.bootcamp.firebase.UserDatabase;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;

public class RecipeActivityTest {

    private static final FirebaseDatabase fb = FirebaseDatabase.getInstance();
    private static final Database database = new Database(fb);

    private static Recipe omelette;

    private static CountDownLatch latch;
    @BeforeClass
    public static void setUpClass() throws InterruptedException {
        latch = new CountDownLatch(1);
        database.getByNameAsync("omelettte1")
                .addOnSuccessListener(list -> {
                    omelette = list.get(0);
                    latch.countDown();
                })
                .addOnFailureListener(e -> {
                    omelette = ExampleRecipes.recipes.get(0);
                    latch.countDown();
                });

        latch.await();
    }

    @Before
    public void prepareEmulator() {
        FirebaseApp.clearInstancesForTest();
        FirebaseApp.initializeApp(getApplicationContext());
        FirebaseAuth.getInstance().useEmulator("10.0.2.2", 9099);
        FirebaseDatabase.getInstance().useEmulator("10.0.2.2", 9000);

        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            FirebaseAuthActivityTest.logoutSync();
        }
    }

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
/*
    @Test
    public void isCorrectRecipePictureDisplayed1() {
        ActivityScenario scenario = ActivityScenario.launch(i);
        onView(withId(R.id.recipePicture))

                .check(matches(withDrawable(Integer.parseInt(omelette.image))));

        scenario.close();
    }

    @Test
    public void testImageUrl() {
        ActivityScenario scenario = ActivityScenario.launch(i);
        onView(withId(R.id.recipePicture))
                .check(matches(withImageUrl(omelette.image)));
        scenario.close();
    }
*/



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
/*
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
*/
    @Test
    public void heartButtonBecomesFullWhenAuthenticated(){

        FirebaseAuthActivityTest.loginSync("eylulipci00@gmail.com");
        ActivityScenario scenario = ActivityScenario.launch(i);
        scenario.onActivity(activity -> {
            // Check that the background drawable has changed to the checked state drawable
            ToggleButton heart = (ToggleButton) activity.findViewById(R.id.favoriteButton);
            heart.performClick();
            System.out.println(heart==null);
            String actual = (String) heart.getTag();
            String expected = "full";
            assertTrue(actual.equals(expected));

        });
        scenario.close();
        FirebaseAuthActivityTest.logoutSync();
    }

    @Test
    public void heartButtonBecomesEmptyWhenTheUserIsAuthenticatedAndTheRecipeIsInFavorites(){

    }

    @Test
    public void heartButtonRemainsEmptyWhenTheUserIsNotAuthenticated(){

    }

    @Test
    public void heartButtonFullWhenTheRecipeIsInFavorites(){

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

    public static Matcher<View> withDrawable(final Drawable expectedDrawable) {
        return new BoundedMatcher<View, ImageView>(ImageView.class) {
            @Override
            public void describeTo(Description description) {
                description.appendText("with drawable: ");
                description.appendValue(expectedDrawable);
            }

            @Override
            public boolean matchesSafely(ImageView imageView) {
                Drawable actualDrawable = imageView.getDrawable();
                if (actualDrawable instanceof BitmapDrawable && expectedDrawable instanceof BitmapDrawable) {
                    Bitmap expectedBitmap = ((BitmapDrawable) expectedDrawable).getBitmap();
                    Bitmap actualBitmap = ((BitmapDrawable) actualDrawable).getBitmap();
                    return expectedBitmap.sameAs(actualBitmap);
                } else {
                    return false;
                }
            }
        };
    }

    public static Matcher<View> withImageUrl(final String expectedUrl) {
        return new BoundedMatcher<View, ImageView>(ImageView.class) {
            @Override
            public void describeTo(Description description) {
                description.appendText("with image URL: ");
                description.appendValue(expectedUrl);
            }

            @Override
            public boolean matchesSafely(ImageView imageView) {
                try {
                    Bitmap expectedBitmap = fetchImage(expectedUrl);
                    Bitmap actualBitmap = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
                    return expectedBitmap.sameAs(actualBitmap);
                } catch (Exception e) {
                    e.printStackTrace();
                    return false;
                }
            }
        };
    }

    public static Bitmap fetchImage(String urlString) throws IOException {
        URL url = new URL(urlString);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setDoInput(true);
        connection.connect();
        InputStream inputStream = connection.getInputStream();
        Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
        inputStream.close();
        return bitmap;
    }



    private static Drawable fetchDrawable(String urlString, Context context) throws IOException {
        InputStream is = (InputStream) new URL(urlString).getContent();
        return Drawable.createFromStream(is, null);
    }









}


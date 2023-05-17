package com.github.siela1915.bootcamp;

import static androidx.test.InstrumentationRegistry.getTargetContext;
import static androidx.test.core.app.ApplicationProvider.getApplicationContext;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.scrollTo;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.Intents.intending;
import static androidx.test.espresso.intent.matcher.IntentMatchers.anyIntent;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static androidx.test.espresso.matcher.ViewMatchers.Visibility.GONE;
import static androidx.test.espresso.matcher.ViewMatchers.Visibility.VISIBLE;
import static androidx.test.espresso.matcher.ViewMatchers.assertThat;
import static androidx.test.espresso.matcher.ViewMatchers.isDescendantOfA;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withEffectiveVisibility;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static androidx.test.platform.app.InstrumentationRegistry.getInstrumentation;
import static com.google.android.gms.common.ConnectionResult.TIMEOUT;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import android.app.Activity;
import android.app.Application;
import android.app.Instrumentation;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.RemoteException;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.ToggleButton;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.Lifecycle;
import androidx.recyclerview.widget.RecyclerView;
import androidx.test.core.app.ActivityScenario;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.espresso.IdlingRegistry;
import androidx.test.espresso.IdlingResource;
import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.idling.CountingIdlingResource;
import androidx.test.espresso.intent.Intents;
import androidx.test.espresso.matcher.BoundedMatcher;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.uiautomator.By;
import androidx.test.uiautomator.UiDevice;
import androidx.test.uiautomator.UiObject;
import androidx.test.uiautomator.UiObjectNotFoundException;
import androidx.test.uiautomator.UiSelector;
import androidx.test.uiautomator.Until;

import com.github.siela1915.bootcamp.Recipes.Comment;
import com.github.siela1915.bootcamp.Recipes.ExampleRecipes;
import com.github.siela1915.bootcamp.Recipes.Ingredient;
import com.github.siela1915.bootcamp.Recipes.Recipe;
import com.github.siela1915.bootcamp.firebase.Database;
import com.github.siela1915.bootcamp.firebase.UserDatabase;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;


@RunWith(AndroidJUnit4.class)
public class RecipeActivityTest {

    private static FirebaseDatabase fb; //= FirebaseDatabase.getInstance();
    private static Database database; //= new Database(fb);

    private static Recipe omelette;// = ExampleRecipes.recipes.get(0);

    private static final long WAITING_TIME_MS = 5000; // Time to wait for the tag change (in milliseconds)
    private IdlingResource idlingResource;
    private static DatabaseIdlingResource databaseIdlingResource;
    private static boolean isDatabaseFetchComplete;
/*
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
    */

    @BeforeClass
    public static void prepareEmulator() throws InterruptedException {
        FirebaseApp.clearInstancesForTest();
        FirebaseApp.initializeApp(getApplicationContext());
        FirebaseAuth.getInstance().useEmulator("10.0.2.2", 9099);
        fb = FirebaseDatabase.getInstance();
        fb.useEmulator("10.0.2.2", 9000);

        database = new Database(fb);


        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            FirebaseAuthActivityTest.logoutSync();
        }

        databaseIdlingResource = new DatabaseIdlingResource();
        IdlingRegistry.getInstance().register(databaseIdlingResource);

        database.getByNameAsync("omelettte1")
                .addOnSuccessListener(list -> {
                    omelette = list.get(0);
                    isDatabaseFetchComplete = true;
                    databaseIdlingResource.setIdle(true); // Signal that the asynchronous operation is complete
                })
                .addOnFailureListener(e -> {
                    omelette = ExampleRecipes.recipes.get(0);
                    isDatabaseFetchComplete = true;
                    databaseIdlingResource.setIdle(true); // Signal that the asynchronous operation is complete
                });

        databaseIdlingResource.setIdle(false); // Set the resource as not idle initially
        isDatabaseFetchComplete = false;

    }
    private void waitForDatabaseFetchCompletion(long timeout, TimeUnit unit) {
        long endTime = System.currentTimeMillis() + unit.toMillis(timeout);
        while (!isDatabaseFetchComplete && System.currentTimeMillis() < endTime) {
            // Wait until the database fetch is complete or until timeout
            // You can also use Thread.sleep() for a small delay to avoid busy waiting
            // if your scenario permits it.
        }

        if (!isDatabaseFetchComplete) {
            fail("Timeout occurred while waiting for the database fetch to complete");
        }
    }

    @AfterClass
    public static void cleanup() {
        IdlingRegistry.getInstance().unregister(databaseIdlingResource);
    }

    @Before
    public void setUp(){
        idlingResource = new TimerIdlingResource(WAITING_TIME_MS);
        IdlingRegistry.getInstance().register(idlingResource);

    }

    @After
    public void tearDown() {
        if(FirebaseAuth.getInstance().getCurrentUser() != null){
            FirebaseAuthActivityTest.logoutSync();
        }
        IdlingRegistry.getInstance().unregister(idlingResource);
    }

    //Intent i = RecipeConverter.convertToIntent(omelette, ApplicationProvider.getApplicationContext());


    @Test
    public void isRecipePictureOnDisplay() {
        waitForDatabaseFetchCompletion(10, TimeUnit.SECONDS);
        Intent i = RecipeConverter.convertToIntent(omelette, ApplicationProvider.getApplicationContext());

        ActivityScenario scenario = ActivityScenario.launch(i);

        onView(withId(R.id.recipePicture)).check(matches(isDisplayed()));
        scenario.close();
    }



    @Test
    public void isCorrectRecipeNameOnDisplay() {
        waitForDatabaseFetchCompletion(300, TimeUnit.SECONDS);
        Intent i = RecipeConverter.convertToIntent(omelette, ApplicationProvider.getApplicationContext());

        ActivityScenario scenario = ActivityScenario.launch(i);


        onView(withId(R.id.recipeNameText)).check(matches(withText(omelette.recipeName)));
        scenario.close();

    }
/*
    @Test
    public void isCorrectUserNameOnDisplay() {
        waitForDatabaseFetchCompletion(300, TimeUnit.SECONDS);
        Intent i = RecipeConverter.convertToIntent(omelette, ApplicationProvider.getApplicationContext());

        ActivityScenario scenario = ActivityScenario.launch(i);
        onView(withId(R.id.userNameText)).check(matches(withText(omelette.userName)));
        scenario.close();
    }
*/
    @Test
    public void isCorrectCookTimeDisplayed() {
        waitForDatabaseFetchCompletion(300, TimeUnit.SECONDS);
        Intent i = RecipeConverter.convertToIntent(omelette, ApplicationProvider.getApplicationContext());

        ActivityScenario scenario = ActivityScenario.launch(i);
        onView(withId(R.id.cookTimeNbMins)).check(matches(withText(String.valueOf(omelette.cookTime))));
        scenario.close();
    }

    @Test
    public void isCorrectServingsOnDisplay() {
        waitForDatabaseFetchCompletion(300, TimeUnit.SECONDS);
        Intent i = RecipeConverter.convertToIntent(omelette, ApplicationProvider.getApplicationContext());

        ActivityScenario scenario = ActivityScenario.launch(i);
        onView(withId(R.id.nbServings)).check(matches(withText(String.valueOf(omelette.servings))));
        scenario.close();
    }

    @Test
    public void areCorrectCommentsOnDisplay() {
        waitForDatabaseFetchCompletion(300, TimeUnit.SECONDS);
        Intent i = RecipeConverter.convertToIntent(omelette, ApplicationProvider.getApplicationContext());

        ActivityScenario scenario = ActivityScenario.launch(i);

        scenario.onActivity(activity -> {

            RecyclerView commentsList = activity.findViewById(R.id.commentsList);
            CommentAdapter commentAdapter = (CommentAdapter) commentsList.getAdapter();

            // Iterate through the list and compare each element with the adapter's data set
            for (int a = 0; a < omelette.comments.size(); a++) {
                Comment expectedData = omelette.comments.get(a);
                Comment actualData = commentAdapter.getData().get(a);
                assertEquals(expectedData.getContent(), actualData.getContent());
            }

        });
        scenario.close();
    }

    @Test
    public void commentsListStaysTheSameAfterEmptyStringIsSent() {
        waitForDatabaseFetchCompletion(300, TimeUnit.SECONDS);
        Intent i = RecipeConverter.convertToIntent(omelette, ApplicationProvider.getApplicationContext());

        ActivityScenario scenario = ActivityScenario.launch(i);
        onView(withId(R.id.sendCommentButton))
                .perform(scrollTo(), click());

        scenario.onActivity(activity -> {
            RecyclerView commentsList = activity.findViewById(R.id.commentsList);
            CommentAdapter commentAdapter = (CommentAdapter) commentsList.getAdapter();

            for (int a = 0; a < omelette.comments.size(); a++) {
                Comment expectedData = omelette.comments.get(a);
                Comment actualData = commentAdapter.getData().get(a);
                assertEquals(expectedData.getContent(), actualData.getContent());
            }
        });
        scenario.close();

    }


    @Test
    public void cannotCommentWhenUnauthenticated(){
        waitForDatabaseFetchCompletion(300, TimeUnit.SECONDS);
        Intent i = RecipeConverter.convertToIntent(omelette, ApplicationProvider.getApplicationContext());

        ActivityScenario scenario = ActivityScenario.launch(i);

        String test = "test";

        onView(withId(R.id.enterComment)).perform(scrollTo(), typeText(test));

        onView(withId(R.id.sendCommentButton))
                .perform(scrollTo(), click());

        scenario.onActivity(activity -> {
            RecyclerView commentsList = activity.findViewById(R.id.commentsList);
            CommentAdapter commentAdapter = (CommentAdapter) commentsList.getAdapter();

            for (int a = 0; a < omelette.comments.size(); a++) {
                Comment expectedData = omelette.comments.get(a);
                Comment actualData = commentAdapter.getData().get(a);
                assertEquals(expectedData.getContent(), actualData.getContent());
            }
        });
        scenario.close();
    }

    @Test
    public void likeButtonRemainsTheSameWhenUnauthenticated(){

        waitForDatabaseFetchCompletion(300, TimeUnit.SECONDS);
        Intent i = RecipeConverter.convertToIntent(omelette, ApplicationProvider.getApplicationContext());

        ActivityScenario scenario = ActivityScenario.launch(i);

        int commentIndex = 0;

        scenario.onActivity(activity -> {
            RecyclerView commentsList = activity.findViewById(R.id.commentsList);
            CommentViewHolder viewHolder = (CommentViewHolder) commentsList.findViewHolderForAdapterPosition(commentIndex);

            // Check that the tag value of the button changes when clicked
            ToggleButton thumb = viewHolder.itemView.findViewById(R.id.thumbButton);
            thumb.performClick();
            String actual = (String) thumb.getTag();
            String expected = "unliked";
            assertTrue(actual.equals(expected));

            thumb.performClick();
            actual = (String) thumb.getTag();
            expected = "unliked";
            assertTrue(actual.equals(expected));
        });

        scenario.close();

    }
    @Test
    public void likeCounterRemainsTheSameWhenUnauthenticated(){
        waitForDatabaseFetchCompletion(300, TimeUnit.SECONDS);
        Intent i = RecipeConverter.convertToIntent(omelette, ApplicationProvider.getApplicationContext());

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
            int expected = likes;
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
        waitForDatabaseFetchCompletion(300, TimeUnit.SECONDS);
        Intent i = RecipeConverter.convertToIntent(omelette, ApplicationProvider.getApplicationContext());

        ActivityScenario scenario = ActivityScenario.launch(i);

        onView(withId(R.id.utensilsList)).check(matches(withText(String.join(", ", omelette.utensils.getUtensils()))));
        scenario.close();
    }

    @Test
    public void areCorrectsStepsDisplayed() {
        waitForDatabaseFetchCompletion(300, TimeUnit.SECONDS);
        Intent i = RecipeConverter.convertToIntent(omelette, ApplicationProvider.getApplicationContext());

        ActivityScenario scenario = ActivityScenario.launch(i);

        onView(withId(R.id.stepsText)).check(matches(withText(String.join("\n\n", omelette.steps))));
        scenario.close();
    }

    @Test
    public void isCorrectIngredientsListOnDisplay() {
        waitForDatabaseFetchCompletion(300, TimeUnit.SECONDS);
        Intent i = RecipeConverter.convertToIntent(omelette, ApplicationProvider.getApplicationContext());

        ActivityScenario scenario = ActivityScenario.launch(i);

        scenario.onActivity(activity -> {

            RecyclerView ingredientsList = activity.findViewById(R.id.ingredientsList);
            IngredientAdapter ingredientAdapter = (IngredientAdapter) ingredientsList.getAdapter();

            // Iterate through the list and compare each element with the adapter's data set
            for (int a = 0; a < omelette.getIngredientList().size(); a++) {
                Ingredient expectedData = omelette.getIngredientList().get(a);
                Ingredient actualData = ingredientAdapter.getData().get(a);
                assertEquals(expectedData.getIngredient(), actualData.getIngredient());
                assertEquals(expectedData.getUnit().getInfo(), actualData.getUnit().getInfo());
                assertEquals(expectedData.getUnit().getValue(), actualData.getUnit().getValue());
            }

        });
        scenario.close();
    }


    @Test
    public void isRatingCorrect() {
        waitForDatabaseFetchCompletion(300, TimeUnit.SECONDS);
        Intent i = RecipeConverter.convertToIntent(omelette, ApplicationProvider.getApplicationContext());

        ActivityScenario scenario = ActivityScenario.launch(i);

        onView(withId(R.id.ratingBar)).check(matches(withRating((float) omelette.rating)));
        scenario.close();

    }

    @Test
    public void isRatingActivityStarted() {
        FirebaseAuthActivityTest.loginSync("example@gmail.com");
        waitForDatabaseFetchCompletion(300, TimeUnit.SECONDS);
        Intent i = RecipeConverter.convertToIntent(omelette, ApplicationProvider.getApplicationContext());

        ActivityScenario scenario = ActivityScenario.launch(i);

        //Intents.release();
        Intents.init();
        Intent intent = new Intent();
        Instrumentation.ActivityResult intentResult = new Instrumentation.ActivityResult(Activity.RESULT_OK,intent);
        intending(anyIntent()).respondWith(intentResult);

        onView(withId(R.id.rateButton))
                .perform(scrollTo(), click());

        intended(allOf(hasComponent(RatingActivity.class.getName())));

        Intents.release();
        scenario.close();
        FirebaseAuthActivityTest.logoutSync();
    }



    @Test
    public void plusButtonIncreasesNumberOfServings() {
        waitForDatabaseFetchCompletion(300, TimeUnit.SECONDS);
        Intent i = RecipeConverter.convertToIntent(omelette, ApplicationProvider.getApplicationContext());

        ActivityScenario scenario = ActivityScenario.launch(i);

        onView(withId(R.id.plusButton))
                .perform( scrollTo(), click());

        onView(withId(R.id.nbServings)).check(matches(withText(String.valueOf(omelette.servings + 1))));
        onView(withId(R.id.servings)).check(matches(withText(String.valueOf(omelette.servings + 1))));

        scenario.close();

    }

    @Test
    public void minusButtonDecreasesNumberOfServings() {

        waitForDatabaseFetchCompletion(300, TimeUnit.SECONDS);
        Intent i = RecipeConverter.convertToIntent(omelette, ApplicationProvider.getApplicationContext());

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

        waitForDatabaseFetchCompletion(300, TimeUnit.SECONDS);
        Intent i = RecipeConverter.convertToIntent(omelette, ApplicationProvider.getApplicationContext());

        ActivityScenario scenario = ActivityScenario.launch(i);

        for (int a = 0; a < omelette.servings; a++) {
            onView(withId(R.id.minusButton))
                    .perform(scrollTo(), click());
        }

        onView(withId(R.id.nbServings)).check(matches(withText(String.valueOf(1))));
        onView(withId(R.id.servings)).check(matches(withText(String.valueOf(1))));

        scenario.close();

    }

    @Test
    public void ingredientAmountsAreCorrectlyUpdatedAfterPlusButtonIsClicked() {
        waitForDatabaseFetchCompletion(300, TimeUnit.SECONDS);
        Intent i = RecipeConverter.convertToIntent(omelette, ApplicationProvider.getApplicationContext());

        ActivityScenario scenario = ActivityScenario.launch(i);

        onView(withId(R.id.plusButton))
                .perform(scrollTo(), click());
        int newServings = omelette.getServings() + 1;

        scenario.onActivity(activity -> {
            RecyclerView ingList = activity.findViewById(R.id.ingredientsList);
            IngredientAdapter ingAdapter = (IngredientAdapter) ingList.getAdapter();
            List<Ingredient> data = ingAdapter.getData();
            for (int a = 0; a < data.size(); a++) {
                Ingredient original = omelette.getIngredientList().get(a);
                int oldValue = original.getUnit().getValue();
                double ratio = ((double) newServings / omelette.getServings());
                int expectedValue = (int) Math.ceil(ratio * oldValue);
                int actualValue = data.get(a).getUnit().getValue();

                assertEquals(expectedValue, actualValue);
            }
        });
        scenario.close();
    }

    @Test
    public void ingredientAmountsAreCorrectlyUpdatedAfterMinusButtonIsClicked() {

        waitForDatabaseFetchCompletion(300, TimeUnit.SECONDS);
        Intent i = RecipeConverter.convertToIntent(omelette, ApplicationProvider.getApplicationContext());

        ActivityScenario scenario = ActivityScenario.launch(i);

        onView(withId(R.id.plusButton))
                .perform(scrollTo(), click());
        onView(withId(R.id.minusButton))
                .perform(scrollTo(), click());

        scenario.onActivity(activity -> {
            RecyclerView ingList = activity.findViewById(R.id.ingredientsList);
            IngredientAdapter ingAdapter = (IngredientAdapter) ingList.getAdapter();
            List<Ingredient> data = ingAdapter.getData();

            for (int a = 0; a < data.size(); a++) {
                Ingredient original = omelette.getIngredientList().get(a);
                int oldValue = original.getUnit().getValue();
                int expectedValue = (int) Math.ceil(1 * oldValue);
                int actualValue = data.get(a).getUnit().getValue();

                assertEquals(expectedValue, actualValue);
            }
        });

        scenario.close();
    }

    @Test
    public void heartButtonStillEmptyWhenUnauthenticated(){

        //FirebaseAuthActivityTest.loginSync("example@gmail.com");
        waitForDatabaseFetchCompletion(300, TimeUnit.SECONDS);
        Intent i = RecipeConverter.convertToIntent(omelette, ApplicationProvider.getApplicationContext());

        ActivityScenario scenario = ActivityScenario.launch(i);

        scenario.onActivity(activity -> {
            // Check that the background drawable has changed to the checked state drawable
            ToggleButton heart = (ToggleButton) activity.findViewById(R.id.favoriteButton);
            heart.performClick();

            String actual = (String) heart.getTag();
            String expected = "empty";
            assertTrue(actual.equals(expected));

        });
        scenario.close();
    }


    @Test
    public void heartButtonBecomesEmptyWhenClicked2Times() {
        waitForDatabaseFetchCompletion(300, TimeUnit.SECONDS);
        Intent i = RecipeConverter.convertToIntent(omelette, ApplicationProvider.getApplicationContext());

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
        waitForDatabaseFetchCompletion(300, TimeUnit.SECONDS);
        Intent i = RecipeConverter.convertToIntent(omelette, ApplicationProvider.getApplicationContext());

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

    @Test
    public void openNutritionalValuesCollapse() {
        Intent i = RecipeConverter.convertToIntent(ExampleRecipes.recipes.get(0), ApplicationProvider.getApplicationContext());

        ActivityScenario scenario = ActivityScenario.launch(i);

        onView(withId(R.id.nutritionalCollapseToggle)).perform(ViewActions.scrollTo(), click());

        onView(withId(R.id.card_group)).check(matches(withEffectiveVisibility(VISIBLE)));

        scenario.close();
    }

    @Test
    public void closeNutritionalValuesCollapse() {
        Intent i = RecipeConverter.convertToIntent(ExampleRecipes.recipes.get(0), ApplicationProvider.getApplicationContext());

        ActivityScenario scenario = ActivityScenario.launch(i);

        onView(withId(R.id.nutritionalCollapseToggle)).perform(ViewActions.scrollTo(), click());
        onView(withId(R.id.nutritionalCollapseToggle)).perform(ViewActions.scrollTo(), click());

        onView(withId(R.id.card_group)).check(matches(withEffectiveVisibility(GONE)));

        scenario.close();
    }


    @Test
    public void replyUnauthenticated() throws InterruptedException {

        waitForDatabaseFetchCompletion(300, TimeUnit.SECONDS);
        Intent i = RecipeConverter.convertToIntent(omelette, ApplicationProvider.getApplicationContext());

        ActivityScenario scenario = ActivityScenario.launch(i);

        int commentIndex = 0;

        scenario.onActivity(activity -> {
            RecyclerView commentsList = activity.findViewById(R.id.commentsList);
            CommentViewHolder viewHolder = (CommentViewHolder) commentsList.findViewHolderForAdapterPosition(commentIndex);

            // Check that the tag value of the button changes when clicked
            Button reply = viewHolder.itemView.findViewById(R.id.replyButton);
            reply.performClick();

            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        });

        String test = "test";

        onView(allOf(withId(R.id.enterReply), withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE))).perform(scrollTo(), typeText(test), closeSoftKeyboard());

        onView(allOf(withId(R.id.sendReplyButton), withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)))
                .perform(scrollTo(), click());

        Thread.sleep(5000);

        onView(withId(R.id.enterComment)).perform(scrollTo()).check(matches(isDisplayed()));

        scenario.close();

    }

    @Test
    public void enterReplyBecomesInvisibleWhenClickedTwice(){
        waitForDatabaseFetchCompletion(300, TimeUnit.SECONDS);
        Intent i = RecipeConverter.convertToIntent(omelette, ApplicationProvider.getApplicationContext());

        ActivityScenario scenario = ActivityScenario.launch(i);

        int commentIndex = 0;

        scenario.onActivity(activity -> {
            RecyclerView commentsList = activity.findViewById(R.id.commentsList);
            CommentViewHolder viewHolder = (CommentViewHolder) commentsList.findViewHolderForAdapterPosition(commentIndex);

            // Check that the tag value of the button changes when clicked
            Button reply = viewHolder.itemView.findViewById(R.id.replyButton);
            reply.performClick();

            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

            reply.performClick();

            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

        });
        
        onView(withId(R.id.enterComment)).perform(scrollTo()).check(matches(isDisplayed()));
        scenario.close();

    }

    // authenticated interactions

    // Comment
    @Test
    public void commentAuthenticated() throws InterruptedException {
        FirebaseAuthActivityTest.loginSync("example@email.com");
        waitForDatabaseFetchCompletion(300, TimeUnit.SECONDS);
        Intent i = RecipeConverter.convertToIntent(omelette, ApplicationProvider.getApplicationContext());

        ActivityScenario scenario = ActivityScenario.launch(i);

        String test = "test";

        onView(withId(R.id.enterComment)).perform(scrollTo(), typeText(test), closeSoftKeyboard());

        onView(withId(R.id.sendCommentButton))
                .perform(scrollTo(), click());

        Thread.sleep(10000);

        onView(withId(R.id.enterComment)).check(matches(isDisplayed()));

        scenario.close();

        FirebaseAuthActivityTest.logoutSync();

    }


    // Like
    @Test
    public void likeAuthenticated() {
        FirebaseAuthActivityTest.loginSync("example@email.com");
        waitForDatabaseFetchCompletion(300, TimeUnit.SECONDS);
        Intent i = RecipeConverter.convertToIntent(omelette, ApplicationProvider.getApplicationContext());

        ActivityScenario scenario = ActivityScenario.launch(i);

        int commentIndex = 0;

        scenario.onActivity(activity -> {
            RecyclerView commentsList = activity.findViewById(R.id.commentsList);
            CommentViewHolder viewHolder = (CommentViewHolder) commentsList.findViewHolderForAdapterPosition(commentIndex);

            // Check that the tag value of the button changes when clicked
            ToggleButton thumb = viewHolder.itemView.findViewById(R.id.thumbButton);
            thumb.performClick();
            try {
                Thread.sleep(10000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

        });

        onView(withId(R.id.enterComment)).perform(scrollTo()).check(matches(isDisplayed()));

        scenario.close();

        FirebaseAuthActivityTest.logoutSync();

    }

    // Favorite
    @Test
    public void heartAuthenticated(){
        FirebaseAuthActivityTest.loginSync("example@mail.com");
        waitForDatabaseFetchCompletion(300, TimeUnit.SECONDS);
        Intent i = RecipeConverter.convertToIntent(omelette, ApplicationProvider.getApplicationContext());

        ActivityScenario scenario = ActivityScenario.launch(i);
        scenario.onActivity(activity -> {
                    // Check that the background drawable has changed to the checked state drawable
                    ToggleButton heart = (ToggleButton) activity.findViewById(R.id.favoriteButton);
                    heart.performClick();

                    try {
                        Thread.sleep(5000); // Delay the check for 5000 milliseconds
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }

                });

        onView(withId(R.id.favoriteButton)).check(matches(isDisplayed()));

        scenario.onActivity(activity -> {

            ToggleButton heart = (ToggleButton) activity.findViewById(R.id.favoriteButton);
            heart.performClick();
            try {
                Thread.sleep(5000); // Delay the check for 5000 milliseconds
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        });

        onView(withId(R.id.favoriteButton)).check(matches(isDisplayed()));

        scenario.close();
        FirebaseAuthActivityTest.logoutSync();
    }


    // Reply
    @Test
    public void replyAuthenticated() throws InterruptedException {
        FirebaseAuthActivityTest.loginSync("example@email.com");
        waitForDatabaseFetchCompletion(300, TimeUnit.SECONDS);
        Intent i = RecipeConverter.convertToIntent(omelette, ApplicationProvider.getApplicationContext());

        ActivityScenario scenario = ActivityScenario.launch(i);

        int commentIndex = 0;

        scenario.onActivity(activity -> {
            RecyclerView commentsList = activity.findViewById(R.id.commentsList);
            CommentViewHolder viewHolder = (CommentViewHolder) commentsList.findViewHolderForAdapterPosition(commentIndex);

            // Check that the tag value of the button changes when clicked
            Button reply = viewHolder.itemView.findViewById(R.id.replyButton);
            reply.performClick();

            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        });

        String test = "test";

        onView(allOf(withId(R.id.enterReply), withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE))).perform(scrollTo(), typeText(test), closeSoftKeyboard());

        onView(allOf(withId(R.id.sendReplyButton), withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)))
                .perform(scrollTo(), click());

        Thread.sleep(5000);

        onView(withId(R.id.enterComment)).perform(scrollTo()).check(matches(isDisplayed()));

        scenario.close();

        FirebaseAuthActivityTest.logoutSync();
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

class TimerIdlingResource implements IdlingResource {
    private final long startTime;
    private final long waitingTime;
    private ResourceCallback resourceCallback;
    private boolean idle;

    public TimerIdlingResource(long waitingTime) {
        this.startTime = System.currentTimeMillis();
        this.waitingTime = waitingTime;
        this.idle = false;
    }

    @Override
    public String getName() {
        return TimerIdlingResource.class.getName();
    }

    @Override
    public boolean isIdleNow() {
        long elapsedTime = System.currentTimeMillis() - startTime;
        idle = (elapsedTime >= waitingTime);

        if (idle && resourceCallback != null) {
            resourceCallback.onTransitionToIdle();
        }

        return idle;
    }

    @Override
    public void registerIdleTransitionCallback(ResourceCallback resourceCallback) {
        this.resourceCallback = resourceCallback;
    }
}


class DatabaseIdlingResource implements IdlingResource {
    private ResourceCallback resourceCallback;
    private boolean isIdle = false;

    public void setIdle(boolean idle) {
        isIdle = idle;
        if (isIdle && resourceCallback != null) {
            resourceCallback.onTransitionToIdle();
        }
    }

    @Override
    public String getName() {
        return DatabaseIdlingResource.class.getName();
    }

    @Override
    public boolean isIdleNow() {
        return isIdle;
    }

    @Override
    public void registerIdleTransitionCallback(ResourceCallback resourceCallback) {
        this.resourceCallback = resourceCallback;
    }
}

class DatabaseIdlingResource2 implements IdlingResource {

    private final CountingIdlingResource countingResource;
    private final DatabaseReference databaseReference;

    public DatabaseIdlingResource2(DatabaseReference databaseReference) {
        this.countingResource = new CountingIdlingResource("database");
        this.databaseReference = databaseReference;
    }

    @Override
    public String getName() {
        return countingResource.getName();
    }

    @Override
    public boolean isIdleNow() {
        return countingResource.isIdleNow();
    }

    @Override
    public void registerIdleTransitionCallback(ResourceCallback callback) {
        countingResource.registerIdleTransitionCallback(callback);
    }

    public void waitForDatabase() {
        countingResource.increment();

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                countingResource.decrement();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                countingResource.decrement();
            }
        });
    }
}






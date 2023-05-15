package com.github.siela1915.bootcamp;

import static androidx.test.core.app.ApplicationProvider.getApplicationContext;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
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
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withEffectiveVisibility;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import android.app.Activity;
import android.app.Instrumentation;
import android.content.Context;
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
import androidx.test.espresso.IdlingRegistry;
import androidx.test.espresso.IdlingResource;
import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.intent.Intents;
import androidx.test.espresso.matcher.BoundedMatcher;
import androidx.test.espresso.matcher.ViewMatchers;

import com.github.siela1915.bootcamp.Recipes.Comment;
import com.github.siela1915.bootcamp.Recipes.ExampleRecipes;
import com.github.siela1915.bootcamp.Recipes.Ingredient;
import com.github.siela1915.bootcamp.Recipes.Recipe;
import com.github.siela1915.bootcamp.firebase.Database;
import com.github.siela1915.bootcamp.firebase.UserDatabase;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

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

        waitForDatabaseFetchCompletion(300, TimeUnit.SECONDS);
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
    public void heartButtonBecomesFullAuthenticated(){
        waitForDatabaseFetchCompletion(300, TimeUnit.SECONDS);
        Intent i = RecipeConverter.convertToIntent(omelette, ApplicationProvider.getApplicationContext());

        ActivityScenario scenario = ActivityScenario.launch(i);

        FirebaseAuthActivityTest.loginSync("eylulipci00@gmail.com");

        onView(withId(R.id.favoriteButton))
                .perform(ViewActions.click())
                .check(matches(ViewMatchers.withTagValue(is("full"))));

        FirebaseAuthActivityTest.logoutSync();
        scenario.close();
    } */





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

    public TimerIdlingResource(long waitingTime) {
        this.startTime = System.currentTimeMillis();
        this.waitingTime = waitingTime;
    }

    @Override
    public String getName() {
        return TimerIdlingResource.class.getName();
    }

    @Override
    public boolean isIdleNow() {
        long elapsedTime = System.currentTimeMillis() - startTime;
        boolean idle = (elapsedTime >= waitingTime);
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




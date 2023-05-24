package com.github.siela1915.bootcamp;

import static androidx.test.core.app.ApplicationProvider.getApplicationContext;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDescendantOfA;
import static androidx.test.espresso.matcher.ViewMatchers.withClassName;
import static androidx.test.espresso.matcher.ViewMatchers.withEffectiveVisibility;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.endsWith;

import android.Manifest;
import android.location.Location;

import androidx.fragment.app.testing.FragmentScenario;
import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.rule.GrantPermissionRule;

import com.github.siela1915.bootcamp.Recipes.Ingredient;
import com.github.siela1915.bootcamp.Recipes.Unit;
import com.github.siela1915.bootcamp.firebase.Database;
import com.github.siela1915.bootcamp.firebase.FirebaseInstanceManager;
import com.github.siela1915.bootcamp.firebase.LocationDatabase;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.util.Arrays;
import java.util.concurrent.ExecutionException;

public class MyFridgeFragmentTest {
    FragmentScenario<MyFridgeFragment> scenario;

    private LocationDatabase locDb;
    private Database db;

    @Rule
    public GrantPermissionRule mRuntimePermissionRule = GrantPermissionRule.grant(
            Manifest.permission.ACCESS_FINE_LOCATION);


    @Before
    public void prepare() {
        FirebaseInstanceManager.emulator = true;

        if (locDb == null) {
            locDb = new LocationDatabase();
        }

        if (db == null) {
            db = new Database(FirebaseInstanceManager.getDatabase());
        }

        if (FirebaseInstanceManager.getAuth().getCurrentUser() != null) {
            FirebaseAuthActivityTest.logoutSync();
        }
    }

    @After
    public void cleanUp() {
        if (scenario != null) {
            scenario.close();
        }
    }

    @Test
    public void offerHelpUploadsOffers() {
        FirebaseAuthActivityTest.loginSync("offerHelpUploadsOffers@myfridge.com");

        scenario = FragmentScenario.launchInContainer(MyFridgeFragment.class);

        onView(withId(R.id.myFridgeAddIngredient))
                .perform(ViewActions.click());

        onView(allOf(
                isDescendantOfA(withId(R.id.ingredientsName)),
                withClassName(endsWith("AutoCompleteTextView"))
        )).perform(ViewActions.typeText("testIngredient"), ViewActions.closeSoftKeyboard());
        onView(allOf(
                isDescendantOfA(withId(R.id.ingredientsAmount)),
                withClassName(endsWith("EditText"))
        )).perform(ViewActions.typeText("3"), ViewActions.closeSoftKeyboard());
        onView(allOf(
                isDescendantOfA(withId(R.id.ingredientsUnit)),
                withClassName(endsWith("EditText"))
        )).perform(ViewActions.typeText("g"), ViewActions.closeSoftKeyboard());

        onView(withId(R.id.updateOfferedFridge))
                .perform(ViewActions.click());

        FirebaseAuthActivityTest.logoutSync();
    }

    @Test
    public void fridgeUploadsCorrectly() {
        FirebaseAuthActivityTest.loginSync("fridgeUploadsCorrectly@myfridge.com");

        scenario = FragmentScenario.launchInContainer(MyFridgeFragment.class);

        onView(withId(R.id.myFridgeAddIngredient))
                .perform(ViewActions.click());

        onView(allOf(
                isDescendantOfA(withId(R.id.ingredientsName)),
                withClassName(endsWith("AutoCompleteTextView"))
        )).perform(ViewActions.typeText("testIngredient"), ViewActions.closeSoftKeyboard());
        onView(allOf(
                isDescendantOfA(withId(R.id.ingredientsAmount)),
                withClassName(endsWith("EditText"))
        )).perform(ViewActions.typeText("3"), ViewActions.closeSoftKeyboard());
        onView(allOf(
                isDescendantOfA(withId(R.id.ingredientsUnit)),
                withClassName(endsWith("EditText"))
        )).perform(ViewActions.typeText("g"), ViewActions.closeSoftKeyboard());

        onView(withId(R.id.submitMyFridgeButton))
                .perform(ViewActions.click());

        FirebaseAuthActivityTest.logoutSync();
    }

    @Test
    public void offerHelpShowsOffers() {
        FirebaseAuthActivityTest.loginSync("offerHelpShowsOffers@myfridge.com");

        fillOffersInDatabase();

        scenario = FragmentScenario.launchInContainer(MyFridgeFragment.class);

        onView(withId(R.id.myFridgeIngredientLayout))
                .check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));

        FirebaseAuthActivityTest.logoutSync();
    }

    public void fillOffersInDatabase() {
        try {
            Location loc = new Location("LocationDatabase");
            loc.setLatitude(37.4226711);
            loc.setLongitude(-122.0849872);
            Tasks.await(locDb.updateLocation(loc));
            Tasks.await(db.updateFridge(Arrays.asList(
                    new Ingredient("testIngredient", new Unit(2, "g")),
                    new Ingredient("testIngredient", new Unit(100, "g")),
                    new Ingredient("testIngredient", new Unit(1, "kg")),
                    new Ingredient("testIngredient2", new Unit(5, "cups")))));
            Tasks.await(locDb.updateOffered(Arrays.asList(0, 1, 2, 3)));
        } catch (ExecutionException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}

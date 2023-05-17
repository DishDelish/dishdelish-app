package com.github.siela1915.bootcamp;

import static androidx.test.core.app.ApplicationProvider.getApplicationContext;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.Espresso.pressBack;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.Visibility.VISIBLE;
import static androidx.test.espresso.matcher.ViewMatchers.isDescendantOfA;
import static androidx.test.espresso.matcher.ViewMatchers.withClassName;
import static androidx.test.espresso.matcher.ViewMatchers.withContentDescription;
import static androidx.test.espresso.matcher.ViewMatchers.withEffectiveVisibility;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.endsWith;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertNotNull;

import android.Manifest;
import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.testing.FragmentScenario;
import androidx.test.espresso.action.ViewActions;
import androidx.test.rule.GrantPermissionRule;

import com.github.siela1915.bootcamp.Recipes.Ingredient;
import com.github.siela1915.bootcamp.Recipes.Unit;
import com.github.siela1915.bootcamp.firebase.LocationDatabase;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.ExecutionException;

public class NearbyHelpFragmentTest {
    FragmentScenario<NearbyHelpFragment> scenario;

    private LocationDatabase locDb;
    private FusedLocationProviderClient fusedLocationClient;

    @Rule
    public GrantPermissionRule mRuntimePermissionRule = GrantPermissionRule.grant(
            Manifest.permission.ACCESS_FINE_LOCATION);


    @Before
    public void prepare() {
        FirebaseApp.clearInstancesForTest();
        FirebaseApp.initializeApp(getApplicationContext());
        FirebaseAuth.getInstance().useEmulator("10.0.2.2", 9099);
        FirebaseDatabase.getInstance().useEmulator("10.0.2.2", 9000);

        if (locDb == null) {
            locDb = new LocationDatabase();
        }

        fusedLocationClient = LocationServices.getFusedLocationProviderClient((Context) getApplicationContext());

        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
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
    public void showsCorrectInitialScreen() {
        FirebaseAuthActivityTest.loginSync("test@example.com");

        scenario = FragmentScenario.launchInContainer(NearbyHelpFragment.class);

        onView(withId(R.id.chooseHelpGroup)).check(matches(withEffectiveVisibility(VISIBLE)));
        onView(withId(R.id.askHelpGroup)).check(matches(not(withEffectiveVisibility(VISIBLE))));
        onView(withId(R.id.offerHelpGroup)).check(matches(not(withEffectiveVisibility(VISIBLE))));
        FirebaseAuthActivityTest.logoutSync();
    }

    @Test
    public void showsCorrectAskHelpScreen() {
        FirebaseAuthActivityTest.loginSync("test@example.com");

        scenario = FragmentScenario.launchInContainer(NearbyHelpFragment.class);

        onView(withId(R.id.askHelpButton))
                .perform(ViewActions.click());

        onView(withId(R.id.chooseHelpGroup)).check(matches(not(withEffectiveVisibility(VISIBLE))));
        onView(withId(R.id.askHelpGroup)).check(matches(withEffectiveVisibility(VISIBLE)));
        onView(withId(R.id.offerHelpGroup)).check(matches(not(withEffectiveVisibility(VISIBLE))));
        FirebaseAuthActivityTest.logoutSync();
    }

    @Test
    public void showsCorrectOfferHelpScreen() {
        FirebaseAuthActivityTest.loginSync("test@example.com");

        scenario = FragmentScenario.launchInContainer(NearbyHelpFragment.class);

        onView(withId(R.id.offerHelpButton))
                .perform(ViewActions.click());

        onView(withId(R.id.chooseHelpGroup)).check(matches(not(withEffectiveVisibility(VISIBLE))));
        onView(withId(R.id.askHelpGroup)).check(matches(not(withEffectiveVisibility(VISIBLE))));
        onView(withId(R.id.offerHelpGroup)).check(matches(withEffectiveVisibility(VISIBLE)));
        FirebaseAuthActivityTest.logoutSync();
    }

    @Test
    public void showsCorrectOfferedIngredientsOnOfferHelpScreen() {
        FirebaseAuthActivityTest.loginSync("showsCorrectOffered@test.com");

        scenario = FragmentScenario.launchInContainer(NearbyHelpFragment.class);

        fillOffersInDatabase();

        onView(withId(R.id.offerHelpButton))
                .perform(ViewActions.click());

        onView(withId(R.id.chooseHelpGroup)).check(matches(not(withEffectiveVisibility(VISIBLE))));
        onView(withId(R.id.askHelpGroup)).check(matches(not(withEffectiveVisibility(VISIBLE))));
        onView(withId(R.id.offerHelpGroup)).check(matches(withEffectiveVisibility(VISIBLE)));

        FirebaseAuthActivityTest.logoutSync();
    }

    @Test
    public void askHelpGetsNearbyOffers() {
        FirebaseAuthActivityTest.loginSync("test@example.com");

        scenario = FragmentScenario.launchInContainer(NearbyHelpFragment.class);

        fillOffersInDatabase();

        onView(withId(R.id.askHelpButton)).perform(ViewActions.click());

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

        onView(withId(R.id.submitAskHelpButton))
                .perform(ViewActions.click());

        onView(withContentDescription("Google Map")).check(matches(withEffectiveVisibility(VISIBLE)));

        FirebaseAuthActivityTest.logoutSync();
    }


    @Test
    public void offerHelpUploadsLocation() {
        FirebaseAuthActivityTest.loginSync("test@example.com");

        scenario = FragmentScenario.launchInContainer(NearbyHelpFragment.class);

        onView(withId(R.id.offerHelpButton))
                .perform(ViewActions.click());

        onView(withId(R.id.offerAddIngredient))
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

        onView(withId(R.id.submitOfferHelpButton))
                .perform(ViewActions.click());

        FirebaseAuthActivityTest.logoutSync();
    }

    @Test
    public void fragmentWithArgumentsShowsReplyPage() {
        Bundle args = new Bundle();
        args.putString(NearbyHelpFragment.ARG_REPLY_OFFER_UID, "testUID");
        args.putString(NearbyHelpFragment.ARG_REPLY_INGREDIENT, "test1");
        scenario = FragmentScenario.launchInContainer(NearbyHelpFragment.class, args);

        onView(withId(R.id.replyHelpGroup)).check(matches(withEffectiveVisibility(VISIBLE)));
        onView(withId(R.id.askHelpGroup)).check(matches(not(withEffectiveVisibility(VISIBLE))));
        onView(withId(R.id.offerHelpGroup)).check(matches(not(withEffectiveVisibility(VISIBLE))));
        onView(withId(R.id.chooseHelpGroup)).check(matches(not(withEffectiveVisibility(VISIBLE))));
    }

    @Test
    public void fragmentWithIngredientArgumentShowsMapDirectly() {
        Ingredient ing = new Ingredient();
        ing.setIngredient("testIngredient");
        Bundle args = new Bundle();
        args.putParcelable(NearbyHelpFragment.ARG_ASKED_INGREDIENT, ing);
        scenario = FragmentScenario.launchInContainer(NearbyHelpFragment.class, args);

        onView(withId(R.id.replyHelpGroup)).check(matches(not(withEffectiveVisibility(VISIBLE))));
        onView(withId(R.id.askHelpGroup)).check(matches(not(withEffectiveVisibility(VISIBLE))));
        onView(withId(R.id.offerHelpGroup)).check(matches(not(withEffectiveVisibility(VISIBLE))));
        onView(withId(R.id.chooseHelpGroup)).check(matches(not(withEffectiveVisibility(VISIBLE))));

        onView(withContentDescription("Google Map")).check(matches(withEffectiveVisibility(VISIBLE)));
    }

    @Test
    public void fragmentWithMultipleIngredientsArgumentShowsMapDirectly() {
        ArrayList<Ingredient> ing = new ArrayList<>(Arrays.asList(
                new Ingredient("testIngredient", new Unit()),
                new Ingredient("testIngredient2", new Unit())));
        Bundle args = new Bundle();
        args.putParcelableArrayList(NearbyHelpFragment.ARG_ASKED_INGREDIENTS, ing);
        scenario = FragmentScenario.launchInContainer(NearbyHelpFragment.class, args);

        onView(withId(R.id.replyHelpGroup)).check(matches(not(withEffectiveVisibility(VISIBLE))));
        onView(withId(R.id.askHelpGroup)).check(matches(not(withEffectiveVisibility(VISIBLE))));
        onView(withId(R.id.offerHelpGroup)).check(matches(not(withEffectiveVisibility(VISIBLE))));
        onView(withId(R.id.chooseHelpGroup)).check(matches(not(withEffectiveVisibility(VISIBLE))));

        onView(withContentDescription("Google Map")).check(matches(withEffectiveVisibility(VISIBLE)));
    }

    @Test
    public void backButtonWorksCorrectlyInAskHelpScreen() {
        scenario = FragmentScenario.launchInContainer(NearbyHelpFragment.class);

        onView(withId(R.id.askHelpButton)).perform(ViewActions.click());
        onView(withId(R.id.askHelpGroup)).check(matches(withEffectiveVisibility(VISIBLE)));

        pressBack();

        onView(withId(R.id.askHelpGroup)).check(matches(not(withEffectiveVisibility(VISIBLE))));
        onView(withId(R.id.chooseHelpGroup)).check(matches(withEffectiveVisibility(VISIBLE)));
    }

    @Test
    public void backButtonWorksCorrectlyInOfferHelpScreen() {
        scenario = FragmentScenario.launchInContainer(NearbyHelpFragment.class);

        onView(withId(R.id.offerHelpButton)).perform(ViewActions.click());
        onView(withId(R.id.offerHelpGroup)).check(matches(withEffectiveVisibility(VISIBLE)));

        pressBack();

        onView(withId(R.id.offerHelpGroup)).check(matches(not(withEffectiveVisibility(VISIBLE))));
        onView(withId(R.id.chooseHelpGroup)).check(matches(withEffectiveVisibility(VISIBLE)));
    }

    @Test
    public void replyPageShowsErrorWhenNotLoggedIn() {
        Bundle args = new Bundle();
        args.putString(NearbyHelpFragment.ARG_REPLY_OFFER_UID, "testUID");
        args.putString(NearbyHelpFragment.ARG_REPLY_INGREDIENT, "test1");
        scenario = FragmentScenario.launchInContainer(NearbyHelpFragment.class, args);

        onView(withId(R.id.replyInputHelp)).perform(ViewActions.typeText("Test reply to notification"),
                ViewActions.closeSoftKeyboard());
        onView(withId(R.id.sendReplyHelpButton)).perform(ViewActions.click());

        onView(withId(R.id.replyInputHelp)).check(matches(withText("Can't reply without being authenticated")));
    }

    @Test
    public void replyPageCanSendReply() {
        FirebaseAuthActivityTest.loginSync("replyPagePutsReplyIntoDatabase@test.com");
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        assertNotNull(user);
        Bundle args = new Bundle();
        args.putString(NearbyHelpFragment.ARG_REPLY_OFFER_UID, user.getUid());
        args.putString(NearbyHelpFragment.ARG_REPLY_INGREDIENT, "test1");
        scenario = FragmentScenario.launchInContainer(NearbyHelpFragment.class, args);

        onView(withId(R.id.replyInputHelp)).perform(ViewActions.typeText("Test reply to notification"),
                ViewActions.closeSoftKeyboard());
        onView(withId(R.id.sendReplyHelpButton)).perform(ViewActions.click());

        FirebaseAuthActivityTest.logoutSync();
    }

    public void fillOffersInDatabase() {
        try {
            Tasks.await(locDb.updateLocation(Tasks.await(fusedLocationClient.getLastLocation())));
            Tasks.await(locDb.updateOffered(Arrays.asList(
                    new Ingredient("testIngredient", new Unit(2, "g")),
                    new Ingredient("testIngredient", new Unit(100, "g")),
                    new Ingredient("testIngredient", new Unit(1, "kg")),
                    new Ingredient("testIngredient2", new Unit(5, "cups")))));
        } catch (ExecutionException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}

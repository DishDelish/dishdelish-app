package com.github.siela1915.bootcamp;

import static androidx.test.core.app.ApplicationProvider.getApplicationContext;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.scrollTo;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.Visibility.VISIBLE;
import static androidx.test.espresso.matcher.ViewMatchers.withEffectiveVisibility;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static org.hamcrest.Matchers.not;

import androidx.fragment.app.testing.FragmentScenario;
import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.matcher.ViewMatchers;

import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class NearbyHelpFragmentTest {
    FragmentScenario<NearbyHelpFragment> scenario;

    @Before
    public void prepare() {
        FirebaseApp.clearInstancesForTest();
        FirebaseApp.initializeApp(getApplicationContext());
        FirebaseAuth.getInstance().useEmulator("10.0.2.2", 9099);
        FirebaseDatabase.getInstance().useEmulator("10.0.2.2", 9000);

        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            FirebaseAuthActivityTest.logoutSync();
        }
    }

    @After
    public void cleanUp() {
        scenario.close();
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
    public void askHelpGetsNearbyOffers() {
        FirebaseAuthActivityTest.loginSync("test@example.com");

        scenario = FragmentScenario.launchInContainer(NearbyHelpFragment.class);

        onView(withId(R.id.askHelpButton)).perform(ViewActions.click());

        onView(withId(R.id.askedIngredient)).perform(ViewActions.typeText("testIngredient"), ViewActions.closeSoftKeyboard());
        onView(withId(R.id.askSharePosition)).perform(ViewActions.click());

        onView(withId(R.id.submitAskHelpButton))
                .perform(ViewActions.click());

        FirebaseAuthActivityTest.logoutSync();
    }


    @Test
    public void offerHelpUploadsLocation() {
        FirebaseAuthActivityTest.loginSync("test@example.com");

        scenario = FragmentScenario.launchInContainer(NearbyHelpFragment.class);

        onView(withId(R.id.offerHelpButton))
                .perform(ViewActions.click());

        onView(withId(R.id.offeredIngredient)).perform(ViewActions.typeText("testIngredient"), ViewActions.closeSoftKeyboard());
        onView(withId(R.id.offerSharePosition)).perform(ViewActions.click());

        onView(withId(R.id.submitOfferHelpButton))
                .perform(ViewActions.click());

        FirebaseAuthActivityTest.logoutSync();
    }
}

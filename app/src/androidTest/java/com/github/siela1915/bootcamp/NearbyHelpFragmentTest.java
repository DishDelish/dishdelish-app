package com.github.siela1915.bootcamp;

import static androidx.test.core.app.ApplicationProvider.getApplicationContext;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.Visibility.VISIBLE;
import static androidx.test.espresso.matcher.ViewMatchers.withContentDescription;
import static androidx.test.espresso.matcher.ViewMatchers.withEffectiveVisibility;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import android.Manifest;
import android.os.Bundle;

import androidx.fragment.app.testing.FragmentScenario;
import androidx.test.espresso.action.ViewActions;
import androidx.test.rule.GrantPermissionRule;

import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.util.concurrent.ExecutionException;

public class NearbyHelpFragmentTest {
    FragmentScenario<NearbyHelpFragment> scenario;

    @Rule
    public GrantPermissionRule mRuntimePermissionRule = GrantPermissionRule.grant(
            Manifest.permission.ACCESS_FINE_LOCATION);


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

        onView(withId(R.id.offeredIngredient)).perform(ViewActions.typeText("testIngredient"), ViewActions.closeSoftKeyboard());

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
}

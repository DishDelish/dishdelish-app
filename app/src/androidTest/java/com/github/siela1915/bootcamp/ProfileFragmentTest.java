package com.github.siela1915.bootcamp;

import static androidx.test.core.app.ApplicationProvider.getApplicationContext;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.Visibility.VISIBLE;
import static androidx.test.espresso.matcher.ViewMatchers.withEffectiveVisibility;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static org.hamcrest.Matchers.not;

import androidx.fragment.app.testing.FragmentScenario;
import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.matcher.ViewMatchers;

import com.github.siela1915.bootcamp.firebase.FirebaseInstanceManager;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class ProfileFragmentTest {
    FragmentScenario<ProfileFragment> scenario;

    @Before
    public void prepare() {
        FirebaseInstanceManager.emulator = true;

        if (FirebaseInstanceManager.getAuth().getCurrentUser() != null) {
            FirebaseAuthActivityTest.logoutSync();
        }
    }

    @After
    public void cleanUp() {
        FirebaseAuthActivityTest.logoutSync();
        scenario.close();
    }

    @Test
    public void loggedOutShowsCorrectItemsTest() {
        scenario = FragmentScenario.launchInContainer(ProfileFragment.class);

        onView(withId(R.id.profileLoggedOut)).check(matches(withEffectiveVisibility(VISIBLE)));
        onView(withId(R.id.profileLoggedIn)).check(matches(not(withEffectiveVisibility(VISIBLE))));
        onView(withId(R.id.profileTextFixed)).check(matches(not(withEffectiveVisibility(VISIBLE))));
        onView(withId(R.id.profileTextEdit)).check(matches(not(withEffectiveVisibility(VISIBLE))));
    }

    @Test
    public void loggedInShowsCorrectItemsTest() {
        FirebaseAuthActivityTest.loginSync("test@example.com");

        scenario = FragmentScenario.launchInContainer(ProfileFragment.class);

        onView(withId(R.id.profileLoggedIn)).check(matches(withEffectiveVisibility(VISIBLE)));
        onView(withId(R.id.profileLoggedOut)).check(matches(not(withEffectiveVisibility(VISIBLE))));
        onView(withId(R.id.profileTextFixed)).check(matches(withEffectiveVisibility(VISIBLE)));
        onView(withId(R.id.profileTextEdit)).check(matches(not(withEffectiveVisibility(VISIBLE))));

        FirebaseAuthActivityTest.logoutSync();
    }

    @Test
    public void loggedInCanEditProfileTest() {
        FirebaseAuthActivityTest.loginSync("test@example.com");

        scenario = FragmentScenario.launchInContainer(ProfileFragment.class);

        onView(withId(R.id.profileEdit)).perform(ViewActions.click());

        onView(withId(R.id.profileTextEdit)).check(matches(withEffectiveVisibility(VISIBLE)));
        onView(withId(R.id.profileTextFixed)).check(matches(not(withEffectiveVisibility(VISIBLE))));

        onView(withId(R.id.profileDisplayNameEdit)).perform(ViewActions.replaceText("Modified NewTest"));

        onView(withId(R.id.profileDisplayNameEdit)).check(matches(ViewMatchers.withText("Modified NewTest")));

        FirebaseAuthActivityTest.logoutSync();
    }

    @Test
    public void loggedInCanSubmitProfileChangesTest() {
        FirebaseAuthActivityTest.loginSync("test@example.com");

        scenario = FragmentScenario.launchInContainer(ProfileFragment.class);

        onView(withId(R.id.profileLoggedIn)).check(matches(withEffectiveVisibility(VISIBLE)));
        onView(withId(R.id.profileEdit)).perform(ViewActions.click());
        onView(withId(R.id.profileDisplayNameEdit)).perform(ViewActions.replaceText("Modified NewTest"));
        onView(withId(R.id.profileSubmit)).perform(ViewActions.click());

        onView(withId(R.id.profileTextFixed)).check(matches(withEffectiveVisibility(VISIBLE)));
        onView(withId(R.id.profileTextEdit)).check(matches(not(withEffectiveVisibility(VISIBLE))));
        onView(withId(R.id.profileDisplayName)).check(matches(ViewMatchers.withText("Modified NewTest")));

        FirebaseAuthActivityTest.logoutSync();
    }
}

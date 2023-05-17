package com.github.siela1915.bootcamp;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.Visibility.VISIBLE;
import static androidx.test.espresso.matcher.ViewMatchers.withEffectiveVisibility;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.platform.app.InstrumentationRegistry.getInstrumentation;

import static org.junit.Assert.assertTrue;

import android.os.Bundle;

import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.testing.FragmentScenario;
import androidx.test.espresso.action.ViewActions;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class CookNowTimerFragmentTest {
    FragmentScenario<CookNowTimerFragment> scenario;
    private int testIndex = 1;

    private Bundle createTimerBundle() {
        Bundle bundle = new Bundle();

        bundle.putInt("index", testIndex);

        return bundle;
    }

    @Before
    public void launchFragment() {
        scenario = FragmentScenario.launchInContainer(CookNowTimerFragment.class, createTimerBundle(), R.style.Theme_SDPBootcamp);
    }

    @After
    public void cleanUp() {
        scenario.close();
    }

    @Test
    public void displayTest() {
        onView(withId(R.id.cookNowTimerTrigger)).check(matches(withEffectiveVisibility(VISIBLE)));
    }

    @Test
    public void openTimerEditorDialogTest() {
        onView(withId(R.id.cookNowTimerTrigger)).perform(click());

        getInstrumentation().waitForIdleSync();
        scenario.onFragment(fragment -> {
            Fragment dialog = fragment.getActivity().getSupportFragmentManager().findFragmentByTag("cook_now_timer_editor_dialog");
            assertTrue(dialog instanceof DialogFragment);
            assertTrue(((DialogFragment) dialog).getShowsDialog());
        });
    }
}

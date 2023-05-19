package com.github.siela1915.bootcamp;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.Visibility.VISIBLE;
import static androidx.test.espresso.matcher.ViewMatchers.withEffectiveVisibility;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

import android.os.Bundle;

import androidx.fragment.app.testing.FragmentScenario;
import androidx.test.espresso.matcher.ViewMatchers;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class CookNowStepFragmentTest {
    FragmentScenario<CookNowStepFragment> scenario;
    private String testStep = "test-step";
    private String stringBeginning = "\u25CF ";
    private int testIndex = 1;

    private Bundle createStepBundle() {
        Bundle bundle = new Bundle();

        bundle.putString("step", testStep);
        bundle.putInt("index", testIndex);

        return bundle;
    }

    @Before
    public void launchFragment() {
        scenario = FragmentScenario.launchInContainer(CookNowStepFragment.class, createStepBundle());
    }

    @After
    public void cleanUp() {
        scenario.close();
    }

    @Test
    public void displayTest() {
        onView(withId(R.id.cookNowStepTitle)).check(matches(withEffectiveVisibility(VISIBLE)));
        onView(withId(R.id.cookNowStepTitle)).check(matches(ViewMatchers.withText("Step " + (testIndex + 1))));
        onView(withId(R.id.cookNowStepContentContainer)).check(matches(withEffectiveVisibility(VISIBLE)));
        onView(withId(R.id.cookNowStepContent)).check(matches(withEffectiveVisibility(VISIBLE)));
        onView(withId(R.id.cookNowStepContent)).check(matches(ViewMatchers.withText(stringBeginning + testStep)));
    }
}

package com.github.siela1915.bootcamp;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.isNotEnabled;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

import android.view.Gravity;

import androidx.drawerlayout.widget.DrawerLayout;
import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.ViewAction;
import androidx.test.espresso.action.ViewActions;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class MainHomeActivityTest {
    @Rule
    public ActivityScenarioRule<MainHomeActivity> testRule = new ActivityScenarioRule<>(MainHomeActivity.class);

    @Test
    public void test2(){
        ActivityScenario<MainHomeActivity> scenario = ActivityScenario.launch(MainHomeActivity.class);
        onView(withId(R.id.drawer_layout)).perform(ViewActions.click());

    }
}

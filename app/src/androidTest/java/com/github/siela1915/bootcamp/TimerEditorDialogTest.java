package com.github.siela1915.bootcamp;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.RootMatchers.isDialog;
import static androidx.test.espresso.matcher.ViewMatchers.Visibility.VISIBLE;
import static androidx.test.espresso.matcher.ViewMatchers.withEffectiveVisibility;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.fragment.app.testing.FragmentScenario;
import androidx.test.espresso.matcher.BoundedMatcher;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class TimerEditorDialogTest {
    FragmentScenario<TimerEditorDialog> scenario;
    private int stepIndex = 1;
    private Context context;
    // add tolerance as there will be delay between each action
    private static int tolerance = 1;

    private Bundle createMockStepBundle() {
        Bundle bundle = new Bundle();

        bundle.putInt("index", stepIndex);

        return bundle;
    }

    // create a matcher to check if the actual number is within the accepted tolerance
    private static Matcher<View> isWithinTolerance(int expected) {
        return new BoundedMatcher<View, TextView>(TextView.class) {
            @Override
            protected boolean matchesSafely(TextView textView) {
                int actual = Integer.parseInt(textView.getText().toString());

                return (actual <= expected + tolerance) || (actual >= expected - tolerance);
            }

            @Override
            public void describeTo(Description description) {
                description.appendText("Expect to match with: " + expected);
            }
        };
    }

    @Before
    public void launchFragment() {
        context = InstrumentationRegistry.getInstrumentation().getTargetContext();
        scenario = FragmentScenario.launch(TimerEditorDialog.class, createMockStepBundle(), R.style.Theme_SDPBootcamp);
    }

    @After
    public void cleanUp() {
        scenario.close();
    }

    @Test
    public void displayTest() {
        onView(withId(R.id.timerCreate)).inRoot(isDialog()).check(matches(withEffectiveVisibility(VISIBLE)));
        onView(withId(R.id.timerCreateTitle)).check(matches(ViewMatchers.withText(R.string.cook_now_timer_create_title)));
        onView(withId(R.id.timerCreateTitleName)).check(matches(ViewMatchers.withText(context.getResources().getString(R.string.cook_now_timer_item_title) + " " + (stepIndex + 1))));
        onView(withId(R.id.startTimer)).check(matches(withEffectiveVisibility(VISIBLE)));
        onView(withId(R.id.startTimer)).check(matches(ViewMatchers.withText(context.getResources().getString(R.string.cook_now_timer_create_button))));
        onView(withId(R.id.timerList)).check(matches(withEffectiveVisibility(VISIBLE)));
        onView(withId(R.id.timerListTitle)).check(matches(ViewMatchers.withText(context.getResources().getString(R.string.cook_now_timer_list_title))));
    }

    @Test
    public void createATimerAndEnterCreateMode() {
        onView(withId(R.id.startTimer)).inRoot(isDialog()).perform(click());

        onView(withId(R.id.timerEdit)).check(matches(withEffectiveVisibility(VISIBLE)));
        onView(withId(R.id.timerEditTitle)).check(matches(ViewMatchers.withText(R.string.cook_now_timer_edit_title)));
        onView(withId(R.id.timerEditTitleName)).check(matches(ViewMatchers.withText(context.getResources().getString(R.string.cook_now_timer_item_title) + " " + (stepIndex + 1))));
        onView(withId(R.id.pauseTimer)).check(matches(withEffectiveVisibility(VISIBLE)));
        onView(withId(R.id.pauseTimer)).check(matches(ViewMatchers.withText(context.getResources().getString(R.string.cook_now_timer_edit_pause_button))));
        onView(withId(R.id.resumeTimer)).check(matches(withEffectiveVisibility(VISIBLE)));
        onView(withId(R.id.resumeTimer)).check(matches(ViewMatchers.withText(context.getResources().getString(R.string.cook_now_timer_edit_resume_button))));
        onView(withId(R.id.cancelTimer)).check(matches(withEffectiveVisibility(VISIBLE)));
        onView(withId(R.id.cancelTimer)).check(matches(ViewMatchers.withText(context.getResources().getString(R.string.cook_now_timer_edit_cancel_button))));
    }

    @Test
    public void pauseTimerTest() throws InterruptedException {
        onView(withId(R.id.startTimer)).inRoot(isDialog()).perform(click());
        onView(withId(R.id.pauseTimer)).perform(click());

        Thread.sleep(3000);

        onView(withId(R.id.timerSecNumber)).check(matches(isWithinTolerance(Integer.parseInt(context.getResources().getString(R.string.timer_sec_hint)))));
        onView(withId(R.id.timerMinNumber)).check(matches(isWithinTolerance(Integer.parseInt(context.getResources().getString(R.string.timer_min_hint)))));
        onView(withId(R.id.timerHourNumber)).check(matches(isWithinTolerance(Integer.parseInt(context.getResources().getString(R.string.timer_hour_hint)))));
    }

    @Test
    public void resumeTimerTest() throws InterruptedException {
        int waitTime = 2;

        onView(withId(R.id.startTimer)).inRoot(isDialog()).perform(click());
        onView(withId(R.id.pauseTimer)).perform(click());
        Thread.sleep(waitTime * 1000);
        onView(withId(R.id.resumeTimer)).perform(click());
        Thread.sleep(waitTime * 1000);

        onView(withId(R.id.timerSecNumber)).check(matches(isWithinTolerance(Integer.parseInt(context.getResources().getString(R.string.timer_sec_hint)) - 2 * waitTime)));
        onView(withId(R.id.timerMinNumber)).check(matches(isWithinTolerance(Integer.parseInt(context.getResources().getString(R.string.timer_min_hint)))));
        onView(withId(R.id.timerHourNumber)).check(matches(isWithinTolerance(Integer.parseInt(context.getResources().getString(R.string.timer_hour_hint)))));
    }

    @Test
    public void cancelTimerAndEnterCreateMode() {
        onView(withId(R.id.startTimer)).inRoot(isDialog()).perform(click());
        onView(withId(R.id.cancelTimer)).perform(click());

        onView(withId(R.id.timerCreate)).check(matches(withEffectiveVisibility(VISIBLE)));
        onView(withId(R.id.timerCreateTitle)).check(matches(ViewMatchers.withText(R.string.cook_now_timer_create_title)));
        onView(withId(R.id.timerCreateTitleName)).check(matches(ViewMatchers.withText(context.getResources().getString(R.string.cook_now_timer_item_title) + " " + (stepIndex + 1))));
        onView(withId(R.id.startTimer)).check(matches(withEffectiveVisibility(VISIBLE)));
        onView(withId(R.id.startTimer)).check(matches(ViewMatchers.withText(context.getResources().getString(R.string.cook_now_timer_create_button))));
    }
}

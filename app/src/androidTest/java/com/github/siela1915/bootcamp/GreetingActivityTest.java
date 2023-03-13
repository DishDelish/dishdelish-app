package com.github.siela1915.bootcamp;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;

import android.content.Intent;

import androidx.test.core.app.ActivityScenario;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.github.dishdelish.R;

import org.hamcrest.Matchers;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class GreetingActivityTest {
    @Test
    public void displaysCorrectMessage() {
        Intent messageIntent = new Intent(ApplicationProvider.getApplicationContext(), GreetingActivity.class);
        messageIntent.putExtra("com.github.siela1915.bootcamp.userName", "TestName");

        try (ActivityScenario activityScenario = ActivityScenario.launch(messageIntent)) {
            onView(ViewMatchers.withId(R.id.greetingMessage)).check(matches(ViewMatchers.withText("Hello, TestName!")));
        }
    }
}

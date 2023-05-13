package com.github.siela1915.bootcamp;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.intent.Intents.intending;
import static androidx.test.espresso.intent.matcher.IntentMatchers.anyIntent;

import android.app.Activity;
import android.app.Instrumentation;
import android.content.Intent;

import androidx.test.espresso.ViewInteraction;
import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.intent.Intents;
import androidx.test.espresso.intent.matcher.IntentMatchers;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.hamcrest.Matchers;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class MainActivityTest {
    @Rule
    public ActivityScenarioRule<MainActivity> testRule = new ActivityScenarioRule<>(MainActivity.class);

    @Test
    public void emptyNameSent() {
        Intents.init();
        ViewInteraction button = onView(ViewMatchers.withId(R.id.mainGoButton));

        button.perform(ViewActions.click());

        Intents.intended(Matchers.allOf(IntentMatchers.hasExtra("com.github.siela1915.bootcamp.userName", ""), IntentMatchers.hasComponent(GreetingActivity.class.getName())));

        Intents.release();
    }

    @Test
    public void correctNameSent() {
        Intents.init();
        ViewInteraction button = onView(ViewMatchers.withId(R.id.mainGoButton));
        ViewInteraction nameField = onView(ViewMatchers.withId(R.id.mainInputText));

        nameField.perform(ViewActions.typeText("Test"));
        nameField.perform(ViewActions.closeSoftKeyboard());
        button.perform(ViewActions.click());

        Intents.intended(Matchers.allOf(
                IntentMatchers.hasExtra("com.github.siela1915.bootcamp.userName", "Test"),
                IntentMatchers.hasComponent(GreetingActivity.class.getName())
        ));

        Intents.release();
    }

    @Test
    public void correctLoginIntentSent() {
        Intents.init();
        Intent intent = new Intent();
        Instrumentation.ActivityResult intentResult = new Instrumentation.ActivityResult(Activity.RESULT_OK,intent);
        intending(anyIntent()).respondWith(intentResult);

        ViewInteraction button = onView(ViewMatchers.withId(R.id.mainLoginButton));

        button.perform(ViewActions.click());

        Intents.intended(Matchers.allOf(
                IntentMatchers.hasExtra("com.github.siela1915.bootcamp.authaction", FirebaseAuthActivity.AUTH_ACTION.LOGIN),
                IntentMatchers.hasExtra("com.github.siela1915.bootcamp.postauthintent", IntentMatchers.hasComponent(GreetingActivity.class.getName()))));

        Intents.release();
    }

    @Test
    public void correctLogoutIntentSent() {
        Intents.init();
        Intent intent = new Intent();
        Instrumentation.ActivityResult intentResult = new Instrumentation.ActivityResult(Activity.RESULT_OK,intent);
        intending(anyIntent()).respondWith(intentResult);

        ViewInteraction button = onView(ViewMatchers.withId(R.id.mainLogoutButton));

        button.perform(ViewActions.click());

        Intents.intended(Matchers.allOf(
                IntentMatchers.hasExtra("com.github.siela1915.bootcamp.authaction", FirebaseAuthActivity.AUTH_ACTION.LOGOUT),
                IntentMatchers.hasExtra("com.github.siela1915.bootcamp.postauthintent", IntentMatchers.hasComponent(MainActivity.class.getName()))));

        Intents.release();
    }
}

package com.github.siela1915.bootcamp;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.doesNotExist;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import android.app.Activity;
import android.content.Intent;

import androidx.test.core.app.ActivityScenario;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.espresso.ViewInteraction;
import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.intent.Intents;
import androidx.test.espresso.intent.matcher.IntentMatchers;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.FirebaseDatabase;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Objects;
import java.util.concurrent.ExecutionException;

@RunWith(AndroidJUnit4.class)
public class FirebaseAuthActivityTest {
    public static void loginSync(String email) {
        String userJson;
        try {
            userJson = new JSONObject()
                    .put("sub", email)
                    .put("email", email)
                    .put("email_verified", "true")
                    .toString();
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

        AuthCredential credential = GoogleAuthProvider.getCredential(userJson, null);
        Task<AuthResult> result = FirebaseAuth.getInstance().signInWithCredential(credential);

        try {
            AuthResult authResult = Tasks.await(result);
        } catch (ExecutionException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public static void logoutSync() {
        Task<Void> result = AuthUI.getInstance().signOut(ApplicationProvider.getApplicationContext());
        try {
            Tasks.await(result);
        } catch (ExecutionException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public static void deleteSync(String email) {
        logoutSync();
        loginSync(email);
        Task<Void> result = AuthUI.getInstance().delete(ApplicationProvider.getApplicationContext());
        try {
            Tasks.await(result);
        } catch (ExecutionException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private Intent doneIntent;

    @Before
    public void prepare() {
        FirebaseApp.clearInstancesForTest();
        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseDatabase.getInstance().useEmulator("10.0.2.2", 9000);

        auth.useEmulator("10.0.2.2", 9099);

        if (auth.getCurrentUser() != null) {
            auth.signOut();
        }

        doneIntent = new Intent(ApplicationProvider.getApplicationContext(), GreetingActivity.class);
    }

    @Test
    public void testLoggedIn() {
        loginSync("foo@example.com");
        Intents.init();
        Intent loginIntent = FirebaseAuthActivity.createIntent(ApplicationProvider.getApplicationContext(), FirebaseAuthActivity.AUTH_ACTION.LOGIN, doneIntent);
        try (ActivityScenario<Activity> activityScenario = ActivityScenario.launch(loginIntent)) {
            Thread.sleep(3000);
            Intents.intended(IntentMatchers.hasComponent(GreetingActivity.class.getName()));
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        Intents.release();
        logoutSync();
    }

    @Test
    public void testLogOut() {
        loginSync("foo@example.com");
        Intents.init();
        Intent logoutIntent = FirebaseAuthActivity.createIntent(ApplicationProvider.getApplicationContext(), FirebaseAuthActivity.AUTH_ACTION.LOGOUT, doneIntent);
        try (ActivityScenario<Activity> activityScenario = ActivityScenario.launch(logoutIntent)) {
            Thread.sleep(3000);
            Intents.intended(IntentMatchers.hasComponent(GreetingActivity.class.getName()));
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        Intents.release();
    }

    @Test
    public void testDelete() {
        loginSync("foo@example.com");
        Intents.init();
        Intent deleteIntent = FirebaseAuthActivity.createIntent(ApplicationProvider.getApplicationContext(), FirebaseAuthActivity.AUTH_ACTION.DELETE, doneIntent);
        try (ActivityScenario<Activity> activityScenario = ActivityScenario.launch(deleteIntent)) {
            Thread.sleep(3000);
            Intents.intended(IntentMatchers.hasComponent(GreetingActivity.class.getName()));
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        Intents.release();
    }

    @Test
    public void testUpdate() {
        loginSync("foo@example.com");

        String initDisplayName = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getDisplayName();
        Task<Void> result = FirebaseAuthActivity.update(new UserProfileChangeRequest.Builder()
                .setDisplayName(initDisplayName + "updated")
                .build());
        try {
            Tasks.await(result);

            assertThat(FirebaseAuth.getInstance().getCurrentUser().getDisplayName(), is(initDisplayName + "updated"));
        } catch (ExecutionException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void testPromptLoginDoNow() {
        Intents.init();
        try (ActivityScenario<Activity> activityScenario = ActivityScenario.launch(new Intent(ApplicationProvider.getApplicationContext(), MainActivity.class))) {
            activityScenario.onActivity(activity -> FirebaseAuthActivity.promptLogin(ApplicationProvider.getApplicationContext(), activity, new Intent(ApplicationProvider.getApplicationContext(), GreetingActivity.class)));

            ViewInteraction loginButton = onView(ViewMatchers.withId(R.id.loginPromptLoginButton));
            loginButton.check(matches(ViewMatchers.isDisplayed()));

            loginSync("test@example.com");

            loginButton.perform(ViewActions.click());

            loginButton.check(doesNotExist());
            Intents.intended(IntentMatchers.hasComponent(FirebaseAuthActivity.class.getName()));
        }
        Intents.release();
        logoutSync();
    }

    @Test
    public void testPromptLoginLater() {
        Intents.init();
        try (ActivityScenario<Activity> activityScenario = ActivityScenario.launch(new Intent(ApplicationProvider.getApplicationContext(), MainActivity.class))) {
            activityScenario.onActivity(activity -> FirebaseAuthActivity.promptLogin(ApplicationProvider.getApplicationContext(), activity, new Intent(ApplicationProvider.getApplicationContext(), GreetingActivity.class)));

            ViewInteraction laterButton = onView(ViewMatchers.withId(R.id.loginPromptLaterButton));
            laterButton.check(matches(ViewMatchers.isDisplayed()));

            laterButton.perform(ViewActions.click());

            laterButton.check(doesNotExist());
        }
        Intents.release();
        logoutSync();
    }
}

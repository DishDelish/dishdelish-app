package com.github.siela1915.bootcamp.firebase;

import static androidx.test.core.app.ApplicationProvider.getApplicationContext;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThrows;

import android.net.Uri;
import android.security.keystore.UserNotAuthenticatedException;

import com.github.siela1915.bootcamp.FirebaseAuthActivity;
import com.github.siela1915.bootcamp.FirebaseAuthActivityTest;
import com.github.siela1915.bootcamp.firebase.User;
import com.github.siela1915.bootcamp.firebase.UserDatabase;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessaging;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.Objects;
import java.util.concurrent.ExecutionException;

public class UserDatabaseTest {
    private UserDatabase userDb;

    @Before
    public void prepareEmulator() {
        FirebaseInstanceManager.emulator = true;

        if (userDb == null) {
            userDb = new UserDatabase(FirebaseInstanceManager.getDatabase(getApplicationContext()));
        }

        if (FirebaseInstanceManager.getAuth().getCurrentUser() != null) {
            FirebaseAuthActivityTest.logoutSync();
        }
    }

    @After
    public void cleanUp() {
        FirebaseAuthActivityTest.logoutSync();
    }

    @Test
    public void getInexistentUserReturnsNull() {
        try {
            User inexistent = Tasks.await(userDb.getUser("inexistent"));
            assertThat(inexistent, is(nullValue()));
        } catch (ExecutionException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void setThenGetReturnsNewUser() {
        User testUser = new User();
        testUser.setId("setThenGetReturnsNewUser");
        testUser.setEmail("setThenGetReturnsNewUser@example.com");
        testUser.setDisplayName("setThenGet ReturnsNewUser");
        testUser.setPhotoUrl("https://setThenGet.ReturnsNewUser");

        try {
            User dbUser = Tasks.await(userDb.updateUser(testUser).continueWithTask(task -> userDb.getUser("setThenGetReturnsNewUser")));
            assertThat(dbUser.getDisplayName(), is(testUser.getDisplayName()));
            assertThat(dbUser.getEmail(), is(testUser.getEmail()));
            assertThat(dbUser.getPhotoUrl(), is(testUser.getPhotoUrl()));
            assertThat(dbUser.getId(), is(testUser.getId()));
        } catch (ExecutionException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void updateProfileWithFirebaseUser() {
        FirebaseAuthActivityTest.loginSync("preUpdateProfile@example.com");
        User testUser = new User();
        String userId = Objects.requireNonNull(FirebaseInstanceManager.getAuth().getCurrentUser()).getUid();
        testUser.setId(userId);
        testUser.setEmail("preUpdateProfile@example.com");
        testUser.setDisplayName("preUpdate Profile");
        testUser.setPhotoUrl("https://preUpdate.Profile");

        UserProfileChangeRequest changeUser = new UserProfileChangeRequest.Builder()
                .setDisplayName("postUpdate Profile")
                .setPhotoUri(null)
                .build();
        try {
            User dbUser = Tasks.await(userDb.updateUser(testUser)
                    .continueWithTask(task -> FirebaseAuthActivity.update(changeUser))
                    .continueWithTask(task -> Objects.requireNonNull(FirebaseInstanceManager.getAuth().getCurrentUser()).updateEmail("postUpdateProfile@example.com"))
                    .continueWithTask(task -> userDb.updateProfile(Objects.requireNonNull(FirebaseInstanceManager.getAuth().getCurrentUser())))
                    .continueWithTask(task -> userDb.getUser(userId)));
            assertThat(dbUser.getEmail(), is("postUpdateProfile@example.com"));
            assertThat(dbUser.getPhotoUrl(), is(nullValue()));
            assertThat(dbUser.getDisplayName(), is("postUpdate Profile"));
        } catch (ExecutionException | InterruptedException e) {
            throw new RuntimeException(e);
        }

        FirebaseAuthActivityTest.logoutSync();
    }

    @Test
    public void addDeviceTokenToExistingUser() {
        FirebaseAuthActivityTest.loginSync("addDeviceTokenToExisting@example.com");
        User testUser = new User();
        String userId = Objects.requireNonNull(FirebaseInstanceManager.getAuth().getCurrentUser()).getUid();
        testUser.setId(userId);
        testUser.setEmail("addDeviceTokenToExisting@example.com");
        testUser.setDisplayName("addDeviceToken ToExisting");
        testUser.setPhotoUrl("https://addDeviceToken.ToExisting");

        userDb.updateUser(testUser);

        try {
            User dbUser = Tasks.await(FirebaseAuthActivity.update(new UserProfileChangeRequest.Builder()
                    .setPhotoUri(new Uri.Builder().appendPath(testUser.getPhotoUrl()).build()).build())
                    .continueWithTask(task -> userDb.addDeviceToken())
                    .continueWithTask(task -> userDb.getUser(userId)));
            assertThat(dbUser.getTokens().size(), is(1));
            String token = Tasks.await(FirebaseMessaging.getInstance().getToken());
            assertThat(dbUser.getTokens().get(token), is(notNullValue()));
        } catch (ExecutionException | InterruptedException e) {
            throw new RuntimeException(e);
        }

        FirebaseAuthActivityTest.logoutSync();
    }

    @Test
    public void addDeviceTokenToInexistentUser() {
        FirebaseAuthActivityTest.loginSync("addDeviceTokenToInexistent@example.com");
        String userId = Objects.requireNonNull(FirebaseInstanceManager.getAuth().getCurrentUser()).getUid();
        try {
            User dbUser = Tasks.await(userDb.addDeviceToken()
                    .continueWithTask(task -> userDb.getUser(userId)));
            assertThat(dbUser.getTokens().size(), is(1));
            String token = Tasks.await(FirebaseMessaging.getInstance().getToken());
            assertThat(dbUser.getTokens().get(token), is(notNullValue()));
        } catch (ExecutionException | InterruptedException e) {
            throw new RuntimeException(e);
        }

        FirebaseAuthActivityTest.logoutSync();
    }

    @Test
    public void removeExistentDeviceToken() {
        FirebaseAuthActivityTest.loginSync("removeExistentDeviceToken@example.com");
        User testUser = new User();
        String userId = Objects.requireNonNull(FirebaseInstanceManager.getAuth().getCurrentUser()).getUid();
        testUser.setId(userId);
        testUser.setEmail("removeExistentDeviceToken@example.com");
        testUser.setDisplayName("removeExistent DeviceToken");
        testUser.setPhotoUrl("https://removeExistent.DeviceToken");

        userDb.updateUser(testUser);

        try {
            User dbUser = Tasks.await(userDb.addDeviceToken()
                    .continueWithTask(task -> userDb.removeDeviceToken())
                    .continueWithTask(task -> userDb.getUser(userId)));
            assertThat(dbUser.getTokens().size(), is(0));
            String token = Tasks.await(FirebaseMessaging.getInstance().getToken());
            assertThat(dbUser.getTokens().get(token), is(nullValue()));
        } catch (ExecutionException | InterruptedException e) {
            throw new RuntimeException(e);
        }

        FirebaseAuthActivityTest.logoutSync();
    }

    @Test
    public void removeInexistentDeviceToken() {
        FirebaseAuthActivityTest.loginSync("removeInexistentDeviceToken@example.com");
        User testUser = new User();
        String userId = Objects.requireNonNull(FirebaseInstanceManager.getAuth().getCurrentUser()).getUid();
        testUser.setId(userId);
        testUser.setEmail("removeInexistentDeviceToken@example.com");
        testUser.setDisplayName("removeInexistent DeviceToken");
        testUser.setPhotoUrl("https://removeInexistent.DeviceToken");

        userDb.updateUser(testUser);

        try {
            User dbUser = Tasks.await(userDb.removeDeviceToken()
                    .continueWithTask(task -> userDb.getUser(userId)));
            assertThat(dbUser.getTokens().size(), is(0));
            String token = Tasks.await(FirebaseMessaging.getInstance().getToken());
            assertThat(dbUser.getTokens().get(token), is(nullValue()));
        } catch (ExecutionException | InterruptedException e) {
            throw new RuntimeException(e);
        }

        FirebaseAuthActivityTest.logoutSync();
    }

    @Test
    public void removeDeviceTokenFromInexistentUser() {
        FirebaseAuthActivityTest.loginSync("removeDeviceTokenFromInexistentUser@example.com");
        String userId = Objects.requireNonNull(FirebaseInstanceManager.getAuth().getCurrentUser()).getUid();

        try {
            User dbUser = Tasks.await(userDb.removeDeviceToken()
                    .continueWithTask(task -> userDb.getUser(userId)));
            assertThat(dbUser, is(nullValue()));
        } catch (ExecutionException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void addOrRemoveDeviceTokenWhileLoggedOut() {
        assertThrows(UserNotAuthenticatedException.class, () -> {
            try {
                Task<Void> task = userDb.removeDeviceToken();
                Tasks.await(task);
            } catch (ExecutionException exception) {
                if (exception.getCause() != null) {
                    throw exception.getCause();
                }
            }
        });
        assertThrows(UserNotAuthenticatedException.class, () -> {
            try {
                Task<Void> task = userDb.addDeviceToken();
                Tasks.await(task);
            } catch (ExecutionException exception) {
                if (exception.getCause() != null) {
                    throw exception.getCause();
                }
            }
        });
    }
}

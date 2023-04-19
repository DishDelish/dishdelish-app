package com.github.siela1915.bootcamp.firebase;

import static androidx.test.core.app.ApplicationProvider.getApplicationContext;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThrows;

import android.location.Location;
import android.util.Pair;

import com.github.siela1915.bootcamp.FirebaseAuthActivityTest;
import com.github.siela1915.bootcamp.Recipes.ExampleRecipes;
import com.github.siela1915.bootcamp.Recipes.Ingredient;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import org.junit.Before;
import org.junit.Test;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutionException;

public class LocationDatabaseTest {
    private LocationDatabase locDb;

    @Before
    public void prepareEmulator() {
        FirebaseApp.clearInstancesForTest();
        FirebaseApp.initializeApp(getApplicationContext());
        FirebaseAuth.getInstance().useEmulator("10.0.2.2", 9099);
        FirebaseDatabase.getInstance().useEmulator("10.0.2.2", 9000);

        if (locDb == null) {
            locDb = new LocationDatabase();
        }

        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            FirebaseAuthActivityTest.logoutSync();
        }
    }

    @Test
    public void setThenGetInRadiusReturnsSingleObject() {
        FirebaseAuthActivityTest.loginSync("test@example.com");

        Location loc = new Location("test");
        loc.setLongitude(15.0);
        loc.setLatitude(5.0);
        try {
            Tasks.await(locDb.updateLocation(loc));
            List<Pair<String, Location>> list = Tasks.await(locDb.getNearby(loc));
            assertThat(list.size(), is(1));
            assertThat(list.get(0).first, is(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid()));
            assertThat(list.get(0).second, equalTo(loc));
        } catch (ExecutionException | InterruptedException e) {
            throw new RuntimeException(e);
        }
        FirebaseAuthActivityTest.logoutSync();
    }

    @Test
    public void setThenGetOfferedReturnsCorrectIngredient() {
        FirebaseAuthActivityTest.loginSync("test@example.com");
        Ingredient ing = ExampleRecipes.recipes.get(0).getIngredientList().get(0);

        try {
            Tasks.await(locDb.updateOffered(ing));
            Ingredient res = Tasks.await(locDb.getOffered(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid()));
            assertThat(res, equalTo(ing));
        } catch (ExecutionException | InterruptedException e) {
            throw new RuntimeException(e);
        }
        FirebaseAuthActivityTest.logoutSync();
    }

    @Test
    public void updateLocationLoggedOutReturnsExceptionTask() {
        Location loc = new Location("test");
        loc.setLongitude(15.0);
        loc.setLatitude(5.0);

        assertThrows(ExecutionException.class, () -> Tasks.await(locDb.updateLocation(loc)));
    }

    @Test
    public void getNearbyLoggedOutReturnsExceptionTask() {
        Location loc = new Location("test");
        loc.setLongitude(15.0);
        loc.setLatitude(5.0);

        assertThrows(ExecutionException.class, () -> Tasks.await(locDb.getNearby(loc)));
    }

    @Test
    public void updateOfferedLoggedOutReturnsExceptionTask() {
        Ingredient ing = ExampleRecipes.recipes.get(0).getIngredientList().get(0);

        assertThrows(ExecutionException.class, () -> Tasks.await(locDb.updateOffered(ing)));
    }
}

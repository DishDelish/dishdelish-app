package com.github.siela1915.bootcamp.firebase;

import static androidx.test.core.app.ApplicationProvider.getApplicationContext;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.lessThan;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThrows;

import android.location.Location;
import android.util.Pair;

import com.github.siela1915.bootcamp.FirebaseAuthActivityTest;
import com.github.siela1915.bootcamp.Recipes.Ingredient;
import com.github.siela1915.bootcamp.Recipes.Unit;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import org.junit.Before;
import org.junit.Test;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutionException;

public class LocationDatabaseTest {
    private LocationDatabase locDb;
    private Database db;

    @Before
    public void prepareEmulator() {
        FirebaseApp.clearInstancesForTest();
        FirebaseApp.initializeApp(getApplicationContext());
        FirebaseAuth.getInstance().useEmulator("10.0.2.2", 9099);
        FirebaseDatabase.getInstance().useEmulator("10.0.2.2", 9000);

        if (locDb == null) {
            locDb = new LocationDatabase();
        }

        if (db == null) {
            db = new Database(FirebaseDatabase.getInstance());
        }

        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            FirebaseAuthActivityTest.logoutSync();
        }
    }

    @Test
    public void setThenGetInRadiusReturnsSingleObject() {
        FirebaseAuthActivityTest.loginSync("test@example.com");

        Location loc = new Location("LocationDatabase");
        loc.setLongitude(15.0);
        loc.setLatitude(5.0);
        try {
            Tasks.await(locDb.updateLocation(loc));
            List<Pair<String, Location>> list = Tasks.await(locDb.getNearby(loc));
            assertThat(list.size(), is(1));
            assertThat(list.get(0).first, is(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid()));
            assertThat(list.get(0).second.distanceTo(loc), lessThan(0.001f));
        } catch (ExecutionException | InterruptedException e) {
            throw new RuntimeException(e);
        }
        FirebaseAuthActivityTest.logoutSync();
    }

    @Test
    public void setThenGetOfferedReturnsCorrectIngredient() {
        FirebaseAuthActivityTest.loginSync("test@example.com");
        List<Ingredient> ing = Collections.singletonList(new Ingredient("Test", new Unit(3, "pieces")));

        Location loc = new Location("LocationDatabase");
        loc.setLongitude(15.0);
        loc.setLatitude(5.0);
        try {
            Tasks.await(locDb.updateLocation(loc));
            Tasks.await(db.updateFridge(ing));
            Tasks.await(locDb.updateOffered(Collections.singletonList(0)));
            List<Ingredient> res = Tasks.await(locDb.getOfferedIngredients(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid()));
            assertNotNull(res);
            assertThat(res, containsInAnyOrder(ing.toArray(new Ingredient[0])));
        } catch (ExecutionException | InterruptedException e) {
            throw new RuntimeException(e);
        }
        FirebaseAuthActivityTest.logoutSync();
    }

    @Test
    public void updateLocationLoggedOutReturnsExceptionTask() {
        Location loc = new Location("LocationDatabase");
        loc.setLongitude(15.0);
        loc.setLatitude(5.0);

        assertThrows(ExecutionException.class, () -> Tasks.await(locDb.updateLocation(loc)));
    }

    @Test
    public void getNearbyLoggedOutReturnsExceptionTask() {
        Location loc = new Location("LocationDatabase");
        loc.setLongitude(15.0);
        loc.setLatitude(5.0);

        assertThrows(ExecutionException.class, () -> Tasks.await(locDb.getNearby(loc)));
    }

    @Test
    public void updateOfferedLoggedOutReturnsExceptionTask() {
        List<Integer> ing = Collections.singletonList(0);

        assertThrows(ExecutionException.class, () -> Tasks.await(locDb.updateOffered(ing)));
    }
}

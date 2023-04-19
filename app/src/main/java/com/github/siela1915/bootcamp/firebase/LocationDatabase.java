package com.github.siela1915.bootcamp.firebase;

import android.location.Location;
import android.security.keystore.UserNotAuthenticatedException;
import android.util.Pair;

import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQuery;
import com.firebase.geofire.GeoQueryEventListener;
import com.github.siela1915.bootcamp.Recipes.Ingredient;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskCompletionSource;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LocationDatabase {
    private final String HELP = "help";
    private final DatabaseReference db;
    private final GeoFire geoFire;

    public LocationDatabase() {
        this.db = FirebaseDatabase.getInstance().getReference(HELP);
        this.geoFire = new GeoFire(db);
    }

    public Task<Void> updateLocation(Location location) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            return Tasks.forException(new UserNotAuthenticatedException("User needs to be authenticated to store location"));
        }

        TaskCompletionSource<Void> tcs = new TaskCompletionSource<>();
        geoFire.setLocation(user.getUid(), new GeoLocation(location.getLatitude(), location.getLongitude()),
                (key, error) -> {
                    if (error != null) {
                        tcs.setException(error.toException());
                    } else {
                        tcs.setResult(null);
                    }
                });

        return tcs.getTask();
    }

    public Task<Void> updateOffered(Ingredient ing) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            return Tasks.forException(new UserNotAuthenticatedException("User needs to be authenticated to store location"));
        }

        return db.child(HELP + "/" + user.getUid() + "/offered").setValue(ing);
    }

    public Task<Ingredient> getOffered(String userId) {
        return db.child(HELP + "/" + userId + "/offered").get()
                .continueWith(dataTask -> dataTask.getResult().getValue(Ingredient.class));
    }

    public Task<List<Pair<String, Location>>> getNearby(Location location) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            return Tasks.forException(new UserNotAuthenticatedException("User needs to be authenticated to store location"));
        }

        GeoQuery geoQuery = geoFire.queryAtLocation(new GeoLocation(location.getLatitude(), location.getLongitude()), 0.6);

        TaskCompletionSource<List<Pair<String, Location>>> res = new TaskCompletionSource<>();
        List<Pair<String, Location>> resList = new ArrayList<>();

        geoQuery.addGeoQueryEventListener(new GeoQueryEventListener() {
            @Override
            public void onKeyEntered(String key, GeoLocation location) {
                Location loc = new Location("LocationDatabase");
                loc.setLatitude(location.latitude);
                loc.setLongitude(location.longitude);
                resList.add(new Pair<>(key, loc));
            }

            @Override
            public void onKeyExited(String key) {
                System.out.printf("Key %s is no longer in the search area%n", key);
            }

            @Override
            public void onKeyMoved(String key, GeoLocation location) {
                System.out.printf("Key %s moved within the search area to [%f,%f]%n", key, location.latitude, location.longitude);
            }

            @Override
            public void onGeoQueryReady() {
                res.setResult(resList);
                geoQuery.removeAllListeners();
            }

            @Override
            public void onGeoQueryError(DatabaseError error) {
                res.setException(error.toException());
                geoQuery.removeAllListeners();
            }
        });

        return res.getTask();
    }
}

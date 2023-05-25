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
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class LocationDatabase {
    private final String HELP = "help";
    private final DatabaseReference dbRef;
    private final Database db;
    private final GeoFire geoFire;

    public LocationDatabase(FirebaseDatabase db) {
        this.dbRef = db.getReference(HELP);
        this.geoFire = new GeoFire(this.dbRef);
        this.db = new Database(db);
    }

    public Task<Void> updateLocation(Location location) {
        FirebaseUser user = FirebaseInstanceManager.getAuth().getCurrentUser();
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

    public Task<Void> updateOffered(List<Integer> indexes) {
        FirebaseUser user = FirebaseInstanceManager.getAuth().getCurrentUser();
        if (user == null) {
            return Tasks.forException(new UserNotAuthenticatedException("User needs to be authenticated to store location"));
        }

        return dbRef.child(user.getUid() + "/offered").setValue(indexes, "ff");
    }

    public Task<List<Ingredient>> getOfferedIngredients(String userId) {
        return dbRef.child(userId + "/offered").get()
                .continueWithTask(offersTask -> {
                    Set<Integer> indexes = new HashSet<>();
                    for (DataSnapshot ds : offersTask.getResult().getChildren()) {
                        indexes.add(ds.getValue(Integer.class));
                    }
                    return db.getFridge().continueWith(fridgeTask -> {
                        List<Ingredient> list = new ArrayList<>();
                        for (int i = 0; i < fridgeTask.getResult().size(); ++i) {
                            if (indexes.contains(i)) {
                                list.add(fridgeTask.getResult().get(i));
                            }
                        }
                        return list;
                    });
                });
    }

    public Task<List<Integer>> getOffered(String userId) {
        return dbRef.child(userId + "/offered").get()
                .continueWith(offersTask -> {
                    List<Integer> indexes = new ArrayList<>();
                    for (DataSnapshot ds : offersTask.getResult().getChildren()) {
                        indexes.add(ds.getValue(Integer.class));
                    }
                    return indexes;
                });
    }

    public Task<List<Pair<String, Location>>> getNearby(Location location) {
        FirebaseUser user = FirebaseInstanceManager.getAuth().getCurrentUser();
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

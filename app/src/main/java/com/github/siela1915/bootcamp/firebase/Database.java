package com.github.siela1915.bootcamp.firebase;

import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.internal.api.FirebaseNoSignedInUserException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

/**
 * Java class which connects to the Firebase database to set and retrieve data
 *
 */
public class Database {

    private DatabaseReference db;
    private final static String RECIPES = "recipes";
    private final static String FAVORITES = "favorites";

    public Database() {
        db = FirebaseDatabase.getInstance().getReference();
    }



    public String get(String uniqueKey) {
        for (int i = 0; i < 5; ++i) {
            Task<DataSnapshot> task = db.child(RECIPES).child(uniqueKey).get();
            try {
                DataSnapshot snapshot = Tasks.await(task);
                return snapshot.getValue() == null ? null : snapshot.getValue().toString();
            } catch (ExecutionException | InterruptedException e) {
                continue;
            }
        }
        return null;
    }

    public String set(Map<String, Object> value) {
        String uniqueKey = db.child(RECIPES).child("new").push().getKey();
        db.child(RECIPES).child(uniqueKey).updateChildren(value);
        return uniqueKey;
    }

    public void remove(String key) {
        db.child(RECIPES).child(key).removeValue();
    }

    public String getByName(String name) {
        Query query = db.child(RECIPES).orderByChild("name");
        Task<DataSnapshot> task = query.get();
        try {
            DataSnapshot snapshot = Tasks.await(task);
            return snapshot.getValue() == null ? null : snapshot.getValue().toString();
        } catch (ExecutionException | InterruptedException e) {
            throw new RuntimeException(e.getMessage());
        }

    }

    public Task<Void> addFavorite(String recipeId) {
        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            return Tasks.forException(new FirebaseNoSignedInUserException("Sign in to add favorites"));
        }
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        Task<DataSnapshot> existing = db.child(FAVORITES + "/" + userId + "/" + recipeId).get();
        return existing.continueWithTask(t -> {
            if (t.getResult().getValue() != null) {
                return Tasks.forResult(null);
            }
            return db.child(FAVORITES + "/" + userId).updateChildren(Collections.singletonMap(recipeId, System.currentTimeMillis()));
        });
    }

    public Task<Void> removeFavorite(String recipeId) {
        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            return Tasks.forException(new FirebaseNoSignedInUserException("Sign in to remove favorites"));
        }
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        return db.child(FAVORITES + "/" + userId + "/" + recipeId).removeValue();
    }

    public Task<List<String>> getFavorites() {
        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            return Tasks.forException(new FirebaseNoSignedInUserException("Sign in to get favorites"));
        }
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        Query query = db.child(FAVORITES + "/" + userId).orderByValue();
        Task<DataSnapshot> task = query.get();
        return task.continueWith(snapshot -> {
            List<String> favs = new ArrayList<>();
            for (DataSnapshot recipe : snapshot.getResult().getChildren()) {
                favs.add(recipe.getKey());
            }
            return favs;
        });
    }
}

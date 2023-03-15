package com.github.siela1915.bootcamp.firebase;

import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import java.util.Map;
import java.util.concurrent.ExecutionException;

/**
 * Java class which connects to the Firebase database to set and retrieve data
 *
 */
public class Database {

    private DatabaseReference db;
    private final static String RECIPES = "recipes";

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

}

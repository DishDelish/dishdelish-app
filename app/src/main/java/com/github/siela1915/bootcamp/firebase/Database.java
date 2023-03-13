package com.github.siela1915.bootcamp.firebase;

import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Map;
import java.util.concurrent.ExecutionException;

/**
 * Java class which connects to the Firebase database to set and retrieve data
 *
 */
public class Database {

    private DatabaseReference db;

    public Database() {
        db = FirebaseDatabase.getInstance().getReference();
    }

    public String get(String key) {
        for (int i = 0; i < 5; ++i) {
            Task<DataSnapshot> task = db.child("recipes").child(key).get();
            try {
                DataSnapshot snapshot = Tasks.await(task);
                return snapshot.getValue().toString();
            } catch (ExecutionException | InterruptedException e) {
                continue;
            }
        }
        return null;
    }

    public void set(String key, Map<String, Object> value) {
        db.child("recipes").child(key).updateChildren(value);
    }

}

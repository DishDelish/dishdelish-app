package com.github.siela1915.bootcamp.firebase;

import android.annotation.SuppressLint;
import android.content.Context;

import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

public class FirebaseInstanceManager {
    public static boolean emulator = false;
    public static FirebaseDatabase db;

    @SuppressLint("VisibleForTests")
    public static FirebaseDatabase getDatabase(Context context) {
        if (db == null) {
            db = FirebaseDatabase.getInstance();
        }
        if (emulator) {
            try {
                db.useEmulator("10.0.2.2", 9000);
            } catch (IllegalStateException exception) {
                if (exception.getMessage() != null &&
                        exception.getMessage().contains("after instance has already been")) {
                    if (!db.getReference().toString().equals("http://10.0.2.2:9000")) {
                        FirebaseApp.clearInstancesForTest();
                        FirebaseApp.initializeApp(context);

                        db = null;

                        return getDatabase(context);
                    }
                } else {
                    throw exception;
                }
            }
        }

        return db;
    }

    public static FirebaseAuth getAuth() {
        FirebaseAuth auth = FirebaseAuth.getInstance();

        if (emulator) {
            try {
                auth.useEmulator("10.0.2.2", 9099);
            } catch (IllegalStateException exception) {
                if (exception.getMessage() == null ||
                        !exception.getMessage().contains("after instance has already been")) {
                    throw exception;
                }
            }
        }

        return auth;
    }
}

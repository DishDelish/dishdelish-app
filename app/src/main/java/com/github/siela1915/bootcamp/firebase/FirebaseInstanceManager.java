package com.github.siela1915.bootcamp.firebase;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

public class FirebaseInstanceManager {
    public static boolean emulator = false;

    public static FirebaseDatabase getDatabase() {
        FirebaseDatabase db = FirebaseDatabase.getInstance();

        if (emulator) {
            try {
                db.useEmulator("10.0.2.2", 9000);
            } catch (IllegalStateException exception) {
                if (exception.getMessage() == null ||
                        !exception.getMessage().contains("after instance has already been") ||
                        !db.getReference().toString().equals("http://10.0.2.2:9000")) {
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

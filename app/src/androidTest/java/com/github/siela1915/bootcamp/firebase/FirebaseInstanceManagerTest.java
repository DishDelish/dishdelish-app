package com.github.siela1915.bootcamp.firebase;

import static androidx.test.core.app.ApplicationProvider.getApplicationContext;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;

import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import org.junit.Before;
import org.junit.Test;

public class FirebaseInstanceManagerTest {
    @Before
    public void clearEmulator() {
        FirebaseApp.clearInstancesForTest();
        FirebaseApp.initializeApp(getApplicationContext());
    }

    @Test
    public void withoutEmulatorGivesCorrectDatabase() {
        FirebaseDatabase db = FirebaseInstanceManager.getDatabase(getApplicationContext());

        assertThat(db.getReference().toString(), is(not("http://10.0.2.2:9000")));

        FirebaseDatabase db2 = FirebaseInstanceManager.getDatabase(getApplicationContext());

        assertThat(db2.getReference().toString(), is(not("http://10.0.2.2:9000")));
    }

    @Test
    public void withEmulatorGivesCorrectDatabase() {
        FirebaseInstanceManager.emulator = true;

        FirebaseDatabase db = FirebaseInstanceManager.getDatabase(getApplicationContext());

        assertThat(db.getReference().toString(), is("http://10.0.2.2:9000"));

        FirebaseDatabase db2 = FirebaseInstanceManager.getDatabase(getApplicationContext());

        assertThat(db2.getReference().toString(), is("http://10.0.2.2:9000"));
    }

    @Test
    public void withoutEmulatorGivesAuthentication() {
        FirebaseAuth auth = FirebaseInstanceManager.getAuth();
    }

    @Test
    public void withEmulatorGivesCorrectAuthentication() {
        FirebaseInstanceManager.emulator = true;

        FirebaseAuth auth = FirebaseInstanceManager.getAuth();
    }
}

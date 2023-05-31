package com.github.siela1915.bootcamp.firebase;

import static androidx.core.content.ContextCompat.getSystemService;
import static androidx.test.core.app.ApplicationProvider.getApplicationContext;
import static com.github.siela1915.bootcamp.firebase.PushNotificationService.sendNotification;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.Intent;

import com.github.siela1915.bootcamp.FirebaseAuthActivityTest;
import com.github.siela1915.bootcamp.MainHomeActivity;

import org.junit.After;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

public class PushNotificationServiceTest {
    @After
    public void tearDown() {
        NotificationManager manager = getSystemService(getApplicationContext(), NotificationManager.class);
        if (manager != null) {
            manager.cancelAll();
        }
    }

    @After
    public void cleanUp() {
        FirebaseAuthActivityTest.logoutSync();
    }

    @Test
    public void testSendNotificationWithOnlyTitleAndBody() {
        Map<String, String> notifMap = new HashMap<>();
        notifMap.put("title", "TestNotification");
        notifMap.put("body", "Body text of the test notification");

        Intent intent = new Intent(getApplicationContext(), MainHomeActivity.class);
        sendNotification(getApplicationContext(), notifMap, intent);
        NotificationManager manager = getSystemService(getApplicationContext(), NotificationManager.class);

        assertThat(manager, is(notNullValue()));
        assertThat(manager.getActiveNotifications().length, is(greaterThan(0)));
        Notification notif = manager.getActiveNotifications()[0].getNotification();

        assertThat(notif.extras.get(Notification.EXTRA_TITLE), is("TestNotification"));
        assertThat(notif.extras.get(Notification.EXTRA_TEXT), is("Body text of the test notification"));
    }
}

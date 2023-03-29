package com.github.siela1915.bootcamp.firebase;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.github.siela1915.bootcamp.MainHomeActivity;
import com.github.siela1915.bootcamp.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class PushNotificationService extends FirebaseMessagingService {
    private UserDatabase userDb;
    private final String TAG = "PushNotificationService";

    @Override
    public void onCreate() {
        super.onCreate();

        userDb = new UserDatabase();
        createNotificationChannel();
    }

    @Override
    public void onMessageReceived(@NonNull RemoteMessage message) {
        super.onMessageReceived(message);

        // TODO(developer): Handle FCM messages here.
        // Not getting messages here? See why this may be: https://goo.gl/39bRNJ
        Log.d(TAG, "From: " + message.getFrom());

        // Check if message contains a data payload.
        if (message.getData().size() > 0) {
            Log.d(TAG, "Message data payload: " + message.getData());

        }

        // Check if message contains a notification payload.
        if (message.getNotification() != null) {
            Log.d(TAG, "Message Notification Body: " + message.getNotification().getBody());
            Map<String, String> notifMap = new HashMap<>();
            notifMap.put("title", message.getNotification().getTitle());
            notifMap.put("body", message.getNotification().getBody());
            notifMap.put("color", message.getNotification().getColor());
            notifMap.put("channelId", message.getNotification().getChannelId());
            sendNotification(this, notifMap);
        }
    }

    @Override
    public void onNewToken(@NonNull String token) {
        super.onNewToken(token);

        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            userDb.addDeviceToken();
        }
    }

    static void sendNotification(Context context, Map<String, String> notification) {
        Intent intent = new Intent(context, MainHomeActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0 /* Request code */, intent,
                PendingIntent.FLAG_IMMUTABLE);

        String channelId = Optional.ofNullable(notification.get("channelId")).orElse(context.getString(R.string.default_notification_channel_id));
        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(context, channelId)
                        .setSmallIcon(R.drawable.chef_hat)
                        .setContentTitle(notification.get("title"))
                        .setContentText(notification.get("body"))
                        .setAutoCancel(true)
                        .setSound(defaultSoundUri)
                        .setContentIntent(pendingIntent);

        if (notification.containsKey("color")) {
            notificationBuilder.setColor(Color.parseColor(notification.get("color").toUpperCase()));
        }

        NotificationManager notificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        String idString = notification.get("id");
        if (idString == null) {
            idString = "0";
        }
        notificationManager.notify(Integer.parseInt(idString), notificationBuilder.build());
    }

    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getString(R.string.default_notification_channel_id);
            String description = getString(R.string.default_notification_channel_description);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(name.toString(), name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }
}

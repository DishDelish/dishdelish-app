package com.github.siela1915.bootcamp.firebase;

import android.security.keystore.UserNotAuthenticatedException;

import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.HashMap;
import java.util.Map;

public class UserDatabase {
    private final String USERS = "users";
    private final DatabaseReference db;

    public UserDatabase() {
        this.db = FirebaseDatabase.getInstance().getReference(USERS);
    }


    public Task<User> getUser(String userId) {
        return db.child(userId).get().continueWith(task -> task.getResult().getValue(User.class));
    }

    public Task<Void> updateUser(User user) {
        return db.child(user.getId()).setValue(user);
    }

    public Task<Void> updateProfile(FirebaseUser user) {
        Map<String, Object> profile = new HashMap<>();
        profile.put("displayName", user.getDisplayName());
        profile.put("email", user.getEmail());
        if (user.getPhotoUrl() != null) {
            profile.put("photoUrl", user.getPhotoUrl().toString());
        }
        profile.put("id", user.getUid());
        return db.child(user.getUid()).updateChildren(profile);
    }

    public Task<Void> addDeviceToken() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            return getUser(user.getUid()).continueWithTask(userTask -> {
                User fullUser = userTask.getResult();
                if (fullUser == null) {
                    fullUser = new User();
                    if (user.getPhotoUrl() != null) {
                        fullUser.setPhotoUrl(user.getPhotoUrl().toString());
                    }
                    fullUser.setId(user.getUid());
                    fullUser.setEmail(user.getEmail());
                    fullUser.setDisplayName(user.getDisplayName());
                }
                User finalFullUser = fullUser;
                return FirebaseMessaging.getInstance().getToken().continueWithTask(tokenTask -> {
                    String token = tokenTask.getResult();
                    finalFullUser.addToken(token);
                    return updateUser(finalFullUser);
                });
            });
        }

        return Tasks.forException(new UserNotAuthenticatedException("User needs to be authenticated to add device token"));
    }

    public Task<Void> removeDeviceToken() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            return getUser(user.getUid()).continueWithTask(userTask -> {
                User fullUser = userTask.getResult();
                if (fullUser == null) {
                    return Tasks.forResult(null);
                }
                return FirebaseMessaging.getInstance().getToken().continueWithTask(tokenTask -> {
                    String token = tokenTask.getResult();
                    if (fullUser.removeToken(token)) {
                        return updateUser(fullUser);
                    }
                    return Tasks.forResult(null);
                });
            });
        }

        return Tasks.forException(new UserNotAuthenticatedException("User needs to be authenticated to add device token"));
    }
}

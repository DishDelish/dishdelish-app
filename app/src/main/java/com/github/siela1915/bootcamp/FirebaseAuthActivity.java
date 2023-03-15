package com.github.siela1915.bootcamp;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract;
import com.firebase.ui.auth.IdpResponse;
import com.firebase.ui.auth.data.model.FirebaseAuthUIAuthenticationResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

import java.util.Collections;
import java.util.List;
import java.util.Locale;

public class FirebaseAuthActivity extends AppCompatActivity {
    enum AUTH_ACTION {
        LOGIN,
        LOGOUT,
        DELETE
    }

    private Intent postAuthIntent;

    // See: https://developer.android.com/training/basics/intents/result
    private final ActivityResultLauncher<Intent> signInLauncher = registerForActivityResult(
            new FirebaseAuthUIActivityResultContract(),
            this::onSignInResult
    );

    @NonNull
    public static Intent createIntent(@NonNull Context context, AUTH_ACTION action, Intent postActionIntent) {
        return new Intent(context, FirebaseAuthActivity.class)
                .putExtra("com.github.siela1915.bootcamp.postauthintent", postActionIntent)
                .putExtra("com.github.siela1915.bootcamp.authaction", action);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        postAuthIntent = getIntent().getParcelableExtra("com.github.siela1915.bootcamp.postauthintent");
        AUTH_ACTION auth_action = (AUTH_ACTION) getIntent().getSerializableExtra("com.github.siela1915.bootcamp.authaction");

        if (auth_action == null) {
            finish();
            return;
        }
        switch (auth_action) {
            case LOGIN:
                signIn();
                break;
            case LOGOUT:
                signOut();
                break;
            case DELETE:
                delete();
                break;
        }
    }

    private void onSignInResult(FirebaseAuthUIAuthenticationResult result) {
        IdpResponse response = result.getIdpResponse();
        if (result.getResultCode() == RESULT_OK) {
            // Successfully signed in
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            if (user != null) {
                // Launch supplied intent with the user info
                finish();
                startActivity(postAuthIntent);
            }
        }
        if (response != null && response.getError() != null) { // If response is null, user canceled with back button, so no popup
            // inflate the layout of the popup window
            LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
            @SuppressLint("InflateParams") View popupView = inflater.inflate(R.layout.popup_window, null);

            TextView popupTextView = popupView.findViewById(R.id.popupText);
            popupTextView.setText(String.format(Locale.ENGLISH, "Login failed with error: %s", response.getError().getLocalizedMessage()));

            int width_height = LinearLayout.LayoutParams.WRAP_CONTENT;
            boolean focusable = true; // lets taps outside the popup also dismiss it
            final PopupWindow popupWindow = new PopupWindow(popupView, width_height, width_height, focusable);

            popupWindow.showAtLocation(findViewById(android.R.id.content).getRootView(), Gravity.CENTER, 0, 0);
            popupWindow.setOnDismissListener(this::onBackPressed);

            // dismiss the popup window when touched
            popupView.setOnTouchListener((v, event) -> {
                v.performClick();
                popupWindow.dismiss();

                return true;
            });
        }
    }

    public void signIn() {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        if (auth.getCurrentUser() != null) {
            finish();
            startActivity(postAuthIntent);
        } else {
            // Choose authentication providers
            List<AuthUI.IdpConfig> providers = Collections.singletonList(
                    new AuthUI.IdpConfig.GoogleBuilder().build());
            // Create and launch sign-in intent
            Intent signInIntent = AuthUI.getInstance()
                    .createSignInIntentBuilder()
                    .setAvailableProviders(providers)
                    .setIsSmartLockEnabled(!BuildConfig.DEBUG /* credentials */, true /* hints */)
                    .build();
            signInLauncher.launch(signInIntent);
        }
    }

    public void signOut() {
        AuthUI.getInstance()
                .signOut(this)
                .addOnCompleteListener(task -> {
                    finish();
                    startActivity(postAuthIntent);
                });
    }

    public void delete() {
        AuthUI.getInstance()
                .delete(this)
                .addOnCompleteListener(task -> {
                    finish();
                    startActivity(postAuthIntent);
                });
    }

    public static void update(UserProfileChangeRequest changeRequest) {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        if (auth.getCurrentUser() != null) {
            FirebaseUser user = auth.getCurrentUser();
            user.updateProfile(changeRequest);
        }
    }

}

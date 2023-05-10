package com.github.siela1915.bootcamp;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.Manifest;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

import java.io.InputStream;
import java.util.concurrent.CompletableFuture;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ProfileFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ProfileFragment extends Fragment {
    // Declare the launcher at the top of your Activity/Fragment:
    private final ActivityResultLauncher<String> requestPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted) {
                    // FCM SDK (and your app) can post notifications.
                } else {
                    // TODO: Inform user that that your app will not show notifications.
                }
            });
    
    public ProfileFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment ProfileFragment.
     */
    public static ProfileFragment newInstance() {
        return new ProfileFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        Intent navToProfile = new Intent(getActivity(), MainHomeActivity.class);
        navToProfile.putExtra("com.github.siela1915.bootcamp.navToProfile", true);
        if (user == null) {
            view.findViewById(R.id.profileLoggedOut).setVisibility(View.VISIBLE);
            view.findViewById(R.id.profileLoggedIn).setVisibility(View.INVISIBLE);
            view.findViewById(R.id.profileTextFixed).setVisibility(View.INVISIBLE);
            view.findViewById(R.id.profileTextEdit).setVisibility(View.INVISIBLE);

            Button loginButton = view.findViewById(R.id.profileLogin);
            loginButton.setOnClickListener((v -> startActivity(FirebaseAuthActivity.createIntent((requireActivity()), FirebaseAuthActivity.AUTH_ACTION.LOGIN, navToProfile))));
        } else {
            view.findViewById(R.id.profileLoggedOut).setVisibility(View.INVISIBLE);
            view.findViewById(R.id.profileLoggedIn).setVisibility(View.VISIBLE);
            view.findViewById(R.id.profileTextFixed).setVisibility(View.VISIBLE);
            view.findViewById(R.id.profileTextEdit).setVisibility(View.INVISIBLE);

            TextView displayName = view.findViewById(R.id.profileDisplayName);
            TextView email = view.findViewById(R.id.profileEmail);
            EditText displayNameEdit = view.findViewById(R.id.profileDisplayNameEdit);
            ImageView profilePicture = view.findViewById(R.id.profilePicture);

            if (user.getPhotoUrl() != null) {
                profilePicture.setImageBitmap(null);
                CompletableFuture.supplyAsync(() -> {
                    Bitmap mIcon11 = null;
                    try {
                        InputStream in = new java.net.URL(user.getPhotoUrl().toString()).openStream();
                        mIcon11 = BitmapFactory.decodeStream(in);
                    } catch (Exception e) {
                        Log.e("Error", e.getMessage());
                        e.printStackTrace();
                    }
                    return mIcon11;
                }).thenAccept(profilePicture::setImageBitmap);
            }

            displayName.setText(user.getDisplayName());
            email.setText(user.getEmail());
            displayNameEdit.setText(user.getDisplayName());

            Button logoutButton = view.findViewById(R.id.profileLogout);
            logoutButton.setOnClickListener((v -> startActivity(FirebaseAuthActivity.createIntent((requireActivity()), FirebaseAuthActivity.AUTH_ACTION.LOGOUT, navToProfile))));

            Button editButton = view.findViewById(R.id.profileEdit);
            editButton.setOnClickListener(v -> {
                view.findViewById(R.id.profileTextFixed).setVisibility(View.INVISIBLE);
                view.findViewById(R.id.profileTextEdit).setVisibility(View.VISIBLE);
            });
            Button submitButton = view.findViewById(R.id.profileSubmit);
            submitButton.setOnClickListener(v -> {
                view.findViewById(R.id.profileTextFixed).setVisibility(View.VISIBLE);
                view.findViewById(R.id.profileTextEdit).setVisibility(View.INVISIBLE);

                FirebaseAuthActivity.update(new UserProfileChangeRequest.Builder()
                        .setDisplayName(displayNameEdit.getText().toString())
                        .build());

                displayName.setText(displayNameEdit.getText());
            });
        }
    }
}
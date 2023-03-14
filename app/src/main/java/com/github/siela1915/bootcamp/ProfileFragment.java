package com.github.siela1915.bootcamp;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ProfileFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ProfileFragment extends Fragment {

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
        ProfileFragment fragment = new ProfileFragment();
        return fragment;
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
        if (user == null) {
            view.findViewById(R.id.profileLoggedOut).setVisibility(View.VISIBLE);
            view.findViewById(R.id.profileLoggedIn).setVisibility(View.INVISIBLE);
        } else {
            view.findViewById(R.id.profileLoggedOut).setVisibility(View.INVISIBLE);
            view.findViewById(R.id.profileLoggedIn).setVisibility(View.VISIBLE);

            TextView displayName = view.findViewById(R.id.profileDisplayName);
            TextView username = view.findViewById(R.id.profileUsername);
            TextView email = view.findViewById(R.id.profileEmail);
            ImageView picture = view.findViewById(R.id.profilePicture);
            displayName.setText(user.getDisplayName());
            username.setText(user.getUid());
            email.setText(user.getEmail());
            picture.setImageURI(user.getPhotoUrl());
        }
    }

    public void loginClicked(View view) {
        Intent loggedIn = new Intent(getActivity(), MainActivity.class);
        startActivity(FirebaseAuthActivity.createIntent(getActivity(), FirebaseAuthActivity.AUTH_ACTION.LOGIN, loggedIn));
    }

    public void logoutClicked(View view) {
        Intent loggedIn = new Intent(getActivity(), MainActivity.class);
        startActivity(FirebaseAuthActivity.createIntent(getActivity(), FirebaseAuthActivity.AUTH_ACTION.LOGOUT, loggedIn));
    }
}
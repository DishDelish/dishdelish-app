package com.github.siela1915.bootcamp;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.Group;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link NearbyHelpFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class NearbyHelpFragment extends Fragment {

    public NearbyHelpFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment NearbyHelpFragment.
     */
    public static NearbyHelpFragment newInstance() {
        NearbyHelpFragment fragment = new NearbyHelpFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
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
        return inflater.inflate(R.layout.fragment_nearby_help, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Group selectionGroup = view.findViewById(R.id.chooseHelpGroup);
        Group askGroup = view.findViewById(R.id.askHelpGroup);
        selectionGroup.setVisibility(View.VISIBLE);
        askGroup.setVisibility(View.INVISIBLE);

        Button askSelectionButton = view.findViewById(R.id.askHelpButton);
        Button offerSelectionButton = view.findViewById(R.id.offerHelpButton);

        askSelectionButton.setOnClickListener(v -> {
            askGroup.setVisibility(View.VISIBLE);
            selectionGroup.setVisibility(View.INVISIBLE);
        });
    }
}
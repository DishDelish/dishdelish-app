package com.github.siela1915.bootcamp;

import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.Group;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.github.siela1915.bootcamp.Recipes.Ingredient;
import com.github.siela1915.bootcamp.Recipes.Unit;
import com.github.siela1915.bootcamp.firebase.LocationDatabase;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link NearbyHelpFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class NearbyHelpFragment extends Fragment {
    private final LocationDatabase locDb = new LocationDatabase();
    private FusedLocationProviderClient fusedLocationClient;

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
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext());
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
        Group offerGroup = view.findViewById(R.id.offerHelpGroup);
        selectionGroup.setVisibility(View.VISIBLE);
        askGroup.setVisibility(View.INVISIBLE);
        offerGroup.setVisibility(View.INVISIBLE);

        Button askSelectionButton = view.findViewById(R.id.askHelpButton);
        Button offerSelectionButton = view.findViewById(R.id.offerHelpButton);
        Button submitAskHelp = view.findViewById(R.id.submitAskHelpButton);
        Button submitOfferHelp = view.findViewById(R.id.submitOfferHelpButton);

        askSelectionButton.setOnClickListener(v -> {
            askGroup.setVisibility(View.VISIBLE);
            selectionGroup.setVisibility(View.INVISIBLE);
        });

        offerSelectionButton.setOnClickListener(v -> {
            offerGroup.setVisibility(View.VISIBLE);
            selectionGroup.setVisibility(View.INVISIBLE);
        });

        submitAskHelp.setOnClickListener(v -> {
            if (ActivityCompat.checkSelfPermission(requireContext(), android.Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(requireContext(), android.Manifest.permission.ACCESS_COARSE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }

            fusedLocationClient.getLastLocation()
                    .continueWithTask(locTask -> locDb.getNearby(locTask.getResult()))
                    .continueWith(nearbyTask -> {
                        List<Pair<String, Location>> nearby = nearbyTask.getResult();

                        nearby.forEach(pair -> {
                            locDb.getOffered(pair.first).continueWith(offeredTask -> {
                                Log.d("OFFERED", String.format("%s offered %s near %s",
                                        pair.first, offeredTask.getResult().toString(),
                                        pair.second.toString()));
                                return null;
                            });
                        });

                        return null;
                    });
        });

        submitOfferHelp.setOnClickListener(v -> {
            EditText offeredInput = view.findViewById(R.id.offeredIngredient);
            Ingredient offered = new Ingredient(offeredInput.getText().toString(), new Unit());
            fusedLocationClient.getLastLocation()
                    .continueWithTask(locTask -> locDb.updateLocation(locTask.getResult()))
                    .continueWithTask(task -> locDb.updateOffered(offered));
        });
    }
}
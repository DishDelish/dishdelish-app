package com.github.siela1915.bootcamp;

import static android.content.Context.LAYOUT_INFLATER_SERVICE;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.util.Pair;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.Group;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentContainerView;

import com.github.siela1915.bootcamp.Recipes.Ingredient;
import com.github.siela1915.bootcamp.Recipes.Unit;
import com.github.siela1915.bootcamp.firebase.LocationDatabase;
import com.github.siela1915.bootcamp.firebase.PushNotificationService;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMapOptions;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link NearbyHelpFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class NearbyHelpFragment extends Fragment implements OnMapReadyCallback, GoogleMap.OnInfoWindowClickListener {
    private final LocationDatabase locDb = new LocationDatabase();
    private FusedLocationProviderClient fusedLocationClient;
    private String asked = "";
    private final Map<Marker, Pair<String, Ingredient>> offers = new HashMap<>();

    private final ActivityResultLauncher<String> requestPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (!isGranted) {
                    // TODO: Inform user that that your app will not access location.
                }
            });

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

    @SuppressLint("MissingPermission")
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        askLocationPermission();

        Group selectionGroup = view.findViewById(R.id.chooseHelpGroup);
        Group askGroup = view.findViewById(R.id.askHelpGroup);
        Group offerGroup = view.findViewById(R.id.offerHelpGroup);
        FragmentContainerView map = view.findViewById(R.id.map);
        selectionGroup.setVisibility(View.VISIBLE);
        askGroup.setVisibility(View.INVISIBLE);
        offerGroup.setVisibility(View.INVISIBLE);
        map.setVisibility(View.INVISIBLE);

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
            askGroup.setVisibility(View.INVISIBLE);
            map.setVisibility(View.VISIBLE);

            EditText askedInput = view.findViewById(R.id.askedIngredient);
            asked = askedInput.getText().toString();

            GoogleMapOptions options = new GoogleMapOptions();

            options.zoomControlsEnabled(true);
            SupportMapFragment mapFragment = SupportMapFragment.newInstance(options);
            requireActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .add(R.id.map, mapFragment)
                    .commit();
            mapFragment.getMapAsync(this);
        });

        submitOfferHelp.setOnClickListener(v -> {
            EditText offeredInput = view.findViewById(R.id.offeredIngredient);
            Ingredient offered = new Ingredient(offeredInput.getText().toString(), new Unit());
            fusedLocationClient.getLastLocation()
                    .continueWithTask(locTask -> locDb.updateLocation(locTask.getResult()))
                    .continueWithTask(task -> locDb.updateOffered(offered));
        });
    }


    @SuppressLint("MissingPermission")
    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        googleMap.setOnInfoWindowClickListener(this);
        fusedLocationClient.getLastLocation()
                .continueWithTask(locTask -> {
                    googleMap.moveCamera(CameraUpdateFactory.newLatLng(
                            new LatLng(locTask.getResult().getLatitude(),
                            locTask.getResult().getLongitude())));
                    return locDb.getNearby(locTask.getResult());
                })
                .continueWith(nearbyTask -> {
                    List<Pair<String, Location>> nearby = nearbyTask.getResult();

                    nearby.forEach(pair -> locDb.getOffered(pair.first).continueWith(offeredTask -> {
                        if (offeredTask.getResult().toString().contains(asked.toLowerCase())) {
                            Marker marker = googleMap.addMarker(new MarkerOptions()
                                    .position(new LatLng(pair.second.getLatitude(), pair.second.getLongitude()))
                                    .title(String.format(Locale.ENGLISH,"%s: (Lat: %f Lon: %f)",
                                            offeredTask.getResult().getIngredient(),
                                            pair.second.getLatitude(), pair.second.getLongitude()))
                                    .snippet(pair.first)
                            );
                            offers.put(marker, new Pair<>(pair.first, offeredTask.getResult()));
                        }
                        return null;
                    }));

                    return null;
                });
    }

    @Override
    public void onInfoWindowClick(@NonNull Marker marker) {
        Pair<String, Ingredient> offer = offers.get(marker);
        if (offer != null) {
            PushNotificationService.sendRemoteNotification(offer.first, offer.second)
                    .addOnSuccessListener(result -> {
                        // inflate the layout of the popup window
                        LayoutInflater inflater = (LayoutInflater) requireContext().getSystemService(LAYOUT_INFLATER_SERVICE);
                        @SuppressLint("InflateParams") View popupView = inflater.inflate(R.layout.error_popup, null);

                        TextView popupTextView = popupView.findViewById(R.id.popupText);
                        popupTextView.setText("Sent request!");

                        int width_height = LinearLayout.LayoutParams.WRAP_CONTENT;
                        boolean focusable = true; // lets taps outside the popup also dismiss it
                        final PopupWindow popupWindow = new PopupWindow(popupView, width_height, width_height, focusable);

                        popupWindow.showAtLocation(requireActivity().findViewById(android.R.id.content).getRootView(), Gravity.CENTER, 0, 0);

                        // dismiss the popup window when touched
                        popupView.setOnTouchListener((v, event) -> {
                            v.performClick();
                            popupWindow.dismiss();

                            return true;
                        });
                    });
        }
    }

    private void askLocationPermission() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION) !=
                        PackageManager.PERMISSION_GRANTED) {
            // Directly ask for the permission
            requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION);
        }
    }
}
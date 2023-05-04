package com.github.siela1915.bootcamp;

import static android.content.Context.LAYOUT_INFLATER_SERVICE;
import static com.google.android.gms.location.Priority.PRIORITY_BALANCED_POWER_ACCURACY;

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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

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
    public static final String ARG_REPLY_OFFER_UID = "reply-offer-uid";
    public static final String ARG_REPLY_INGREDIENT = "reply-ingredient";
    private String mReplyOfferUid = null;
    private String mReplyIngredient = null;

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
    public static NearbyHelpFragment newInstance(String replyOfferUid, String replyIngredient) {
        NearbyHelpFragment fragment = new NearbyHelpFragment();
        Bundle args = new Bundle();
        args.putString(ARG_REPLY_OFFER_UID, replyOfferUid);
        args.putString(ARG_REPLY_INGREDIENT, replyIngredient);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        askLocationPermission();

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext());

        if (getArguments() != null && getArguments().getString(ARG_REPLY_OFFER_UID) != null) {
            mReplyOfferUid = getArguments().getString(ARG_REPLY_OFFER_UID);
            mReplyIngredient = getArguments().getString(ARG_REPLY_INGREDIENT);
        }
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

        Group selectionGroup = view.findViewById(R.id.chooseHelpGroup);
        Group askGroup = view.findViewById(R.id.askHelpGroup);
        Group offerGroup = view.findViewById(R.id.offerHelpGroup);
        Group replyGroup = view.findViewById(R.id.replyHelpGroup);
        FragmentContainerView map = view.findViewById(R.id.map);
        selectionGroup.setVisibility(View.VISIBLE);
        askGroup.setVisibility(View.INVISIBLE);
        offerGroup.setVisibility(View.INVISIBLE);
        map.setVisibility(View.INVISIBLE);
        replyGroup.setVisibility(View.INVISIBLE);

        if (mReplyOfferUid != null) {
            selectionGroup.setVisibility(View.INVISIBLE);
            replyGroup.setVisibility(View.VISIBLE);

            Button sendReplyButton = view.findViewById(R.id.sendReplyHelpButton);

            sendReplyButton.setOnClickListener(v -> {
                EditText replyInput = view.findViewById(R.id.replyInputHelp);
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                if (user == null) {
                    replyInput.setText("Can't reply without being authenticated");
                    return;
                }
                PushNotificationService.sendRemoteNotification(mReplyOfferUid,
                        String.format(Locale.ENGLISH, "Reply from %s for %s",
                                user.getDisplayName(), mReplyIngredient),
                        replyInput.getText().toString(), null);
            });
        }

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
            Ingredient offered = new Ingredient(offeredInput.getText().toString(), null);
            fusedLocationClient.getLastLocation()
                    .continueWithTask(locTask -> locDb.updateLocation(locTask.getResult()))
                    .continueWithTask(task -> locDb.updateOffered(offered));
        });
    }


    @SuppressLint("MissingPermission")
    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        googleMap.setOnInfoWindowClickListener(this);
        fusedLocationClient.getCurrentLocation(PRIORITY_BALANCED_POWER_ACCURACY, null)
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
                                    .title(offeredTask.getResult().toString())
                                    .snippet(String.format(Locale.ENGLISH, "(Lat: %f Lon: %f)",
                                            pair.second.getLatitude(), pair.second.getLongitude()))
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
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (offer != null && user != null) {
            Map<String, String> data = new HashMap<>();
            data.put("ingredient", offer.second.toString());
            PushNotificationService.sendRemoteNotification(offer.first,
                            String.format(Locale.ENGLISH, "%s is interested in %s",
                                    user.getDisplayName(), offer.second.getIngredient()),
                            String.format(Locale.ENGLISH, "%s is interested in %s. Answer him now!",
                                    user.getDisplayName(), offer.second),
                            data)
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
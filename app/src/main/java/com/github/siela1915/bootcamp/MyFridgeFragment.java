package com.github.siela1915.bootcamp;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import com.github.siela1915.bootcamp.AutocompleteApi.IngredientAutocomplete;
import com.github.siela1915.bootcamp.Recipes.Ingredient;
import com.github.siela1915.bootcamp.UploadRecipe.RecipeStepAndIngredientManager;
import com.github.siela1915.bootcamp.firebase.Database;
import com.github.siela1915.bootcamp.firebase.LocationDatabase;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 */
public class MyFridgeFragment extends Fragment {
    private Database db;
    private final LocationDatabase locDb = new LocationDatabase();
    private final IngredientAutocomplete apiService = new IngredientAutocomplete();
    private final Map<String, Integer> idMap = new HashMap<>();
    private RecipeStepAndIngredientManager ingredientManager;
    private FusedLocationProviderClient fusedLocationClient;

    private final ActivityResultLauncher<String> requestPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (!isGranted) {
                    // TODO: Inform user that that your app will not access location.
                }
            });

    public MyFridgeFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        db = new Database(FirebaseDatabase.getInstance());

        askLocationPermission();

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_my_fridge, container, false);
    }

    @SuppressLint({"SetTextI18n", "MissingPermission"})
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        LinearLayout ingLayout = view.findViewById(R.id.myFridgeIngredientLayout);
        Button addIngredient = view.findViewById(R.id.myFridgeAddIngredient);
        Button updateFridge = view.findViewById(R.id.submitMyFridgeButton);
        Button updateOffered = view.findViewById(R.id.updateOfferedFridge);

        ingredientManager = new RecipeStepAndIngredientManager(requireContext(), null, ingLayout);

        addIngredient.setOnClickListener(v -> ingredientManager.addIngredient(idMap, apiService, true, null));

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            Task<List<Ingredient>> ingTask = db.getFridge();
            Task<List<Integer>> indexTask = locDb.getOffered(user.getUid());

            Tasks.whenAll(ingTask, indexTask)
                    .addOnSuccessListener(empty -> {
                        for (Ingredient ing : ingTask.getResult()) {
                            ingredientManager.addIngredient(idMap, apiService, true,
                                    Arrays.asList(
                                            Integer.toString(ing.getUnit().getValue()),
                                            ing.getUnit().getInfo(),
                                            ing.getIngredient()));
                        }
                        for (Integer i : indexTask.getResult()) {
                            ingLayout.getChildAt(i).findViewById(R.id.offerIngredientItem).setActivated(true);
                            ingLayout.getChildAt(i).findViewById(R.id.offerIngredientItem).setBackgroundColor(Color.GREEN);
                        }
                    });

            updateFridge.setOnClickListener(v -> {
                if (ingredientManager.isIngredientValid()) {
                    List<Ingredient> fridge = ingredientManager.getIngredients();
                    db.updateFridge(fridge);
                }
            });

            updateOffered.setOnClickListener(v -> {
                if (ingredientManager.isIngredientValid()) {
                    List<Integer> offered = ingredientManager.getOfferable();
                    fusedLocationClient.getLastLocation()
                            .continueWithTask(locTask -> locDb.updateLocation(locTask.getResult()))
                            .continueWithTask(task -> locDb.updateOffered(offered));
                }
            });
        }
    }

    private void askLocationPermission() {
        if (ContextCompat.checkSelfPermission(requireContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(requireContext(), android.Manifest.permission.ACCESS_COARSE_LOCATION) !=
                        PackageManager.PERMISSION_GRANTED) {
            // Directly ask for the permission
            requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION);
        }
    }
}
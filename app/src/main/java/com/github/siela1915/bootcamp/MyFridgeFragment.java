package com.github.siela1915.bootcamp;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.Group;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.github.siela1915.bootcamp.AutocompleteApi.IngredientAutocomplete;
import com.github.siela1915.bootcamp.Recipes.Ingredient;
import com.github.siela1915.bootcamp.UploadRecipe.RecipeStepAndIngredientManager;
import com.github.siela1915.bootcamp.firebase.Database;
import com.github.siela1915.bootcamp.firebase.LocationDatabase;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 */
public class MyFridgeFragment extends Fragment {
    private Database db;
    private final IngredientAutocomplete apiService = new IngredientAutocomplete();
    private final Map<String, Integer> idMap = new HashMap<>();
    private RecipeStepAndIngredientManager ingredientManager;

    public MyFridgeFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        db = new Database(FirebaseDatabase.getInstance());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_my_fridge, container, false);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        LinearLayout ingLayout = view.findViewById(R.id.myFridgeIngredientLayout);
        Button addIngredient = view.findViewById(R.id.myFridgeAddIngredient);
        Button updateFridge = view.findViewById(R.id.submitMyFridgeButton);

        ingredientManager = new RecipeStepAndIngredientManager(requireContext(), null, ingLayout);

        addIngredient.setOnClickListener(v -> ingredientManager.addIngredient(idMap, apiService));

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            db.getFridge()
                    .addOnSuccessListener(list -> {
                        for (Ingredient ing : list) {
                            ingredientManager.addIngredient(idMap, apiService);
                            int lastIndex = ingLayout.getChildCount() - 1;
                            if (ingLayout.getChildAt(lastIndex) instanceof ConstraintLayout) {
                                ConstraintLayout step = (ConstraintLayout) ingLayout.getChildAt(lastIndex);
                                if (step.getChildAt(0) instanceof TextInputLayout && step.getChildAt(1) instanceof TextInputLayout && step.getChildAt(2) instanceof TextInputLayout) {
                                    EditText ingredientEdit = ((TextInputLayout) step.getChildAt(2)).getEditText();
                                    if (ingredientEdit != null) {
                                        ingredientEdit.setText(ing.getIngredient());
                                    }
                                    EditText unitNameEdit = ((TextInputLayout) step.getChildAt(1)).getEditText();
                                    if (unitNameEdit != null) {
                                        unitNameEdit.setText(ing.getUnit().getInfo());
                                    }
                                    EditText unitValueEdit = ((TextInputLayout) step.getChildAt(0)).getEditText();
                                    if (unitValueEdit != null) {
                                        unitValueEdit.setText(Integer.toString(ing.getUnit().getValue()));
                                    }
                                }
                            }
                        }
                    });

            updateFridge.setOnClickListener(v -> {
                if (ingredientManager.isIngredientValid()) {
                    List<Ingredient> fridge = ingredientManager.getIngredients();
                    db.updateFridge(fridge);
                }
            });
        }
    }
}
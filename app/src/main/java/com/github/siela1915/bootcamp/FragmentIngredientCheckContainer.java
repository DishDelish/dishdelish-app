package com.github.siela1915.bootcamp;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.siela1915.bootcamp.Recipes.Ingredient;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FragmentIngredientCheckContainer#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FragmentIngredientCheckContainer extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ingredients = "ingredients";

    // TODO: Rename and change types of parameters
    private ArrayList<Ingredient> ing;

    public FragmentIngredientCheckContainer() {
        // Required empty public constructor
    }


    public static FragmentIngredientCheckContainer newInstance(ArrayList<Ingredient> ingList) {
        FragmentIngredientCheckContainer fragment = new FragmentIngredientCheckContainer();
        Bundle args = new Bundle();
        args.putParcelableArrayList(ingredients, ingList);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            ing = getArguments().getParcelableArrayList(ingredients);
        }
        FragmentIngredientCheck initialFrag= FragmentIngredientCheck.newInstance(ing);
        getParentFragmentManager().beginTransaction()
                .add(R.id.ingredientContainer,initialFrag)
                .addToBackStack("initial")
                .commit();
        if (getArguments() != null) {
            ing = getArguments().getParcelableArrayList(ingredients);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_ingredient_check_container, container, false);
    }
}
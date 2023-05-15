package com.github.siela1915.bootcamp;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Selection;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.github.siela1915.bootcamp.Recipes.ExampleRecipes;
import com.github.siela1915.bootcamp.Recipes.Ingredient;
import com.github.siela1915.bootcamp.Recipes.Recipe;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FragmentIngredientCheck#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FragmentIngredientCheck extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private Recipe recipe;
    private RecyclerView recyclerView;
    private Button shoppingCartBtn,nearbyBtn;
    private IngredientCheckAdapter adapter;
    private ShoppingListManager shoppingManager;

    public FragmentIngredientCheck() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment FragmentIngredientCheck.
     */
    // TODO: Rename and change types and number of parameters
    public static FragmentIngredientCheck newInstance(String param1, String param2) {
        FragmentIngredientCheck fragment = new FragmentIngredientCheck();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_ingredient_check, container, false);
        //recipe= getArguments().getParcelable("key");
        recipe= ExampleRecipes.recipes.get(0);
        recyclerView = view.findViewById(R.id.neededIngredientsRV);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(),2));
        List<String> ingredients = extractNameOfIngredient(recipe.getIngredientList());
        adapter = new IngredientCheckAdapter(ingredients);
        recyclerView.setAdapter(adapter);
        shoppingCartBtn = view.findViewById(R.id.addToShoppingListBtn);
        nearbyBtn=view.findViewById(R.id.nearByBtn);
        shoppingManager = new ShoppingListManager(getContext());
        shoppingCartBtn.setOnClickListener(v -> {
            for(String item: adapter.getSelectedItems()){
                shoppingManager.addIngredient(item);
                System.out.println(item);
            }
            ShoppingCartFragment fragment = new ShoppingCartFragment();
            getParentFragmentManager().beginTransaction()
                    .replace(R.id.ingredientContainer,fragment)
                    .addToBackStack(null)
                    .commit();
        });
        nearbyBtn.setOnClickListener(v->{
            NearbyHelpFragment fragment= new NearbyHelpFragment();
            getParentFragmentManager().beginTransaction()
                    .replace(R.id.ingredientContainer,fragment)
                    .addToBackStack(null)
                    .commit();
        });
        return view;
    }

    private List<String> extractNameOfIngredient(List<Ingredient> ingredientList) {
        List<String> result= new ArrayList<>();
        for(Ingredient ingredient: ingredientList){
            result.add(ingredient.toString());
        }
        return result;
    }
}
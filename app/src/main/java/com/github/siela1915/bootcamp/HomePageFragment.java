package com.github.siela1915.bootcamp;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.github.siela1915.bootcamp.Recipes.ExampleRecipes;
import com.github.siela1915.bootcamp.Recipes.Recipe;

import com.github.siela1915.bootcamp.Recipes.RecipeItemAdapter;
import com.github.siela1915.bootcamp.firebase.Database;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HomePageFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomePageFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private  String cuis= "";
    private TextView homeTextView;
    private RecyclerView recipeListRecyclerView;
    private final FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();

    private final Database database = new Database(firebaseDatabase);
    private RecipeItemAdapter recipeAdapter;

    public RecipeItemAdapter getRecipeAdapter() {
        return recipeAdapter;
    }
    
    public HomePageFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment HomePageFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static HomePageFragment newInstance(String param1, String param2) {
        HomePageFragment fragment = new HomePageFragment();
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
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_home_page, container, false);
        //homeTextView = view.findViewById(R.id.homeFragTextView);
        Bundle data= getArguments();
        if(data != null){
            cuis+= data.getString("selected cuisine");
        }
        //homeTextView.setText(cuis);
        //homeTextView.setTextSize(30);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Button button = view.findViewById(R.id.homeFragButton);
        recipeListRecyclerView= view.findViewById(R.id.rand_recipe_recyclerView);
        recipeListRecyclerView.setHasFixedSize(true);
        recipeListRecyclerView.setLayoutManager(new GridLayoutManager(getContext(),2));

        database.getNRandomAsync(20).addOnSuccessListener(list->{
                    recipeAdapter =new RecipeItemAdapter(list, view.getContext());
                    recipeListRecyclerView.setAdapter(this.recipeAdapter/*new RecipeItemAdapter(list, view.getContext())*/);
        })
                .addOnFailureListener(e->{
                    e.printStackTrace();
                    recipeListRecyclerView.setAdapter(new RecipeItemAdapter(ExampleRecipes.recipes, view.getContext()));
                });
        button.setOnClickListener(v -> {
            //Recipe recipe = ExampleRecipes.recipes.get((int)(Math.random()*2.999));
            database.getByNameAsync("omelettte1")
                    .addOnSuccessListener(recipes -> startActivity(RecipeConverter.convertToIntent(recipes.get(0), getContext())))
                    .addOnFailureListener(e -> {
                        e.printStackTrace();
                        throw new RuntimeException(e);
                    });
        });
    }
}
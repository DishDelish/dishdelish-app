package com.github.siela1915.bootcamp;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.github.siela1915.bootcamp.Labelling.AllergyType;
import com.github.siela1915.bootcamp.Labelling.CuisineType;
import com.github.siela1915.bootcamp.Labelling.DietType;
import com.github.siela1915.bootcamp.Labelling.RecipeFetcher;
import com.github.siela1915.bootcamp.Recipes.ExampleRecipes;
import com.github.siela1915.bootcamp.Recipes.PreparationTime;
import com.github.siela1915.bootcamp.Recipes.Recipe;
import com.github.siela1915.bootcamp.Recipes.RecipeItemAdapter;
import com.github.siela1915.bootcamp.Tools.SuggestionCalculator;
import com.github.siela1915.bootcamp.firebase.Database;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
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
    public static final int DIET = 1;
    public static final int CUISINE = 2;
    public static final int ALLERGY = 3;
    public static final int PREP_TIME = 4;
    private String mParam2;
    private  String cuis= "";
    private TextView homeTextView;
    private RecyclerView recipeListRecyclerView;
    private final FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();

    private final Database database = new Database(firebaseDatabase);
    private RecipeItemAdapter recipeAdapter;
    private TextView moreFilter;
    private LinearLayout filterLayout, recipeListLinearLayout;
    private SearchView searchView;
    private Button dietBtn,allergyBtn,cuisineBtn,prepTimeBtn;
    private List<Recipe> fetchedRecipes= new ArrayList<>();
    private List<Integer> selectedCuisine = new ArrayList<>();
    private List<Integer> selectedDiet = new ArrayList<>();
    private List<Integer> selectedAllery= new ArrayList<>();
    private List<Integer> selectedPrepTime= new ArrayList<>();
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
        TextView underlinedText = view.findViewById(R.id.moreFilterTextView);
        underlinedText.setPaintFlags(underlinedText.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //Button button = view.findViewById(R.id.homeFragButton);
        recipeListRecyclerView= view.findViewById(R.id.rand_recipe_recyclerView);
        recipeListRecyclerView.setHasFixedSize(true);
        recipeListRecyclerView.setLayoutManager(new GridLayoutManager(getContext(),2));
        moreFilter= view.findViewById(R.id.moreFilterTextView);
        filterLayout = view.findViewById(R.id.filter);
        recipeListLinearLayout = view.findViewById(R.id.recipeListLinearLayout);
        searchView= view.findViewById(R.id.searchView);
        searchView.clearFocus();
        dietBtn = view.findViewById(R.id.btnDiet);
        allergyBtn=view.findViewById(R.id.btnAllergy);
        cuisineBtn = view.findViewById(R.id.btnCuisine);
        //prepTimeBtn= view.findViewById(R.id.btnPrpTime);


        cuisineBtn.setOnClickListener(v -> {
            String [] cuisineTypes= CuisineType.getAll();
            boolean[] checksum= new boolean[cuisineTypes.length];
            String title = "Choose your preferred cuisine";
            popUpDialogBuilder(cuisineTypes,checksum,title,selectedCuisine,CUISINE);
        });

/*        prepTimeBtn.setOnClickListener(v -> {
            String [] prepTime= PreparationTime.getAll();
            boolean[] checksum= new boolean[prepTime.length];
            String title = "Choose the preparation time";
            popUpDialogBuilder(prepTime,checksum,title,selectedPrepTime,PREP_TIME);
        });

 */
        dietBtn.setOnClickListener(v -> {
            String [] diets= DietType.getAll();
            boolean[] checksum= new boolean[diets.length];
            String title = "Choose your diet";
            popUpDialogBuilder(diets,checksum,title,selectedDiet,DIET);
        });
        allergyBtn.setOnClickListener(v -> {
            String [] allergies= AllergyType.getAll();
            boolean[] checksum= new boolean[allergies.length];
            String title = "what are you allergic to";
            popUpDialogBuilder(allergies,checksum,title,selectedAllery,ALLERGY);
        });
        moreFilter.setOnClickListener(v->{
            if (filterLayout.getVisibility() == View.VISIBLE) {
                filterLayout.animate()
                        .alpha(0f)
                        .setDuration(200)
                        .withEndAction(new Runnable() {
                            @Override
                            public void run() {
                                filterLayout.setVisibility(View.GONE);
                                adjustMainLayoutHeight(true);
                            }
                        })
                        .start();
                moreFilter.setText("more filters");
                selectedAllery.clear();
                selectedCuisine.clear();
                selectedDiet.clear();
                selectedPrepTime.clear();
                recipeAdapter.setRecipes(fetchedRecipes);
            } else {
                moreFilter.setText("clear filters");
                filterLayout.setVisibility(View.VISIBLE);
                filterLayout.setAlpha(0f);
                filterLayout.animate()
                        .alpha(1f)
                        .setDuration(200)
                        .withEndAction(new Runnable() {
                            @Override
                            public void run() {
                                //filterLayout.setVisibility(View.VISIBLE);
                                adjustMainLayoutHeight(false);
                            }
                        })
                        .start();

            }
        });
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }
            @Override
            public boolean onQueryTextChange(String newText) {
                if(newText.isEmpty()){
                    recipeAdapter.setRecipes(fetchedRecipes);
                }
                filterRecipesByName(newText);
                return false;
            }
        });
       // database.getNRandomAsync(100).addOnSuccessListener(list->{
         //           fetchedRecipes = new ArrayList<>(list);

        SuggestionCalculator.getSuggestions(database).addOnSuccessListener(list->{
                    fetchedRecipes = list;
                    recipeAdapter =new RecipeItemAdapter(list, view.getContext());
                    recipeListRecyclerView.setAdapter(this.recipeAdapter/*new RecipeItemAdapter(list, view.getContext())*/);
        })
                .addOnFailureListener(e->{
                    e.printStackTrace();
                    //recipeAdapter = new RecipeItemAdapter(ExampleRecipes.recipes,view.getContext());
                    recipeListRecyclerView.setAdapter(new RecipeItemAdapter(ExampleRecipes.recipes,view.getContext()));
                    //fetchedRecipes= ExampleRecipes.recipes;
                });
    }

    private void filterRecipesByName(String text) {
        List<Recipe> filtered= new ArrayList<>();
        for(Recipe recipe: fetchedRecipes){
            if(recipe.getRecipeName().toLowerCase().contains(text.toLowerCase())){
                filtered.add(recipe);
            }
        }
        if (filtered.isEmpty()){
            recipeAdapter.setRecipes(filtered);
            Toast.makeText(getContext(),"No recipe found",Toast.LENGTH_SHORT).show();
        }else{
            recipeAdapter.setRecipes(filtered);
        }
    }

    private void adjustMainLayoutHeight(boolean filterLayoutGone) {

        LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) recipeListLinearLayout.getLayoutParams();

        if (filterLayoutGone) {
            layoutParams.height = 0;
            layoutParams.weight = 0.90f;
        } else {
            layoutParams.height = 0;
            layoutParams.weight = 0.75f;
        }

        recipeListLinearLayout.setLayoutParams(layoutParams);
    }
    @SuppressLint("ResourceAsColor")
    private void popUpDialogBuilder(String[] items, boolean[] checksum, String title, List<Integer> selected, int filter){
        AlertDialog.Builder builder= new AlertDialog.Builder(getContext(),R.style.AlertDialogTheme);
        builder.setTitle(title);
        builder.setCancelable(false);

        builder.setMultiChoiceItems(items,checksum,(dialog,which,isChecked)->{
            checksum[which]=isChecked;
        });
        builder.setPositiveButton("Ok", (dialog, which) -> {
            for(int i=0; i< checksum.length; i++){
                if(checksum[i]){
                    switch (filter){
                        case ALLERGY:
                            AllergyType at= AllergyType.fromString(items[i]);
                            selected.add(at.ordinal());
                            break;
                        case DIET:
                            DietType dt= DietType.fromString(items[i]);
                            selected.add(dt.ordinal());
                            break;
                        case CUISINE:
                            CuisineType ct= CuisineType.fromString(items[i]);
                            selected.add(ct.ordinal());
                            break;
                        default:
                    }
                }
            }
            RecipeFetcher recipeFetcher = new RecipeFetcher(selectedAllery,selectedCuisine,selectedDiet,fetchedRecipes);
            List<String> filteredRecipes= recipeFetcher.fetchRecipeList();
            List<Recipe> recipeList = new ArrayList<>();
            for (String recipeName: filteredRecipes){
                for(int i=0; i<fetchedRecipes.size();i++){
                    if(fetchedRecipes.get(i).recipeName.equalsIgnoreCase(recipeName)){
                        recipeList.add(fetchedRecipes.get(i));
                    }
                }

            }
            recipeAdapter.setRecipes(recipeList);
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> {
            for(int i= 0; i< checksum.length; i++){
                checksum[i]=false;
            }
            dialog.cancel();
        });

        AlertDialog dialog=builder.create();
        dialog.setOnShowListener(arg0 -> {
            dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(R.color.orange);
            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(R.color.orange);
        });
        dialog.show();
    }
}
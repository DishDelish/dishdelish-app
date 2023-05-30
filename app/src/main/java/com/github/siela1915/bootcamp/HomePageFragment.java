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
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.github.siela1915.bootcamp.Labelling.AllergyType;
import com.github.siela1915.bootcamp.Labelling.CuisineType;
import com.github.siela1915.bootcamp.Labelling.DietType;
import com.github.siela1915.bootcamp.Labelling.RecipeFetcher;
import com.github.siela1915.bootcamp.Recipes.ExampleRecipes;
import com.github.siela1915.bootcamp.Recipes.Ingredient;
import com.github.siela1915.bootcamp.Recipes.Recipe;
import com.github.siela1915.bootcamp.Recipes.RecipeItemAdapter;
import com.github.siela1915.bootcamp.Tools.SuggestionCalculator;
import com.github.siela1915.bootcamp.firebase.Database;
import com.github.siela1915.bootcamp.firebase.FirebaseInstanceManager;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HomePageFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomePageFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    public static final String ARG_MYFRIDGE = "myFridge";

    public static final int DIET = 1;
    public static final int CUISINE = 2;
    public static final int ALLERGY = 3;
    public static final int PREP_TIME = 4;
    private  String cuis= "";
    private TextView homeTextView;
    private RecyclerView recipeListRecyclerView;
    private FirebaseDatabase firebaseDatabase;

    private Database database;
    private RecipeItemAdapter recipeAdapter;
    private Button moreFilter;
    private ConstraintLayout filterLayout;
    private LinearLayout recipeListLinearLayout;
    private SearchView searchView;
    private Button dietBtn,allergyBtn,cuisineBtn,prepTimeBtn;
    private List<Recipe> fetchedRecipes= new ArrayList<>();
    private List<Ingredient> myFridge;
    private List<Integer> selectedCuisine = new ArrayList<>();
    private List<Integer> selectedDiet = new ArrayList<>();
    private List<Integer> selectedAllergy = new ArrayList<>();
    private List<Integer> selectedPrepTime= new ArrayList<>();
    public HomePageFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param fridgeContent Content of My Fridge.
     * @return A new instance of fragment HomePageFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static HomePageFragment newInstance(List<Ingredient> fridgeContent) {
        HomePageFragment fragment = new HomePageFragment();
        Bundle args = new Bundle();
        args.putParcelableArrayList(ARG_MYFRIDGE, new ArrayList<>(fridgeContent));
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            myFridge = getArguments().getParcelableArrayList(ARG_MYFRIDGE);
        }

        firebaseDatabase = FirebaseInstanceManager.getDatabase(requireContext().getApplicationContext());
        database = new Database(firebaseDatabase);
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
        CheckBox fridgeButton = view.findViewById(R.id.btnFridge);
        //prepTimeBtn= view.findViewById(R.id.btnPrpTime);


        cuisineBtn.setOnClickListener(v -> {
            String [] cuisineTypes= CuisineType.getAll();
            boolean[] checksum= new boolean[cuisineTypes.length];
            selectedCuisine.forEach(index -> checksum[index] = true);
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
            selectedDiet.forEach(index -> checksum[index] = true);
            String title = "Choose your diet";
            popUpDialogBuilder(diets,checksum,title,selectedDiet,DIET);
        });
        allergyBtn.setOnClickListener(v -> {
            String [] allergies= AllergyType.getAll();
            boolean[] checksum= new boolean[allergies.length];
            selectedAllergy.forEach(index -> checksum[index] = true);
            String title = "what are you allergic to";
            popUpDialogBuilder(allergies,checksum,title, selectedAllergy,ALLERGY);
        });

        fridgeButton.setChecked(myFridge != null);

        fridgeButton.setOnCheckedChangeListener((v, isChecked) -> {
            if (!isChecked) {
                myFridge = null;
                updateRecipes();
            } else {
                database.getFridge().addOnSuccessListener(ingredients -> {
                    myFridge = ingredients;
                    updateRecipes();
                });
            }
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
                selectedAllergy.clear();
                selectedCuisine.clear();
                selectedDiet.clear();
                selectedPrepTime.clear();
                fridgeButton.setChecked(false);
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

                updateRecipes();
            }
        });
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchView.clearFocus();
                return false;
            }
            @Override
            public boolean onQueryTextChange(String newText) {
                if(newText.isEmpty()){
                    recipeAdapter.setRecipes(fetchedRecipes);
                    searchView.clearFocus();
                }
                filterRecipesByName(newText);
                return false;
            }
        });

        SuggestionCalculator.getSuggestions(database).addOnSuccessListener(list->{
                    fetchedRecipes = list;
                    recipeAdapter =new RecipeItemAdapter(list, view.getContext());
                    recipeListRecyclerView.setAdapter(this.recipeAdapter/*new RecipeItemAdapter(list, view.getContext())*/);
                    if (myFridge != null) {
                        moreFilter.callOnClick();
                    }
        })
                .addOnFailureListener(e->{
                    e.printStackTrace();
                    recipeListRecyclerView.setAdapter(new RecipeItemAdapter(ExampleRecipes.recipes,view.getContext()));
                    if (myFridge != null) {
                        moreFilter.callOnClick();
                    }
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
            layoutParams.weight = 0.88f;
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

            updateRecipes();
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

    private void updateRecipes() {
        RecipeFetcher recipeFetcher = new RecipeFetcher(selectedAllergy,selectedCuisine,selectedDiet,fetchedRecipes);
        Set<String> filteredRecipes = new HashSet<>(recipeFetcher.fetchRecipeList());
        if (myFridge != null) {
            List<String> ingRecipes = recipeFetcher.filterByIngredients(myFridge);
            filteredRecipes.retainAll(ingRecipes);
        }

        List<Recipe> recipeList = fetchedRecipes.stream()
                .filter(recipe -> filteredRecipes.contains(recipe.recipeName))
                .collect(Collectors.toList());
        //fetchedRecipes= recipeList;
        System.out.println("\n\n\n\n\n\n recipeList size "+recipeList.size());
        System.out.println("\n\n\n\n\n\n fetechedone size "+fetchedRecipes.size());
        recipeAdapter.setRecipes(recipeList);
    }
}
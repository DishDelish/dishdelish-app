package com.github.siela1915.bootcamp;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentContainerView;
import androidx.fragment.app.FragmentManager;

import com.github.siela1915.bootcamp.Labelling.AllergyType;
import com.github.siela1915.bootcamp.Labelling.CuisineType;
import com.github.siela1915.bootcamp.Labelling.DietType;
import com.github.siela1915.bootcamp.Labelling.RecipeFetcher;
import com.github.siela1915.bootcamp.Recipes.ExampleRecipes;
import com.github.siela1915.bootcamp.Recipes.Ingredient;
import com.github.siela1915.bootcamp.Recipes.PreparationTime;
import com.github.siela1915.bootcamp.Recipes.Recipe;
import com.github.siela1915.bootcamp.UploadRecipe.UploadingRecipeFragment;
import com.github.siela1915.bootcamp.firebase.Database;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class MainHomeActivity extends AppCompatActivity {
    DrawerLayout drawerLayout;
    NavigationView navigationView;
    ActionBarDrawerToggle toggle;
    View filterView;
    FragmentContainerView fragmentContainerView;
    Button cuisineBtn,timeBtn,allergyBtn, dietBtn,filterBtn;
    FragmentManager fragmentManager;

    @SuppressLint({"MissingInflatedId", "NonConstantResourceId"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_home2);
        fragmentManager= getSupportFragmentManager();
        filterView = findViewById(R.id.scrollview);
        filterView.setVisibility(View.GONE);
        if(savedInstanceState== null){
            setContainerContent(R.id.fragContainer,HomePageFragment.class,true);
            //setContainerContent(R.id.fragContainer,FragmentIngredientCheckContainer.class,true);
        }

        handleBackPress();

        drawerLayout= findViewById(R.id.drawer_layout);
        navigationView= findViewById(R.id.navView);
        cuisineBtn =findViewById(R.id.cuisineBtn);
        timeBtn=findViewById(R.id.timingBtn);
        allergyBtn=findViewById(R.id.allergyBtn);
        dietBtn=findViewById(R.id.dietBtn);
        filterBtn=findViewById(R.id.filterBtn);


        List<String> selectedCuisine = new ArrayList<>();
        List<String> selectedDiet = new ArrayList<>();
        List<String> selectedAllery= new ArrayList<>();
        List<String> selectedPrepTime= new ArrayList<>();

        cuisineBtn.setOnClickListener(v -> {
            String [] cuisineTypes= CuisineType.getAll();
            boolean[] checksum= new boolean[cuisineTypes.length];
            String title = "Choose your preferred cuisine";
            popUpDialogBuilder(cuisineTypes,checksum,title,selectedCuisine);
        });

        dietBtn.setOnClickListener(v -> {
            String [] diets= DietType.getAll();
            boolean[] checksum= new boolean[diets.length];
            String title = "Choose your diet";
            popUpDialogBuilder(diets,checksum,title,selectedDiet);
        });
        timeBtn.setOnClickListener(v -> {
            String [] prepTime= PreparationTime.getAll();
            boolean[] checksum= new boolean[prepTime.length];
            String title = "Choose the preparation time";
            popUpDialogBuilder(prepTime,checksum,title,selectedPrepTime);
        });
        allergyBtn.setOnClickListener(v -> {
            String [] allergies= AllergyType.getAll();
            boolean[] checksum= new boolean[allergies.length];
            String title = "what are you allergic to";
            popUpDialogBuilder(allergies,checksum,title,selectedAllery);
        });
        filterBtn.setOnClickListener(v -> {
            List<Integer> allergy= new ArrayList<>();
            List<Integer> cuisineType= new ArrayList<>();
            List<Integer>  dietType= new ArrayList<>();
            for(String elem: selectedAllery){
                AllergyType at= AllergyType.fromString(elem);
                allergy.add(at.ordinal());
            }
            for(String elem: selectedDiet){
                DietType dt= DietType.fromString(elem);
                dietType.add(dt.ordinal());
            }
            for(String elem: selectedCuisine){
                CuisineType ct= CuisineType.fromString(elem);
                cuisineType.add(ct.ordinal());
            }

            RecipeFetcher recipeFetcher = new RecipeFetcher(allergy,cuisineType,dietType);
            List<String> filteredRecipes= recipeFetcher.fetchRecipeList();
            List<Recipe> recipeList = new ArrayList<>();
            for (String recipeName: filteredRecipes){
                for(int i=0; i<ExampleRecipes.recipes.size();i++){
                    if(ExampleRecipes.recipes.get(i).recipeName.equalsIgnoreCase(recipeName)){
                        recipeList.add(ExampleRecipes.recipes.get(i));
                    }
                }

            }
            setContainerContent(R.id.fragContainer,RecipeListFragment.newInstance(recipeList),false);

        });
        toggle= new ActionBarDrawerToggle(this,drawerLayout,R.string.open,R.string.close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        navigationView.setNavigationItemSelectedListener(item ->{
            filterView.setVisibility(View.GONE);
            switch (item.getItemId()){
                case R.id.menuItem_home:
                    setContainerContent(R.id.fragContainer,HomePageFragment.class,false);
                    System.out.println("\n\n\n\n inside the switch "+fragmentManager.getBackStackEntryCount()+"\n\n\n\n\n");
                    break;

                case R.id.menuItem_about:
                    setContainerContent(R.id.fragContainer,AboutPageFragment.class,false);
                    break;
                case R.id.menuItem_login:
                    setContainerContent(R.id.fragContainer,ProfileFragment.class,false);
                    break;
                case R.id.menuItem_upload:
                    setContainerContent(R.id.fragContainer, UploadingRecipeFragment.class, false);
                    break;
                case R.id.menuItem_filter:
                    if(item.isChecked()){
                        item.setChecked(false);
                        filterView.setVisibility(View.GONE);
                    }else{
                        item.setChecked(true);
                        filterView.setVisibility(View.VISIBLE);
                    }
                    fragmentContainerView= findViewById(R.id.fragContainer);
                    boolean homeFragmentCheck=fragmentContainerView.getFragment().getId()==R.id.homeFragment;
                    if(!homeFragmentCheck) {
                        setContainerContent(R.id.fragContainer,HomePageFragment.class,false);
                    }
                    break;
                case R.id.menuItem_favorites:
                    loadAndOpenFavorites();
                    break;
                case R.id.menuItem_help:
                    openHelp();
                    break;
                case R.id.menuItem_soppingCart:
                    filterView.setVisibility(View.GONE);
                    setContainerContent(R.id.fragContainer,ShoppingCartFragment.class,false);
                    break;
                default:
            }
            drawerLayout.close();
            return true;
        });

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            if (extras.containsKey("com.github.siela1915.bootcamp.navToProfile")) {
                navigationView.setCheckedItem(R.id.menuItem_login);
                setContainerContent(R.id.fragContainer, ProfileFragment.class, false);
            }

            if (extras.containsKey("com.github.siela1915.bootcamp.navToFavorites")) {
                navigationView.setCheckedItem(R.id.menuItem_favorites);
                loadAndOpenFavorites();
            }

            if (extras.containsKey("navToHelp")) {
                openHelp();
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(toggle.onOptionsItemSelected(item)){
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void loadAndOpenFavorites() {
        Intent intent = new Intent(this, MainHomeActivity.class);
        intent.putExtra("com.github.siela1915.bootcamp.navToFavorites", true);
        FirebaseAuthActivity.promptLogin(this, this, intent);

        setContainerContent(R.id.fragContainer, RecipeListFragment.newInstance(new ArrayList<>()), false);

        Database db = new Database(FirebaseDatabase.getInstance());
        db.getFavorites().addOnSuccessListener(favorites -> {
            List<Task<Recipe>> favListTasks = favorites.stream().map(db::getAsync).collect(Collectors.toList());
            Tasks.whenAll(favListTasks).addOnSuccessListener(voidRes -> {
                Fragment currentFrag = fragmentManager.findFragmentById(R.id.fragContainer);
                if (currentFrag == null || currentFrag.getClass() != RecipeListFragment.class) {
                    return;
                }
                fragmentManager.popBackStack();
                List<Recipe> favRecipes = favListTasks
                        .stream().map(Task::getResult).collect(Collectors.toList());
                setContainerContent(R.id.fragContainer, RecipeListFragment.newInstance(
                        favRecipes
                ), false);
            });
        });
    }

    private void openHelp() {
        Bundle extras = getIntent().getExtras();
        navigationView.setCheckedItem(R.id.menuItem_help);
        Intent intent = new Intent(this, MainHomeActivity.class);
        if (extras != null) {
            intent.putExtras(extras);
        }
        intent.putExtra("navToHelp", true);
        FirebaseAuthActivity.promptLogin(this, this, intent);

        if (extras != null && extras.containsKey("sender")) {
            setContainerContent(R.id.fragContainer, NearbyHelpFragment.newInstance(
                            extras.getString("sender"),
                            extras.getString("ingredient")),
                    false);
        } else if (extras != null && extras.containsKey("askedIngredient")) {
            setContainerContent(R.id.fragContainer, NearbyHelpFragment.newInstance(
                    (Ingredient)extras.getParcelable("askedIngredient")),
                    false);
        } else {
            setContainerContent(R.id.fragContainer, NearbyHelpFragment.class, false);
        }
    }

    private void setContainerContent(int containerId, @NonNull Class<? extends Fragment> fragmentClass, boolean setOrReplace){
        if(setOrReplace){
            fragmentManager.beginTransaction()
                    .setReorderingAllowed(true)
                    .add(containerId,fragmentClass,null)
                    .addToBackStack(fragmentClass.getName())
                    .commit();
        }else{
            fragmentManager.beginTransaction()
                    .setReorderingAllowed(true)
                    .replace(containerId,fragmentClass,null)
                    .addToBackStack(fragmentClass.getName())
                    .commit();
        }
    }

    @SuppressLint("ResourceAsColor")
    private void popUpDialogBuilder(String[] items, boolean[] checksum, String title, List<String> selected){
        AlertDialog.Builder builder= new AlertDialog.Builder(this,R.style.AlertDialogTheme);
        builder.setTitle(title);
        builder.setCancelable(false);
        //List<String> selected = new ArrayList<>();

        builder.setMultiChoiceItems(items,checksum,(dialog,which,isChecked)->{
            checksum[which]=isChecked;
        });
        builder.setPositiveButton("Ok", (dialog, which) -> {
            for(int i=0; i< checksum.length; i++){
                if(checksum[i]){
                    selected.add(items[i]);
                }
            }
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> {
            for(int i= 0; i< checksum.length; i++){
                checksum[i]=false;
            }
            dialog.cancel();
        });

        AlertDialog dialog=builder.create();
        dialog.setOnShowListener(arg0 -> {
            dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(R.color.teal_700);
            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(R.color.teal_700);
        });
        dialog.show();
    }
    private void setContainerContent(int containerId, @NonNull Fragment fragment, boolean setOrReplace){
        if(setOrReplace){
            getSupportFragmentManager().beginTransaction()
                    .setReorderingAllowed(true)
                    .add(containerId,fragment,null)
                    .addToBackStack(fragment.getClass().getName())
                    .commit();
        }else{
            getSupportFragmentManager().beginTransaction()
                    .setReorderingAllowed(true)
                    .replace(containerId,fragment,null)
                    .addToBackStack(fragment.getClass().getName())
                    .commit();
        }
    }

    private void handleBackPress() {
        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                    FragmentManager fragmentManager = getSupportFragmentManager();
                    if (fragmentManager.getBackStackEntryCount() == 1) {
                        moveTaskToBack(true);
                    } else {
                        fragmentManager.popBackStackImmediate();
                    }
                }
        };
        getOnBackPressedDispatcher().addCallback(this, callback);
    }
}
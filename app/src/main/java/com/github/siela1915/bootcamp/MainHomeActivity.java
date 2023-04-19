package com.github.siela1915.bootcamp;

import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentContainerView;


import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.view.View;
import android.widget.Button;

import com.github.siela1915.bootcamp.Labelling.AllergyType;
import com.github.siela1915.bootcamp.Labelling.CuisineType;
import com.github.siela1915.bootcamp.Labelling.DietType;
import com.github.siela1915.bootcamp.Labelling.RecipeFetcher;
import com.github.siela1915.bootcamp.Recipes.PreparationTime;

import com.github.siela1915.bootcamp.Recipes.ExampleRecipes;
import com.github.siela1915.bootcamp.Recipes.Recipe;

import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;
import java.util.List;

public class MainHomeActivity extends AppCompatActivity {
    DrawerLayout drawerLayout;
    NavigationView navigationView;
    ActionBarDrawerToggle toggle;
    ConstraintLayout constraintLayout;
    FragmentContainerView fragmentContainerView;
    Button cuisineBtn,timeBtn,allergyBtn, dietBtn,filterBtn;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_home2);
        constraintLayout = findViewById(R.id.filterLayout);
        constraintLayout.setVisibility(View.GONE);
        if(savedInstanceState== null){
            setContainerContent(R.id.fragContainer,HomePageFragment.class,true);
        }
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
            constraintLayout.setVisibility(View.GONE);
            switch (item.getItemId()){
                case R.id.menuItem_home:
                    setContainerContent(R.id.fragContainer,HomePageFragment.class,false);
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
                        constraintLayout.setVisibility(View.GONE);
                    }else{
                        item.setChecked(true);
                        constraintLayout.setVisibility(View.VISIBLE);
                    }
                    fragmentContainerView= findViewById(R.id.fragContainer);
                    boolean homeFragmentCheck=fragmentContainerView.getFragment().getId()==R.id.homeFragment;
                    if(!homeFragmentCheck) {
                        setContainerContent(R.id.fragContainer,HomePageFragment.class,false);
                    }
                    break;
                case R.id.menuItem_favorites:
                    setContainerContent(R.id.fragContainer, RecipeListFragment.newInstance(
                                        ExampleRecipes.recipes
                                ), false);
                    break;
                default:
            }
            drawerLayout.close();
            return true;
        });
        
        if (getIntent().hasExtra("com.github.siela1915.bootcamp.navToProfile")) {
            navigationView.setCheckedItem(R.id.menuItem_login);
            setContainerContent(R.id.fragContainer,ProfileFragment.class,false);
        }
    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(toggle.onOptionsItemSelected(item)){
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void setContainerContent(int containerId, @NonNull Class<? extends Fragment> fragmentClass, boolean setOrReplace){
        if(setOrReplace){
            getSupportFragmentManager().beginTransaction()
                    .setReorderingAllowed(true)
                    .add(containerId,fragmentClass,null)
                    .commit();
        }else{
            getSupportFragmentManager().beginTransaction()
                    .setReorderingAllowed(true)
                    .replace(containerId,fragmentClass,null)
                    .commit();
        }
    }

    private void popUpDialogBuilder(String[] items, boolean[] checksum, String title, List<String> selected){
        AlertDialog.Builder builder= new AlertDialog.Builder(MainHomeActivity.this,R.style.AlertDialogTheme);
        builder.setTitle(title);
        builder.setCancelable(false);
        //List<String> selected = new ArrayList<>();

        builder.setMultiChoiceItems(items,checksum,(dialog,which,isChecked)->{
            checksum[which]=isChecked;
        });
        builder.setPositiveButton("Ok", (dialog, which) -> {
            for(int i=0; i< checksum.length; i++){
                if(checksum[i]== true){
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
            dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(getResources().getColor(R.color.teal_700));
            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.teal_700));
        });
        dialog.show();
    }
    private void setContainerContent(int containerId, @NonNull Fragment fragment, boolean setOrReplace){
        if(setOrReplace){
            getSupportFragmentManager().beginTransaction()
                    .setReorderingAllowed(true)
                    .add(containerId,fragment,null)
                    .commit();
        }else{
            getSupportFragmentManager().beginTransaction()
                    .setReorderingAllowed(true)
                    .replace(containerId,fragment,null)
                    .commit();
        }
    }
}
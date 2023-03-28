package com.github.siela1915.bootcamp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentContainer;
import androidx.fragment.app.FragmentContainerView;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.github.siela1915.bootcamp.Recipes.Diet;
import com.github.siela1915.bootcamp.Recipes.Ingredient;
import com.github.siela1915.bootcamp.Recipes.PreparationTime;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;
import java.util.List;

public class MainHomeActivity extends AppCompatActivity {
    DrawerLayout drawerLayout;
    NavigationView navigationView;
    ActionBarDrawerToggle toggle;
    ConstraintLayout constraintLayout;
    FragmentContainerView fragmentContainerView;
    Button pantryBtn,timeBtn,allergyBtn, dietBtn;

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
        pantryBtn=findViewById(R.id.pantryBtn);
        timeBtn=findViewById(R.id.timingBtn);
        allergyBtn=findViewById(R.id.notIncludIngBtn);
        dietBtn=findViewById(R.id.dietBtn);
        pantryBtn.setOnClickListener(v -> {
            String [] pantry= Ingredient.getAll();
            boolean[] checksum= new boolean[pantry.length];
            String title = "Chose the ingredients you have";
            popUpDialogBuilder(pantry,checksum,title);
        });
        dietBtn.setOnClickListener(v -> {
            String [] diets= Diet.getAll();
            boolean[] checksum= new boolean[diets.length];
            String title = "Chose your diet";
            popUpDialogBuilder(diets,checksum,title);
        });
        timeBtn.setOnClickListener(v -> {
            String [] prepTime= PreparationTime.getAll();
            boolean[] checksum= new boolean[prepTime.length];
            String title = "Chose your the preparation time";
            popUpDialogBuilder(prepTime,checksum,title);
        });
        allergyBtn.setOnClickListener(v -> {
            String [] ingredients= Ingredient.getAll();
            boolean[] checksum= new boolean[ingredients.length];
            String title = "what are you allergic to";
            popUpDialogBuilder(ingredients,checksum,title);
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

    private void popUpDialogBuilder(String[] items, boolean[] checksum, String title){
        AlertDialog.Builder builder= new AlertDialog.Builder(MainHomeActivity.this,R.style.AlertDialogTheme);
        builder.setTitle(title);
        builder.setCancelable(false);
        List<String> selected = new ArrayList<>();

        builder.setMultiChoiceItems(items,checksum,(dialog,which,isChecked)->{
            checksum[which]=isChecked;
            Toast.makeText(MainHomeActivity.this, items[which] + " " + isChecked,Toast.LENGTH_SHORT).show();
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
}
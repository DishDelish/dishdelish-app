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

import com.github.siela1915.bootcamp.Recipes.Ingredient;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.navigation.NavigationView;

public class MainHomeActivity extends AppCompatActivity {
    DrawerLayout drawerLayout;
    NavigationView navigationView;
    ActionBarDrawerToggle toggle;
    ConstraintLayout constraintLayout;
    FragmentContainerView fragmentContainerView;
    Button pantryBtn,timeBtn,allergyBtn;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_home2);
        constraintLayout = findViewById(R.id.filterLayout);
        constraintLayout.setVisibility(View.GONE);
        if(savedInstanceState== null){
            /*
            getSupportFragmentManager().beginTransaction()
                    .setReorderingAllowed(true)
                    .add(R.id.fragContainer,HomePageFragment.class,null)
                    .commit();

             */
            setContainerContent(R.id.fragContainer,HomePageFragment.class,true);
        }
        drawerLayout= findViewById(R.id.drawer_layout);
        navigationView= findViewById(R.id.navView);
        pantryBtn=findViewById(R.id.pantryBtn);
        pantryBtn.setOnClickListener(v -> {
            AlertDialog.Builder builder= new AlertDialog.Builder(MainHomeActivity.this,R.style.AlertDialogTheme);
            builder.setTitle("choose what you have");
            builder.setCancelable(false);
            String[] fridgeItems= Ingredient.getAll();
            boolean[] checksum=new boolean[fridgeItems.length];
            builder.setMultiChoiceItems(fridgeItems, checksum, (dialog, which, isChecked) -> {
                    //TODO
            });
            builder.setPositiveButton("Ok", (dialog, which) -> {
                //TODO
            });
            builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());
            AlertDialog dialog=builder.create();
            dialog.setOnShowListener(arg0 -> {
                dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(getResources().getColor(R.color.teal_700));
                dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.teal_700));
            });
            dialog.show();
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
}
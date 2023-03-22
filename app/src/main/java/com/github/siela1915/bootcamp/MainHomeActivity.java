package com.github.siela1915.bootcamp;

import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import com.github.siela1915.bootcamp.Recipes.ExampleRecipes;
import com.github.siela1915.bootcamp.firebase.Database;
import com.google.android.material.navigation.NavigationView;

public class MainHomeActivity extends AppCompatActivity {
    DrawerLayout drawerLayout;
    NavigationView navigationView;
    ActionBarDrawerToggle toggle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_home2);

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
        toggle= new ActionBarDrawerToggle(this,drawerLayout,R.string.open,R.string.close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Database database = new Database();

        navigationView.setNavigationItemSelectedListener(item ->{
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
                case R.id.menuItem_favorites:
                    setContainerContent(R.id.fragContainer, RecipeListFragment.newInstance(
//                                        Tasks.await(database.getFavorites()
//                                                .continueWith((favoriteIds) -> {
//                                                    return favoriteIds.getResult().stream().map(database::get).collect(Collectors.toList());
//                                                }))
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
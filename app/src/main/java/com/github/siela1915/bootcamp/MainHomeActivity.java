package com.github.siela1915.bootcamp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import android.os.Bundle;
import android.view.MenuItem;

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
            getSupportFragmentManager().beginTransaction()
                    .setReorderingAllowed(true)
                    .add(R.id.fragContainer,HomePageFragment.class,null)
                    .commit();
        }
        drawerLayout= findViewById(R.id.drawer_layout);
        navigationView= findViewById(R.id.navView);
        toggle= new ActionBarDrawerToggle(this,drawerLayout,R.string.open,R.string.close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        navigationView.setNavigationItemSelectedListener(item ->{
            /*
            switch (item.getItemId()){
                case R.id.menuItem_home:
                    getSupportFragmentManager().beginTransaction()
                            .setReorderingAllowed(true)
                            .replace(R.id.fragContainer,HomePageFragment.class,null)
                            .commit();
                    break;

                case R.id.menuItem_about:
                    getSupportFragmentManager().beginTransaction()
                            .setReorderingAllowed(true)
                            .replace(R.id.fragContainer,AboutPageFragment.class,null)
                            .commit();
                    break;
                default:
            }

             */
            if(item.getItemId()== R.id.menuItem_home){
                getSupportFragmentManager().beginTransaction()
                        .setReorderingAllowed(true)
                        .replace(R.id.fragContainer,HomePageFragment.class,null)
                        .commit();
            }
            else {
                if(item.getItemId()== R.id.menuItem_about){
                    getSupportFragmentManager().beginTransaction()
                            .setReorderingAllowed(true)
                            .replace(R.id.fragContainer,AboutPageFragment.class,null)
                            .commit();
                }else{
                    //to be done when the other functionalities are implemented
                }
            }

            drawerLayout.close();
            return true;
        });
    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(toggle.onOptionsItemSelected(item)){
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
package com.github.siela1915.bootcamp;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentManager;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.navigation.NavigationView;

public class MainActivity extends AppCompatActivity {
    boolean pressed= false;
    FragmentManager fragmentManager = getSupportFragmentManager();
    ActionBarDrawerToggle toggle;
    DrawerLayout drawerLayout;
    public MainActivity(){
        super(R.layout.activity_main);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(savedInstanceState == null){
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.fragContainer,ExampleFragment.class,null)
                    .commit();
        }
        setContentView(R.layout.activity_main);
        drawerLayout = findViewById(R.id.drawer_layout);
        NavigationView navigationView= findViewById(R.id.navView);
        toggle= new ActionBarDrawerToggle(this,drawerLayout,R.string.open,R.string.close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        navigationView.setNavigationItemSelectedListener(it ->{
            switch (it.getItemId()){
                case R.id.upload:
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.fragContainer,NextFragment.class,null)
                            .commit();


                    break;
                case R.id.favorites:
                    Toast.makeText(getApplicationContext(),"Clicked on Item 2",Toast.LENGTH_SHORT).show();
                    break;
                case R.id.login:
                    Toast.makeText(getApplicationContext(),"Clicked on Item 3",Toast.LENGTH_SHORT).show();
                    break;
                case R.id.about:
                    Toast.makeText(getApplicationContext(),"Clicked on Item 4",Toast.LENGTH_SHORT).show();
                    break;
            }
            it.setChecked(true);
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
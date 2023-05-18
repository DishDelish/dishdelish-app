package com.github.siela1915.bootcamp;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toolbar;

import com.github.siela1915.bootcamp.Recipes.ExampleRecipes;
import com.github.siela1915.bootcamp.Recipes.Ingredient;
import com.github.siela1915.bootcamp.Recipes.Recipe;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CookNowActivity extends AppCompatActivity {

    //index for keeping track which fragments to show
    private int index = 0;
    private Recipe recipe;


    private FragmentManager fragmentManager;
    private int currentIndex = 0;
    private List<Fragment> stepFragments;
    private List<Fragment> timerFragments;

    private int numSteps;

    //override the back button to go back to the previous activity
    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        recipe = getIntent().getParcelableExtra("recipe");
        if (recipe == null)
            recipe = ExampleRecipes.recipes.get(0);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cook_now);

        numSteps = recipe.steps.size() + 1;



// Initialize member variables and set initial fragments
        fragmentManager = getSupportFragmentManager();
        stepFragments = new ArrayList<>();
        timerFragments = new ArrayList<>();
        Bundle bundle = new Bundle();
        ArrayList<Ingredient> l = new ArrayList<>(recipe.getIngredientList());
        bundle.putParcelableArrayList("ingredients", l);
        FragmentIngredientCheckContainer f = new FragmentIngredientCheckContainer();
        f.setArguments(bundle);
        stepFragments.add(f);
        int index = 0;
        for (String step : recipe.getSteps()) {
            CookNowStepFragment s = CookNowStepFragment.newInstance(index, step);
            stepFragments.add(s);
            CookNowTimerFragment t = CookNowTimerFragment.newInstance(index);
            timerFragments.add(t);
            index+=1;
        }
        // Set initial fragments
        fragmentManager.beginTransaction()
                .replace(R.id.container_step, stepFragments.get(currentIndex))
                .commit();
//        fragmentManager.beginTransaction()
//                .replace(R.id.container_timer, timerFragments.get(currentIndex))
//                .commit();

        // Button click listeners
        Button btnBackward = findViewById(R.id.btnBackward);
        btnBackward.setOnClickListener(v -> {
            if (currentIndex > 0) {
                currentIndex--;
                fragmentManager.beginTransaction()
                        .replace(R.id.container_step, stepFragments.get(currentIndex))
                        .commit();
                if(currentIndex!=0)
                    fragmentManager.beginTransaction()
                            .replace(R.id.container_timer, timerFragments.get(currentIndex))
                            .commit();
            }
        });

        Button btnForward = findViewById(R.id.btnForward);
        btnForward.setOnClickListener(v -> {
            if (currentIndex < stepFragments.size()-1 ) {
                currentIndex++;
                fragmentManager.beginTransaction()
                        .replace(R.id.container_step, stepFragments.get(currentIndex))
                        .commit();
                fragmentManager.beginTransaction()
                        .replace(R.id.container_timer, timerFragments.get(currentIndex-1))
                        .commit();
            }
        });


    }
}
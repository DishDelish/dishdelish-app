package com.github.siela1915.bootcamp;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import android.os.Bundle;
import android.widget.Toolbar;

import com.github.siela1915.bootcamp.Recipes.ExampleRecipes;
import com.github.siela1915.bootcamp.Recipes.Ingredient;
import com.github.siela1915.bootcamp.Recipes.Recipe;

import java.util.ArrayList;

public class CookNowActivity extends AppCompatActivity {

    //index for keeping track which fragments to show
    private int index = 0;
    private Recipe recipe;
    private CookNowStepFragment stepFrag;
    private CookNowTimerFragment timerFrag;
    private FragmentManager manager;
    private ViewPager2 viewPager;
    private ViewPager2 viewPagerTimer;

    private FragmentStateAdapter pagerAdapterStep;
    private FragmentStateAdapter pagerAdapterTimer;
    private int numSteps;

    //override the back button to go back to the previous activity
    @Override
    public boolean onSupportNavigateUp() {
        if(viewPager.getCurrentItem() == numSteps-1) {
            viewPager.setCurrentItem(0);
        }else{

            finish();
        }
        return true;
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        recipe = getIntent().getParcelableExtra("recipe");
        if(recipe==null)
            recipe = ExampleRecipes.recipes.get(0);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cook_now);

        numSteps = recipe.steps.size()+2;

        //page sliding
        viewPager = findViewById(R.id.container_step);
        pagerAdapterStep = new ScreenSlidePagerAdapterStep(this);
        viewPager.setAdapter(pagerAdapterStep);
        viewPager.setPageTransformer(new ZoomOutPageTransformer());
        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                if(position != 0){
                    index=position;
                }
            }

        });

        viewPagerTimer = findViewById(R.id.container_timer);
        pagerAdapterTimer = new ScreenSlidePagerAdapterTimer(this);
        viewPagerTimer.setAdapter(pagerAdapterTimer);

        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                viewPagerTimer.setCurrentItem(position, false);
            }

        });

        manager = getSupportFragmentManager();
    }

    @Override
    public void onBackPressed() {
        if (viewPager.getCurrentItem() == 0) {
            // If the user is currently looking at the first step, allow the system to handle the
            // Back button. This calls finish() on this activity and pops the back stack.
            super.onBackPressed();

        } else if(viewPager.getCurrentItem() == numSteps-1){
            viewPager.setCurrentItem(0);
        }
        else {
            // Otherwise, select the previous step.
            viewPager.setCurrentItem(viewPager.getCurrentItem() - 1);
        }
    }


    public class ScreenSlidePagerAdapterStep extends FragmentStateAdapter {
        public ScreenSlidePagerAdapterStep(FragmentActivity fa) {
            super(fa);
        }

        @Override
        public Fragment createFragment(int position) {
            if(position == numSteps-1){
                return new ShoppingCartFragment();
            }
            if(position == 0){
                //Arrays.asList doesn't return an actual array list, have to add this so its parcelable
                ArrayList<Ingredient> list = new ArrayList<>(recipe.getIngredientList());
                return FragmentIngredientCheck.newInstance(list);
            }
            return CookNowStepFragment.newInstance(position-1, recipe.steps.get(position-1));
        }

        @Override
        public int getItemCount() {
            return numSteps;
        }
    }

    public class ScreenSlidePagerAdapterTimer extends FragmentStateAdapter {
        public ScreenSlidePagerAdapterTimer(FragmentActivity fa) {
            super(fa);
        }

        @Override
        public Fragment createFragment(int position) {
            CookNowTimerFragment frag = CookNowTimerFragment.newInstance(position-1);
            return frag;
        }

        @Override
        public int getItemCount() {
            return numSteps;
        }
    }

    public void goToShoppingCart(){
        viewPager.setCurrentItem(numSteps);
    }


}
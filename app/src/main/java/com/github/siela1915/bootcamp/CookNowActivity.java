package com.github.siela1915.bootcamp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import android.os.Bundle;

import com.github.siela1915.bootcamp.Recipes.ExampleRecipes;
import com.github.siela1915.bootcamp.Recipes.Recipe;

public class CookNowActivity extends AppCompatActivity {

    //index for keeping track which fragments to show
    private int index = 0;
    private Recipe recipe;
    private CookNowStepFragment stepFrag;
    private CookNowTimerFragment timerFrag;
    private FragmentManager manager;
    private ViewPager2 viewPager;
    private ViewPager2 viewPagerTimer;
    private FragmentStateAdapter pagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //TODO temp testing
        recipe = ExampleRecipes.recipes.get(0);
        //recipe = getIntent().getParcelableExtra("Recipe");

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guided_cooking);

        //page sliding
        viewPager = findViewById(R.id.container_step);
        pagerAdapter = new ScreenSlidePagerAdapterStep(this);
        viewPager.setAdapter(pagerAdapter);
        viewPager.setPageTransformer(new ZoomOutPageTransformer());
        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);

                if(position != 0){

                    index=position;
                    //updateTimerFragment();
                }
            }

        });

        viewPagerTimer = findViewById(R.id.container_timer);
        pagerAdapter = new ScreenSlidePagerAdapterTimer(this);
        viewPagerTimer.setAdapter(pagerAdapter);

        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                viewPagerTimer.setCurrentItem(position, false);
            }

        });

        manager = getSupportFragmentManager();
        //updateTimerFragment();
    }

    @Override
    public void onBackPressed() {
        if (viewPager.getCurrentItem() == 0) {
            // If the user is currently looking at the first step, allow the system to handle the
            // Back button. This calls finish() on this activity and pops the back stack.
            super.onBackPressed();
        } else {
            // Otherwise, select the previous step.
            viewPager.setCurrentItem(viewPager.getCurrentItem() - 1);
        }
    }


    private class ScreenSlidePagerAdapterStep extends FragmentStateAdapter {
        public ScreenSlidePagerAdapterStep(FragmentActivity fa) {
            super(fa);
        }

        @Override
        public Fragment createFragment(int position) {
            if(position == 0){
                //TODO placeholder
                return CookNowTimerFragment.newInstance(0);
            }
            return CookNowStepFragment.newInstance(position-1, recipe.steps.get(position-1));
        }

        @Override
        public int getItemCount() {
            return recipe.steps.size()+1;
        }
    }

    private class ScreenSlidePagerAdapterTimer extends FragmentStateAdapter {
        public ScreenSlidePagerAdapterTimer(FragmentActivity fa) {
            super(fa);
        }

        @Override
        public Fragment createFragment(int position) {
            return CookNowTimerFragment.newInstance(position);
        }

        @Override
        public int getItemCount() {
            return recipe.steps.size();
        }
    }


}
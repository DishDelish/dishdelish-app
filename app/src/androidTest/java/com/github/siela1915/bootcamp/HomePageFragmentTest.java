package com.github.siela1915.bootcamp;

import static androidx.test.core.app.ApplicationProvider.getApplicationContext;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.Espresso.pressBack;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.Visibility.VISIBLE;
import static androidx.test.espresso.matcher.ViewMatchers.isDescendantOfA;
import static androidx.test.espresso.matcher.ViewMatchers.withClassName;
import static androidx.test.espresso.matcher.ViewMatchers.withContentDescription;
import static androidx.test.espresso.matcher.ViewMatchers.withEffectiveVisibility;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.endsWith;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertNotNull;

import android.Manifest;
import android.location.Location;
import android.os.Bundle;

import androidx.fragment.app.testing.FragmentScenario;
import androidx.test.espresso.action.ViewActions;
import androidx.test.rule.GrantPermissionRule;

import com.github.siela1915.bootcamp.Recipes.ExampleRecipes;
import com.github.siela1915.bootcamp.Recipes.Ingredient;
import com.github.siela1915.bootcamp.Recipes.Unit;
import com.github.siela1915.bootcamp.firebase.Database;
import com.github.siela1915.bootcamp.firebase.FirebaseInstanceManager;
import com.github.siela1915.bootcamp.firebase.LocationDatabase;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.FirebaseUser;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class HomePageFragmentTest {
    FragmentScenario<HomePageFragment> scenario;

    private Database db;

    @Before
    public void prepare() {
        FirebaseInstanceManager.emulator = true;

        if (db == null) {
            db = new Database(FirebaseInstanceManager.getDatabase(getApplicationContext()));
        }

        if (FirebaseInstanceManager.getAuth().getCurrentUser() != null) {
            FirebaseAuthActivityTest.logoutSync();
        }
    }

    @After
    public void cleanUp() {
        if (scenario != null) {
            scenario.close();
        }
    }

    @Test
    public void fragmentWithArgumentShowsFilterMenu() {
        ArrayList<Ingredient> ingredients = new ArrayList<>(ExampleRecipes.recipes.get(0).ingredientList);
        Bundle args = new Bundle();
        args.putParcelableArrayList(HomePageFragment.ARG_MYFRIDGE, ingredients);
        scenario = FragmentScenario.launchInContainer(HomePageFragment.class, args);

        onView(withId(R.id.moreFilterTextView)).check(matches(withEffectiveVisibility(VISIBLE)));
    }
}

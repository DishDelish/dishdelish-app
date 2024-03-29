package com.github.siela1915.bootcamp;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.doesNotExist;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.contrib.RecyclerViewActions.actionOnItemAtPosition;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static com.github.siela1915.bootcamp.RecipeListFragmentTest.RecyclerViewItemCountAssertion.withItemCount;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import android.content.res.Resources;
import android.os.Bundle;
import android.view.View;

import androidx.fragment.app.testing.FragmentScenario;
import androidx.recyclerview.widget.RecyclerView;
import androidx.test.espresso.NoMatchingViewException;
import androidx.test.espresso.ViewAssertion;
import androidx.test.espresso.intent.Intents;
import androidx.test.espresso.intent.matcher.IntentMatchers;

import com.github.siela1915.bootcamp.Recipes.ExampleRecipes;
import com.github.siela1915.bootcamp.Recipes.Recipe;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.junit.After;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Objects;

public class RecipeListFragmentTest {
    FragmentScenario<RecipeListFragment> scenario;

    @After
    public void cleanUp() {
        scenario.close();
        try {
            Intents.release();
        } catch (IllegalStateException exception) {
            if (exception.getMessage() == null ||
                    !exception.getMessage().contains("init() must be called prior to using this method")) {
                throw exception;
            }
        }
    }

    @Test
    public void emptyListFragmentShowsNothing() {
        Bundle bundle = new Bundle();
        bundle.putParcelableArrayList(RecipeListFragment.ARG_RECIPE_LIST, new ArrayList<>());
        scenario = FragmentScenario.launchInContainer(RecipeListFragment.class, bundle);

        onView(withId(R.id.recipeCardView)).check(doesNotExist());
    }

    @Test
    public void listFragmentWithSingleItemShowsOneItem() {
        Bundle bundle = new Bundle();
        ArrayList<Recipe> recipeList = new ArrayList<>(Collections.singletonList(ExampleRecipes.recipes.get(0)));
        bundle.putParcelableArrayList(RecipeListFragment.ARG_RECIPE_LIST, recipeList);
        scenario = FragmentScenario.launchInContainer(RecipeListFragment.class, bundle);

        onView(withId(R.id.recipeList)).check(withItemCount(1));
    }

    @Test
    public void listFragmentWithMultipleItemsShowsCorrectNumberOfItems() {
        Bundle bundle = new Bundle();
        ArrayList<Recipe> recipeList = new ArrayList<>(ExampleRecipes.recipes);
        bundle.putParcelableArrayList(RecipeListFragment.ARG_RECIPE_LIST, recipeList);
        scenario = FragmentScenario.launchInContainer(RecipeListFragment.class, bundle);

        onView(withId(R.id.recipeList)).check(withItemCount(ExampleRecipes.recipes.size()));
    }

    @Test
    public void listFragmentKeepsCorrectOrder() {
        Bundle bundle = new Bundle();
        ArrayList<Recipe> recipeList = new ArrayList<>(ExampleRecipes.recipes);
        bundle.putParcelableArrayList(RecipeListFragment.ARG_RECIPE_LIST, recipeList);
        scenario = FragmentScenario.launchInContainer(RecipeListFragment.class, bundle);

        onView(new RecyclerViewMatcher(R.id.recipeList).atPositionOnView(0, R.id.textView_recipeName))
                .check(matches(withText(recipeList.get(0).recipeName)));
        onView(new RecyclerViewMatcher(R.id.recipeList).atPositionOnView(1, R.id.textView_recipeName))
                .check(matches(withText(recipeList.get(1).recipeName)));
        onView(new RecyclerViewMatcher(R.id.recipeList).atPositionOnView(2, R.id.textView_recipeName))
                .check(matches(withText(recipeList.get(2).recipeName)));
    }

    @Test
    public void clickingOnItemLaunchesDetailedRecipeView() {
        Bundle bundle = new Bundle();
        ArrayList<Recipe> recipeList = new ArrayList<>(Collections.singletonList(ExampleRecipes.recipes.get(0)));
        bundle.putParcelableArrayList(RecipeListFragment.ARG_RECIPE_LIST, recipeList);
        scenario = FragmentScenario.launchInContainer(RecipeListFragment.class, bundle);

        Intents.init();

        onView(withId(R.id.recipeList)).perform(actionOnItemAtPosition(0, click()));

        Intents.intended(IntentMatchers.hasComponent(RecipeActivity.class.getName()));
    }

    /**
     * Class to check item count in recycler view, taken from <a href="https://stackoverflow.com/a/43207009">...</a>
     */
    static class RecyclerViewItemCountAssertion implements ViewAssertion {
        private final Matcher<Integer> matcher;

        public static RecyclerViewItemCountAssertion withItemCount(int expectedCount) {
            return withItemCount(is(expectedCount));
        }

        public static RecyclerViewItemCountAssertion withItemCount(Matcher<Integer> matcher) {
            return new RecyclerViewItemCountAssertion(matcher);
        }

        private RecyclerViewItemCountAssertion(Matcher<Integer> matcher) {
            this.matcher = matcher;
        }

        @Override
        public void check(View view, NoMatchingViewException noViewFoundException) {
            if (noViewFoundException != null) {
                throw noViewFoundException;
            }

            RecyclerView recyclerView = (RecyclerView) view;
            RecyclerView.Adapter adapter = recyclerView.getAdapter();
            assertThat(Objects.requireNonNull(adapter).getItemCount(), matcher);
        }
    }

    /**
     * Class to be able to check something on an item at a specific index in recycler view, taken from <a href="https://stackoverflow.com/a/52773940">...</a>
     */
    static class RecyclerViewMatcher {
        private final int recyclerViewId;

        public RecyclerViewMatcher(int recyclerViewId) {
            this.recyclerViewId = recyclerViewId;
        }

        public Matcher<View> atPosition(final int position) {
            return atPositionOnView(position, -1);
        }

        public Matcher<View> atPositionOnView(final int position, final int targetViewId) {

            return new TypeSafeMatcher<View>() {
                Resources resources = null;
                View childView;

                public void describeTo(Description description) {
                    String idDescription = Integer.toString(recyclerViewId);
                    if (this.resources != null) {
                        try {
                            idDescription = this.resources.getResourceName(recyclerViewId);
                        } catch (Resources.NotFoundException var4) {
                            idDescription = String.format("%s (resource name not found)",
                                    recyclerViewId);
                        }
                    }

                    description.appendText("with id: " + idDescription);
                }

                public boolean matchesSafely(View view) {

                    this.resources = view.getResources();

                    if (childView == null) {
                        RecyclerView recyclerView =
                                view.getRootView().findViewById(recyclerViewId);
                        if (recyclerView != null && recyclerView.getId() == recyclerViewId) {
                            childView = Objects.requireNonNull(recyclerView.findViewHolderForAdapterPosition(position)).itemView;
                        } else {
                            return false;
                        }
                    }

                    if (targetViewId == -1) {
                        return view == childView;
                    } else {
                        View targetView = childView.findViewById(targetViewId);
                        return view == targetView;
                    }

                }
            };
        }
    }
}

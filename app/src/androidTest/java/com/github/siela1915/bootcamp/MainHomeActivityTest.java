package com.github.siela1915.bootcamp;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.scrollTo;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.contrib.NavigationViewActions.navigateTo;
import static androidx.test.espresso.contrib.RecyclerViewActions.actionOnItemAtPosition;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static androidx.test.espresso.matcher.ViewMatchers.hasDescendant;
import static androidx.test.espresso.matcher.ViewMatchers.isClickable;
import static androidx.test.espresso.matcher.ViewMatchers.isDescendantOfA;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withEffectiveVisibility;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.endsWithIgnoringCase;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;

import android.view.View;

import androidx.recyclerview.widget.RecyclerView;
import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.NoMatchingViewException;
import androidx.test.espresso.ViewAssertion;
import androidx.test.espresso.ViewInteraction;
import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.contrib.DrawerActions;
import androidx.test.espresso.contrib.RecyclerViewActions;
import androidx.test.espresso.intent.Intents;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class MainHomeActivityTest {
    @Rule
    public ActivityScenarioRule<MainHomeActivity> testRule = new ActivityScenarioRule<>(MainHomeActivity.class);


    ActivityScenario<MainHomeActivity> scenario = ActivityScenario.launch(MainHomeActivity.class);

    @Test
    public void startingApplicationWithHomePageViewTest(){
        ActivityScenario scenario1= ActivityScenario.launch(MainHomeActivity.class);
        onView(withId(R.id.drawer_layout)).perform(DrawerActions.open());
        onView(withId(R.id.navView)).perform(navigateTo(R.id.menuItem_soppingCart));    }

    @Test
    public void clickingOnToggleButtonOpensNavigationMenuTest(){
        onView(withId(R.id.drawer_layout))
                .perform(DrawerActions.open());
        onView(withId(R.id.navView)).check(matches(isDisplayed()));
    }
    @Test
    public void openingAndClosingTheNavigationDrawerDoesNotChangeTheContentContainerTest(){
        onView(withId(R.id.drawer_layout)).perform(DrawerActions.open());
        onView(withId(R.id.navView))
                .perform(navigateTo(R.id.menuItem_about));
        onView(withId(R.id.aboutFragment)).check(matches(isDisplayed()));

        onView(withId(R.id.drawer_layout))
                .perform(DrawerActions.open())
                .perform(DrawerActions.close());
        onView(withId(R.id.aboutFragment)).check(matches(isDisplayed()));

    }

    @Test
    public void clickingOnProfileMenuNavigatesToProfileFragmentTest() {
        onView(withId(R.id.drawer_layout)).perform(DrawerActions.open());
        onView(withId(R.id.navView))
                .perform(navigateTo(R.id.menuItem_login));
        onView(withId(R.id.profileFragment)).check(matches(isDisplayed()));
    }

    @Test
    public void clickingOnFavoritesMenuNavigatesToProfileFragmentTest() {
        onView(withId(R.id.drawer_layout)).perform(DrawerActions.open());
        onView(withId(R.id.navView))
                .perform(navigateTo(R.id.menuItem_favorites));
        onView(withId(R.id.recipeList)).check(matches(isDisplayed()));
    }

    @Test
    public void transitionBetweenDifferentFragmentsTest(){
        onView(withId(R.id.drawer_layout)).perform(DrawerActions.open());
        onView(withId(R.id.navView)).perform(navigateTo(R.id.menuItem_about));
        onView(withId(R.id.aboutFragment)).check(matches(isDisplayed()));
    }
    @Test
    public void choosingOtherMenusThanHomeAndAboutDoesNotChangeContentForTheMomentTest(){
        onView(withId(R.id.drawer_layout)).perform(DrawerActions.open());
        onView(withId(R.id.navView)).perform(navigateTo(R.id.menuItem_filter));
        onView(withId(R.id.homeFragment)).check(matches(isDisplayed()));
        
    }
    @Test
    public void testOnChoosingFilter(){
        onView(withId(R.id.drawer_layout)).perform(DrawerActions.open());
        onView(withId(R.id.navView))
                .perform(navigateTo(R.id.menuItem_filter));
        onView(withId(R.id.homeFragment)).check(matches(isDisplayed()));
        onView(withId(R.id.filterLayout)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
        onView(withId(R.id.cuisineBtn)).check(matches(isClickable()));
        onView(withId(R.id.allergyBtn)).check(matches(isClickable()));
        onView(withId(R.id.dietBtn)).check(matches(isClickable()));
        onView(withId(R.id.timingBtn)).check(matches(isClickable()));
        onView(withId(R.id.filterBtn)).check(matches(isClickable()));
    }
    @Test
    public void testingCuisineTypeBtn(){
        onView(withId(R.id.drawer_layout)).perform(DrawerActions.open());
        onView(withId(R.id.navView))
                .perform(navigateTo(R.id.menuItem_filter));
        onView(withId(R.id.drawer_layout)).perform(DrawerActions.close());
        onView(allOf(withId(R.id.cuisineBtn), isDescendantOfA(withId(R.id.filterLayout)))).perform(ViewActions.scrollTo(),click());
        onView(withText("Choose your preferred cuisine")).check(matches(isDisplayed()));

    }
    @Test
    public void testingDietTypeBtn(){
        onView(withId(R.id.drawer_layout)).perform(DrawerActions.open());
        onView(withId(R.id.navView)).perform(navigateTo(R.id.menuItem_filter));
        onView(withId(R.id.drawer_layout)).perform(DrawerActions.close());
        onView(allOf(withId(R.id.dietBtn), isDescendantOfA(withId(R.id.filterLayout)))).perform(ViewActions.scrollTo(),click());
        onView((withText(endsWithIgnoringCase("Choose your diet")))).check(matches(isDisplayed()));
    }
    @Test
    public void testingAllergyBtn(){
        onView(withId(R.id.drawer_layout)).perform(DrawerActions.open());
        onView(withId(R.id.navView)).perform(navigateTo(R.id.menuItem_filter));
        onView(withId(R.id.drawer_layout)).perform(DrawerActions.close());
        try {

            onView(allOf(withId(R.id.allergyBtn), isDescendantOfA(withId(R.id.filterLayout)))).perform(ViewActions.scrollTo(),click());
            onView((withText("what are you allergic to"))).check(matches(isDisplayed()));
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    @Test
    public void testingPrepTimeBtn(){
        onView(withId(R.id.drawer_layout)).perform(DrawerActions.open());
        onView(withId(R.id.navView)).perform(navigateTo(R.id.menuItem_filter));
        onView(withId(R.id.drawer_layout)).perform(DrawerActions.close());
        onView(allOf(withId(R.id.timingBtn), isDescendantOfA(withId(R.id.filterLayout)))).perform(ViewActions.scrollTo(),click());
        onView((withText("Choose the preparation time"))).check(matches(isDisplayed()));

    }
    @Test
    public void testingFilterBtn(){
        onView(withId(R.id.drawer_layout)).perform(DrawerActions.open());
        onView(withId(R.id.navView)).perform(navigateTo(R.id.menuItem_filter));
        onView(withId(R.id.drawer_layout)).perform(DrawerActions.close());
        onView(allOf(withId(R.id.filterBtn), isDescendantOfA(withId(R.id.filterLayout)))).perform(ViewActions.scrollTo(),click());
        onView((withId(R.id.recipeList))).check(matches(isDisplayed()));
    }
    @Test
    public void clickingOnShoppingCartMenuDisplaysAppropriateFragment(){
        onView(withId(R.id.drawer_layout)).perform(DrawerActions.open());
        onView(withId(R.id.navView)).perform(navigateTo(R.id.menuItem_soppingCart));
        onView(withId(R.id.shoppingCartFragment)).check(matches(isDisplayed()));
    }
    @Test
    public void test1(){
        ActivityScenario scenario1= ActivityScenario.launch(MainHomeActivity.class);
        onView(withId(R.id.drawer_layout)).perform(DrawerActions.open());
        onView(withId(R.id.navView)).perform(navigateTo(R.id.menuItem_soppingCart));
        scenario.onActivity(activity -> {
            ShoppingListManager manager =new ShoppingListManager(activity.getApplicationContext());
            manager.addIngredient("item1");
        });
        try {
            //onView(withId(R.id.shoppingList)).check(matches(hasDescendant(withText("item1"))));
            onView(withText("item1")).check(matches(isDisplayed()));
            ViewInteraction recyclerView= onView(withId(R.id.shoppingList));
            recyclerView.perform(actionOnItemAtPosition(1,click()));
            onView(withText("Yes")).check(matches(isDisplayed()));
            onView(withText("Yes")).perform(click());
            onView(withId(R.id.shoppingCartFragment)).check(matches(isDisplayed()));
        }catch (Exception e){
            e.printStackTrace();
        }
        scenario1.close();

    }
    @Test
    public void isCorrectListOfRecipesDisplayed(){
        Intents.release();
        Intents.init();
        onView(withId(R.id.drawer_layout)).perform(DrawerActions.open());
        onView(withId(R.id.navView)).perform(navigateTo(R.id.menuItem_home));
        onView(withId(R.id.homeFragment)).check(matches(isDisplayed()));

        onView(withId(R.id.rand_recipe_recyclerView)).check(matches(isDisplayed()));

        onView(withId(R.id.rand_recipe_recyclerView)).check(new RecyclerViewItemCountAssertion(13));
        onView(withId(R.id.rand_recipe_recyclerView)).perform(actionOnItemAtPosition(0, click()));
        Intents.intended(hasComponent(RecipeActivity.class.getName()));
        Intents.release();
    }

}
 class RecyclerViewItemCountAssertion implements ViewAssertion {
    private final int expectedCount;

    public RecyclerViewItemCountAssertion(int expectedCount) {
        this.expectedCount = expectedCount;
    }

    @Override
    public void check(View view, NoMatchingViewException noViewFoundException) {
        if (noViewFoundException != null) {
            throw noViewFoundException;
        }

        RecyclerView recyclerView = (RecyclerView) view;
        RecyclerView.Adapter adapter = recyclerView.getAdapter();
        assertThat(adapter.getItemCount(), is(expectedCount));
    }
}

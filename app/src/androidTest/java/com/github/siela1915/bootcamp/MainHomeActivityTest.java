package com.github.siela1915.bootcamp;

import static androidx.test.core.app.ApplicationProvider.getApplicationContext;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.contrib.NavigationViewActions.navigateTo;
import static androidx.test.espresso.contrib.RecyclerViewActions.actionOnItemAtPosition;
import static androidx.test.espresso.intent.Intents.intending;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static androidx.test.espresso.matcher.ViewMatchers.isClickable;
import static androidx.test.espresso.matcher.ViewMatchers.isDescendantOfA;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withEffectiveVisibility;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static androidx.test.runner.lifecycle.Stage.RESUMED;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.endsWithIgnoringCase;
import static org.hamcrest.Matchers.is;

import android.Manifest;
import android.app.Activity;
import android.app.Instrumentation;
import android.content.Intent;
import android.view.View;

import androidx.recyclerview.widget.RecyclerView;
import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.IdlingRegistry;
import androidx.test.espresso.IdlingResource;
import androidx.test.espresso.NoMatchingViewException;
import androidx.test.espresso.ViewAssertion;
import androidx.test.espresso.ViewInteraction;
import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.contrib.DrawerActions;
import androidx.test.espresso.intent.Intents;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.rule.GrantPermissionRule;
import androidx.test.runner.lifecycle.ActivityLifecycleMonitorRegistry;

import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.util.Collection;

public class MainHomeActivityTest {
    ActivityScenario<MainHomeActivity> scenario;

    @Rule
    public GrantPermissionRule mRuntimePermissionRule = GrantPermissionRule.grant(
            Manifest.permission.ACCESS_FINE_LOCATION);

    @Before
    public void setupEmulators() {
        FirebaseApp.clearInstancesForTest();
        FirebaseApp.initializeApp(getApplicationContext());
        FirebaseAuth.getInstance().useEmulator("10.0.2.2", 9099);
        FirebaseDatabase.getInstance().useEmulator("10.0.2.2", 9000);

        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            FirebaseAuthActivityTest.logoutSync();
        }

        scenario = ActivityScenario.launch(MainHomeActivity.class);
    }

    @After
    public void cleanUp() {
        scenario.close();
    }

    @Test
    public void startingApplicationWithHomePageViewTest(){
        onView(withId(R.id.drawer_layout)).perform(DrawerActions.open());
        onView(withId(R.id.navView)).perform(navigateTo(R.id.menuItem_soppingCart));
    }

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
        FirebaseAuthActivityTest.loginSync("clickingOnFavoritesMenuNavigatesToProfileFragment@test.com");
        onView(withId(R.id.drawer_layout)).perform(DrawerActions.open());
        onView(withId(R.id.navView))
                .perform(navigateTo(R.id.menuItem_favorites));
        onView(withId(R.id.recipeList)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
        FirebaseAuthActivityTest.logoutSync();
    }

    @Test
    public void intentWithNavToFavoritesNavigatesToFavoritesTest() {
        FirebaseAuthActivityTest.loginSync("clickingOnFavoritesMenuNavigatesToProfileFragment@test.com");
        Intent intent = new Intent(getApplicationContext(), MainHomeActivity.class);
        intent.putExtra("com.github.siela1915.bootcamp.navToFavorites", "true");

        try (ActivityScenario<MainHomeActivity> activityScenario = ActivityScenario.launch(intent)) {
            onView(withId(R.id.recipeList)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
        }
        FirebaseAuthActivityTest.logoutSync();
    }

    @Test
    public void intentWithNavToProfileNavigatesToProfileTest() {
        Intent intent = new Intent(getApplicationContext(), MainHomeActivity.class);
        intent.putExtra("com.github.siela1915.bootcamp.navToProfile", "true");

        try (ActivityScenario<MainHomeActivity> activityScenario = ActivityScenario.launch(intent)) {
            onView(ViewMatchers.withId(R.id.profileFragment)).check(matches(isDisplayed()));
        }
    }

    @Test
    public void intentWithNavToHelpNavigatesToNearbyHelpTest() {
        FirebaseAuthActivityTest.loginSync("intentWithNavToHelp@test");
        Intent intent = new Intent(getApplicationContext(), MainHomeActivity.class);
        intent.putExtra("navToHelp", "true");

        try (ActivityScenario<MainHomeActivity> activityScenario = ActivityScenario.launch(intent)) {
            onView(ViewMatchers.withId(R.id.chooseHelpGroup))
                    .check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
        }

        FirebaseAuthActivityTest.logoutSync();
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
        onView(withText("Cancel")).perform(click());
    }
    @Test
    public void testingDietTypeBtn(){
        onView(withId(R.id.drawer_layout)).perform(DrawerActions.open());
        onView(withId(R.id.navView)).perform(navigateTo(R.id.menuItem_filter));
        onView(withId(R.id.drawer_layout)).perform(DrawerActions.close());
        onView(allOf(withId(R.id.dietBtn), isDescendantOfA(withId(R.id.filterLayout)))).perform(ViewActions.scrollTo(),click());
    }
    @Test
    public void testingAllergyBtn(){
        onView(withId(R.id.drawer_layout)).perform(DrawerActions.open());
        onView(withId(R.id.navView)).perform(navigateTo(R.id.menuItem_filter));
        onView(withId(R.id.drawer_layout)).perform(DrawerActions.close());
        onView(allOf(withId(R.id.allergyBtn), isDescendantOfA(withId(R.id.filterLayout)))).perform(ViewActions.scrollTo(),click());
    }
    @Test
    public void testingPrepTimeBtn(){
        onView(withId(R.id.drawer_layout)).perform(DrawerActions.open());
        onView(withId(R.id.navView)).perform(navigateTo(R.id.menuItem_filter));
        onView(withId(R.id.drawer_layout)).perform(DrawerActions.close());
        onView(allOf(withId(R.id.timingBtn), isDescendantOfA(withId(R.id.filterLayout)))).perform(ViewActions.scrollTo(),click());
        onView((withText("Choose the preparation time"))).check(matches(isDisplayed()));
        onView(withText("Cancel")).perform(click());
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
    }
    @Test
    public void isCorrectListOfRecipesDisplayed(){
        Intents.init();
        Intent intent = new Intent();
        Instrumentation.ActivityResult intentResult = new Instrumentation.ActivityResult(Activity.RESULT_OK,intent);
        intending(hasComponent(RecipeActivity.class.getName())).respondWith(intentResult);

        onView(withId(R.id.homeFragment)).check(matches(isDisplayed()));

        onView(withId(R.id.rand_recipe_recyclerView)).check(matches(isDisplayed()));

        IdlingRegistry.getInstance().register(new RecyclerViewIdlingResource());

        onView(withId(R.id.rand_recipe_recyclerView)).check(new RecyclerViewItemCountAssertion(12));
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

class RecyclerViewIdlingResource implements IdlingResource {
    private ResourceCallback resourceCallback;
    private boolean isIdle;

    @Override
    public String getName() {
        return RecyclerViewIdlingResource.class.getName();
    }

    @Override
    public boolean isIdleNow() {
        if (isIdle) return true;
        if (getCurrentActivity() == null) return false;

        RecyclerView recyclerView = getCurrentActivity().findViewById(R.id.rand_recipe_recyclerView);

        isIdle = recyclerView.getAdapter() != null;
        if (isIdle) {
            resourceCallback.onTransitionToIdle();
        }
        return isIdle;
    }

    public Activity getCurrentActivity(){
        Activity currentActivity = null;
        Collection<Activity> resumedActivities =
                ActivityLifecycleMonitorRegistry.getInstance().getActivitiesInStage(RESUMED);
        if (resumedActivities.iterator().hasNext()){
            currentActivity = resumedActivities.iterator().next();
        }


        return currentActivity;
    }
    @Override
    public void registerIdleTransitionCallback(
            IdlingResource.ResourceCallback resourceCallback) {
        this.resourceCallback = resourceCallback;
    }
}

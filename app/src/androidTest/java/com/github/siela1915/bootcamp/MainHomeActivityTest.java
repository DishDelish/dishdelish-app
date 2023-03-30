package com.github.siela1915.bootcamp;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.scrollTo;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import android.icu.text.ListFormatter;

import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.PerformException;
import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.contrib.DrawerActions;
import androidx.test.espresso.contrib.NavigationViewActions;
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
        onView(withId(R.id.homeFragment)).check(matches(isDisplayed()));
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
                .perform(NavigationViewActions.navigateTo(R.id.menuItem_about));
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
                .perform(NavigationViewActions.navigateTo(R.id.menuItem_login));
        onView(withId(R.id.profileFragment)).check(matches(isDisplayed()));
    }

    @Test
    public void clickingOnFavoritesMenuNavigatesToProfileFragmentTest() {
        onView(withId(R.id.drawer_layout)).perform(DrawerActions.open());
        onView(withId(R.id.navView))
                .perform(NavigationViewActions.navigateTo(R.id.menuItem_favorites));
        onView(withId(R.id.recipeList)).check(matches(isDisplayed()));
    }

    @Test
    public void transitionBetweenDifferentFragmentsTest(){
        onView(withId(R.id.drawer_layout)).perform(DrawerActions.open());
        onView(withId(R.id.navView)).perform(NavigationViewActions.navigateTo(R.id.menuItem_about));
        onView(withId(R.id.aboutFragment)).check(matches(isDisplayed()));
    }
    @Test
    public void choosingOtherMenusThanHomeAndAboutDoesNotChangeContentForTheMomentTest(){
        onView(withId(R.id.drawer_layout)).perform(DrawerActions.open());
        onView(withId(R.id.navView)).perform(NavigationViewActions.navigateTo(R.id.menuItem_filter));
        onView(withId(R.id.homeFragment)).check(matches(isDisplayed()));
        
    }
    @Test
    public void testOnChoosingFilter(){
        onView(withId(R.id.drawer_layout)).perform(DrawerActions.open());
        onView(withId(R.id.navView))
                .perform(NavigationViewActions.navigateTo(R.id.menuItem_filter));
        onView(withId(R.id.homeFragment)).check(matches(isDisplayed()));
    }
    @Test
    public void testingCuisineTypeBtn(){
        onView(withId(R.id.drawer_layout)).perform(DrawerActions.open());
        onView(withId(R.id.navView))
                .perform(NavigationViewActions.navigateTo(R.id.menuItem_filter));
        onView(withId(R.id.drawer_layout)).perform(DrawerActions.close());
        try {
            onView(withId(R.id.cuisineBtn))
                    .perform(ViewActions.click());
            onView(withText("Chose your preferred cuisine")).check(matches(isDisplayed()));

        } catch (PerformException e) {

            e.printStackTrace();
        }
    }
    @Test
    public void testingDietTypeBtn(){
        onView(withId(R.id.drawer_layout)).perform(DrawerActions.open());
        onView(withId(R.id.navView)).perform(NavigationViewActions.navigateTo(R.id.menuItem_filter));
        onView(withId(R.id.drawer_layout)).perform(DrawerActions.close());
        try {
            onView(withId(R.id.dietBtn)).perform(ViewActions.click());
            onView((withText("Chose your diet"))).check(matches(isDisplayed()));
        }catch (PerformException e){
            e.printStackTrace();
        }
    }
    @Test
    public void testingAllergyBtn(){
        onView(withId(R.id.drawer_layout)).perform(DrawerActions.open());
        onView(withId(R.id.navView)).perform(NavigationViewActions.navigateTo(R.id.menuItem_filter));
        onView(withId(R.id.drawer_layout)).perform(DrawerActions.close());
        try {
            onView(withId(R.id.notIncludIngBtn)).perform(ViewActions.click());
            onView((withText("what are you allergic to"))).check(matches(isDisplayed()));
        }catch (PerformException e){
            e.printStackTrace();
        }
    }
    @Test
    public void testingPrepTimeBtn(){
        onView(withId(R.id.drawer_layout)).perform(DrawerActions.open());
        onView(withId(R.id.navView)).perform(NavigationViewActions.navigateTo(R.id.menuItem_filter));
        onView(withId(R.id.drawer_layout)).perform(DrawerActions.close());
        try {
            onView(withId(R.id.timingBtn)).perform(ViewActions.click());
            onView((withText("Chose the preparation time"))).check(matches(isDisplayed()));
        }catch (PerformException e){
            e.printStackTrace();
        }
    }
    @Test
    public void testingFilterBtn(){
        onView(withId(R.id.drawer_layout)).perform(DrawerActions.open());
        onView(withId(R.id.navView)).perform(NavigationViewActions.navigateTo(R.id.menuItem_filter));
        onView(withId(R.id.drawer_layout)).perform(DrawerActions.close());
        try {
            onView(withId(R.id.filterBtn)).perform(ViewActions.click());
            onView((withId(R.id.recipeList))).check(matches(isDisplayed()));
        }catch (PerformException e){
            e.printStackTrace();
        }


    }
}

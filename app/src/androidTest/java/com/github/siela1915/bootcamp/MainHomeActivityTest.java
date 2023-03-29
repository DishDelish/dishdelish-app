package com.github.siela1915.bootcamp;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.RootMatchers.isDialog;
import static androidx.test.espresso.matcher.ViewMatchers.isClickable;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayingAtLeast;
import static androidx.test.espresso.matcher.ViewMatchers.isRoot;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import static org.hamcrest.Matchers.allOf;

import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.contrib.DrawerActions;
import androidx.test.espresso.contrib.NavigationViewActions;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.github.siela1915.bootcamp.Labelling.CuisineType;

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
    public void transitionBetweenDifferentFragmentsTest(){
        onView(withId(R.id.drawer_layout)).perform(DrawerActions.open());
        onView(withId(R.id.navView)).perform(NavigationViewActions.navigateTo(R.id.menuItem_about));
        onView(withId(R.id.aboutFragment)).check(matches(isDisplayed()));
    }
    @Test
    public void choosingOtherMenusThanHomeAndAboutDoesNotChangeContentForTheMomentTest(){
        onView(withId(R.id.drawer_layout)).perform(DrawerActions.open());
        onView(withId(R.id.navView)).perform(NavigationViewActions.navigateTo(R.id.menuItem_favorites));
        onView(withId(R.id.homeFragment)).check(matches(isDisplayed()));
        
    }
    @Test
    public void testOnChosingFilter(){
        onView(withId(R.id.drawer_layout)).perform(DrawerActions.open());
        onView(withId(R.id.navView))
                .perform(NavigationViewActions.navigateTo(R.id.menuItem_filter));
        onView(withId(R.id.homeFragment)).check(matches(isDisplayed()));
        onView(withId(R.id.filterLayout)).check(matches(isDisplayed()));
        /*onView(withId(R.id.cuisineBtn)).check(matches(isDisplayed()));
        onView(withId(R.id.notIncludIngBtn)).check(matches(isDisplayed()));
        onView(withId(R.id.dietBtn)).check(matches(isDisplayed()));
        onView(withId(R.id.timingBtn)).check(matches(isDisplayed()));
        onView(withId(R.id.filterBtn)).check(matches(isDisplayed()));
        onView(withId(R.id.cuisineLabel)).check(matches (isDisplayed()));
        onView(withId(R.id.prepTimeLabel)).check(matches(isDisplayed()));
        onView(withId(R.id.dietLabel)).check(matches(isDisplayed()));
        onView(withId(R.id.notIncludLabel)).check(matches(isDisplayed()));

         */
    }
    @Test
    public void testingCuisineTypeBtn(){
        onView(withId(R.id.drawer_layout)).perform(DrawerActions.open());
        onView(withId(R.id.navView))
                .perform(NavigationViewActions.navigateTo(R.id.menuItem_filter));
        onView(withId(R.id.cuisineBtn)).perform(click(),closeSoftKeyboard());
        onView(withText("Chose your preferred cuisine")).check(matches(isDisplayed()));
        /*onView(withText("Ok")).inRoot(isDialog()).check(matches(isClickable()));
        onView(withText("Ok")).inRoot(isDialog()).check(matches(isDisplayed()));
        onView(withText("Ok")).inRoot(isDialog()).perform(click());
        onView(withId(R.id.filterLayout)).check(matches(isDisplayed()));

         */

    }
    @Test
    public void testingDietTypeBtn(){

    }
    @Test
    public void testingAllergyBtn(){

    }
    @Test
    public void testingPrepTimeBtn(){

    }
    @Test
    public void testingFilterBtn(){

    }
}

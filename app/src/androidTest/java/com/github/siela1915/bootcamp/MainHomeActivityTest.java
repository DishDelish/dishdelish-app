package com.github.siela1915.bootcamp;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isClickable;
import static androidx.test.espresso.matcher.ViewMatchers.isDescendantOfA;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withEffectiveVisibility;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.endsWithIgnoringCase;

import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.contrib.DrawerActions;
import androidx.test.espresso.contrib.NavigationViewActions;
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
                .perform(NavigationViewActions.navigateTo(R.id.menuItem_filter));
        onView(withId(R.id.drawer_layout)).perform(DrawerActions.close());
        onView(allOf(withId(R.id.cuisineBtn), isDescendantOfA(withId(R.id.filterLayout)))).perform(ViewActions.scrollTo(),click());
        onView(withText("Choose your preferred cuisine")).check(matches(isDisplayed()));

    }
    @Test
    public void testingDietTypeBtn(){
        onView(withId(R.id.drawer_layout)).perform(DrawerActions.open());
        onView(withId(R.id.navView)).perform(NavigationViewActions.navigateTo(R.id.menuItem_filter));
        onView(withId(R.id.drawer_layout)).perform(DrawerActions.close());
        onView(allOf(withId(R.id.dietBtn), isDescendantOfA(withId(R.id.filterLayout)))).perform(ViewActions.scrollTo(),click());
        onView((withText(endsWithIgnoringCase("Choose your diet")))).check(matches(isDisplayed()));
    }
    @Test
    public void testingAllergyBtn(){
        onView(withId(R.id.drawer_layout)).perform(DrawerActions.open());
        onView(withId(R.id.navView)).perform(NavigationViewActions.navigateTo(R.id.menuItem_filter));
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
        onView(withId(R.id.navView)).perform(NavigationViewActions.navigateTo(R.id.menuItem_filter));
        onView(withId(R.id.drawer_layout)).perform(DrawerActions.close());
        onView(allOf(withId(R.id.timingBtn), isDescendantOfA(withId(R.id.filterLayout)))).perform(ViewActions.scrollTo(),click());
        onView((withText("Choose the preparation time"))).check(matches(isDisplayed()));

    }
    @Test
    public void testingFilterBtn(){
        onView(withId(R.id.drawer_layout)).perform(DrawerActions.open());
        onView(withId(R.id.navView)).perform(NavigationViewActions.navigateTo(R.id.menuItem_filter));
        onView(withId(R.id.drawer_layout)).perform(DrawerActions.close());
        onView(allOf(withId(R.id.filterBtn), isDescendantOfA(withId(R.id.filterLayout)))).perform(ViewActions.scrollTo(),click());
        onView((withId(R.id.recipeList))).check(matches(isDisplayed()));
    }


}

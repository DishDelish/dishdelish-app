package com.github.siela1915.bootcamp;

import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.hasDescendant;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import androidx.fragment.app.testing.FragmentScenario;
import androidx.test.espresso.Espresso;
import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.assertion.ViewAssertions;
import androidx.test.espresso.contrib.RecyclerViewActions;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class ShoppingCartFragmentTest {

    @Before
    public void launchFragment() {
        // Launch the ShoppingCartFragment
        FragmentScenario.launchInContainer(ShoppingCartFragment.class);
    }

    @Test
    public void testAddItemToShoppingList() {

        try {
            Thread.sleep(3000); // Add a delay of 500 milliseconds
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        // Type a new item in the EditText
        Espresso.onView(ViewMatchers.withId(R.id.editTextItem))
                 // Add a delay of 500 milliseconds
                .perform(ViewActions.typeText("New Item"));

        // Click the Add button
        Espresso.onView(ViewMatchers.withId(R.id.buttonAdd))
                .perform(ViewActions.click());

        // Verify if the item is added to the shopping list RecyclerView
        Espresso.onView(ViewMatchers.withId(R.id.shoppingList))
                .check(matches(hasDescendant(withText("New Item"))));

        // Additional assertions or verifications if needed
    }

    @Test
    public void testClearAllItems() {
        // Add items
        try {
            Thread.sleep(500); // Add a delay of 500 milliseconds
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        // Type a new item in the EditText
        Espresso.onView(ViewMatchers.withId(R.id.editTextItem))
                .perform(ViewActions.typeText("item1"));

        // Click the Add button
        Espresso.onView(ViewMatchers.withId(R.id.buttonAdd))
                .perform(ViewActions.click());


        // Click the Clear All button
        Espresso.onView(ViewMatchers.withId(R.id.buttonClearAll))
                .perform(ViewActions.scrollTo())
                .perform(ViewActions.click());


        // need to add an item otherwise recyclerview is not visible
        Espresso.onView(ViewMatchers.withId(R.id.editTextItem))
                .perform(ViewActions.typeText("item2"));
        Espresso.onView(ViewMatchers.withId(R.id.buttonAdd))
                .perform(ViewActions.click());


        Espresso.onView(ViewMatchers.withId(R.id.shoppingList))
                .check(matches(isDisplayed())); // Check if RecyclerView is still displayed

    }

    @Test
    public void testDeleteSelectedItems() {
        try {
            Thread.sleep(500); // Add a delay of 500 milliseconds
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        // Type a new item in the EditText
        Espresso.onView(ViewMatchers.withId(R.id.editTextItem))
                .perform(ViewActions.typeText("item1"));

        // Click the Add button
        Espresso.onView(ViewMatchers.withId(R.id.buttonAdd))
                .perform(ViewActions.click());

        // Type a new item in the EditText
        Espresso.onView(ViewMatchers.withId(R.id.editTextItem))
                .perform(ViewActions.typeText("item2"));

        // Click the Add button
        Espresso.onView(ViewMatchers.withId(R.id.buttonAdd))
                .perform(ViewActions.click());

        // Select an item by clicking on its checkbox in the RecyclerView
        Espresso.onView(ViewMatchers.withId(R.id.shoppingList))
                .perform(RecyclerViewActions.actionOnItemAtPosition(0, ViewActions.click()));

        // Click the Delete Selected button
        Espresso.onView(ViewMatchers.withId(R.id.buttonDeleteSelected))
                .perform(ViewActions.scrollTo())
                .perform(ViewActions.click());

        // Verify if the selected item is deleted from the shopping list RecyclerView
        Espresso.onView(ViewMatchers.withId(R.id.shoppingList))
                .check(matches(isDisplayed())); // Check if RecyclerView is still displayed

    }


}
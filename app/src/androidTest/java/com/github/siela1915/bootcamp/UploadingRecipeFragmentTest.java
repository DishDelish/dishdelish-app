package com.github.siela1915.bootcamp;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.doesNotExist;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.Visibility.INVISIBLE;
import static androidx.test.espresso.matcher.ViewMatchers.Visibility.VISIBLE;
import static androidx.test.espresso.matcher.ViewMatchers.isAssignableFrom;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withEffectiveVisibility;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.isEmptyOrNullString;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertNotEquals;

import android.widget.EditText;

import androidx.fragment.app.testing.FragmentScenario;
import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.matcher.ViewMatchers;

import com.github.siela1915.bootcamp.firebase.Database;
import com.google.android.material.chip.Chip;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import org.junit.After;
import org.junit.Test;

import java.util.HashMap;

public class UploadingRecipeFragmentTest {
    FragmentScenario<UploadingRecipeFragment> scenario;

    @After
    public void cleanUp() {
        scenario.close();
    }

    @Test
    public void initialDisplayTest() {
        scenario = FragmentScenario.launchInContainer(UploadingRecipeFragment.class);

        onView(withId(R.id.uploadRecipeTitle)).check(matches(withEffectiveVisibility(VISIBLE)));
        onView(withId(R.id.recipeName)).check(matches(withEffectiveVisibility(VISIBLE)));
        onView(withId(R.id.prepTime)).check(matches(withEffectiveVisibility(VISIBLE)));
        onView(withId(R.id.cookTime)).check(matches(withEffectiveVisibility(VISIBLE)));
        onView(withId(R.id.servings)).check(matches(withEffectiveVisibility(VISIBLE)));
        onView(withId(R.id.utensils)).check(matches(withEffectiveVisibility(VISIBLE)));
        onView(withId(R.id.ingredientsGroup)).check(matches(withEffectiveVisibility(VISIBLE)));
        onView(withId(R.id.ingredientsAmount)).check(matches(withEffectiveVisibility(VISIBLE)));
        onView(withId(R.id.ingredientsUnit)).check(matches(withEffectiveVisibility(VISIBLE)));
        onView(withId(R.id.ingredientsName)).check(matches(withEffectiveVisibility(VISIBLE)));
        onView(withId(R.id.addIngredientButton)).check(matches(withEffectiveVisibility(VISIBLE)));
        onView(withId(R.id.stepGroup)).check(matches(withEffectiveVisibility(VISIBLE)));
        onView(withId(R.id.step)).check(matches(withEffectiveVisibility(VISIBLE)));
        onView(withId(R.id.addStepButton)).check(matches(withEffectiveVisibility(VISIBLE)));
        onView(withId(R.id.recipeImage)).check(matches(withEffectiveVisibility(VISIBLE)));
        onView(withId(R.id.recipeUploadButton)).check(matches(withEffectiveVisibility(VISIBLE)));
    }

    @Test
    public void editRecipeNameTest() {
        scenario = FragmentScenario.launchInContainer(UploadingRecipeFragment.class);

        onView(withId(R.id.recipeName)).perform(ViewActions.replaceText("recipe-test"));

        onView(withId(R.id.recipeName)).check(matches(ViewMatchers.withText("recipe-test")));
    }

    @Test
    public void editRecipePrepTimeTest() {
        scenario = FragmentScenario.launchInContainer(UploadingRecipeFragment.class);

        onView(withId(R.id.prepTime)).perform(ViewActions.replaceText("10"));

        onView(withId(R.id.prepTime)).check(matches(ViewMatchers.withText("10")));
    }

    @Test
    public void editRecipeCookTimeTest() {
        scenario = FragmentScenario.launchInContainer(UploadingRecipeFragment.class);

        onView(withId(R.id.cookTime)).perform(ViewActions.replaceText("10"));

        onView(withId(R.id.cookTime)).check(matches(ViewMatchers.withText("10")));
    }

    @Test
    public void editRecipeServingsTest() {
        scenario = FragmentScenario.launchInContainer(UploadingRecipeFragment.class);

        onView(withId(R.id.servings)).perform(ViewActions.replaceText("10"));

        onView(withId(R.id.servings)).check(matches(ViewMatchers.withText("10")));
    }

    @Test
    public void editRecipeIngredientTest() {
        scenario = FragmentScenario.launchInContainer(UploadingRecipeFragment.class);

        onView(withId(R.id.ingredientsAmount)).perform(ViewActions.replaceText("10"));
        onView(withId(R.id.ingredientsName)).perform(ViewActions.replaceText("ingredient-name-test"));
        onView(withId(R.id.ingredientsUnit)).perform(ViewActions.replaceText("ingredient-unit-test"));

        onView(withId(R.id.ingredientsAmount)).check(matches(ViewMatchers.withText("10")));
        onView(withId(R.id.ingredientsName)).check(matches(ViewMatchers.withText("ingredient-name-test")));
        onView(withId(R.id.ingredientsUnit)).check(matches(ViewMatchers.withText("ingredient-unit-test")));
    }

    @Test
    public void addRecipeIngredientTest() {
        scenario = FragmentScenario.launchInContainer(UploadingRecipeFragment.class);

        String amountTest = "10";
        String nameTest = "ingredient-name-test";
        String unitTest = "ingredient-unit-test";

        onView(withId(R.id.ingredientsAmount)).perform(ViewActions.replaceText(amountTest));
        onView(withId(R.id.ingredientsName)).perform(ViewActions.replaceText(nameTest));
        onView(withId(R.id.ingredientsUnit)).perform(ViewActions.replaceText(unitTest)).perform(ViewActions.closeSoftKeyboard());

        onView(withId(R.id.addIngredientButton)).perform(ViewActions.click());

        String expectedDisplay = amountTest + " " + unitTest + " " + nameTest;
        onView(allOf(withText(containsString(expectedDisplay)), isAssignableFrom(Chip.class))).check((matches(isDisplayed())));
    }

    @Test
    public void deleteRecipeIngredientTest() {
        scenario = FragmentScenario.launchInContainer(UploadingRecipeFragment.class);

        String amountTest = "10";
        String nameTest = "ingredient-name-test";
        String unitTest = "ingredient-unit-test";

        // add ingredient
        onView(withId(R.id.ingredientsAmount)).perform(ViewActions.replaceText(amountTest));
        onView(withId(R.id.ingredientsName)).perform(ViewActions.replaceText(nameTest));
        onView(withId(R.id.ingredientsUnit)).perform(ViewActions.replaceText(unitTest)).perform(ViewActions.closeSoftKeyboard());
        onView(withId(R.id.addIngredientButton)).perform(ViewActions.click());

        onView(withId(com.google.android.material.R.id.chip)).perform(ViewActions.click());

        String expectedDisplay = amountTest + " " + unitTest + " " + nameTest;
        onView(allOf(withText(containsString(expectedDisplay)), isAssignableFrom(Chip.class))).check(doesNotExist());
    }

    @Test
    public void editRecipeUtensilsTest() {
        scenario = FragmentScenario.launchInContainer(UploadingRecipeFragment.class);

        onView(withId(R.id.utensils)).perform(ViewActions.replaceText("utensils-test"));

        onView(withId(R.id.utensils)).check(matches(ViewMatchers.withText("utensils-test")));
    }

    @Test
    public void editRecipeStepTest() {
        scenario = FragmentScenario.launchInContainer(UploadingRecipeFragment.class);

        onView(withId(R.id.step)).perform(ViewActions.replaceText("step-test"));

        onView(withId(R.id.step)).check(matches(ViewMatchers.withText("step-test")));
    }

    @Test
    public void addRecipeStepTest() {
        scenario = FragmentScenario.launchInContainer(UploadingRecipeFragment.class);

        onView(withId(R.id.step)).perform(ViewActions.replaceText("step-test"));

        onView(withId(R.id.addStepButton)).perform(ViewActions.click());

        onView(allOf(withText(containsString("step-test")), isAssignableFrom(EditText.class))).check((matches(isDisplayed())));
    }

    @Test
    public void uploadRecipeToFirebase() {
        // set up emulator user
        FirebaseAuth.getInstance().useEmulator("10.0.2.2", 9099);
        FirebaseDatabase.getInstance().useEmulator("10.0.2.2", 9000);

        scenario = FragmentScenario.launchInContainer(UploadingRecipeFragment.class);

        String recipeName = "recipe-test";
        String prepTime = "10";
        String cookTime = "10";
        String servings = "10";
        String utensils = "utensils-test";
        String step = "step-test";
        String ingredientsAmountTest = "10";
        String ingredientsNameTest = "ingredient-name-test";
        String ingredientsUnitTest = "ingredient-unit-test";

        // simulate user input
        onView(withId(R.id.recipeName)).perform(ViewActions.replaceText(recipeName));
        onView(withId(R.id.prepTime)).perform(ViewActions.replaceText(prepTime));
        onView(withId(R.id.cookTime)).perform(ViewActions.replaceText(cookTime));
        onView(withId(R.id.servings)).perform(ViewActions.replaceText(servings));
        onView(withId(R.id.ingredientsAmount)).perform(ViewActions.replaceText(ingredientsAmountTest));
        onView(withId(R.id.ingredientsName)).perform(ViewActions.replaceText(ingredientsNameTest));
        onView(withId(R.id.ingredientsUnit)).perform(ViewActions.replaceText(ingredientsUnitTest));
        onView(withId(R.id.addIngredientButton)).perform(ViewActions.click());
        onView(withId(R.id.utensils)).perform(ViewActions.replaceText(utensils));
        onView(withId(R.id.step)).perform(ViewActions.replaceText(step));

        onView(withId(R.id.recipeUploadButton)).perform(ViewActions.scrollTo(),ViewActions.click());

        Database db = new Database();

        HashMap<String, Object> recipe = new HashMap<>();
        recipe.put("name", recipeName);
        recipe.put("cookTime", cookTime);
        recipe.put("prepTime", prepTime);
        recipe.put("servings", servings);
        recipe.put("utensils", utensils);
        recipe.put("ingredientList", ingredientsAmountTest + " " + ingredientsUnitTest + " " + ingredientsNameTest);
        recipe.put("steps", step);

        String recipeReadFromFirebase = db.getByName(recipeName);
        assertNotEquals(recipeReadFromFirebase, recipe.toString());

        // remember to delete the test recipe from database
    }
}
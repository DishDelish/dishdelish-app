package com.github.siela1915.bootcamp;

import com.google.firebase.database.FirebaseDatabase;

import org.junit.Before;

public class SuggestionsTest {

    private FirebaseDatabase firebaseInstance;

    @Before
    public void useEmulator() {
        firebaseInstance = FirebaseDatabase.getInstance();
        firebaseInstance.useEmulator("10.0.2.2", 9000);
    }

    @Test
    public void testGetSuggestions() throws ExecutionException, InterruptedException {
        // Mock Database and Utilities
        Database db = new Database(null);
        Utilities.setMockData();

        // Mock the expected results
        List<Recipe> expectedSuggestions = Arrays.asList(
                new Recipe("Recipe 1"),
                new Recipe("Recipe 2"),
                new Recipe("Recipe 3")
        );

        // Stub the async operations with mock data
        MockTask<List<Recipe>> favouritesTask = new MockTask<>(Utilities.getMockFavourites());
        MockTask<List<Recipe>> randomTask = new MockTask<>(Utilities.getMockRandomRecipes());
        MockTask<List<Recipe>> maxLikesTask = new MockTask<>(Utilities.getMockTopRecipes());

        // Create a list of the stubbed tasks
        List<MockTask<List<Recipe>>> tasks = Arrays.asList(favouritesTask, randomTask, maxLikesTask);

        // Execute the getSuggestions() method
        Task<List<Recipe>> suggestionsTask = Suggestions.getSuggestions(tasks, db);

        // Await the completion of the task and get the result
        List<Recipe> actualSuggestions = suggestionsTask.getResult();

        // Verify the result
        Assert.assertEquals(expectedSuggestions.size(), actualSuggestions.size());
        Assert.assertTrue(actualSuggestions.containsAll(expectedSuggestions));
    }
}

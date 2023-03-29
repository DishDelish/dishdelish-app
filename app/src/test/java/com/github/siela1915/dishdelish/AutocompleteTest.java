package com.github.siela1915.dishdelish;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import com.github.siela1915.bootcamp.AutocompleteApi.ApiResponse;
import com.github.siela1915.bootcamp.AutocompleteApi.ApiService;
import com.github.siela1915.bootcamp.AutocompleteApi.IngredientAutocomplete;
import com.github.siela1915.dishdelish.ApiTests.MockApi;

import org.junit.Test;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class AutocompleteTest {
    IngredientAutocomplete fetcher = new IngredientAutocomplete();

    @Test
    public void apiCallReturnsListOfIngredients() throws InterruptedException {
        List<String> correctResponse = Arrays.asList("apple", "apple juice", "apple cider", "apple jelly");
        List<ApiResponse> rep = new ArrayList<>();
        fetcher.completeSearch("app", rep);
        Thread.sleep(100);
        for (String r: rep.stream().map(r -> r.name).collect(Collectors.toList())) {
            assertTrue(correctResponse.contains(r));
        }
    }

    @Test
    public void mockApiCallReturnsNonEmptyResponse(){
        IngredientAutocomplete fetcher = new IngredientAutocomplete();
        fetcher.service = new MockApi(false);
        List<ApiResponse> ing = new ArrayList<>();
        assertEquals("apple", fetcher.completeSearch("app", ing).get(0).name);
    }

    @Test
    public void failedCallReturnsEmptyList() throws InterruptedException {
        IngredientAutocomplete fetcher = new IngredientAutocomplete();
        fetcher.service = new MockApi(true);
        List<ApiResponse> ing = new ArrayList<>();
        assertTrue(ing.isEmpty());

    }

    @Test
    public void failedCallDoesntChangeList(){
        IngredientAutocomplete fetcher = new IngredientAutocomplete();
        fetcher.service = new MockApi(true);


        IngredientAutocomplete fetcher2 = new IngredientAutocomplete();
        fetcher2.service = new MockApi(false);
        List<ApiResponse> ing = new ArrayList<>();
        fetcher.completeSearch("a", ing);
        assertEquals("apple", fetcher2.completeSearch("app", ing).get(0).name);
        fetcher.completeSearch("a", ing);
        assertEquals("apple", ing.get(0).name);

    }

    @Test
    public void mockitoTest(){
        ApiService mockedApi = Mockito.mock(ApiService.class);

    }

}

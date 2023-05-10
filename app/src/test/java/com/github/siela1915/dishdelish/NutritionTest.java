package com.github.siela1915.dishdelish;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

import com.github.siela1915.bootcamp.AutocompleteApi.ApiResponse;
import com.github.siela1915.bootcamp.AutocompleteApi.IngredientAutocomplete;
import com.github.siela1915.bootcamp.AutocompleteApi.Nutrient;
import com.github.siela1915.bootcamp.AutocompleteApi.NutrientsResponse;
import com.github.siela1915.bootcamp.AutocompleteApi.Nutrition;
import com.github.siela1915.bootcamp.Recipes.Ingredient;

import org.checkerframework.checker.units.qual.A;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import okhttp3.Request;
import okio.Timeout;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NutritionTest {

    @Test
    public void nutritionEqualsWorks() throws InterruptedException {
        Nutrient n1 = new Nutrient("apple", 1, "g", 1);
        Nutrient n2 = new Nutrient("apple", 1, "g", 1);
        Nutrient n3 = new Nutrient("pear", 1, "g", 1);
        assertTrue(n1.equals(n2));
        assertFalse(n1.equals(n3));
    }

    @Test
    public void nutritionResponseEqualsWorks(){
        NutrientsResponse r1 = new NutrientsResponse(new Nutrition(Arrays.asList(new Nutrient("apple", 1, "g", 1))));
        NutrientsResponse r2 = new NutrientsResponse(new Nutrition(Arrays.asList(new Nutrient("apple", 1, "g", 1))));
        NutrientsResponse r3 = new NutrientsResponse(new Nutrition(Arrays.asList(new Nutrient("pear", 1, "g", 1))));
        assertTrue(r1.equals(r2));
        assertFalse(r1.equals(r3));
    }
}

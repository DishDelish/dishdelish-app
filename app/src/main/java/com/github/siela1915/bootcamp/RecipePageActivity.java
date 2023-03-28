package com.github.siela1915.bootcamp;


import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.github.siela1915.bootcamp.Labelling.AllergyType;
import com.github.siela1915.bootcamp.Labelling.CuisineType;
import com.github.siela1915.bootcamp.Labelling.DietType;
import com.github.siela1915.bootcamp.Labelling.RecipeFetcher;
import com.github.siela1915.bootcamp.Recipes.Ingredient;
import com.github.siela1915.bootcamp.Recipes.Unit;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class RecipePageActivity extends AppCompatActivity {
    private RecipeFetcher fetcher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_page);


        List<Integer> allergies = Arrays.asList(AllergyType.EGGS.ordinal());
        List<Integer> cuisines = Arrays.asList(CuisineType.FRENCH.ordinal());
        List<Integer> diets = new ArrayList<>();

        fetcher = new RecipeFetcher(allergies, cuisines, diets);
    }

    public void buttonClicked(View view) {
        TextView userName = findViewById(R.id.fetched_recipes_view);
        userName.setText(fetcher.fetchRecipeList().stream().collect(Collectors.joining("\n")));
    }
}
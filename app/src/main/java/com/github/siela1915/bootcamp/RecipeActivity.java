package com.github.siela1915.bootcamp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

public class RecipeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe);

        Button rateButton = (Button) findViewById(R.id.rateButton);
        rateButton.setOnClickListener(v -> {
            Intent ratingIntent = new Intent(v.getContext(), RatingActivity.class);

            // later add an identifier for the recipe in question
            // v.putExtra("rid", recipe.id.toString()); etc.
            v.getContext().startActivity(ratingIntent);
        });


    }
}
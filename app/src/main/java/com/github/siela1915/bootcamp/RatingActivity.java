package com.github.siela1915.bootcamp;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.Toast;

import com.github.siela1915.bootcamp.Recipes.Recipe;
import com.github.siela1915.bootcamp.firebase.Database;
import com.google.firebase.database.FirebaseDatabase;

public class RatingActivity extends AppCompatActivity implements RatingBar.OnRatingBarChangeListener {
    private float rating;
    private Recipe recipe;

    private final FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();

    private final Database database = new Database(firebaseDatabase);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rating);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        recipe = getIntent().getParcelableExtra("Recipe");

        rating = 0;
        RatingBar rbar = (RatingBar)findViewById(R.id.ratingActivityBar);
        rbar.setOnRatingBarChangeListener(this);

        Button submitButton = (Button) findViewById(R.id.submitRatingButton);
        submitButton.setOnClickListener(v -> {

            float newRating = (float) ((recipe.rating * recipe.numRatings + rating) / (recipe.numRatings + 1));
            recipe.setRating(newRating);
            recipe.setNumRatings(recipe.numRatings + 1);

            database.updateAsync(recipe).addOnCompleteListener(task -> {
                if(task.isSuccessful()){
                    Toast.makeText(this, "Thanks for the feedback!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "Error adding new comment", Toast.LENGTH_SHORT).show();
                }
            });
            onBackPressed();
        });

    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    @Override
    public void onRatingChanged(RatingBar ratingBar, float v, boolean b) {
        rating = v;
    }
}
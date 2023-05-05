package com.github.siela1915.bootcamp;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.github.siela1915.bootcamp.Recipes.Recipe;
import com.github.siela1915.bootcamp.firebase.Database;
import com.google.firebase.database.FirebaseDatabase;

public class RatingActivity extends AppCompatActivity implements RatingBar.OnRatingBarChangeListener, View.OnClickListener {
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
        RatingBar rbar = (RatingBar) findViewById(R.id.ratingActivityBar);
        rbar.setOnRatingBarChangeListener(this);

        Button submitButton = (Button) findViewById(R.id.submitRatingButton);
        submitButton.setOnClickListener(this);
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

    /**
     * @param v The view that was clicked.
     */
    @Override
    public void onClick(View v) {

        double oldRating = recipe.getRating();

        float newRating = (float) ((recipe.rating * recipe.numRatings + rating) / (recipe.numRatings + 1));
        recipe.setRating(newRating);
        recipe.setNumRatings(recipe.numRatings + 1);

        database.updateAsync(recipe)
                .addOnSuccessListener(arg -> {
                    Toast.makeText(this, "Thanks for the feedback! ", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {

                    // undo
                    recipe.setRating(oldRating);
                    recipe.setNumRatings(recipe.numRatings - 1);
                    Toast.makeText(this, "Try again", Toast.LENGTH_SHORT).show();
                });
        onBackPressed();
    }
}
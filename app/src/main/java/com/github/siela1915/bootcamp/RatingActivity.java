package com.github.siela1915.bootcamp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.RatingBar;

public class RatingActivity extends AppCompatActivity implements RatingBar.OnRatingBarChangeListener {
    private float rating;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rating);

        rating = 0;
        RatingBar rbar = (RatingBar)findViewById(R.id.ratingActivityBar);
        rbar.setOnRatingBarChangeListener(this);

        Button submitButton = (Button) findViewById(R.id.submitRatingButton);
        submitButton.setOnClickListener(v -> {
            //Later do something with the rating
            Intent submitIntent = new Intent(v.getContext(), MainActivity.class);
            startActivity(submitIntent);
        });

    }

    @Override
    public void onRatingChanged(RatingBar ratingBar, float v, boolean b) {
        rating = v;
    }
}
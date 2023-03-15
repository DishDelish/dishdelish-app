package com.github.siela1915.bootcamp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import org.w3c.dom.Text;

public class RecipeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe);

        Intent i = getIntent();

        setPageContents(i);

        Button rateButton = (Button) findViewById(R.id.rateButton);
        rateButton.setOnClickListener(v -> {
            Intent ratingIntent = new Intent(v.getContext(), RatingActivity.class);

            v.getContext().startActivity(ratingIntent);
        });


    }

    private void setPageContents(Intent i){

        ImageView recipePicture =(ImageView) findViewById(R.id.recipePicture);
        TextView recipeNameText =(TextView) findViewById(R.id.recipeNameText);
        ImageView userAvatar=(ImageView) findViewById(R.id.userAvatar);
        TextView userNameText=(TextView) findViewById(R.id.userNameText);
        TextView prepTime=(TextView) findViewById(R.id.prepTimeNbMins);
        TextView kcalCount=(TextView) findViewById(R.id.kcalCount);
        TextView cookTime=(TextView) findViewById(R.id.cookTimeNbMins);
        TextView nbServings=(TextView) findViewById(R.id.nbServings);
        TextView ingredientsList=(TextView) findViewById(R.id.ingredientsList);
        TextView utensilsList=(TextView) findViewById(R.id.utensilsList);
        TextView stepsText=(TextView) findViewById(R.id.stepsText);
        TextView commentsText=(TextView) findViewById(R.id.commentsText);
        RatingBar ratingBar = (RatingBar) findViewById(R.id.ratingBar);

        Bitmap recipeImage = BitmapFactory.decodeResource(this.getResources(), i.getIntExtra("image",0));
        recipePicture.setImageBitmap(recipeImage);

        Bitmap avatar = BitmapFactory.decodeResource(this.getResources(), i.getIntExtra("profilePicture",0));
        userAvatar.setImageBitmap(avatar);

        recipeNameText.setText(i.getStringExtra("recipeName"));
        userNameText.setText(i.getStringExtra("userName"));

        ratingBar.setRating((float) i.getDoubleExtra("rating", 0.0));
        prepTime.setText(String.valueOf(i.getIntExtra("prepTime",0)));

        cookTime.setText(String.valueOf(i.getIntExtra("cookTime",0)));
        nbServings.setText(String.valueOf(i.getIntExtra("servings",0)));
        utensilsList.setText(String.join(", ", i.getStringArrayExtra("utensils")));

        ingredientsList.setText(String.join("\n", i.getStringArrayListExtra("ingredients")));
        stepsText.setText(String.join("\n\n", i.getStringArrayExtra("steps")));
        commentsText.setText(String.join("\n", i.getStringArrayExtra("comments")));

    }
}
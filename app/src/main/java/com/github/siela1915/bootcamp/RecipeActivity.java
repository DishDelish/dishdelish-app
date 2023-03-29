package com.github.siela1915.bootcamp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.github.siela1915.bootcamp.Recipes.Recipe;

public class RecipeActivity extends AppCompatActivity {

    private Recipe recipe;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        recipe = getIntent().getParcelableExtra("Recipe");

        setPageContents();

        Button rateButton = (Button) findViewById(R.id.rateButton);
        rateButton.setOnClickListener(v -> {
            Intent ratingIntent = new Intent(v.getContext(), RatingActivity.class);

            v.getContext().startActivity(ratingIntent);
        });

        ToggleButton heart = (ToggleButton) findViewById(R.id.favoriteButton);
        heart.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                heart.setBackground(getDrawable(R.drawable.heart_full));
            } else {
                heart.setBackground(getDrawable(R.drawable.heart_empty));
            }
        });


    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    private void setPageContents(){

        ImageView recipePicture =(ImageView) findViewById(R.id.recipePicture);
        TextView recipeNameText =(TextView) findViewById(R.id.recipeNameText);
        ImageView userAvatar=(ImageView) findViewById(R.id.userAvatar);
        TextView userNameText=(TextView) findViewById(R.id.userNameText);
        TextView prepTime=(TextView) findViewById(R.id.prepTimeNbMins);
        TextView kcalCount=(TextView) findViewById(R.id.kcalCount);
        TextView cookTime=(TextView) findViewById(R.id.cookTimeNbMins);
        TextView nbServings=(TextView) findViewById(R.id.nbServings);
        RecyclerView ingredientsList=(RecyclerView) findViewById(R.id.ingredientsList);
        TextView utensilsList=(TextView) findViewById(R.id.utensilsList);
        TextView stepsText=(TextView) findViewById(R.id.stepsText);
        RecyclerView commentsList=(RecyclerView) findViewById(R.id.commentsList);
        RatingBar ratingBar = (RatingBar) findViewById(R.id.ratingBar);

        Bitmap recipeImage = BitmapFactory.decodeResource(this.getResources(), recipe.image);
        recipePicture.setImageBitmap(recipeImage);

        Bitmap avatar = BitmapFactory.decodeResource(this.getResources(), recipe.profilePicture);
        userAvatar.setImageBitmap(avatar);

        recipeNameText.setText(recipe.recipeName);
        userNameText.setText(recipe.userName);

        ratingBar.setRating((float) recipe.rating);
        prepTime.setText(String.valueOf(recipe.prepTime));

        cookTime.setText(String.valueOf(recipe.cookTime));
        nbServings.setText(String.valueOf(recipe.servings));
        utensilsList.setText(String.join(", ", recipe.utensils.getUtensils()));

        stepsText.setText(String.join("\n\n", recipe.steps));

        // Set Comment fields

        commentsList.setLayoutManager(new LinearLayoutManager(this));
        CommentAdapter commentAdapter = new CommentAdapter(getApplicationContext(), recipe.comments);
        commentsList.setAdapter(commentAdapter);

        EditText commentBox = (EditText) findViewById(R.id.enterComment);
        Button sendComment = (Button) findViewById(R.id.sendCommentButton);

        sendComment.setOnClickListener(view -> {
            String input = commentBox.getText().toString();
            System.out.println(input);
            if(!input.isEmpty()){

                // if logged in
                // update
                commentBox.setText("");
                recipe.comments.add(input);
                commentAdapter.notifyItemInserted(recipe.comments.size()-1);
                // else
                // login popup

            }
        });

        // Set Ingredient List fields
        ingredientsList.setLayoutManager(new LinearLayoutManager(this));
        IngredientAdapter ingredientAdapter = new IngredientAdapter(getApplicationContext(), recipe.ingredientList);
        ingredientsList.setAdapter(ingredientAdapter);

    }
}
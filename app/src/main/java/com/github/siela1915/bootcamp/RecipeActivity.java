package com.github.siela1915.bootcamp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.github.siela1915.bootcamp.Recipes.Comment;
import com.github.siela1915.bootcamp.Recipes.Ingredient;
import com.github.siela1915.bootcamp.Recipes.Recipe;
import com.github.siela1915.bootcamp.Recipes.Unit;

import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class RecipeActivity extends AppCompatActivity {

    private Recipe recipe;
    private ShoppingListManager shoppingListManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe);

        shoppingListManager = new ShoppingListManager(this);


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

                // for testing
                heart.setTag("full");
            } else {
                heart.setBackground(getDrawable(R.drawable.heart_empty));
                heart.setTag("empty");
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
        TextView servings = (TextView) findViewById(R.id.servings);

        // not sure about this
        //Bitmap recipeImage = BitmapFactory.decodeResource(this.getResources(), Integer.valueOf(recipe.image));
        //recipePicture.setImageBitmap(recipeImage);

        //Picasso.get().load(recipe.image).into(recipePicture);

        Bitmap avatar = BitmapFactory.decodeResource(this.getResources(), recipe.profilePicture);
        userAvatar.setImageBitmap(avatar);

        recipeNameText.setText(recipe.recipeName);
        userNameText.setText(recipe.userName);

        ratingBar.setRating((float) recipe.rating);
        prepTime.setText(String.valueOf(recipe.prepTime));

        cookTime.setText(String.valueOf(recipe.cookTime));
        utensilsList.setText(String.join(", ", recipe.utensils.getUtensils()));

        stepsText.setText(String.join("\n\n", recipe.steps));


        // Set Ingredient List content
        setIngredientListContents(ingredientsList);

        // Set servings info
        setServingInfo(nbServings, servings, ingredientsList);

        // Set Comment fields
        setCommentContents(commentsList);

    }

    private void modifyIngredientAmounts(int n, int previous, RecyclerView ingredientsList) {
        IngredientAdapter adapter = (IngredientAdapter) ingredientsList.getAdapter();
        List<Ingredient> data = adapter.getData();

        for(int i = 0; i < data.size(); i++){
            Ingredient original = recipe.getIngredientList().get(i);
            String ingredient = original.getIngredient();
            String info = original.getUnit().getInfo();
            int oldValue = original.getUnit().getValue();
            double temp = ((double)n/recipe.servings);
            int newValue = (int)(Math.ceil(temp * oldValue));

            data.set(i, new Ingredient(ingredient, new Unit(newValue , info)));
            adapter.notifyItemChanged(i);
        }
    }

    private void setIngredientListContents(RecyclerView ingredientsList){

        ingredientsList.setLayoutManager(new LinearLayoutManager(this));
        IngredientAdapter ingredientAdapter = new IngredientAdapter(getApplicationContext(), new ArrayList<>(recipe.getIngredientList()), shoppingListManager);
        ingredientsList.setAdapter(ingredientAdapter);

    }

    private void setCommentContents(RecyclerView commentsList){

        commentsList.setLayoutManager(new LinearLayoutManager(this));
        CommentAdapter commentAdapter = new CommentAdapter(getApplicationContext(), recipe.comments);
        commentsList.setAdapter(commentAdapter);

        EditText commentBox = (EditText) findViewById(R.id.enterComment);
        Button sendComment = (Button) findViewById(R.id.sendCommentButton);

        sendComment.setOnClickListener(view -> {
            String input = commentBox.getText().toString();
            if(!input.isEmpty()){

                commentBox.setText("");
                recipe.comments.add(new Comment(input));
                commentAdapter.notifyItemInserted(recipe.comments.size()-1);

            }
        });


    }

    private void setServingInfo(TextView nbServings, TextView servings, RecyclerView ingredientsList){

        nbServings.setText(String.valueOf(recipe.servings));
        servings.setText(String.valueOf(recipe.servings));

        Button plusButton = (Button) findViewById(R.id.plusButton);
        Button minusButton = (Button) findViewById(R.id.minusButton);

        plusButton.setOnClickListener(v -> {

            int old = Integer.valueOf(nbServings.getText().toString());
            int newVal = old+1;

            nbServings.setText(String.valueOf(newVal));
            servings.setText(String.valueOf(newVal));

            modifyIngredientAmounts(newVal, old, ingredientsList);

        });

        minusButton.setOnClickListener(v -> {
            int old = Integer.valueOf(nbServings.getText().toString());
            if(old > 1){
                int newVal = old-1;
                nbServings.setText(String.valueOf(newVal));
                servings.setText(String.valueOf(newVal));

                modifyIngredientAmounts(newVal, old, ingredientsList);
            }
        });

    }
}


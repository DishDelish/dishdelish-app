package com.github.siela1915.bootcamp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.Group;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.transition.AutoTransition;
import androidx.transition.TransitionManager;

import com.github.siela1915.bootcamp.Recipes.Comment;
import com.github.siela1915.bootcamp.Recipes.Ingredient;
import com.github.siela1915.bootcamp.Recipes.Recipe;
import com.github.siela1915.bootcamp.Recipes.Unit;
import com.github.siela1915.bootcamp.Tools.LanguageFilter;
import com.github.siela1915.bootcamp.firebase.Database;
import com.github.siela1915.bootcamp.firebase.FirebaseInstanceManager;
import com.github.siela1915.bootcamp.firebase.UserDatabase;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class RecipeActivity extends AppCompatActivity implements CompoundButton.OnCheckedChangeListener {

    private Recipe recipe;
    private ShoppingListManager shoppingListManager;

    private Database database;

    private FirebaseAuth firebaseAuth;
    private static final DecimalFormat nutritionalValueFormat = new DecimalFormat("0.00");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe);

        firebaseAuth = FirebaseInstanceManager.getAuth();
        FirebaseDatabase firebaseDatabase = FirebaseInstanceManager.getDatabase(getApplicationContext());
        database = new Database(firebaseDatabase);

        shoppingListManager = new ShoppingListManager(this);


        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        recipe = getIntent().getParcelableExtra("Recipe");

        setPageContents();

        Button rateButton = (Button) findViewById(R.id.rateButton);
        rateButton.setOnClickListener(v -> {

            if (firebaseAuth.getCurrentUser() == null) {
                Toast.makeText(this, "Sign in to rate this recipe", Toast.LENGTH_SHORT).show();
            } else {
                Intent ratingIntent = new Intent(v.getContext(), RatingActivity.class);
                ratingIntent.putExtra("Recipe", recipe);

                v.getContext().startActivity(ratingIntent);
            }
        });

        Button cookNow = findViewById(R.id.cookNowButton);
        cookNow.setOnClickListener(v -> {
             Intent cookNowIntent = new Intent(v.getContext(), CookNowActivity.class);
             cookNowIntent.putExtra("Recipe", recipe);

             v.getContext().startActivity(cookNowIntent);
        });


        setNutritionalValueOnClickListener();
    }

    private void setNutritionalValueOnClickListener() {
        // collapsible card for nutritional values
        CardView nutritionalValuesCard = findViewById(R.id.nutritionalValueCard);
        ImageView expandNutritionalValues = findViewById(R.id.nutritionalCollapseToggle);
        Group nutritionalValuesContent = findViewById(R.id.card_group);

        expandNutritionalValues.setOnClickListener(view -> {
            TransitionManager.beginDelayedTransition(nutritionalValuesCard, new AutoTransition());

            if (nutritionalValuesContent.getVisibility() == View.VISIBLE) {
                nutritionalValuesContent.setVisibility(View.GONE);
                expandNutritionalValues.setImageResource(android.R.drawable.arrow_down_float);
            } else {
                nutritionalValuesContent.setVisibility(View.VISIBLE);
                expandNutritionalValues.setImageResource(android.R.drawable.arrow_up_float);
            }
        });

    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    private void setPageContents() {

        ImageView recipePicture = (ImageView) findViewById(R.id.recipePicture);
        TextView recipeNameText = (TextView) findViewById(R.id.recipeNameText);
        ImageView userAvatar = (ImageView) findViewById(R.id.userAvatar);
        TextView userNameText = (TextView) findViewById(R.id.userNameText);
        TextView prepTime = (TextView) findViewById(R.id.prepTimeNbMins);
        TextView kcalCount = (TextView) findViewById(R.id.kcalCount);
        TextView cookTime = (TextView) findViewById(R.id.cookTimeNbMins);
        TextView nbServings = (TextView) findViewById(R.id.nbServings);
        RecyclerView ingredientsList = (RecyclerView) findViewById(R.id.ingredientsList);
        TextView utensilsList = (TextView) findViewById(R.id.utensilsList);
        TextView stepsText = (TextView) findViewById(R.id.stepsText);
        RecyclerView commentsList = (RecyclerView) findViewById(R.id.commentsList);
        RatingBar ratingBar = (RatingBar) findViewById(R.id.ratingBar);
        TextView servings = (TextView) findViewById(R.id.servings);
        TextView calories = (TextView) findViewById(R.id.nutritionalValuesCaloriesValue);
        TextView fat = (TextView) findViewById(R.id.nutritionalValuesFatValue);
        TextView carbohydrates = (TextView) findViewById(R.id.nutritionalValuesCarbohydratesValue);
        TextView sugar = (TextView) findViewById(R.id.nutritionalValuesSugarValue);
        TextView protein = (TextView) findViewById(R.id.nutritionalValuesProteinValue);

        ToggleButton heart = (ToggleButton) findViewById(R.id.favoriteButton);
        heart.setTag("empty");

        if (firebaseAuth.getCurrentUser() != null) {
            database.getFavorites().addOnSuccessListener(favs -> {
                if (favs.contains(recipe.uniqueKey)) {
                    heart.setBackground(getDrawable(R.drawable.heart_full));

                    heart.setOnCheckedChangeListener(null);
                    heart.setChecked(true);
                    heart.setOnCheckedChangeListener(this);

                    heart.setTag("full");
                }
            });
        }

        heart.setOnCheckedChangeListener(this);

        new DownloadImageTask(recipePicture).execute(recipe.image);

        UserDatabase userDb = new UserDatabase(FirebaseInstanceManager.getDatabase(getApplicationContext()));

        userDb.getUser(recipe.getUserId()).addOnSuccessListener(user -> {
            userNameText.setText(user.getDisplayName());
            new DownloadImageTask(userAvatar).execute(user.getPhotoUrl());
        });

        recipeNameText.setText(recipe.recipeName);

        ratingBar.setRating((float) recipe.rating);
        prepTime.setText(String.valueOf(recipe.prepTime));

        cookTime.setText(String.valueOf(recipe.cookTime));
        utensilsList.setText(String.join(", ", recipe.utensils.getUtensils()));

        stepsText.setText(String.join("\n\n", recipe.steps));


        // Set Ingredient List content
        setIngredientListContents(ingredientsList);

        // Set Nutritional Values
        calories.setText(nutritionalValueFormat.format(recipe.calories));
        fat.setText(nutritionalValueFormat.format(recipe.fat));
        carbohydrates.setText(nutritionalValueFormat.format(recipe.carbohydrates));
        sugar.setText(nutritionalValueFormat.format(recipe.sugar));
        protein.setText(nutritionalValueFormat.format(recipe.protein));

        // Set servings info
        setServingInfo(nbServings, servings, ingredientsList);

        // Set Comment fields
        setCommentContents(commentsList);

    }

    private void modifyIngredientAmounts(int n, int previous, RecyclerView ingredientsList) {
        IngredientAdapter adapter = (IngredientAdapter) ingredientsList.getAdapter();
        List<Ingredient> data = adapter.getData();

        for (int i = 0; i < data.size(); i++) {
            Ingredient original = recipe.getIngredientList().get(i);
            String ingredient = original.getIngredient();
            String info = original.getUnit().getInfo();
            int oldValue = original.getUnit().getValue();
            double temp = ((double) n / recipe.servings);
            int newValue = (int) (Math.ceil(temp * oldValue));

            data.set(i, new Ingredient(ingredient, new Unit(newValue, info)));
            adapter.notifyItemChanged(i);
        }
    }

    private void setIngredientListContents(RecyclerView ingredientsList) {

        ingredientsList.setLayoutManager(new LinearLayoutManager(this));
        IngredientAdapter ingredientAdapter = new IngredientAdapter(getApplicationContext(), new ArrayList<>(recipe.getIngredientList()), shoppingListManager);
        ingredientsList.setAdapter(ingredientAdapter);

    }

    private void setCommentContents(RecyclerView commentsList) {

        commentsList.setLayoutManager(new LinearLayoutManager(this));
        for(Comment c : recipe.comments){
            c.setContent(LanguageFilter.filterLanguage(c.getContent()));
        }
        CommentAdapter commentAdapter = new CommentAdapter(getApplicationContext(),
                recipe.comments, recipe);
        commentsList.setAdapter(commentAdapter);

        EditText commentBox = (EditText) findViewById(R.id.enterComment);
        Button sendComment = (Button) findViewById(R.id.sendCommentButton);

        sendComment.setOnClickListener(view -> {
            String input = commentBox.getText().toString();
            if (!input.isEmpty()) {
                commentBox.setText("");

                if (firebaseAuth.getCurrentUser() == null) {
                    Toast.makeText(this, "Sign in to add a comment", Toast.LENGTH_SHORT).show();
                } else {
                    recipe.comments.add(new Comment(LanguageFilter.filterLanguage(input), firebaseAuth.getCurrentUser().getUid()));
                    // Update the database with the new comment
                    database.updateAsync(recipe).addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            commentAdapter.notifyItemInserted(recipe.comments.size() - 1);
                        } else {
                            Toast.makeText(this, "Error adding new comment", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });


    }

    private void setServingInfo(TextView nbServings, TextView servings, RecyclerView ingredientsList) {

        nbServings.setText(String.valueOf(recipe.servings));
        servings.setText(String.valueOf(recipe.servings));

        Button plusButton = (Button) findViewById(R.id.plusButton);
        Button minusButton = (Button) findViewById(R.id.minusButton);

        plusButton.setOnClickListener(v -> {

            int old = Integer.valueOf(nbServings.getText().toString());
            int newVal = old + 1;

            nbServings.setText(String.valueOf(newVal));
            servings.setText(String.valueOf(newVal));

            modifyIngredientAmounts(newVal, old, ingredientsList);

        });

        minusButton.setOnClickListener(v -> {
            int old = Integer.valueOf(nbServings.getText().toString());
            if (old > 1) {
                int newVal = old - 1;
                nbServings.setText(String.valueOf(newVal));
                servings.setText(String.valueOf(newVal));

                modifyIngredientAmounts(newVal, old, ingredientsList);
            }
        });

    }

    /**
     * @param buttonView The compound button view whose state has changed.
     * @param isChecked  The new checked state of buttonView.
     */
    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (isChecked) {

            // Add the recipe to favorites
            database.addFavorite(recipe.uniqueKey).addOnSuccessListener(arg -> {
                // Show a success message to the user
                Toast.makeText(this, "Recipe added to favorites", Toast.LENGTH_SHORT).show();

                //change background
                buttonView.setBackground(getDrawable(R.drawable.heart_full));
                recipe.setLikes(recipe.likes + 1);

                // for testing
                buttonView.setTag("full");

            }).addOnFailureListener(e -> {
                // Show an error message to the user
                Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();

                // remove onCheckedChangeListener before changing the state manually so that it is not triggered
                buttonView.setOnCheckedChangeListener(null);
                buttonView.setChecked(false);
                buttonView.setOnCheckedChangeListener(this);
                buttonView.setTag("add fail");

            });

        } else {

            // remove this recipe from favorites
            database.removeFavorite(recipe.uniqueKey).addOnSuccessListener(s -> {

                // display success message
                Toast.makeText(this, "Recipe removed from favorites", Toast.LENGTH_SHORT).show();

                // change background
                buttonView.setBackground(getDrawable(R.drawable.heart_empty));

                recipe.setLikes(recipe.likes - 1);

                // for testing
                buttonView.setTag("removed");

            }).addOnFailureListener(e -> {

                // display error message
                Toast.makeText(this, "Error removing recipe from favorites: " + e.getMessage(), Toast.LENGTH_SHORT).show();

                buttonView.setOnCheckedChangeListener(null);
                buttonView.setChecked(true);
                buttonView.setOnCheckedChangeListener(this);
                buttonView.setTag("remove fail");

            });
        }
    }
}


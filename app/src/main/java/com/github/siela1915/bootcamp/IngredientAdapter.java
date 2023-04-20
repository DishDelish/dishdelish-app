package com.github.siela1915.bootcamp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.github.siela1915.bootcamp.Recipes.Ingredient;

import java.util.List;

public class IngredientAdapter extends RecyclerView.Adapter<IngredientViewHolder> {

    Context context;
    List<Ingredient> ingredients;

    ShoppingListManager manager;

    public IngredientAdapter(Context context, List<Ingredient> ingredients, ShoppingListManager manager) {
        this.context = context;
        this.ingredients = ingredients;
        this.manager = manager;
    }


    @NonNull
    @Override
    public IngredientViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new IngredientViewHolder(LayoutInflater.from(context).inflate(R.layout.ingredient_item, parent, false), manager);
    }

    @Override
    public void onBindViewHolder(@NonNull IngredientViewHolder holder, int position) {
        Ingredient ingredient = ingredients.get(position);
        holder.ingredientName.setText(ingredient.getIngredient());
        holder.amountInfo.setText(String.valueOf(ingredient.getUnit().getValue()) + "  " + ingredient.getUnit().getInfo() );

    }

    @Override
    public int getItemCount() {
        return ingredients.size();
    }

    public List<Ingredient> getData(){
        return ingredients;
    }
}

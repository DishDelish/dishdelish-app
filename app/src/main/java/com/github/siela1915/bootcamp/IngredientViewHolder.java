package com.github.siela1915.bootcamp;

import static androidx.appcompat.content.res.AppCompatResources.getDrawable;

import android.view.View;
import android.widget.TextView;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

public class IngredientViewHolder extends RecyclerView.ViewHolder {

    TextView ingredientName, amountInfo;
    ShoppingListManager shoppingListManager;

    public IngredientViewHolder(@NonNull View itemView, ShoppingListManager manager) {
        super(itemView);
        ingredientName = itemView.findViewById(R.id.ingredientName);
        amountInfo = itemView.findViewById(R.id.amountInfo);
        shoppingListManager = manager;

        ToggleButton addToListButton = itemView.findViewById(R.id.AddToListButton);

        addToListButton.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                addToListButton.setBackground(ContextCompat.getDrawable(itemView.getContext(), R.drawable.ic_minus_circle_full));
                addToListButton.setTag("added");
                shoppingListManager.addIngredient(ingredientName.getText().toString());
            } else {
                addToListButton.setBackground(ContextCompat.getDrawable(itemView.getContext(), R.drawable.ic_plus_circle_full));
                addToListButton.setTag("removed");
                shoppingListManager.removeIngredient(ingredientName.getText().toString());
            }
        });
    }
}

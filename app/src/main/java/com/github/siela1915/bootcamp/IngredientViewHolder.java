package com.github.siela1915.bootcamp;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class IngredientViewHolder extends RecyclerView.ViewHolder {

    TextView ingredientName, unitInfo, ingredientValue;


    public IngredientViewHolder(@NonNull View itemView) {
        super(itemView);
        ingredientName = itemView.findViewById(R.id.ingredientName);
        unitInfo = itemView.findViewById(R.id.unitInfo);
        ingredientValue = itemView.findViewById(R.id.ingredientValue);
    }
}

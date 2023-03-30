package com.github.siela1915.bootcamp;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class IngredientViewHolder extends RecyclerView.ViewHolder {

    TextView ingredientName, amountInfo;


    public IngredientViewHolder(@NonNull View itemView) {
        super(itemView);
        ingredientName = itemView.findViewById(R.id.ingredientName);
        amountInfo = itemView.findViewById(R.id.amountInfo);
    }
}

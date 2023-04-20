package com.github.siela1915.bootcamp;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class ShoppingListViewHolder extends RecyclerView.ViewHolder{
    TextView shopping_items;
    public ShoppingListViewHolder(@NonNull View itemView) {
        super(itemView);
        shopping_items = (TextView) itemView.findViewById(R.id.itemName);
    }
}

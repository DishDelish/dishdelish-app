package com.github.siela1915.bootcamp;

import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ShoppingListViewHolder extends RecyclerView.ViewHolder implements CompoundButton.OnCheckedChangeListener {
    CheckBox checkBox;
    TextView ingredientName;
    ShoppingListAdapter adapter;

    public ShoppingListViewHolder(@NonNull View itemView, ShoppingListAdapter adapter) {
        super(itemView);
        checkBox = itemView.findViewById(R.id.check_box);
        ingredientName = itemView.findViewById(R.id.ingredientName);
        checkBox.setOnCheckedChangeListener(this);
        this.adapter = adapter;
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        int position = getAdapterPosition();
        if(checkBox.isChecked()){
            adapter.itemSelectionChanged(position, true);
        } else{
            adapter.itemSelectionChanged(position, false);
        }
    }

}

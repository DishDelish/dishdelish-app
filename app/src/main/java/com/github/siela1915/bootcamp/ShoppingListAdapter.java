package com.github.siela1915.bootcamp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ShoppingListAdapter  extends RecyclerView.Adapter<ShoppingListViewHolder>{
    List<String> shoppingList;
    Context context;

    public ShoppingListAdapter(List<String> shoppingList, Context context) {
        this.shoppingList = shoppingList;
        this.context = context;
    }

    @NonNull
    @Override
    public ShoppingListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ShoppingListViewHolder(LayoutInflater.from(context).inflate(R.layout.shopping_items, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ShoppingListViewHolder holder, int position) {
        String item = shoppingList.get(position);
        holder.shopping_items.setText(item);
    }

    @Override
    public int getItemCount() {
        return shoppingList.size();
    }
}

package com.github.siela1915.bootcamp;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class ShoppingListAdapter  extends RecyclerView.Adapter<ShoppingListViewHolder>{
    List<String> shoppingList;

    List<String> selectedItems = new ArrayList<>();
    Context context;
    ShoppingListManager manager;

    public ShoppingListAdapter(List<String> shoppingList, Context context) {
        this.shoppingList = shoppingList;
        this.context = context;
    }

    @NonNull
    @Override
    public ShoppingListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ShoppingListViewHolder(LayoutInflater.from(context).inflate(R.layout.shopping_items, parent, false), this);
    }

    @SuppressLint("ResourceAsColor")
    @Override
    public void onBindViewHolder(@NonNull ShoppingListViewHolder holder, int position) {
        manager = new ShoppingListManager(context);
        String item = shoppingList.get(position);

        // Split the item into ingredient and date
        String[] itemParts = item.split(" - date: ");
        String ingredient = itemParts[0];
        String date = itemParts[1];

        holder.ingredientName.setText(ingredient);
        holder.date.setText(date);
    }

    @Override
    public int getItemCount() {
        return shoppingList.size();
    }

    public void itemSelectionChanged(int position, boolean selected) {
        if(selectedItems.contains(shoppingList.get(position)) && !selected){
            selectedItems.remove(shoppingList.get(position));
        } else if(!selectedItems.contains(shoppingList.get(position)) && selected){
            selectedItems.add(shoppingList.get(position));
        }
    }


    public void removeSelectedItems() {
        for (String selectedItem : selectedItems) {
            manager.removeIngredient(selectedItem);
            shoppingList.remove(selectedItem);
        }
        clearSelectedItems();
        notifyDataSetChanged();
    }

    public void clearSelectedItems() {
        selectedItems.clear();
    }

    public List<String> getShoppingList() {
        return shoppingList;
    }

    public void addItem(String ingredient, String date) {
        String itemWithDate = ingredient + " - date: " + date;
        shoppingList.add(itemWithDate);
        notifyDataSetChanged();
    }
}

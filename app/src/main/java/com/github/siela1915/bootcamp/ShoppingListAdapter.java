package com.github.siela1915.bootcamp;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ShoppingListAdapter  extends RecyclerView.Adapter<ShoppingListViewHolder>{
    List<String> shoppingList;
    Context context;
    ShoppingListManager manager;

    public ShoppingListAdapter(List<String> shoppingList, Context context) {
        this.shoppingList = shoppingList;
        this.context = context;
    }

    @NonNull
    @Override
    public ShoppingListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ShoppingListViewHolder(LayoutInflater.from(context).inflate(R.layout.shopping_items, parent, false));
    }

    @SuppressLint("ResourceAsColor")
    @Override
    public void onBindViewHolder(@NonNull ShoppingListViewHolder holder, int position) {
        manager=new ShoppingListManager(context);
        String item = shoppingList.get(position);
        holder.shopping_items.setText(item);
        holder.itemView.setOnClickListener(view ->{
            AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext(),R.style.AlertDialogTheme);
            builder.setTitle("Are you sure you want to delete this item?");
            builder.setCancelable(false);
            builder.setNegativeButton("Yes", (dialog, which) -> {
                String item_to_remove= shoppingList.get(position);
                shoppingList.remove(position);
                notifyDataSetChanged();
                manager.removeIngredient(item_to_remove);
            });
            builder.setPositiveButton("No",((dialog, which) -> {
                dialog.cancel();
            }));
            AlertDialog dialog=builder.create();
            dialog.setOnShowListener(arg0 -> {
                dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(R.color.teal_700);
                dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(R.color.teal_700);
            });
            dialog.show();

        });
    }

    @Override
    public int getItemCount() {
        return shoppingList.size();
    }

}

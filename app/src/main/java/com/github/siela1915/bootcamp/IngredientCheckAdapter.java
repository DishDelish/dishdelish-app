package com.github.siela1915.bootcamp;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class IngredientCheckAdapter extends RecyclerView.Adapter<IngredientCheckAdapter.ICViewHolder> {
    private List<String> items;
    private List<String> selectedItems = new ArrayList<>();
    public IngredientCheckAdapter(List<String> items) {
        this.items = items;
    }

    @Override
    public ICViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.ingredient_item_view, parent, false);
        return new ICViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ICViewHolder holder, int position) {
        String item = items.get(position);
        holder.textView.setText(item);

        holder.checkBox.setChecked(selectedItems.contains(item));
        holder.checkBox.setOnClickListener(v -> {
            if (selectedItems.contains(item)) {
                selectedItems.remove(item);
            } else {
                selectedItems.add(item);
            }
            holder.checkBox.setChecked(selectedItems.contains(item));
        });
    }


    @Override
    public int getItemCount() {
        return items.size();
    }
    public List<String> getSelectedItems() {
        return this.selectedItems;
    }
    public static class ICViewHolder extends RecyclerView.ViewHolder{
        public TextView textView;
        public CheckBox checkBox;
        public ICViewHolder(View view) {
            super(view);
            textView = view.findViewById(R.id.ingredientName);
            checkBox = view.findViewById(R.id.check_box);
        }
    }

}


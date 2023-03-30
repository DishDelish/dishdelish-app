package com.github.siela1915.bootcamp;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.github.siela1915.bootcamp.Recipes.Recipe;
import com.github.siela1915.bootcamp.databinding.FragmentRecipeItemBinding;

import java.util.List;

/**
 * {@link RecyclerView.Adapter} that can display a {@link Recipe}.
 */
public class RecipeItemRecyclerViewAdapter extends RecyclerView.Adapter<RecipeItemRecyclerViewAdapter.ViewHolder> {

    private final List<Recipe> mValues;

    public RecipeItemRecyclerViewAdapter(List<Recipe> items) {
        mValues = items;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        return new ViewHolder(FragmentRecipeItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));

    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = mValues.get(position);

        Bitmap recipeImage = BitmapFactory.decodeResource(holder.itemView.getResources(), mValues.get(position).image);

        holder.mRecipeItemImage.setImageBitmap(recipeImage);
        holder.mRecipeItemName.setText(mValues.get(position).recipeName);
        holder.mRecipeItemAuthor.setText(mValues.get(position).userName);
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public final ImageView mRecipeItemImage;
        public final TextView mRecipeItemName;
        public final TextView mRecipeItemAuthor;
        public Recipe mItem;

        public ViewHolder(FragmentRecipeItemBinding binding) {
            super(binding.getRoot());
            mRecipeItemImage = binding.recipeItemImage;
            mRecipeItemName = binding.recipeItemName;
            mRecipeItemAuthor = binding.recipeItemAuthor;
            binding.recipeItemLayout.setOnClickListener((view) -> {
                Context context = binding.getRoot().getContext();
                context.startActivity(RecipeConverter.convertToIntent(mItem, context));
            });
        }

        @NonNull
        @Override
        public String toString() {
            return super.toString() + " '" + mRecipeItemName.getText() + "(by " + mRecipeItemAuthor.getText() + ")'";
        }
    }
}
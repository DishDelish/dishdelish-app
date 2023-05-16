package com.github.siela1915.bootcamp.Recipes.Interface;

import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;

import com.github.siela1915.bootcamp.AutocompleteApi.IngredientAutocomplete;
import com.github.siela1915.bootcamp.Recipes.Ingredient;
import com.github.siela1915.bootcamp.databinding.FragmentIngredientBinding;

import java.util.List;

/**
 * {@link RecyclerView.Adapter} that can display a {@link Ingredient}.
 * TODO: Replace the implementation with code for your data type.
 */
public class MyIngredientListRecyclerViewAdapter extends RecyclerView.Adapter<MyIngredientListRecyclerViewAdapter.ViewHolder> {

    private final List<Ingredient> mValues;
    private IngredientAutocomplete apiService = new IngredientAutocomplete();


    public MyIngredientListRecyclerViewAdapter(List<Ingredient> items) {
        mValues = items;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        return new ViewHolder(FragmentIngredientBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));

    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = mValues.get(position);
        holder.mAmountView.setText(mValues.get(position).getUnit().getValue());
        holder.mUnitView.setText(mValues.get(position).getUnit().getInfo());
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final EditText mAmountView;
        public final EditText mUnitView;
        public final AutoCompleteTextView mIngredientView;
        public Ingredient mItem;

        public ViewHolder(FragmentIngredientBinding binding) {
            super(binding.getRoot());
            mAmountView = binding.ingredientItemAmount.getEditText();
            mUnitView = binding.ingredientItemUnit.getEditText();
            mIngredientView = binding.ingredientItemAutoComplete;
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mIngredientView.getText() + "'";
        }
    }
}
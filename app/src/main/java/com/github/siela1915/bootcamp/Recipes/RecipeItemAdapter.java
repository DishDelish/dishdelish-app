package com.github.siela1915.bootcamp.Recipes;

import static androidx.test.InstrumentationRegistry.getContext;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.github.siela1915.bootcamp.R;
import com.github.siela1915.bootcamp.RecipeConverter;
import com.github.siela1915.bootcamp.databinding.FragmentRecipeItemBinding;
import com.github.siela1915.bootcamp.databinding.RecipeItemBinding;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class RecipeItemAdapter extends RecyclerView.Adapter<RecipeItemViewHolder>{
    private final List<Recipe> recipes;
    private Context context;

    public RecipeItemAdapter(List<Recipe> recipes, Context context) {
        this.recipes = recipes;
        this.context= context;
    }

    @NonNull
    @Override
    public RecipeItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new RecipeItemViewHolder(LayoutInflater.from(context).inflate(R.layout.recipe_item,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecipeItemViewHolder holder, int position) {
        holder.textView_title.setText(recipes.get(position).recipeName);
        holder.textView_time.setText(recipes.get(position).cookTime+"h");
        holder.textView_serving.setText(recipes.get(position).servings+"");
        holder.textView_likes.setText(recipes.get(position).likes+"");
        holder.recipe= recipes.get(position);
        Picasso.get().load(recipes.get(position).image).into(holder.recipe_Image);
    }
    @Override
    public int getItemCount() {
        return recipes.size();
    }
}
class RecipeItemViewHolder extends RecyclerView.ViewHolder{
    CardView recipeCardView;
    TextView textView_title,textView_time, textView_serving,  textView_likes;
    ImageView recipe_Image;
    public Recipe recipe;

    public RecipeItemViewHolder(@NonNull View itemView) {
        super(itemView);
        recipeCardView= itemView.findViewById(R.id.recipeCardView);
        textView_title= itemView.findViewById(R.id.textView_recipeName);
        textView_likes= itemView.findViewById(R.id.textView_likes);
        textView_serving= itemView.findViewById(R.id.textView_serving);
        textView_time= itemView.findViewById(R.id.textView_time);
        recipe_Image= itemView.findViewById(R.id.recipeImageView);
        itemView.findViewById(R.id.recipeCardView).setOnClickListener(v->{
            itemView.getContext().startActivity(RecipeConverter.convertToIntent(recipe, itemView.getContext()));
        });
    }
}

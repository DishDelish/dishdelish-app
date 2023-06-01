package com.github.siela1915.bootcamp.Recipes;

import static androidx.test.InstrumentationRegistry.getContext;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.github.siela1915.bootcamp.DownloadImageTask;
import com.github.siela1915.bootcamp.R;
import com.github.siela1915.bootcamp.RecipeActivity;
import com.github.siela1915.bootcamp.RecipeConverter;
import com.github.siela1915.bootcamp.databinding.RecipeItemBinding;
import com.google.android.gms.tasks.Task;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class RecipeItemAdapter extends RecyclerView.Adapter<RecipeItemViewHolder>{
    private  List<Recipe> recipes;
    private List<Bitmap> images;
    private List<CompletableFuture<Void>> imageTasks;
    private Context context;

    public RecipeItemAdapter(List<Recipe> recipes, Context context) {
        this.context= context;

        setRecipes(recipes);
    }
    public void setRecipes(List<Recipe> newList){
        recipes=newList;
        if (imageTasks != null) {
            imageTasks.forEach(task -> task.cancel(true));
        }
        images = new ArrayList<>();
        imageTasks = new ArrayList<>();
        for (int index = 0; index < newList.size(); ++index) {
            images.add(null);
            int finalIndex = index;
            CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
                InputStream in = null;
                try {
                    in = new URL(newList.get(finalIndex).image).openStream();
                } catch (IOException ignored) {}
                images.set(finalIndex, BitmapFactory.decodeStream(in));
                notifyItemChanged(finalIndex);
            });
            imageTasks.add(future);
        }
        notifyDataSetChanged();
    }
    @NonNull
    @Override
    public RecipeItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new RecipeItemViewHolder(LayoutInflater.from(context).inflate(R.layout.recipe_item,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecipeItemViewHolder holder, int position) {
        holder.textView_title.setText(recipes.get(position).recipeName);
        holder.textView_time.setText(recipes.get(position).cookTime+"min");
        holder.textView_serving.setText(recipes.get(position).servings+"");

        holder.textView_likes.setText((int)(recipes.get(position).getRating())+"/5");
        holder.recipe= recipes.get(position);

        holder.recipe_Image.setImageBitmap(images.get(position));

        //Bitmap avatar = BitmapFactory.decodeResource(this.getResources(), recipe.profilePicture);
        //userAvatar.setImageBitmap(avatar);
    }
    @Override
    public int getItemCount() {
        return recipes.size();
    }

    public List<Recipe> getRecipes() {
        return recipes;
    }

    public Context getContext() {
        return context;
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

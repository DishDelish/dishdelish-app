package com.github.siela1915.bootcamp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.github.siela1915.bootcamp.Recipes.Comment;
import com.github.siela1915.bootcamp.Recipes.Recipe;

import java.util.List;

public class CommentAdapter extends RecyclerView.Adapter<CommentViewHolder> {

    Context context;
    List<Comment> comments;

    Recipe recipe;

    public CommentAdapter(Context context, List<Comment> comments, Recipe recipe) {
        this.context = context;
        this.comments = comments;
        this.recipe = recipe;
    }

    @NonNull
    @Override
    public CommentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new CommentViewHolder(LayoutInflater.from(context).inflate(R.layout.comment_item, parent, false), recipe, this);
    }

    @Override
    public void onBindViewHolder(@NonNull CommentViewHolder holder, int position) {

        holder.comment.setText(comments.get(position).getContent());
        holder.likes.setText(Integer.toString(comments.get(position).getLikes()));

        holder.replies.setLayoutManager(new LinearLayoutManager(context));
        ReplyAdapter replyAdapter = new ReplyAdapter(context.getApplicationContext(), comments.get(position).getReplies(), comments.get(position));
        holder.replies.setAdapter(replyAdapter);

    }

    @Override
    public int getItemCount() {
            return comments.size();
    }

    public List<Comment> getData(){
        return comments;
    }

}

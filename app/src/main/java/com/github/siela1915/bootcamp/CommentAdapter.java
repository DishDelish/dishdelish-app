package com.github.siela1915.bootcamp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.github.siela1915.bootcamp.Recipes.Comment;

import java.util.List;

public class CommentAdapter extends RecyclerView.Adapter<CommentViewHolder> {

    Context context;
    List<Comment> comments;

    public CommentAdapter(Context context, List<Comment> comments) {
        this.context = context;
        this.comments = comments;
    }

    @NonNull
    @Override
    public CommentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new CommentViewHolder(LayoutInflater.from(context).inflate(R.layout.comment_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull CommentViewHolder holder, int position) {

        holder.comment.setText(comments.get(position).getContent());

    }

    @Override
    public int getItemCount() {
            return comments.size();
    }

    public List<Comment> getData(){
        return comments;
    }
}

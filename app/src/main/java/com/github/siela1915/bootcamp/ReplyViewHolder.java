package com.github.siela1915.bootcamp;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.github.siela1915.bootcamp.Recipes.Comment;

public class ReplyViewHolder extends RecyclerView.ViewHolder {

    TextView reply, likes;
    //Comment parent;

    public ReplyViewHolder(@NonNull View itemView) {
        super(itemView);

        reply = itemView.findViewById(R.id.replyText);
        likes = itemView.findViewById(R.id.likeCount);
        //parent = comment;
    }
}

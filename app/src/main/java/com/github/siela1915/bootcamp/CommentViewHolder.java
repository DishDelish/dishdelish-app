package com.github.siela1915.bootcamp;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class CommentViewHolder extends RecyclerView.ViewHolder {
    TextView comment;

    public CommentViewHolder(@NonNull View itemView) {
        super(itemView);
        comment = itemView.findViewById(R.id.commentText);
    }
}

package com.github.siela1915.bootcamp;

import android.view.View;
import android.widget.TextView;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

public class CommentViewHolder extends RecyclerView.ViewHolder {
    TextView comment;
    TextView likes;

    public CommentViewHolder(@NonNull View itemView) {
        super(itemView);
        comment = itemView.findViewById(R.id.commentText);
        likes = itemView.findViewById(R.id.likeCount);

        ToggleButton thumb = itemView.findViewById(R.id.thumbButton);
        thumb.setOnCheckedChangeListener(((buttonView, isChecked) -> {
            if(isChecked){
                thumb.setBackground(ContextCompat.getDrawable(itemView.getContext(), R.drawable.ic_thumb_full));
                likes.setText(Integer.toString(Integer.valueOf(likes.getText().toString())+1));
                thumb.setTag("liked");
                // update db
            } else{
                thumb.setBackground(ContextCompat.getDrawable(itemView.getContext(), R.drawable.ic_thumb_empty));
                likes.setText(Integer.toString(Integer.valueOf(likes.getText().toString())-1));
                thumb.setTag("unliked");
                // update db
            }
        }));
    }
}

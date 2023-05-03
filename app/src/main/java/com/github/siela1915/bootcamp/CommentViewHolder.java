package com.github.siela1915.bootcamp;

import static androidx.appcompat.content.res.AppCompatResources.getDrawable;

import androidx.appcompat.app.AppCompatActivity;

import android.view.View;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.github.siela1915.bootcamp.Recipes.Comment;
import com.github.siela1915.bootcamp.Recipes.Recipe;
import com.github.siela1915.bootcamp.firebase.Database;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

public class CommentViewHolder extends RecyclerView.ViewHolder implements CompoundButton.OnCheckedChangeListener {
    TextView comment;
    TextView likes;

    Recipe recipe;

    CommentAdapter adapter;

    private final FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();

    private final Database database = new Database(firebaseDatabase);

    public CommentViewHolder(@NonNull View itemView, Recipe recipe, CommentAdapter adapter) {
        super(itemView);
        comment = itemView.findViewById(R.id.commentText);
        likes = itemView.findViewById(R.id.likeCount);
        this.recipe = recipe;
        this.adapter = adapter;

        ToggleButton thumb = itemView.findViewById(R.id.thumbButton);
        thumb.setTag("empty");
        thumb.setOnCheckedChangeListener(this);

    }

    /**
     * @param buttonView The compound button view whose state has changed.
     * @param isChecked  The new checked state of buttonView.
     */
    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        int position = getAdapterPosition();
        if (position != RecyclerView.NO_POSITION) {
            Comment currentComment = adapter.getData().get(position);
            if (isChecked) {
                if(FirebaseAuth.getInstance().getCurrentUser()==null){
                    Toast.makeText(buttonView.getContext(), "Sign in to like this comment", Toast.LENGTH_SHORT).show();
                } else {
                    currentComment.increaseLikes();
                    database.updateAsync(recipe)
                            .addOnSuccessListener(arg -> {
                                buttonView.setBackground(getDrawable(itemView.getContext(), R.drawable.ic_thumb_full));
                                adapter.notifyItemChanged(position, "likes");
                                buttonView.setTag("liked");
                            })
                            .addOnFailureListener(e -> {

                                // undo
                                currentComment.decreaseLikes();

                                Toast.makeText(buttonView.getContext(), "Try again ", Toast.LENGTH_SHORT).show();

                                buttonView.setOnCheckedChangeListener(null);
                                buttonView.setChecked(false);
                                buttonView.setOnCheckedChangeListener(this);

                            });
                }
            } else {
                currentComment.decreaseLikes();
                database.updateAsync(recipe)
                        .addOnSuccessListener(s -> {
                            // change background
                            buttonView.setBackground(getDrawable(itemView.getContext(), R.drawable.ic_thumb_empty));

                            adapter.notifyItemChanged(position, "likes");

                            // for testing
                            buttonView.setTag("empty");
                        })
                        .addOnFailureListener(e -> {

                            // undo
                            currentComment.increaseLikes();

                            Toast.makeText(buttonView.getContext(), "Try again ", Toast.LENGTH_SHORT).show();

                            buttonView.setOnCheckedChangeListener(null);
                            buttonView.setChecked(true);
                            buttonView.setOnCheckedChangeListener(this);
                        });
            }
        }
    }
}

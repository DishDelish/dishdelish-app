package com.github.siela1915.bootcamp;

import static androidx.appcompat.content.res.AppCompatResources.getDrawable;

import androidx.appcompat.app.AppCompatActivity;

import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.github.siela1915.bootcamp.Recipes.Comment;
import com.github.siela1915.bootcamp.Recipes.Recipe;
import com.github.siela1915.bootcamp.firebase.Database;
import com.github.siela1915.bootcamp.firebase.FirebaseInstanceManager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

public class CommentViewHolder extends RecyclerView.ViewHolder implements CompoundButton.OnCheckedChangeListener {
    TextView comment;
    TextView likes;
    TextView userName;
    ImageView photo;

    RecyclerView replies;

    Recipe recipe;

    CommentAdapter adapter;

    private final Database database;
    private final FirebaseAuth firebaseAuth = FirebaseInstanceManager.getAuth();


    public CommentViewHolder(@NonNull View itemView, Recipe recipe, CommentAdapter adapter) {
        super(itemView);
        comment = itemView.findViewById(R.id.commentText);
        likes = itemView.findViewById(R.id.likeCount);
        userName = itemView.findViewById(R.id.userName);
        photo = itemView.findViewById(R.id.profilePhoto);
        this.recipe = recipe;
        this.adapter = adapter;

        FirebaseDatabase firebaseDatabase = FirebaseInstanceManager.getDatabase(adapter.context);
        database = new Database(firebaseDatabase);

        replies = itemView.findViewById(R.id.repliesRecyclerView);

        ToggleButton thumb = itemView.findViewById(R.id.thumbButton);
        thumb.setTag("unliked");
        thumb.setOnCheckedChangeListener(this);

        Button replyButton = itemView.findViewById(R.id.replyButton);
        Button sendReply = itemView.findViewById(R.id.sendReplyButton);
        EditText enterReply = itemView.findViewById(R.id.enterReply);

        replyButton.setOnClickListener(v -> {
            if (enterReply.getVisibility() == View.VISIBLE) {
                enterReply.setVisibility(View.GONE);
                sendReply.setVisibility(View.GONE);
            } else {
                enterReply.setVisibility(View.VISIBLE);
                sendReply.setVisibility(View.VISIBLE);
            }
        });

        sendReply.setOnClickListener(view -> {
            String input = enterReply.getText().toString();
            if (!input.isEmpty()) {
                enterReply.setText("");
                // authentication check
                if (firebaseAuth.getCurrentUser() == null) {
                    Toast.makeText(view.getContext(), "Sign in to reply", Toast.LENGTH_SHORT).show();
                } else {
                    int position = getAdapterPosition();
                    Comment currentComment = adapter.getData().get(position);

                    currentComment.addReply(input, firebaseAuth.getCurrentUser().getUid());
                    database.updateAsync(recipe).addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            adapter.notifyItemChanged(position);
                        } else {
                            Toast.makeText(view.getContext(), "Error adding new reply", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
                }
            });
    }

    /**
     * @param buttonView The compound button view whose state has changed.
     * @param isChecked  The new checked state of buttonView.
     */
    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        int position = getAdapterPosition();
            Comment currentComment = adapter.getData().get(position);
            if (isChecked) {
                if(FirebaseInstanceManager.getAuth().getCurrentUser()==null){
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

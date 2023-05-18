package com.github.siela1915.bootcamp;

import static androidx.appcompat.content.res.AppCompatResources.getDrawable;

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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

public class ReplyViewHolder extends RecyclerView.ViewHolder implements CompoundButton.OnCheckedChangeListener{

    TextView reply, likes, userName;
    ImageView photo;
    Comment parent;
    ReplyAdapter adapter;
    Recipe recipe;

    private final FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();

    private final Database database = new Database(firebaseDatabase);

    public ReplyViewHolder(@NonNull View itemView, Recipe recipe, Comment comment, ReplyAdapter adapter) {
        super(itemView);

        reply = itemView.findViewById(R.id.replyText);
        likes = itemView.findViewById(R.id.likeCount);
        userName = itemView.findViewById(R.id.userName);
        photo = itemView.findViewById(R.id.replyProfilePhoto);
        parent = comment;
        this.adapter = adapter;
        this.recipe = recipe;

        ToggleButton thumb = itemView.findViewById(R.id.thumbButton);
        thumb.setTag("unliked");
        thumb.setOnCheckedChangeListener(this);
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        int position = getAdapterPosition();
        Comment currentReply = adapter.getData().get(position);
        if (isChecked) {
            if(FirebaseAuth.getInstance().getCurrentUser()==null){
                Toast.makeText(buttonView.getContext(), "Sign in to like this reply", Toast.LENGTH_SHORT).show();
            } else {
                currentReply.increaseLikes();
                database.updateAsync(recipe)
                        .addOnSuccessListener(arg -> {
                            buttonView.setBackground(getDrawable(itemView.getContext(), R.drawable.ic_thumb_full));
                            adapter.notifyItemChanged(position, "likes");
                            buttonView.setTag("liked");
                        })
                        .addOnFailureListener(e -> {
                            currentReply.decreaseLikes();

                            Toast.makeText(buttonView.getContext(), "Try again ", Toast.LENGTH_SHORT).show();

                            buttonView.setOnCheckedChangeListener(null);
                            buttonView.setChecked(false);
                            buttonView.setOnCheckedChangeListener(this);

                        });
            }
        } else {
            currentReply.decreaseLikes();
            database.updateAsync(recipe)
                    .addOnSuccessListener(s -> {
                        // change background
                        buttonView.setBackground(getDrawable(itemView.getContext(), R.drawable.ic_thumb_empty));
                        adapter.notifyItemChanged(position, "likes");
                        buttonView.setTag("empty");
                    })
                    .addOnFailureListener(e -> {
                        currentReply.increaseLikes();

                        Toast.makeText(buttonView.getContext(), "Try again ", Toast.LENGTH_SHORT).show();

                        buttonView.setOnCheckedChangeListener(null);
                        buttonView.setChecked(true);
                        buttonView.setOnCheckedChangeListener(this);
                    });
        }

    }
}

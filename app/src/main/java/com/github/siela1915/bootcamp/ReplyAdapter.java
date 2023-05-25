package com.github.siela1915.bootcamp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.github.siela1915.bootcamp.Recipes.Comment;
import com.github.siela1915.bootcamp.firebase.FirebaseInstanceManager;
import com.github.siela1915.bootcamp.firebase.UserDatabase;

import java.util.List;

public class ReplyAdapter extends RecyclerView.Adapter<ReplyViewHolder> {

    Context context;
    List<Comment> replies;

    Comment parent;

    UserDatabase userDb;

    public ReplyAdapter(Context context, List<Comment> replies, Comment parent){
        this.context = context;
        this.replies = replies;
        this.parent = parent;

        userDb = new UserDatabase(FirebaseInstanceManager.getDatabase(context.getApplicationContext()));
    }
    /**
     * @param parent   The ViewGroup into which the new View will be added after it is bound to
     *                 an adapter position.
     * @param viewType The view type of the new View.
     * @return
     */
    @NonNull
    @Override
    public ReplyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ReplyViewHolder(LayoutInflater.from(context).inflate(R.layout.reply_item, parent, false));
    }

    /**
     * @param holder   The ViewHolder which should be updated to represent the contents of the
     *                 item at the given position in the data set.
     * @param position The position of the item within the adapter's data set.
     */
    @Override
    public void onBindViewHolder(@NonNull ReplyViewHolder holder, int position) {
        holder.reply.setText(replies.get(position).getContent());
        holder.likes.setText(Integer.toString(replies.get(position).getLikes()));
        userDb.getUser(replies.get(position).getUserId()).addOnSuccessListener(user -> {
            holder.userName.setText(user.getDisplayName());
            new DownloadImageTask(holder.photo).execute(user.getPhotoUrl());
        });

    }

    /**
     * @return
     */
    @Override
    public int getItemCount() {
        return replies.size();
    }

    public List<Comment> getData(){
        return replies;
    }

}

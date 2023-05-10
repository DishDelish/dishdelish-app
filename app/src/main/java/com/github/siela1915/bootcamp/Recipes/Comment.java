package com.github.siela1915.bootcamp.Recipes;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import java.util.LinkedList;
import java.util.List;

public class Comment implements Parcelable {
    private int likes;
    private String content;

    private String userId = "";

    private LinkedList<Comment> replies;

    /**
     * Creates a new Comment. Replies are implemented as a doubly linked list as a notion of
     * temporal order of the replies is required. Replies must therefore be kept ordered.
     */
    public Comment(int likes, String content, LinkedList<Comment> replies) {
        this.likes = likes;
        this.content = content;
        this.replies = replies;
    }

    public Comment(int likes, String content) {
        this.likes = likes;
        this.content = content;
        this.replies = new LinkedList<>();
    }

    public Comment(String content) {
        this.likes = 0;
        this.content = content;
        this.replies = new LinkedList<>();
    }

    public Comment() {
        this.likes = 0;
        this.content = "";
        this.replies = new LinkedList<>();
    }


    public int getLikes() {
        return likes;
    }

    public void setLikes(int likes) {
        this.likes = likes;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public List<Comment> getReplies() {
        return replies;
    }

    public void setReplies(LinkedList<Comment> replies) {
        this.replies = replies;
    }

    public void increaseLikes() {
        ++likes;
    }

    public void decreaseLikes() {
        if (likes > 0) {
            --likes;
        }
    }

    /**
     * Add a reply to the comment. Note this method initializes the reply to a comment with
     * 0 likes and no replies.
     */
    public void addReply(String reply) {
        replies.add(new Comment(reply));
    }

    public void removeReply(Comment comment) {
        replies.remove(comment);
    }

    @NonNull
    @Override
    public String toString() {
        return content;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Comment) {
            if (likes != ((Comment)obj).likes
                    || !content.equals(((Comment)obj).content)) {
                return false;
            }
            if (replies.size() != ((Comment)obj).replies.size()) {
                return false;
            }
            for (int i = 0; i < replies.size(); ++i) {
                if (!replies.get(i).equals(((Comment)obj).replies.get(i))) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }

    protected Comment(Parcel in) {
        likes = in.readInt();
        content = in.readString();
    }

    public static final Parcelable.Creator<Comment> CREATOR = new Parcelable.Creator<Comment>() {
        @Override
        public Comment createFromParcel(Parcel in) {
            return new Comment(in);
        }

        @Override
        public Comment[] newArray(int size) {
            return new Comment[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeInt(likes);
        dest.writeString(content);
    }

}

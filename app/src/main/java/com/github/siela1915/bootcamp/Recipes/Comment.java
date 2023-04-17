package com.github.siela1915.bootcamp.Recipes;

public class Comment {
    private int likes;
    private String content;

    public Comment(int likes, String content) {
        this.likes = likes;
        this.content = content;
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

    public void increaseLikes() {
        ++likes;
    }

    public void decreaseLikes() {
        if (likes > 0) {
            --likes;
        }
    }

    @Override
    public String toString() {
        return content;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Comment) {
            return likes == ((Comment)obj).likes
                    && content.equals(((Comment)obj).content);
        }
        return false;
    }

}

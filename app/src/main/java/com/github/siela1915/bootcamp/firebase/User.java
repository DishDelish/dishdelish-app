package com.github.siela1915.bootcamp.firebase;

import java.util.HashMap;
import java.util.Map;

public class User {
    private String id;
    private String displayName;
    private String email;
    private String photoUrl;
    private Map<String, Long> tokens;

    public Map<String, Long> getTokens() {
        return tokens;
    }

    public void setTokens(Map<String, Long> tokens) {
        this.tokens = tokens;
    }

    public void addToken(String token) {
        this.tokens.put(token, System.currentTimeMillis());
    }
    public boolean removeToken(String token) {
        if (tokens.containsKey(token)) {
            this.tokens.remove(token);
            return true;
        }
        return false;
    }

    public User() {
        tokens = new HashMap<>();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }
}

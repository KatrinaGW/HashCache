package com.example.hashcache.models.database_connections.callbacks;

import com.example.hashcache.models.Comment;

import java.util.ArrayList;

public interface GetCommentsCallback {
    void onCallback(ArrayList<Comment> comments);
}
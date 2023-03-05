package com.example.hashcache.database_connections;

import com.example.hashcache.models.Comment;

import java.util.ArrayList;

public interface GetCommentsCallback {
    void onCallback(ArrayList<Comment> comments);
}
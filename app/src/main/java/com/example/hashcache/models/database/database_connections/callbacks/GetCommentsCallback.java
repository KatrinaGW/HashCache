package com.example.hashcache.models.database.database_connections.callbacks;

import com.example.hashcache.models.Comment;

import java.util.ArrayList;

/**
 * Used to get a callback response with a Comments ArrayList after an asynchronous operation has finished
 */
public interface GetCommentsCallback {
    /**
     * Called once an asynchronous operation is finished
     * @param comments an ArrayList of comments from the asynchronous function
     */
    void onCallback(ArrayList<Comment> comments);
}
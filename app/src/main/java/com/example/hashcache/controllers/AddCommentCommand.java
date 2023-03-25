package com.example.hashcache.controllers;

import com.example.hashcache.models.Comment;
import com.example.hashcache.models.database.Database;

import java.util.concurrent.CompletableFuture;

/**
 * Tells the database to add a comment to a given ScannableCode
 */
public class AddCommentCommand {

    /**
     * Adds a comment to a scanableCode
     * @param comment the comment to add to the scannable code
     * @param scannableCodeId the id of the scannable code to add the comment to
     * @return the CompletableFuture that throws an error if there was an error during the operation
     */
    public static CompletableFuture<Void> AddCommentCommand(Comment comment, String scannableCodeId){
        return Database.getInstance().addComment(scannableCodeId, comment);
    }
}

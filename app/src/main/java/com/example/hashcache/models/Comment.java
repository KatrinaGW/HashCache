package com.example.hashcache.models;

import java.util.UUID;

/**
 * Represents a comment left by a player
 */
public class Comment {
    private String body;
    private String commentatorId;
    public Comment(String body, String commentatorId){
        this.body = body;
        this.commentatorId = commentatorId;
    }

    /**
     * Gets the textual body of the comment
     * @return body The textual body of this comment
     */
    public String getBody(){
        return this.body;
    }

    /**
     * Sets the textual body of this comment
     *
     * @param newBody The next textual body for the comment
     */
    public void setBody(String newBody){
        this.body = newBody;
    }

    /**
     * Gets the id for the player who made the comment
     * @return commentatorId The id of the player who made the comment
     */
    public String getCommentatorId(){
        return this.commentatorId;
    }
}

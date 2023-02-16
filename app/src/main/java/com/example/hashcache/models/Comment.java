package com.example.hashcache.models;

import java.util.UUID;

public class Comment {
    private String body;
    private UUID commentatorId;

    public Comment(String body, UUID commentatorId){
        this.body = body;
        this.commentatorId = commentatorId;
    }

    public String getBody(){
        return this.body;
    }

    public void setBody(String newBody){
        this.body = newBody;
    }
}

package com.example.hashcache.models;

import java.util.ArrayList;
import java.util.UUID;

/**
 * Represents a QR or Hashcode that was scanned
 */
public class ScannableCode{
    private UUID scannableCodeId;
    private CodeLocation codeLocation;
    private HashInfo hashInfo;
    private ArrayList<Comment> comments;

    public ScannableCode(CodeLocation codeLocation, HashInfo hashInfo){
        this.codeLocation = codeLocation;
        this.hashInfo = hashInfo;
        this.scannableCodeId = UUID.randomUUID();

        this.comments = new ArrayList<>();
    }

    /**
     * Adds a comment from a user on this code
     * @param newComment Comment from a user to add on the code
     */
    public void addComment(Comment newComment){
        this.comments.add(newComment);
    }

    /**
     * Gets the location information from where the code was scanned
     * @return codeLocation The object containing the information on where the code was scanned
     */
    public CodeLocation getCodeLocation(){
        return this.codeLocation;
    }

    /**
     * Gets the information generated from this code
     * @return hashInfo The information generated from this code
     */
    public HashInfo getHashInfo(){
        return this.hashInfo;
    }

}
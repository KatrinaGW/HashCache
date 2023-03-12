package com.example.hashcache.models;

import java.util.ArrayList;
import java.util.UUID;

/**
 * Represents a QR or Hashcode that was scanned
 */
public class ScannableCode{
    private String scannableCodeId;
    private String codeLocationId;
    private HashInfo hashInfo;
    private ArrayList<Comment> comments;

    public ScannableCode(String codeLocationId, HashInfo hashInfo, long hash){
        this.codeLocationId = codeLocationId;
        this.hashInfo = hashInfo;
        this.scannableCodeId = codeLocationId + Long.toString(hash);

        this.comments = new ArrayList<>();
    }

    public ScannableCode(String id, String codeLocationId, HashInfo hashInfo, ArrayList<Comment> comments){
        this.scannableCodeId = id;
        this.codeLocationId = codeLocationId;
        this.hashInfo = hashInfo;
        this.comments = comments;
    }

    public ArrayList<Comment> getComments(){
        return this.comments;
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
     * @return codeLocationId The id of the codelocation where this code was scanned
     */
    public String getCodeLocationId(){
        return this.codeLocationId;
    }

    /**
     * Gets the information generated from this code
     * @return hashInfo The information generated from this code
     */
    public HashInfo getHashInfo(){
        return this.hashInfo;
    }

    public String getScannableCodeId() {
        return scannableCodeId;
    }
}
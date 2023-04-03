package com.example.hashcache.models;

import android.graphics.Bitmap;

import java.util.ArrayList;
import java.util.UUID;

/**
 * 
 * Represents a scannable code that can be scanned and contains information
 * 
 * about the code and comments by users.
 */
public class ScannableCode {
    private String scannableCodeId;
    private String codeLocationId;
    private HashInfo hashInfo;
    private ArrayList<Comment> comments;
    private Bitmap image;

    /**
     * 
     * Creates a ScannableCode object with a code location id, hash info and SHA256
     * hash.
     * 
     * @param codeLocationId The id of the location where the code was scanned
     * @param hashInfo       The information generated from the code
     * @param sha256Hash     The SHA256 hash of the code
     */
    public ScannableCode(String codeLocationId, HashInfo hashInfo, String sha256Hash) {
        this.codeLocationId = codeLocationId;
        this.hashInfo = hashInfo;
        this.scannableCodeId = sha256Hash;
        this.comments = new ArrayList<>();
    }

    public ScannableCode() {
        this.codeLocationId = "";
        this.hashInfo = new HashInfo(null, "", 0);
        this.scannableCodeId = "";
        this.comments = new ArrayList<>();
    }

    /**
     * 
     * Creates a ScannableCode object with a SHA256 hash and hash info.
     * 
     * @param sha256Hash The SHA256 hash of the code
     * @param hashInfo   The information generated from the code
     */
    public ScannableCode(String sha256Hash, HashInfo hashInfo) {
        this.codeLocationId = "";
        this.hashInfo = hashInfo;
        this.scannableCodeId = sha256Hash;
        this.comments = new ArrayList<>();
    }

    /**
     * 
     * Creates a ScannableCode object with an id, code location id, hash info, and
     * comments.
     * 
     * @param id             The unique id of the code
     * @param codeLocationId The id of the location where the code was scanned
     * @param hashInfo       The information generated from the code
     * @param comments       The list of comments made by users on the code
     */
    public ScannableCode(String id, String codeLocationId, HashInfo hashInfo, ArrayList<Comment> comments) {
        this.scannableCodeId = id;
        this.codeLocationId = codeLocationId;
        this.hashInfo = hashInfo;
        this.comments = comments;
    }

    /**
     * 
     * Creates a ScannableCode object with an id, hash info, and comments.
     * 
     * @param id       The unique id of the code
     * @param hashInfo The information generated from the code
     * @param comments The list of comments made by users on the code
     */
    public ScannableCode(String id, HashInfo hashInfo, ArrayList<Comment> comments) {
        this.scannableCodeId = id;
        this.codeLocationId = "";
        this.hashInfo = hashInfo;
        this.comments = comments;
    }

    /**
     * 
     * Returns the list of comments made by users on the code.
     * 
     * @return The list of comments made by users on the code
     */
    public ArrayList<Comment> getComments() {
        return this.comments;
    }

    /**
     * 
     * Adds a comment to the list of comments made by users on the code.
     * 
     * @param newComment The comment made by a user to add to the list
     */
    public void addComment(Comment newComment) {
        this.comments.add(newComment);
    }

    /**
     * 
     * Gets the id of the location where the code was scanned.
     * 
     * @return The id of the location where the code was scanned
     */
    public String getCodeLocationId() {
        return this.codeLocationId;
    }

    /**
     * Gets the information generated from this code
     * 
     * @return hashInfo The information generated from this code
     */
    public HashInfo getHashInfo() {
        return this.hashInfo;
    }

    /**
     * Gets the id of the scannable code
     * @return scannableCodeId the id of the scannable code
     */
    public String getScannableCodeId() {
        return scannableCodeId;
    }

    /**
     * Gets the image of the scannable code
     * @return image the image of the scannable code
     */
    public Bitmap getImage() {
        return image;
    }

    /**
     * Sets the image of the scannable code
     * @param image the image of the scannable code
     */
    public void setImage(Bitmap image) {
        this.image = image;
    }
}
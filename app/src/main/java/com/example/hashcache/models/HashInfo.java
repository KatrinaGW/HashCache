package com.example.hashcache.models;

import android.media.Image;

/**
 * Contains the generated information for a given hash
 */
public class HashInfo {
    private Image generatedImage;
    private String generatedName;
    private int generatedScore;

    /**
     * Creates a container for the generated elements of the hash
     * @param generatedImage The image generated from the hash
     * @param generatedName The name generated from the Hash
     * @param generatedScore The score generated from the Hash
     */
    public HashInfo(Image generatedImage, String generatedName, int generatedScore){
        this.generatedImage = generatedImage;
        this.generatedName = generatedName;
        this.generatedScore = generatedScore;
    }

    /**
     * Gets the image generated from this hash
     * @return generatedImage The image generated from a hash
     */
    public Image getGeneratedImage() {
        return generatedImage;
    }

    /**
     * Gets the score generated from this hash
     * @return generatedScore The score generated from a hash
     */
    public int getGeneratedScore() {
        return generatedScore;
    }

    /**
     * Gets the name generated from this hash
     * @return generatedName The name generated from a hash
     */
    public String getGeneratedName() {
        return generatedName;
    }
}

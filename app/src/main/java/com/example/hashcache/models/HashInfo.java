package com.example.hashcache.models;

import android.media.Image;

public class HashInfo {
    private Image generatedImage;
    private String generatedString;
    private int scoreInteger;

    public HashInfo(Image generatedImage, String generatedString, int scoreInteger){
        this.generatedImage = generatedImage;
        this.generatedString = generatedString;
        this.scoreInteger = scoreInteger;
    }
}

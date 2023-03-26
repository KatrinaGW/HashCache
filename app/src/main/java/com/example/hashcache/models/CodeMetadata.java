package com.example.hashcache.models;

import android.location.Location;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class CodeMetadata {

    private String documentId;
    private String scannableCodeId;
    private String image;
    private Location location;

    public CodeMetadata(String scannableCodeId, Location location) throws NoSuchAlgorithmException {

        MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
        String idString = scannableCodeId + Double.toString(location.getLatitude()) + Double.toString(location.getLongitude());
        messageDigest.update(idString.getBytes());
        byte[] byteArray = messageDigest.digest();
        this.documentId = new BigInteger(1, byteArray).toString(16);
        this.image = null;
        this.location = location;
        this.scannableCodeId = scannableCodeId;
    }

}
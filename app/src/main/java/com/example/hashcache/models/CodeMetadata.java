package com.example.hashcache.models;

import android.location.Location;
import android.media.Image;

import com.firebase.geofire.GeoFireUtils;
import com.firebase.geofire.GeoLocation;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class CodeMetadata {

    final private String documentId;
    final String scannableCodeId;
    final private GeoLocation location;
    final private String geohash;
    private String image;

    public CodeMetadata(String scannableCodeId, GeoLocation location, String base64Image) throws NoSuchAlgorithmException {
        this.image = base64Image;
        this.location = location;
        this.geohash = GeoFireUtils.getGeoHashForLocation(location);
        this.scannableCodeId = scannableCodeId;
        MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
        String idString = this.scannableCodeId + this.geohash;
        messageDigest.update(idString.getBytes());
        byte[] byteArray = messageDigest.digest();
        this.documentId = new BigInteger(1, byteArray).toString(16);
    }

    public CodeMetadata(String scannableCodeId, GeoLocation location) throws NoSuchAlgorithmException {
        this(scannableCodeId, location, null);
    }


    public String getDocumentId() {
        return documentId;
    }

    public String getScannableCodeId() {
        return scannableCodeId;
    }

    public String getGeohash() {
        return geohash;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public GeoLocation getLocation() {
        return location;
    }
}
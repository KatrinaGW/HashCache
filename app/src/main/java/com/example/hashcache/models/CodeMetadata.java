package com.example.hashcache.models;

import android.location.Location;
import android.media.Image;

import com.firebase.geofire.GeoFireUtils;
import com.firebase.geofire.GeoLocation;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.UUID;

public class CodeMetadata {

    final private String documentId;
    final String scannableCodeId;
    final private GeoLocation location;
    final private String geohash;
    private String image;
    private String userId;

    public CodeMetadata(String scannableCodeId, GeoLocation location, String base64Image, String userId) {
        this.image = base64Image;
        this.location = location;
        this.geohash = GeoFireUtils.getGeoHashForLocation(location);
        this.scannableCodeId = scannableCodeId;
        this.documentId = UUID.randomUUID().toString();
        this.userId = userId;
    }

    public CodeMetadata(String scannableCodeId, GeoLocation location, String base64Image) {
        this(scannableCodeId, location, base64Image, null);
    }

    public CodeMetadata(String scannableCodeId, String base64Image) {
        this(scannableCodeId, null, null, base64Image);
    }

    public CodeMetadata(String scannableCodeId, GeoLocation location){
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

    public String getUserId() {
        return userId;
    }

    public String getImage() {
        return image;
    }

    public GeoLocation getLocation() {
        return location;
    }
}
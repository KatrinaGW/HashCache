package com.example.hashcache.models;

import android.location.Location;
import android.media.Image;

import com.firebase.geofire.GeoFireUtils;
import com.firebase.geofire.GeoLocation;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.UUID;

/**
 * 
 * A class representing metadata for a scannable code.
 * This metadata includes the code's document ID, scannable code ID,
 * location (represented as a GeoLocation and geohash), and image (if
 * available).
 * This class uses the Firebase GeoFireUtils library to obtain a geohash for a
 * given location.
 * It also uses the SHA-256 algorithm to generate a unique document ID based on
 * the scannable code ID and geohash.
 */
public class CodeMetadata {
    // The unique document ID for this code metadata
    final private String documentId;
    // The ID of the scannable code
    final String scannableCodeId;
    // The location of the scannable code
    final private GeoLocation location;
    // The geohash of the scannable code's location
    final private String geohash;
    // The base64-encoded image of the scannable code (if available)
    private String image;
    // The userId of the player this scannable code belongs to
    private String userId;

    /**
     * Constructs a new CodeMetadata object.
     *
     * @param scannableCodeId The ID of the scannable code.
     * @param location        The location of the scannable code.
     * @param base64Image     The base64-encoded image of the scannable code (if
     *                        available).
     * @throws NoSuchAlgorithmException If the SHA-256 algorithm is not available on
     *                                  the current platform.
     */
    public CodeMetadata(String scannableCodeId, String userId, GeoLocation location, String base64Image) {
        this.image = base64Image;
        this.location = location;
        this.geohash = GeoFireUtils.getGeoHashForLocation(location);
        this.scannableCodeId = scannableCodeId;
        this.documentId = UUID.randomUUID().toString();
        this.userId = userId;
    }

    public CodeMetadata(String scannableCodeId, String userId, String base64Image) {
        this(scannableCodeId, userId, null, base64Image);
    }
    public CodeMetadata(String scannableCodeId, String userId) {
        this(scannableCodeId, userId, null, null);
    }

    public CodeMetadata(String scannableCodeId, String userId, GeoLocation location) {
        this(scannableCodeId, userId, location, null);
    }

    public String getDocumentId() {
        return documentId;
    }

    public String getScannableCodeId(){
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
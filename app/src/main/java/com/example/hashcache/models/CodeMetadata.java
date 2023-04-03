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
    final private boolean hasLocation;
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
     */
    public CodeMetadata(String scannableCodeId, String userId, GeoLocation location, String base64Image) {
        this.image = base64Image;
        this.location = location;
        this.hasLocation = location != null;
        this.geohash = this.hasLocation ? GeoFireUtils.getGeoHashForLocation(location): null;
        this.scannableCodeId = scannableCodeId;
        this.documentId = UUID.randomUUID().toString();
        this.userId = userId;
    }

    /**
     * Creates a new CodeMetadata object
     * @param scannableCodeId the id of the scannable code
     * @param userId the id of the user
     * @param base64Image the base64 representation of the image taken
     */
    public CodeMetadata(String scannableCodeId, String userId, String base64Image) {
        this(scannableCodeId, userId, null, base64Image);
    }

    /**
     * Creates a CodeMetadata object with a specific scannableCodeId and userId
     * @param scannableCodeId the id of the scananble code
     * @param userId the id of the user
     */
    public CodeMetadata(String scannableCodeId, String userId) {
        this(scannableCodeId, userId, null, null);
    }

    /**
     * Creates a new CodeMetadata object
     * @param scannableCodeId the id of the scannable code
     * @param userId the id of the user
     * @param location the location where the image was taken
     */
    public CodeMetadata(String scannableCodeId, String userId, GeoLocation location) {
        this(scannableCodeId, userId, location, null);
    }

    /**
     * Checks if this CodeMEtadata has a location
     * @return hasLocation which is true if the CodeMetadata has a location
     */
    public boolean hasLocation(){
        return this.hasLocation;
    }

    /**
     * Checks if this CodeMEtadata has a image
     * @return True if the CodeMetadata has a image
     */
    public boolean hasImage(){
        return image != null;
    }

    /**
     * Gets the id of the the document in the database
     * @return documentId the id of the document in the database
     */
    public String getDocumentId() {
        return documentId;
    }

    /**
     * Gets the scannableCodeId
     * @return scannableCodeId the id for the scannable code
     */
    public String getScannableCodeId(){
        return scannableCodeId;
    }

    /**
     * Gets the geohash
     * @return geohash the has of the geolocation
     */
    public String getGeohash() {
        return geohash;
    }

    /**
     * Gets the userId
     * @return userId the id for the user
     */
    public String getUserId() {
        return userId;
    }

    /**
     * Gets the image
     * @return image the image in the metadata
     */
    public String getImage() {
        return image;
    }

    /**
     * Gets the location
     * @return location the location in the metadata
     */
    public GeoLocation getLocation() {
        return location;
    }
}
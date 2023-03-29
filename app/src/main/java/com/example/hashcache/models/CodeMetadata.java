package com.example.hashcache.models;

import android.location.Location;
import android.media.Image;

import com.firebase.geofire.GeoFireUtils;
import com.firebase.geofire.GeoLocation;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;


/**

 A class representing metadata for a scannable code.
 This metadata includes the code's document ID, scannable code ID,
 location (represented as a GeoLocation and geohash), and image (if available).
 This class uses the Firebase GeoFireUtils library to obtain a geohash for a given location.
 It also uses the SHA-256 algorithm to generate a unique document ID based on the scannable code ID and geohash.
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
    /**
     * Constructs a new CodeMetadata object.
     *
     * @param scannableCodeId The ID of the scannable code.
     * @param location The location of the scannable code.
     * @param base64Image The base64-encoded image of the scannable code (if available).
     * @throws NoSuchAlgorithmException If the SHA-256 algorithm is not available on the current platform.
     */
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

    /**
     * Constructs a new CodeMetadata object with no image.
     *
     * @param scannableCodeId The ID of the scannable code.
     * @param location The location of the scannable code.
     * @throws NoSuchAlgorithmException If the SHA-256 algorithm is not available on the current platform.
     */
        public CodeMetadata(String scannableCodeId, GeoLocation location) throws NoSuchAlgorithmException {
            this(scannableCodeId, location, null);
        }

    /**
     * Returns the document ID of this CodeMetadata object.
     *
     * @return The document ID of this CodeMetadata object.
     */
        public String getDocumentId() {
            return documentId;
        }
    /**
     * Returns the scannable code ID of this CodeMetadata object.
     *
     * @return The scannable code ID of this CodeMetadata object.
     */
        public String getScannableCodeId() {
            return scannableCodeId;
        }
    /**
     * Returns the geohash of this CodeMetadata object.
     *
     * @return The geohash of this CodeMetadata object.
     */

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
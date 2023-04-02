package com.example.hashcache.models;

import android.location.Location;

import java.lang.reflect.Array;

/**
 * Represents the location where a code was scanned
 */
public class CodeLocation {
    private String codeLocationID;
    private Location location;
    /**
     * Constructor for creating a new instance of CodeLocation
     *
     * @param QRString simply the string value of the QR code
     * @param location the location instance containing the lattitude and longitutde
     */
    public CodeLocation(String QRString, Location location){
        this.codeLocationID = QRString;
        this.location = location;
    }



    /**
     * Returns a location object with the location of the QR code
     * @return a location object with the location of the QR code
     */
    public Location getLocation() {
        return this.location;
    }

    /**
     * Sets the location of the QR code
     * @param location the location object containing the information
     */
    public void setLocation(Location location) {
        this.location = location;
    }

    /**
     * Gets the id of a codelocation
     * @return simply the value of the QR content
     */
    public String getId(){
        return this.codeLocationID;
    }
}

package com.example.hashcache.models;

import java.lang.reflect.Array;

/**
 * Represents the location where a code was scanned
 */
public class CodeLocation {
    private String locationName;
    private Coordinate coordinates;
    /**
     * Constructor for creating a new instance of CodeLocation
     *
     * @param locationName The name of the location
     * @param x The x-coordinate of the location
     * @param y The y-coordinate of the location
     * @param z The z-coordinate of the location
     */
    public CodeLocation(String locationName, double x, double y, double z){
        this.locationName = locationName;
        this.coordinates = new Coordinate(x, y, z);
    }

    /**
     * Get the name of the location
     * @return locationName The name of the location
     */
    public String getLocationName() {
        return locationName;
    }

    public void setLocationName(String locationName) {
        this.locationName = locationName;
    }

    /**
     * Get the 3-point coordinates of the location
     * @return coordinates The 3-point coordinates of the location
     */
    public Coordinate getCoordinates() {
        return coordinates;
    }

    /**
     * Sets the 3-point coordinates of the location
     * @param coordinates The 3-point coordinates of the location
     */
    public void setCoordinates(Coordinate coordinates) {
        this.coordinates = coordinates;
    }

    /**
     * Gets the id of a codelocation based on the concatenation of its coordinates
     * @return the concatenation of its coordinates
     */
    public String getId(){
        double[] coordinates = this.coordinates.getCoordinates();
        String x = Double.toString((Double) Array.get(coordinates, 0));
        String y = Double.toString((Double) Array.get(coordinates, 1));
        String z = Double.toString((Double) Array.get(coordinates, 2));

        return x + y + z;
    }
}

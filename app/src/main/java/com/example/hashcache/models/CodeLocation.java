package com.example.hashcache.models;

/**
 * Represents the location where a code was scanned
 */
public class CodeLocation {
    private String locationName;
    private Coordinate coordinates;

    public CodeLocation(String locationName, int x, int y, int z){
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
}

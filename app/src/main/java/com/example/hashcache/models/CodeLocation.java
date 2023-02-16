package com.example.hashcache.models;

public class CodeLocation {
    private String locationName;
    private Coordinate coordinates;

    public CodeLocation(String locationName, int x, int y, int z){
        this.locationName = locationName;
        this.coordinates = new Coordinate(x, y, z);
    }

    public String getLocationName() {
        return locationName;
    }

    public void setLocationName(String locationName) {
        this.locationName = locationName;
    }

    public Coordinate getCoordinates() {
        return coordinates;
    }

    public void setCoordinates(Coordinate coordinates) {
        this.coordinates = coordinates;
    }
}

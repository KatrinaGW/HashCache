package com.example.hashcache.models;

public class CodeLocation {
    private String locationName;
    private Coordinate coordinates;

    public CodeLocation(String locationName, int x, int y, int z){
        this.locationName = locationName;
        this.coordinates = new Coordinate(x, y, z);
    }

}

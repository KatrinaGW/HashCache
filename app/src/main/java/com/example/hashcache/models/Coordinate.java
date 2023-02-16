package com.example.hashcache.models;

/**
 * Represents a coordinate point with a latitude, longitude, and altitude
 */
public class Coordinate {
    private int[] coordinates = new int[3];

    public Coordinate(int x, int y, int z){
        this.coordinates[0] = x;
        this.coordinates[1] = y;
        this.coordinates[2] = z;
    }

    protected void setCoordinate(int value, int position){
        if(position >= 0 && position < 3){
            this.coordinates[position] = value;
        }else{
            throw new IllegalArgumentException("Invalid coordinate position!");
        }

    }

    public int[] getCoordinate(){
        return this.coordinates;
    }
}

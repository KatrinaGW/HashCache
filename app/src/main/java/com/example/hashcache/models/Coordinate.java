package com.example.hashcache.models;

/**
 * Represents a coordinate point with a latitude, longitude, and altitude
 */
public class Coordinate {
    private double[] coordinates = new double[3];

    public Coordinate(int x, int y, int z){
        this.coordinates[0] = x;
        this.coordinates[1] = y;
        this.coordinates[2] = z;
    }

    /**
     * Sets the coordinate's latitude, longitude, or height altitude
     * @param value The new coordinate value
     * @param position The position of the value, 0 is longitude, 1 is latitude, and 2
     *                 is altitude
     * @throws IllegalArgumentException if the position is not 0, 1, or 2
     */
    protected void setCoordinate(double value, int position){
        if(position >= 0 && position < 3){
            this.coordinates[position] = value;
        }else{
            throw new IllegalArgumentException("Invalid coordinate position!");
        }

    }

    /**
     * Get the coordinate values of this point
     * @return this.coordinates The three coordinate points represented by this object
     */
    public double[] getCoordinate(){
        return this.coordinates;
    }
}

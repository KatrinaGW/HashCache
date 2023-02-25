package com.example.hashcache.models;

/**
 * Contains a player's current preferences
 */
public class PlayerPreferences {
    private boolean recordGeoLocation;

    public PlayerPreferences(){
        this.recordGeoLocation = false;
    }

    /**
     * Sets whether or not the player wants their geolocation recorded
     * @param recordGeoLocation Represents whether or not the player's geolocation should be recorded
     */
    public void setGeoLocationRecording(boolean recordGeoLocation){
        this.recordGeoLocation = recordGeoLocation;
    }

    /**
     * Gets whether or not the player wants their geolocation recorded
     * @return recordGeoLocation Represents whether or not the player's geolocation should be recorded
     */
    public boolean getRecordGeolocationPreference(){
        return this.recordGeoLocation;
    }
    
}

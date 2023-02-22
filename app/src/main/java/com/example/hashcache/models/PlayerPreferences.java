package com.example.hashcache.models;

public class PlayerPreferences {
    private boolean recordGeoLocation;

    public PlayerPreferences(){
        this.recordGeoLocation = false;
    }

    public void setGeoLocationRecording(boolean recordGeoLocation){
        this.recordGeoLocation = recordGeoLocation;
    }

    public boolean getRecordGeolocationPreference(){
        return this.recordGeoLocation;
    }
    
}

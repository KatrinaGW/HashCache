package com.example.hashcache.tests;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.example.hashcache.models.PlayerPreferences;

import org.junit.jupiter.api.Test;

public class PlayerPreferencesTest {

    @Test
    void setGeoLocationRecordingTest(){
        Boolean enabled = true;
        PlayerPreferences playerPreferences = new PlayerPreferences();
        playerPreferences.setGeoLocationRecording(enabled);

        assertEquals(enabled, playerPreferences.getRecordGeolocationPreference());
    }

    @Test
    void getGeoLocationRecordingTest(){
        PlayerPreferences playerPreferences = new PlayerPreferences();

        assertFalse(playerPreferences.getRecordGeolocationPreference());
    }
}

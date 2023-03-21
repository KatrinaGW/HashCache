package com.example.hashcache.tests;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.example.hashcache.models.ContactInfo;
import com.example.hashcache.models.Player;
import com.example.hashcache.models.PlayerPreferences;
import com.example.hashcache.models.PlayerWallet;

import org.checkerframework.checker.units.qual.C;
import org.junit.jupiter.api.Test;

public class PlayerTest {
    @Test
    void getUsernameTest(){
        String username = "use this name";
        Player testPlayer = new Player(username);

        assertEquals(testPlayer.getUsername(), username);
    }

    @Test
    void updateUsernameTest(){
        String newUsername = "newMe";
        Player testPlayer = new Player("oldMe");
        testPlayer.updateUserName(newUsername);

        assertEquals(newUsername, testPlayer.getUsername());
    }

    @Test
    void updateContactInfoTest(){
        ContactInfo newContactInfo = new ContactInfo();
        newContactInfo.setEmail("thing@ualberta.ca");
        Player testPlayer = new Player("testName");
        testPlayer.setContactInfo(newContactInfo);

        assertEquals(newContactInfo, testPlayer.getContactInfo());
    }

    @Test
    void getContactInfoTest(){
        ContactInfo testContactInfo = new ContactInfo();
        testContactInfo.setEmail("notAnEmail@gmail.com");
        Player testPlayer = new Player("notARealId", "notARealName", testContactInfo,
                new PlayerPreferences(), new PlayerWallet());

        assertEquals(testContactInfo, testPlayer.getContactInfo());
    }

    @Test
    void getPlayerPreferencesTest(){
        PlayerPreferences testPlayerPreferences = new PlayerPreferences();
        Player testPlayer = new Player("notARealId", "notARealName", new ContactInfo(),
                testPlayerPreferences, new PlayerWallet());

        assertEquals(testPlayerPreferences, testPlayer.getPlayerPreferences());
    }

    @Test
    void setPlayerPreferencesTest(){
        Player testPlayer = new Player("more more words????");
        PlayerPreferences newPreferences = new PlayerPreferences();
        newPreferences.setGeoLocationRecording(true);
        testPlayer.setPlayerPreferences(newPreferences);

        assertEquals(testPlayer.getPlayerPreferences(), newPreferences);
    }

    @Test
    void getUserIdTest(){
        String testId = "Hal";
        Player testPlayer = new Player(testId, "Final words.", new ContactInfo(),
                new PlayerPreferences(), new PlayerWallet());

        assertEquals(testId, testPlayer.getUserId());
    }
}

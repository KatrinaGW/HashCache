package com.example.hashcache.tests;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.example.hashcache.models.Player;

import org.junit.jupiter.api.Test;

public class PlayerTest {
    @Test
    void getUsername(){
        String username = "use this name";
        Player testPlayer = new Player(username);

        assertEquals(testPlayer.getUsername(), username);
    }
}

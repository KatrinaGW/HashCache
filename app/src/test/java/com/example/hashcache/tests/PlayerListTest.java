package com.example.hashcache.tests;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.example.hashcache.database_connections.PlayersConnectionHandler;
import com.example.hashcache.models.PlayerList;
import com.google.firebase.firestore.FirebaseFirestore;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.ArrayList;

public class PlayerListTest {
    @Test
    void testGetPlayer(){
        String newPlayerUsername = "Stubby";
        PlayersConnectionHandler mockPlayerConnectionHandler = Mockito.mock(PlayersConnectionHandler.class);
        Mockito.when(mockPlayerConnectionHandler.getInAppPlayerUserNames()).thenReturn(new ArrayList<>());
        Mockito.when(mockPlayerConnectionHandler.addPlayer(newPlayerUsername)).thenAnswer()
        PlayerList playerList = new PlayerList(mockPlayerConnectionHandler);

        playerList.addPlayer(newPlayerUsername);

        assertEquals(playerList.getPlayerUserNames().get(0), newPlayerUsername);
    }
}

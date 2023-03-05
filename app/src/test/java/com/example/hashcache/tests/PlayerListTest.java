package com.example.hashcache.tests;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.hashcache.database_connections.GetPlayerCallback;
import com.example.hashcache.database_connections.PlayersConnectionHandler;
import com.example.hashcache.models.Player;
import com.example.hashcache.models.PlayerList;
import com.google.firebase.firestore.FirebaseFirestore;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.ArrayList;

public class PlayerListTest {
    @Test
    void testAddPlayerSuccess(){
        String newPlayerUsername = "Stubby";
        Player mockPlayer = new Player(newPlayerUsername);
        ArrayList<String> names = new ArrayList<>();
        PlayersConnectionHandler mockPlayerConnectionHandler = Mockito.mock(PlayersConnectionHandler.class);

        when(mockPlayerConnectionHandler.getInAppPlayerUserNames()).thenReturn(names);
        doAnswer(invocation -> {
            names.add(newPlayerUsername);

            return null;
        }).when(mockPlayerConnectionHandler).addPlayer(mockPlayer);

        PlayerList playerList = new PlayerList(mockPlayerConnectionHandler);

        assertTrue(playerList.addPlayer(newPlayerUsername));
        assertEquals(names.get(0), playerList.getPlayerUserNames().get(0));
    }

    @Test
    void testAddPlayerFailure(){
        String newPlayerUsername = "Stubby";
        PlayersConnectionHandler mockPlayerConnectionHandler = Mockito.mock(PlayersConnectionHandler.class);

        doThrow(new IllegalArgumentException()).when(mockPlayerConnectionHandler).addPlayer(any());
        PlayerList playerList = new PlayerList(mockPlayerConnectionHandler);

        assertFalse(playerList.addPlayer(newPlayerUsername));
    }

    @Test
    void testGetPlayer(){
        String mockPlayerName = "Stubby";
        Player mockPlayer = new Player(mockPlayerName);
        GetPlayerCallback mockGetPlayerCallback = Mockito.mock(GetPlayerCallback.class);

        PlayersConnectionHandler mockPlayerConnectionHandler = Mockito.mock(PlayersConnectionHandler.class);
        doNothing().when(mockPlayerConnectionHandler).getPlayer(mockPlayerName, mockGetPlayerCallback);

        PlayerList playerList = new PlayerList(mockPlayerConnectionHandler);
        playerList.getPlayer(mockPlayerName, mockGetPlayerCallback);

        verify(mockPlayerConnectionHandler, times(1))
                .getPlayer(mockPlayerName, mockGetPlayerCallback);
    }
}

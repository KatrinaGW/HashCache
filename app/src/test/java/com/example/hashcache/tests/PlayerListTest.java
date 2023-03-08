package com.example.hashcache.tests;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.hashcache.models.database_connections.callbacks.BooleanCallback;
import com.example.hashcache.models.database_connections.callbacks.GetPlayerCallback;
import com.example.hashcache.models.database_connections.PlayersConnectionHandler;
import com.example.hashcache.models.Player;
import com.example.hashcache.models.PlayerList;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.ArrayList;

public class PlayerListTest {
    private String newPlayerUsername;
    private Player mockPlayer;
    private ArrayList<String> names;
    private PlayersConnectionHandler mockPlayerConnectionHandler;
    private BooleanCallback mockBooleanCallback;

    @BeforeEach
    void resetMocks(){
        newPlayerUsername = "Stubby";
        mockPlayer = new Player(newPlayerUsername);
        names = new ArrayList<>();
        mockPlayerConnectionHandler = Mockito.mock(PlayersConnectionHandler.class);
        mockBooleanCallback = Mockito.mock(BooleanCallback.class);
        PlayerList.resetInstance();
    }
    @Test
    void testAddPlayerSuccess(){
        when(mockPlayerConnectionHandler.getInAppPlayerUserNames()).thenReturn(names);
        doAnswer(invocation -> {
            return null;
        }).when(mockPlayerConnectionHandler).addPlayer(mockPlayer, mockBooleanCallback);

        PlayerList playerList = PlayerList.getInstance(mockPlayerConnectionHandler);

        assertTrue(playerList.addPlayer(newPlayerUsername, mockBooleanCallback));
        assertEquals(names.get(0), playerList.getPlayerUserNames().get(0));
    }

    @Test
    void testAddPlayerFailure(){
        doThrow(new IllegalArgumentException()).when(mockPlayerConnectionHandler).addPlayer(
                any(), any());

        PlayerList playerList = PlayerList.getInstance(mockPlayerConnectionHandler);

        assertFalse(playerList.addPlayer(newPlayerUsername, mockBooleanCallback));
    }

    @Test
    void testGetPlayer(){
        GetPlayerCallback mockGetPlayerCallback = Mockito.mock(GetPlayerCallback.class);

        doNothing().when(mockPlayerConnectionHandler).getPlayer(newPlayerUsername, mockGetPlayerCallback);

        PlayerList playerList = PlayerList.getInstance(mockPlayerConnectionHandler);
        playerList.getPlayer(newPlayerUsername, mockGetPlayerCallback);

        verify(mockPlayerConnectionHandler, times(1))
                .getPlayer(newPlayerUsername, mockGetPlayerCallback);
    }
}

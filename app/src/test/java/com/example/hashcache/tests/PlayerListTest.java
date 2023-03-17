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

import com.example.hashcache.models.database.DatabaseAdapters.callbacks.GetPlayerCallback;
import com.example.hashcache.models.database.DatabaseAdapters.PlayersDatabaseAdapter;
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
    private PlayersDatabaseAdapter mockPlayerConnectionHandler;
    private GetStringCallback mockGetStringCallback;

    @BeforeEach
    void resetMocks(){
        newPlayerUsername = "Stubby";
        mockPlayer = new Player(newPlayerUsername);
        names = new ArrayList<>();
        mockPlayerConnectionHandler = Mockito.mock(PlayersDatabaseAdapter.class);
        mockGetStringCallback = Mockito.mock(GetStringCallback.class);
        PlayerList.resetInstance();
    }

    @Test
    void testAddPlayerSuccess(){
        GetStringCallback mockGetStringCallback = Mockito.mock(GetStringCallback.class);
        Player mockPlayer = new Player("name");

        when(mockPlayerConnectionHandler.getInAppPlayerUserNames()).thenReturn(names);
        doAnswer(invocation -> {
            names.add(newPlayerUsername);
            mockGetStringCallback.onCallback("id");
            return null;
        }).when(mockPlayerConnectionHandler).createPlayer(mockPlayer.getUsername(), mockGetStringCallback);

        PlayerList playerList = PlayerList.getInstance(mockPlayerConnectionHandler);
        playerList.addPlayer(mockPlayer.getUsername(), mockGetStringCallback);

        verify(mockPlayerConnectionHandler, times(1)).createPlayer(anyString(),
                any(GetStringCallback.class));
    }

    @Test
    void testAddPlayerFailure(){
        doThrow(new IllegalArgumentException()).when(mockPlayerConnectionHandler).createPlayer(
                anyString(), any(GetStringCallback.class));

        PlayerList playerList = PlayerList.getInstance(mockPlayerConnectionHandler);

        assertFalse(playerList.addPlayer(newPlayerUsername, mockGetStringCallback));
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

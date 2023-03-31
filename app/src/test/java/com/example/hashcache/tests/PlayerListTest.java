package com.example.hashcache.tests;

import static org.mockito.Mockito.when;

import com.example.hashcache.models.HashInfo;
import com.example.hashcache.models.Player;
import com.example.hashcache.models.PlayerList;
import com.example.hashcache.models.database.DatabaseAdapters.PlayersDatabaseAdapter;
import com.example.hashcache.models.database.DatabaseAdapters.callbacks.BooleanCallback;
import com.example.hashcache.models.database.DatabasePort;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.CompletableFuture;

public class PlayerListTest {

    private String newPlayerUsername;
    private PlayersDatabaseAdapter mockPlayerConnectionHandler;
    private BooleanCallback mockBooleanCallback;
    private DatabasePort mockDb;

    @BeforeEach
    void resetMocks(){
        newPlayerUsername = "Stubby";
        mockDb = Mockito.mock(DatabasePort.class);
        mockPlayerConnectionHandler = Mockito.mock(PlayersDatabaseAdapter.class);
        mockBooleanCallback = Mockito.mock(BooleanCallback.class);
        PlayerList.resetInstance();
    }

    @Test
    void testSearchPlayers(){
        String searchTerm = "w";
        int k = 2;
        HashMap<String, String> testReturnValue = new HashMap<>();
        String usernameA = "Apfel";
        testReturnValue.put(usernameA, "");
        String usernameB = "Bee Movie";
        testReturnValue.put(usernameB, "");
        String usernameC = "Definitely A Human";
        testReturnValue.put(usernameC, "");
        String usernameD = "Donut worry";
        testReturnValue.put(usernameD, "");
        CompletableFuture<HashMap<String, String>> testCF = new CompletableFuture<>();
        testCF.complete(testReturnValue);

        when(mockDb.getPlayers()).thenReturn(testCF);

        ArrayList<String> result = PlayerList.getInstance(mockPlayerConnectionHandler).searchPlayers(searchTerm, k,
                mockDb).join();

        assert(result.size() == 1);
        assert(result.get(0) == usernameD);
    }

    @Test
    public void testLevenshtein() {
        PlayerList playerList = PlayerList.getInstance(mockPlayerConnectionHandler);
        Integer distance = playerList.computeLevenshteinDistance("benyam", "ephrem");

        assert distance == 5;

        distance = playerList.computeLevenshteinDistance("hashcache", "hashcache");

        assert distance == 0;

        distance = playerList.computeLevenshteinDistance("a", "Ryan");

        assert distance == 3;
    }

    // TODO add a test for searching
    @Test
    public void testSearch() {
    }

    // TODO add a test for using the filter. When more stuff with the leaderboard is setup
    @Test
    public void testFilter() {

    }
}

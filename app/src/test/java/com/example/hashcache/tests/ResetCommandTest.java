package com.example.hashcache.tests;

import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Matchers.any;

import com.example.hashcache.appContext.AppContext;
import com.example.hashcache.controllers.ResetCommand;
import com.example.hashcache.models.Player;
import com.example.hashcache.models.PlayerList;
import com.example.hashcache.models.ScannableCode;
import com.example.hashcache.models.database.DatabaseAdapters.PlayersDatabaseAdapter;
import com.example.hashcache.models.database.DatabaseAdapters.ScannableCodesDatabaseAdapter;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

public class ResetCommandTest {
    @Test
    void resetTest(){
        PlayersDatabaseAdapter mockPlayersDatabaseAdapter = Mockito.mock(PlayersDatabaseAdapter.class);

        PlayerList originalPlayerList = PlayerList.getInstance(mockPlayersDatabaseAdapter);
        AppContext.get().setCurrentPlayer(Mockito.mock(Player.class));

        ResetCommand.reset();

        assertNotEquals(originalPlayerList, PlayerList.getInstance(mockPlayersDatabaseAdapter));
        assertNull(AppContext.get().getCurrentPlayer());
        assertNull(AppContext.get().getDeviceId());
        assertNull(AppContext.get().getCurrentScannableCode());
    }
}

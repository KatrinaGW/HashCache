package com.example.hashcache.tests;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.hashcache.appContext.AppContext;
import com.example.hashcache.controllers.UpdateUserPreferencesCommand;
import com.example.hashcache.models.Player;
import com.example.hashcache.models.PlayerPreferences;
import com.example.hashcache.models.database.DatabasePort;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

public class UpdateUserPreferencesCommandTest {
    @Test
    void toggleGeoLocationPreferenceTest(){
        DatabasePort mockDB = Mockito.mock(DatabasePort.class);
        AppContext mockAppContext = Mockito.mock(AppContext.class);
        Player testPlayer = new Player("hi;dfgs");
        PlayerPreferences testPlayerPreferences = new PlayerPreferences();
        testPlayer.setPlayerPreferences(testPlayerPreferences);

        when(mockAppContext.getCurrentPlayer()).thenReturn(testPlayer);

        UpdateUserPreferencesCommand.toggleGeoLocationPreference(true, mockAppContext,
                mockDB);

        verify(mockDB, times(1)).updatePlayerPreferences(
                testPlayer.getUserId(), testPlayerPreferences
        );

    }

}

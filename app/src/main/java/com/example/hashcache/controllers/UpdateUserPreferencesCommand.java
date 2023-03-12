package com.example.hashcache.controllers;

import com.example.hashcache.models.Player;
import com.example.hashcache.models.database.Database;
import com.example.hashcache.store.AppStore;

public class UpdateUserPreferencesCommand {
    public static void toggleGeoLocationPreference(boolean enabled){
        Player currentPlayer = AppStore.get().getCurrentPlayer();
        currentPlayer.getPlayerPreferences().setGeoLocationRecording(enabled);
        Database.getInstance().updatePlayerPreferences(currentPlayer.getUserId(), currentPlayer.getPlayerPreferences());
    }
}

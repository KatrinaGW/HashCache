package com.example.hashcache.controllers;

import com.example.hashcache.appContext.AppContext;
import com.example.hashcache.models.Player;
import com.example.hashcache.models.database.DatabasePort;

/**
 * The UpdateUserPreferencesCommand class provides methods for updating a user's preferences.
 */
public class UpdateUserPreferencesCommand {
    /**
     * Toggles the geo-location recording preference for the current player and updates it in the database.
     *
     * @param enabled true to enable geo-location recording, false to disable it
     * @param appContext the current app context
     * @param db the database interface for the firestore collection
     *
     */
    public static void toggleGeoLocationPreference(boolean enabled, AppContext appContext,
                                                   DatabasePort db){
        // Get the current player from the AppStore
        Player currentPlayer = appContext.getCurrentPlayer();
        // Set the geo-location recording preference for the current player
        currentPlayer.getPlayerPreferences().setGeoLocationRecording(enabled);
        // Update the player preferences in the database
        db.updatePlayerPreferences(currentPlayer.getUserId(),
                currentPlayer.getPlayerPreferences());
    }
}

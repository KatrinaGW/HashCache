package com.example.hashcache.controllers;

import com.example.hashcache.models.Player;
import com.example.hashcache.models.database.Database;
import com.example.hashcache.context.Context;
/**
 * The UpdateUserPreferencesCommand class provides methods for updating a user's preferences.
 */
public class UpdateUserPreferencesCommand {
    /**
     * Toggles the geo-location recording preference for the current player and updates it in the database.
     *
     * @param enabled true to enable geo-location recording, false to disable it
     */
    public static void toggleGeoLocationPreference(boolean enabled){
        // Get the current player from the AppStore
        Player currentPlayer = Context.get().getCurrentPlayer();
        // Set the geo-location recording preference for the current player
        currentPlayer.getPlayerPreferences().setGeoLocationRecording(enabled);
        // Update the player preferences in the database
        Database.getInstance().updatePlayerPreferences(currentPlayer.getUserId(),
                currentPlayer.getPlayerPreferences());
    }
}

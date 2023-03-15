package com.example.hashcache.models.database.database_connections.callbacks;

import com.example.hashcache.models.PlayerPreferences;

/**
 * Used to get a callback response with a PlayerPreferences object after an asynchronous operation has finished
 */
public interface GetPlayerPreferencesCallback {
    /**
     * Called once an asynchronous operation is finished
     * @param playerPreferences a PlayerPreferences object from the asynchronous function
     */
    void onCallback(PlayerPreferences playerPreferences);
}

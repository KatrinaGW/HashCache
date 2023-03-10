package com.example.hashcache.models.database_connections.callbacks;

//Code from https://stackoverflow.com/a/48500679 add proper reference later

import com.example.hashcache.models.Player;

/**
 * Used to get a callback response with a Player object after an asynchronous operation has finished
 */
public interface GetPlayerCallback {
    /**
     * Called once an asynchronous operation is finished
     * @param player a Player object from the asynchronous function
     */
    void onCallback(Player player);
}
package com.example.hashcache.models.database_connections.callbacks;

import com.example.hashcache.models.PlayerWallet;

/**
 * Used to get a callback response with a PlayerWallet object after an asynchronous operation has finished
 */
public interface GetPlayerWalletCallback {
    /**
     * Called once an asynchronous operation is finished
     * @param playerWallet a PlayerWallet object from the asynchronous function
     */
    void onCallback(PlayerWallet playerWallet);
}

package com.example.hashcache.models.database_connections.callbacks;

/**
 * Used to get a callback response with a String object after an asynchronous operation has finished
 */
public interface GetStringCallback {
    /**
     * Called once an asynchronous operation is finished
     * @param callbackString a String object from the asynchronous function
     */
    void onCallback(String callbackString);
}

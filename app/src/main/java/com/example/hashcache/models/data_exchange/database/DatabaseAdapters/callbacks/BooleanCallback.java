package com.example.hashcache.models.data_exchange.database.DatabaseAdapters.callbacks;

/**
 * Used to get a callback response with a boolean value after an asynchronous operation has finished
 */
public interface BooleanCallback {
    /**
     * Called once an asynchronous operation is finished
     * @param isTrue a boolean value from the asynchronous function
     */
    void onCallback(Boolean isTrue);
}

package com.example.hashcache.models.database.DatabaseAdapters.callbacks;

import com.example.hashcache.models.ScannableCode;

/**
 * Used to get a callback response with a ScannableCode object after an asynchronous operation has finished
 */
public interface GetScannableCodeCallback {
    /**
     * Called once an asynchronous operation is finished
     * @param scannableCode a ScannableCode object from the asynchronous function
     */
    void onCallback(ScannableCode scannableCode);
}

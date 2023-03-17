package com.example.hashcache.models.database.DatabaseAdapters.callbacks;

import com.example.hashcache.models.CodeLocation;

/**
 * Used to get a callback response with a CodeLocation value after an asynchronous operation has finished
 */
public interface GetCodeLocationCallback {
    /**
     * Called once an asynchronous operation is finished
     * @param codeLocation a CodeLocation object from the asynchronous function
     */
    void onCallback(CodeLocation codeLocation);
}

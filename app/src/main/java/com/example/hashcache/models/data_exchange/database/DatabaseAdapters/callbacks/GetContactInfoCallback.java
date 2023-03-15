package com.example.hashcache.models.data_exchange.database.DatabaseAdapters.callbacks;

import com.example.hashcache.models.ContactInfo;

/**
 * Used to get a callback response with a ContactInfo object after an asynchronous operation has finished
 */
public interface GetContactInfoCallback {
    /**
     * Called once an asynchronous operation is finished
     * @param contactInfo a ContactInfo object from the asynchronous function
     */
    void onGetContactInfoCallback(ContactInfo contactInfo);
}

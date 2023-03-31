package com.example.hashcache.controllers;

import android.util.Log;

import com.example.hashcache.models.ContactInfo;
import com.example.hashcache.models.database.Database;
import com.example.hashcache.appContext.AppContext;
import com.example.hashcache.models.database.DatabasePort;

import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

/**

 The UpdateContactInfoCommand class provides a static method to update the contact info of a player in the app.
 The class takes in the user ID and a ContactInfo object to update the user's contact information.
 If the given user ID matches the ID of the currently logged in player, the ContactInfo object of the current player
 is updated in the app store. Otherwise, a CompletableFuture is returned with the result of updating the ContactInfo
 in the database.
 The CompletableFuture is completed with a boolean value indicating whether the update was successful or not.
 The update is executed asynchronously using CompletableFuture.runAsync() method to avoid blocking the main thread.
 @see ContactInfo
 @see Database
 @see AppContext
 @see CompletableFuture
 */
public class UpdateContactInfoCommand {
    /**
     * Update a user's contact information in the database
     * @param userId the id of the user whose contact info needs to be updated
     * @param contactInfo the new contact information that needs to be used for the user
     * @return cf the CompletableFuture with the boolean value indicating if the operation was successful
     */
    public static CompletableFuture<Boolean> updateContactInfoCommand(String userId,
                                                                      ContactInfo contactInfo,
                                                                      DatabasePort db,
                                                                      AppContext appContext){
        CompletableFuture<Boolean> cf = new CompletableFuture<>();

        if(userId.equals(appContext.getCurrentPlayer().getUserId())){
            appContext.getCurrentPlayer().setContactInfo(contactInfo);
        }

        CompletableFuture.runAsync(()->{
            db.updateContactInfo(contactInfo, userId).thenAccept(
                    isComplete -> {
                        cf.complete(isComplete);
                    }
            ).exceptionally(new Function<Throwable, Void>() {
                @Override
                public Void apply(Throwable throwable) {
                    Log.d("ERROR", "Could not update player" + appContext.getCurrentPlayer().getUsername());
                    Log.d("Reason", throwable.getMessage());
                    cf.completeExceptionally(new Exception("Could not update player."));
                    return null;
                }
            });
        });

        return cf;
    }
}

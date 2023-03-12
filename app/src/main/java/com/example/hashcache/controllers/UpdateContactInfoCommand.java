package com.example.hashcache.controllers;

import com.example.hashcache.models.ContactInfo;
import com.example.hashcache.models.database.Database;
import com.example.hashcache.store.AppStore;

import java.util.concurrent.CompletableFuture;
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
 @see AppStore
 @see CompletableFuture
 */
public class UpdateContactInfoCommand {
    public static CompletableFuture<Boolean> updateContactInfoCommand(String userId, ContactInfo contactInfo){
        CompletableFuture<Boolean> cf = new CompletableFuture<>();

        if(userId.equals(AppStore.get().getCurrentPlayer().getUserId())){
            AppStore.get().getCurrentPlayer().setContactInfo(contactInfo);
        }

        CompletableFuture.runAsync(()->{
            Database.getInstance().updateContactInfo(contactInfo, userId).thenAccept(
                    isComplete -> {
                        cf.complete(isComplete);
                    }
            );
        });

        return cf;
    }
}

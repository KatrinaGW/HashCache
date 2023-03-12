package com.example.hashcache.controllers;

import com.example.hashcache.models.ContactInfo;
import com.example.hashcache.models.database.Database;
import com.example.hashcache.store.AppStore;

import java.util.concurrent.CompletableFuture;

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

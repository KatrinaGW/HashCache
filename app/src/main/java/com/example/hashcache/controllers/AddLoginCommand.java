package com.example.hashcache.controllers;

import com.example.hashcache.appContext.AppContext;
import com.example.hashcache.models.database.DatabasePort;

import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

public class AddLoginCommand {
    /**
     * Adds a login record to the DB for the current device and user
     * @param username the username to attach to the device id
     * @param db the instance of the DatabasePort to use
     * @return cf a CompletableFuture that completes exceptionally if there is a problem
     */
    public static CompletableFuture<Void> addLogin(String username, AppContext appContext, DatabasePort
                                                   db){
        CompletableFuture<Void> cf = new CompletableFuture<>();

        CompletableFuture.runAsync(() -> {
            db.addLoginRecord(username)
                    .thenAccept(nullValue -> {
                        cf.complete(null);
                    })
                    .exceptionally(new Function<Throwable, Void>() {
                        @Override
                        public Void apply(Throwable throwable) {
                            cf.completeExceptionally(throwable);
                            return null;
                        }
                    });
        });
        return cf;
    }
}

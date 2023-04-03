package com.example.hashcache.controllers;

import com.example.hashcache.appContext.AppContext;
import com.example.hashcache.models.database.Database;
import com.example.hashcache.models.database.DatabasePort;

import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

/**
 * A controller for checking if a user has logged in on their device before
 */
public class CheckLoginCommand {

    /**
     * Checks if the device has a login record for a specific user
     * @param db the instance of the DatabasePort to use
     * @param setupUserCommand the instance of the SetUpUserCommand to use
     * @return cf the CompletableFuture which returns true if the device has a login and false otherwise
     */
    public static CompletableFuture<Boolean> checkLogin(DatabasePort db, SetupUserCommand setupUserCommand){
        CompletableFuture<Boolean> cf = new CompletableFuture<>();
        CompletableFuture.runAsync(() -> {
            db.getUsernameForDevice()
                    .thenAccept(username -> {
                        if(username!=null){
                            setupUserCommand.setupUser(username, Database.getInstance(), AppContext.get())
                                    .thenAccept(nullValue -> {
                                        cf.complete(true);
                                    })
                                    .exceptionally(new Function<Throwable, Void>() {
                                        @Override
                                        public Void apply(Throwable throwable) {
                                            cf.completeExceptionally(throwable);
                                            return null;
                                        }
                                    });
                        }else{
                            cf.complete(false);
                        }
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

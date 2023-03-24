package com.example.hashcache.controllers;

import com.example.hashcache.context.Context;
import com.example.hashcache.models.database.DatabasePort;

import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
/**
 * The AddUserCommand class is responsible for handling the logic for adding a new user or logging in an existing user.
 */
public class LoginUserCommand {
    /**
     * Logs in the user with the given username or creates a new user with the given username if the user does not exist.
     *
     * @param userName the username of the user to log in or create
     * @param db the database instance of the DatabasePort to interface with the Firestore collection
     * @param context the current app context
     * @param setupUserCommand the instance of the SetupUser command to use
     * @return a CompletableFuture that completes when the user has been logged in or created
     */
    public CompletableFuture<Void> loginUser(String userName, DatabasePort db, Context context,
                                             SetupUserCommand setupUserCommand){
        CompletableFuture<Void> cf = new CompletableFuture<>();
        db.usernameExists(userName).thenAccept(exists -> {
            if(exists){
                setupUserCommand.setupUser(userName, db, context)
                        .thenAccept(nullValue->{
                            cf.complete(null);
                        })
                        .exceptionally(new Function<Throwable, Void>() {
                            @Override
                            public Void apply(Throwable throwable) {
                                cf.completeExceptionally(throwable);
                                return null;
                            }
                        });
            }
            else{
                db.createPlayer(userName).thenAccept(result -> {
                    setupUserCommand.setupUser(userName, db, context)
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
                }).exceptionally(new Function<Throwable, Void>() {
                    @Override
                    public Void apply(Throwable throwable) {
                        cf.completeExceptionally(throwable);
                        return null;
                    }
                });
            }
        });
        return cf;
    }
}

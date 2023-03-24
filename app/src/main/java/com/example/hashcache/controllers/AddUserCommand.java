package com.example.hashcache.controllers;

import android.util.Log;

import com.example.hashcache.context.Context;
import com.example.hashcache.models.database.DatabasePort;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
/**
 * The AddUserCommand class is responsible for handling the logic for adding a new user or logging in an existing user.
 */
public class AddUserCommand {
    FirebaseFirestore db;
    /**
     * Logs in the user with the given username or creates a new user with the given username if the user does not exist.
     *
     * @param userName the username of the user to log in or create
     * @param db the database instance of the DatabasePort to interface with the Firestore collection
     * @param context the current app context
     * @return a CompletableFuture that completes when the user has been logged in or created
     */
    public CompletableFuture<Void> addUser(String userName, DatabasePort db, Context context){
        CompletableFuture<Void> cf = new CompletableFuture<>();
        db.usernameExists(userName).thenAccept(exists -> {
            if(exists){
                cf.completeExceptionally(new IllegalArgumentException(
                        "User already exists with given username!"
                ));
            }
            else{
                db.createPlayer(userName).thenAccept(result -> {
                    setupUser(userName, cf, db, context);
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
    /**
     * Sets up the user with the given username after the user has been logged in or created.
     *
     * @param userName the username of the user to set up
     * @param db the interface to use to conenct to the firestore collection
     * @param context the current context of the app
     * @param cf the CompletableFuture to complete when the user has been set up
     */
    private void setupUser(String userName, CompletableFuture<Void> cf, DatabasePort db,
                           Context context) {
        db.getIdByUsername(userName).thenAccept(userId -> {
            db.getPlayer(userId).thenAccept(player -> {

                AddLoginCommand.addLogin(userName, context, db)
                                .thenAccept(nullValue -> {
                                    context.setCurrentPlayer(player);
                                    context.setupListeners();
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
                    Log.d("ERROR", "Could not get player" + userName);
                    Log.d("Reason", throwable.getMessage());
                    cf.completeExceptionally(new Exception("Could not get player."));
                    return null;
                }
            });
        });
    }
}

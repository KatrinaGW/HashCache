package com.example.hashcache.controllers;

import com.example.hashcache.models.Player;
import com.example.hashcache.models.database.PlayerDatabase;
import com.example.hashcache.models.database_connections.callbacks.BooleanCallback;
import com.example.hashcache.models.PlayerList;
import com.example.hashcache.store.AppStore;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

public class AddUserCommand {
    FirebaseFirestore db;

    public CompletableFuture<Void> loginUser(String userName){
        CompletableFuture<Void> cf = new CompletableFuture<>();
        PlayerDatabase.getInstance().usernameExists(userName).thenAccept(exists -> {
            if(exists){
                setupUser(userName, cf);
            }
            else{
                PlayerDatabase.getInstance().createPlayer(userName).thenAccept(result -> {
                    setupUser(userName, cf);
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

    private void setupUser(String userName, CompletableFuture<Void> cf) {
        PlayerDatabase.getInstance().getIdByUsername(userName).thenAccept(userId -> {
            PlayerDatabase.getInstance().getPlayer(userId).thenAccept(player -> {
                AppStore.get().setCurrentPlayer(player);
                cf.complete(null);
            }).exceptionally(new Function<Throwable, Void>() {
                @Override
                public Void apply(Throwable throwable) {
                    cf.completeExceptionally(new Exception("Could not get player."));
                    return null;
                }
            });
        });
    }
}

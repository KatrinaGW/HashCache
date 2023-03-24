package com.example.hashcache.controllers;

import com.example.hashcache.models.database.Database;
import com.example.hashcache.models.database.DatabasePort;

import org.checkerframework.checker.units.qual.C;

import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

public class AddLoginCommand {
    public static CompletableFuture<Void> addLogin(String username, String deviceId, DatabasePort
                                                   db){
        CompletableFuture<Void> cf = new CompletableFuture<>();

        CompletableFuture.runAsync(() -> {
            db.addLoginRecord(username, deviceId)
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

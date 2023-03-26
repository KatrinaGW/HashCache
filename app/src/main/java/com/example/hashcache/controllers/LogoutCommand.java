package com.example.hashcache.controllers;

import com.example.hashcache.context.Context;
import com.example.hashcache.models.database.Database;
import com.example.hashcache.models.database.DatabasePort;

import org.checkerframework.checker.units.qual.C;

import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

public class LogoutCommand {
    /**
     * Logs the current device out of the current account, and deletes the login
     * entry from the database
     * @param db the instance of the DatabasePort to use
     * @return cf the CompletableFuture that completes exceptionally if there was a problem
     * during the operation
     */
    public static CompletableFuture<Void> logout(DatabasePort db){
        CompletableFuture<Void> cf = new CompletableFuture<>();

        CompletableFuture.runAsync(() -> {
            db.deleteLogin()
                    .thenAccept(nullValue -> {
                        cf.complete(nullValue);
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

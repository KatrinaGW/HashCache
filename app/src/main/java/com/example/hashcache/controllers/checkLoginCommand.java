package com.example.hashcache.controllers;

import com.example.hashcache.context.Context;
import com.example.hashcache.models.database.Database;

import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

public class checkLoginCommand {
    public static CompletableFuture<Boolean> checkLogin(LoginUserCommand loginUserCommand){
        SetupUserCommand setupUserCommand = new SetupUserCommand();
        CompletableFuture<Boolean> cf = new CompletableFuture<>();
        CompletableFuture.runAsync(() -> {
            Database.getInstance().getUsernameForDevice()
                    .thenAccept(username -> {
                        if(username!=null){
                            setupUserCommand.setupUser(username, Database.getInstance(), Context.get())
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

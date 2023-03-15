package com.example.hashcache.models.data_exchange;

import com.example.hashcache.models.CodeLocation;
import com.example.hashcache.models.data_exchange.data_adapters.CodeLocationDataAdapter;

import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

public interface DataExchangePort {

    /**
     * Gets the codeLocation with a given ID from the Database
     * @param codeLocationid the id of the codeLocation to get
     * @return cf the CompleteableFuture with the codeLocation
     */
    static CompletableFuture<CodeLocation> getCodeLocation(String codeLocationid){
        CompletableFuture<CodeLocation> cf = new CompletableFuture<>();

        CompletableFuture.runAsync(() -> {
            CodeLocationDataAdapter.getCodeLocation(codeLocationid).thenAccept(codeLocation -> {
                cf.complete(codeLocation);
            }).exceptionally(new Function<Throwable, Void>() {
                @Override
                public Void apply(Throwable throwable) {
                    cf.completeExceptionally(throwable);
                    return null;
                }
            });
        });
        return cf;
    }

    /**
     * Adds a codeLocation to the database
     * @param codeLocation the codeLocation to add to the database
     * @return cf the CompleteableFuture with True if the operation was successful
     */
    static CompletableFuture<Boolean> addCodeLocation(CodeLocation codeLocation){
        CompletableFuture<Boolean> cf = new CompletableFuture<>();

        CompletableFuture.runAsync(() -> {
            CodeLocationDataAdapter.addCodeLocation(codeLocation).thenAccept(success ->{
                cf.complete(true);
            }).exceptionally(new Function<Throwable, Void>() {
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

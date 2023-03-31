package com.example.hashcache.controllers;

import android.util.Log;

import com.example.hashcache.appContext.AppContext;
import com.example.hashcache.models.Player;
import com.example.hashcache.models.PlayerWallet;
import com.example.hashcache.models.database.Database;
import com.example.hashcache.models.database.DatabaseAdapter;
import com.example.hashcache.models.database.DatabaseAdapters.FireStoreHelper;
import com.example.hashcache.models.database.DatabasePort;
import com.google.firebase.firestore.CollectionReference;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.function.Function;

public class UpdateUserScore {

    public static CompletableFuture<Boolean> updateUserScore(AppContext context, DatabasePort db) throws ExecutionException, InterruptedException {
        Log.i("USER", "CALLED");
        Player currentPlayer = context.getCurrentPlayer();
        PlayerWallet currentPlayerWallet = currentPlayer.getPlayerWallet();
        CompletableFuture<Boolean> cf = new CompletableFuture<>();

        CompletableFuture.runAsync(() -> {
            db.updatePlayerScores(currentPlayer.getUserId(), currentPlayerWallet).thenAccept(
                    isComplete -> {
                        cf.complete(true);
                    }
            ).exceptionally(new Function<Throwable, Void>() {
                        @Override
                        public Void apply(Throwable throwable) {
                            Log.d("ERROR", "Could not update player" + context.getCurrentPlayer().getUsername());
                            Log.d("Reason", throwable.getMessage());
                            cf.completeExceptionally(new Exception("Could not update player."));
                            return null;
                        }
                    });

        });
        return cf;

    }
}

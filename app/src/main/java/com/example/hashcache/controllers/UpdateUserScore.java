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

/**
 * A controller to handle updating the score values saved in a user document
 */
public class UpdateUserScore {

    /**
     * Updates the user's score fields in the database
     * @param context the current app context to use
     * @param db the database instance to use
     * @return cf the CompletableFuture indicating if the operation was successful or not
     */
    public static CompletableFuture<Boolean> updateUserScore(AppContext context, DatabasePort db) {
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

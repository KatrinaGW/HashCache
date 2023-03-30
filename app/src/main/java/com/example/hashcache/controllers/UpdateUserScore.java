package com.example.hashcache.controllers;

import android.util.Log;

import com.example.hashcache.context.Context;
import com.example.hashcache.models.Player;
import com.example.hashcache.models.PlayerWallet;
import com.example.hashcache.models.database.Database;
import com.example.hashcache.models.database.DatabasePort;

import java.util.concurrent.ExecutionException;

public class UpdateUserScore {

    public static void updateUserScore(Context context, DatabasePort db) throws ExecutionException, InterruptedException {
        Log.i("USER", "CALLED");
        Player currentPlayer = context.getCurrentPlayer();
        PlayerWallet currentPlayerWallet = currentPlayer.getPlayerWallet();
        db.updatePlayerScores(currentPlayer.getUserId(), currentPlayerWallet).get();
    }
}

package com.example.hashcache.controllers;

import com.example.hashcache.context.Context;
import com.example.hashcache.models.Player;
import com.example.hashcache.models.PlayerWallet;
import com.example.hashcache.models.database.Database;
import com.example.hashcache.models.database.DatabasePort;

public class UpdateUserScore {

    public static void updateUserScore(Context context, DatabasePort db) {
        Player currentPlayer = context.getCurrentPlayer();
        PlayerWallet currentPlayerWallet = currentPlayer.getPlayerWallet();
        db.updatePlayerScores(currentPlayer.getUserId(), currentPlayerWallet);
    }
}

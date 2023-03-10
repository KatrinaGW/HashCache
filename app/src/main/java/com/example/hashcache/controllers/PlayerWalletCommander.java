package com.example.hashcache.controllers;

import android.media.Image;

import com.example.hashcache.models.database_connections.PlayersConnectionHandler;
import com.example.hashcache.models.database_connections.callbacks.BooleanCallback;

public class PlayerWalletCommander {

    public void addScannableCodeToWallet(String scannableCodeId, Image codeImage, String playerId,
                                          BooleanCallback booleanCallback){
        PlayersConnectionHandler.getInstance().playerScannedCodeAdded(playerId, scannableCodeId,
                codeImage, booleanCallback);
    }

    public void deleteScannableCodeFromWallet(String scannableCodeId, String playerId,
                                         BooleanCallback booleanCallback){
        PlayersConnectionHandler.getInstance().playerScannedCodeDeleted(playerId, scannableCodeId, booleanCallback);
    }
}

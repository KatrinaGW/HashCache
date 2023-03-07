package com.example.hashcache.controllers;

import android.media.Image;

import com.example.hashcache.database_connections.PlayersConnectionHandler;
import com.example.hashcache.database_connections.ScannableCodesConnectionHandler;
import com.example.hashcache.database_connections.callbacks.BooleanCallback;
import com.example.hashcache.models.Player;

import java.util.ArrayList;

public class PlayerWalletCommander {

    public void addScannableCodeToWallet(String scannableCodeId, Image codeImage, String playerId,
                                          BooleanCallback booleanCallback){
        PlayersConnectionHandler.getInstance().playerScannedCodeAdded(playerId, scannableCodeId, codeImage, booleanCallback);
    }

    public void deleteScannableCodeFromWallet(String scannableCodeId, String playerId,
                                         BooleanCallback booleanCallback){
        PlayersConnectionHandler.getInstance().playerScannedCodeDeleted(playerId, scannableCodeId, booleanCallback);
    }
}

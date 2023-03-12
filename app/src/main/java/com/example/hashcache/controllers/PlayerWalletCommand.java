package com.example.hashcache.controllers;

import android.media.Image;

import com.example.hashcache.models.database_connections.PlayersConnectionHandler;
import com.example.hashcache.models.database_connections.callbacks.BooleanCallback;
/**

 The PlayerWalletCommander class handles commands to add or delete scannable codes from a player's wallet.
 */
public class PlayerWalletCommand {
    /**
     * Adds a scannable code to the player's wallet.
     *
     * @param scannableCodeId the ID of the scannable code to add
     * @param codeImage the image of the scannable code
     * @param playerId the ID of the player whose wallet the scannable code should be added to
     * @param booleanCallback a callback to be called with a boolean indicating whether the operation was successful
     */
    public void addScannableCodeToWallet(String scannableCodeId, Image codeImage, String playerId,
                                          BooleanCallback booleanCallback){
        PlayersConnectionHandler.getInstance().playerScannedCodeAdded(playerId, scannableCodeId,
                codeImage, booleanCallback);
    }
    /**
     * Deletes a scannable code from the player's wallet.
     *
     * @param scannableCodeId the ID of the scannable code to delete
     * @param playerId the ID of the player whose wallet the scannable code should be deleted from
     * @param booleanCallback a callback to be called with a boolean indicating whether the operation was successful
     */
    public void deleteScannableCodeFromWallet(String scannableCodeId, String playerId,
                                         BooleanCallback booleanCallback){
        PlayersConnectionHandler.getInstance().playerScannedCodeDeleted(playerId, scannableCodeId, booleanCallback);
    }
}

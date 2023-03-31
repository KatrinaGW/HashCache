package com.example.hashcache.controllers.hashInfo;


import com.example.hashcache.models.Player;
import com.example.hashcache.models.ScannableCode;
import com.example.hashcache.models.database.Database;
import com.example.hashcache.appContext.AppContext;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

/**
 * The HashController class provides methods for managing scannable codes and their associated hash information.
 */
public class HashController {
    /**
     * Adds a new scannable code to the database and associates it with the current player's wallet.
     *
     * @param qrContent the content of the QR code to add as a scannable code
     * @return a CompletableFuture that completes once the scannable code has been added to the database and the player's wallet
     */
    public static CompletableFuture<Void> addScannableCode(String qrContent){
        CompletableFuture<Void> cf = new CompletableFuture<>();
        CompletableFuture.runAsync(new Runnable() {
            @Override
            public void run() {

                try {
                    // Compute the SHA-256 hash of the QR code content
                    MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
                    messageDigest.update(qrContent.getBytes());
                    byte[] byteArray = messageDigest.digest();
                    String hash = new BigInteger(1, byteArray).toString(16);
                    // Generate hash information for the scannable code and check if it already exists in the database
                    HashInfoGenerator.generateHashInfo(byteArray).thenAccept(hashInfo -> {
                        Database.getInstance().scannableCodeExists(hash).thenAccept(exists -> {
                            String userId = AppContext.get().getCurrentPlayer().getUserId();
                            ScannableCode sc = new ScannableCode(hash, hashInfo);
                            // If the scannable code already exists in the database, add it to the player's wallet
                            if(exists){
                                Database.getInstance().scannableCodeExistsOnPlayerWallet(userId, hash).thenAccept(scanExistsOnPlayer -> {
                                    if(!scanExistsOnPlayer){
                                        addScannableCodeToPlayer(hash, userId, cf, sc);
                                    }
                                    else{
                                        cf.completeExceptionally(new HashExceptions.AlreadyHasCodeEx("QR code already on your wallet!"));
                                    }
                                });

                            }
                            // Otherwise, add it to the database and the player's wallet
                            else{
                                Database.getInstance().addScannableCode(sc).thenAccept(id -> {
                                    addScannableCodeToPlayer(hash, userId, cf, sc);
                                }).exceptionally(throwable -> {
                                    cf.completeExceptionally(throwable);
                                    return null;
                                });;
                            }
                        }).exceptionally(throwable -> {
                            cf.completeExceptionally(throwable);
                            return null;
                        });;

                    }).exceptionally(throwable -> {
                        cf.completeExceptionally(throwable);
                        return null;
                    });


                } catch (NoSuchAlgorithmException e) {
                    e.printStackTrace();
                    cf.completeExceptionally(e);
                }
            }
        });
        return cf;
    }
    /**
     * Adds a scannable code to a player's wallet.
     *
     * @param hash the hash of the scannable code to add
     * @param userId the ID of the player whose wallet the scannable code should be added to
     * @param cf a CompletableFuture that completes once the scannable code has been added to the player's wallet
     * @param sc the scannable code to add to the player's wallet
     */
    private static void addScannableCodeToPlayer(String hash, String userId, CompletableFuture<Void> cf, ScannableCode sc) {
        Database.getInstance().addScannableCodeToPlayerWallet(userId, hash).thenAccept(created->{
            // Set the current scannable code to the newly added scannable code
            AppContext.get().setCurrentScannableCode(sc);
            cf.complete(null);
        }).exceptionally(new Function<Throwable, Void>() {
            @Override
            public Void apply(Throwable throwable) {
                cf.completeExceptionally(throwable);
                return null;
            }
        });
    }
    /**
     * Deletes a scannable code from a player's wallet.
     *
     * @param scannableCodeId the ID of the scannable code to delete
     * @param userId the ID of the player whose wallet the scannable code should be deleted from
     * @return a CompletableFuture that completes with a boolean indicating whether the scannable code was deleted successfully
     */
    public static CompletableFuture<Boolean> deleteScannableCodeFromWallet(String scannableCodeId,
                                                                           String userId) {
        CompletableFuture<Boolean> cf = new CompletableFuture<>();
        CompletableFuture.runAsync(new Runnable() {

            @Override
            public void run() {
                // Remove the scannable code from the player's wallet in the database
                Database.getInstance().removeScannableCodeFromWallet(userId, scannableCodeId)
                        .thenAccept(completed -> {
                            // If the scannable code was deleted successfully, update the current player's wallet
                            if(completed){
                        cf.complete(completed);
                        Player currentPlayer = AppContext.get().getCurrentPlayer();
                        // If the deleted scannable code belonged to the current player, remove it from their wallet
                        if(currentPlayer.getUserId() == userId){
                            currentPlayer.getPlayerWallet().deleteScannableCode(scannableCodeId);
                        }
                        // Otherwise, complete exceptionally with an error message
                    }else{
                        cf.completeExceptionally(new Exception("Something went wrong while " +
                                "deleting the scannable code from the wallet"));
                    }
                });
            }
        });

        return cf;
    }
}


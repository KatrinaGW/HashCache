package com.example.hashcache.controllers.hashInfo;


import static com.example.hashcache.controllers.UpdateUserScore.*;

import android.location.Location;
import android.util.Log;

import com.example.hashcache.controllers.LocationController;
import com.example.hashcache.controllers.UpdateUserScore;
import com.example.hashcache.models.CodeMetadata;
import com.example.hashcache.models.Comment;
import com.example.hashcache.models.Player;
import com.example.hashcache.models.PlayerWallet;
import com.example.hashcache.models.ScannableCode;
import com.example.hashcache.models.database.Database;
import com.example.hashcache.appContext.AppContext;
import com.firebase.geofire.GeoLocation;
import com.google.android.gms.internal.location.zzau;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.tasks.Task;
import com.google.rpc.Code;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.function.Function;

/**
 * The HashController class provides methods for managing scannable codes and their associated hash information.
 */
public class HashController {

    private static final String TAG = "HashController";
    /**
     * Adds a new scannable code to the database and associates it with the current player's wallet.
     *
     * @param qrContent the content of the QR code to add as a scannable code
     * @return a CompletableFuture that completes once the scannable code has been added to the database and the player's wallet
     */
    public static CompletableFuture<Void> addScannableCode(String qrContent) {
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
                            if (exists) {
                                Database.getInstance().scannableCodeExistsOnPlayerWallet(userId, hash).thenAccept(scanExistsOnPlayer -> {
                                    if (!scanExistsOnPlayer) {
                                        addScannableCodeToPlayer(hash, userId, cf, sc);
                                    } else {
                                        cf.completeExceptionally(new HashExceptions.AlreadyHasCodeEx("QR code already on your wallet!"));
                                    }
                                });

                            }
                            // Otherwise, add it to the database and the player's wallet
                            else {
                                Database.getInstance().addScannableCode(sc).thenAccept(id -> {
                                    addScannableCodeToPlayer(hash, userId, cf, sc);
                                }).exceptionally(throwable -> {
                                    cf.completeExceptionally(throwable);
                                    return null;
                                });
                                ;
                            }
                        }).exceptionally(throwable -> {
                            cf.completeExceptionally(throwable);
                            return null;
                        });
                        ;

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
    private static void addScannableCodeToPlayer(String hash, String userId, CompletableFuture<Void> cf,
                                                 ScannableCode sc) {

        Log.d(TAG, String.format("Adding scannableCode: %s to userId: %s", hash, userId));
        Database.getInstance().addScannableCodeToPlayerWallet(userId, hash)
                .thenAccept(created->{
            AppContext.get().setCurrentScannableCode(sc);

            PlayerWallet playerWallet = AppContext.get().getCurrentPlayer().getPlayerWallet();

            if(userId.equals(AppContext.get().getCurrentPlayer().getUserId())){
                playerWallet.setQRCount(playerWallet.getQrCount() + 1);
            }



            Log.d(TAG, String.format("Updating scores for userId %s", userId));
            CompletableFuture.runAsync(() -> {
                updateTotalMaxScore()
                        .thenAccept(nullValue -> {
                            Database.getInstance().updatePlayerScores(userId, playerWallet)
                                    .thenAccept(success -> {
                                        Log.d(TAG, String.format("Successfully updated scores for userId %s", userId));
                                        cf.complete(null);
                                    })
                                    .exceptionally(new Function<Throwable, Void>() {
                                        @Override
                                        public Void apply(Throwable throwable) {
                                            cf.completeExceptionally(throwable);
                                            return null;
                                        }
                                    });

                }).exceptionally(new Function<Throwable, Void>() {
                    @Override
                    public Void apply(Throwable throwable) {
                        cf.completeExceptionally(throwable);
                        return null;
                    }
                });
            });
                });
    }
    /**
     * Deletes a scannable code from a player's wallet.
     *
     * @param scannableCodeId the ID of the scannable code to delete
     * @param userId the ID of the player whose wallet the scannable code should be deleted from
     * @return a CompletableFuture that completes with a boolean indicating whether the scannable code was deleted successfully
     */

    public static CompletableFuture<CodeMetadata> initCodeMetadata(String scannableCodeId, String userId, FusedLocationProviderClient fusedLocationProviderClient){
        CompletableFuture<CodeMetadata> cf = new CompletableFuture<>();
        CompletableFuture.runAsync(new Runnable() {
            @Override
            public void run() {
                boolean recordGeo = AppContext.get().getCurrentPlayer().getPlayerPreferences().getRecordGeolocationPreference();
                if(recordGeo){
                    Log.d("Geo", "Trying to get Geolocation");
                    LocationController.getLastLocation(fusedLocationProviderClient).thenAccept(itemLocation -> {
                        Log.d("Geo", "Got location.");
                        GeoLocation geoLoc = new GeoLocation(itemLocation.getLatitude(), itemLocation.getLongitude());
                        CodeMetadata cm = new CodeMetadata(scannableCodeId, userId, geoLoc);
                        cf.complete(cm);
                    });
                }
                else{
                    CodeMetadata cm = new CodeMetadata(scannableCodeId, userId);
                    cf.complete(cm);

                }
            }
        });
        return cf;
    }
    public static CompletableFuture<Boolean> deleteScannableCodeFromWallet(String scannableCodeId,
                                                                           String userId) {
        CompletableFuture<Boolean> cf = new CompletableFuture<>();
        PlayerWallet playerWallet = AppContext.get().getCurrentPlayer().getPlayerWallet();

        CompletableFuture.runAsync(new Runnable() {

            @Override
            public void run() {
                // Remove the scannable code from the player's wallet in the database
                Database.getInstance().removeScannableCodeFromWallet(userId, scannableCodeId)
                        .thenAccept(completed -> {
                            // If the scannable code was deleted successfully, update the current player's wallet
                            if(completed){
                                Player currentPlayer = AppContext.get().getCurrentPlayer();
                                // If the deleted scannable code belonged to the current player, remove it from their wallet
                                if(currentPlayer.getUserId().equals(userId)){
                                    currentPlayer.getPlayerWallet().setQRCount(currentPlayer.getPlayerWallet().getQrCount() - 1);
                                }

                                CompletableFuture.runAsync(() -> {
                                    updateTotalMaxScore()
                                            .thenAccept(nullValue -> {

                                                CompletableFuture.runAsync(() -> {
                                                    Database.getInstance().updatePlayerScores(userId,
                                                                    AppContext.get().getCurrentPlayer().getPlayerWallet())
                                                            .thenAccept(success -> {
                                                                if (success) {
                                                                    cf.complete(completed);
                                                                } else {
                                                                    cf.completeExceptionally(new Exception("Something " +
                                                                            "went wrong while updating the scores"));
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
                                            })
                                            .exceptionally(new Function<Throwable, Void>() {
                                                @Override
                                                public Void apply(Throwable throwable) {
                                                    cf.completeExceptionally(throwable);
                                                    return null;
                                                }
                                            });
                                });
                        // Otherwise, complete exceptionally with an error message
                            }else{
                                cf.completeExceptionally(new Exception("Something went wrong while " +
                                        "deleting the scannable code from the wallet"));
                            }
                })
                .exceptionally(new Function<Throwable, Void>() {
                    @Override
                    public Void apply(Throwable throwable) {
                        cf.completeExceptionally(throwable);
                        return null;
                    }
                });
            }
        });

        return cf;
    }

    private static CompletableFuture<Void> updateTotalMaxScore(){
        CompletableFuture<Void> cf = new CompletableFuture<>();
        Database.getInstance()
                .getPlayerWalletTotalScore(AppContext.get().getCurrentPlayer().getPlayerWallet()
                        .getScannedCodeIds())
                .thenAccept(totalScore -> {
                    AppContext.get().getCurrentPlayer().getPlayerWallet().setTotalScore(totalScore);

                    CompletableFuture.runAsync(() -> {
                        Database.getInstance().getPlayerWalletTopScore(
                                        AppContext.get().getCurrentPlayer().getPlayerWallet().getScannedCodeIds()
                                )
                                .thenAccept(topScore -> {
                                    long maxScore = 0;
                                    if(topScore != null){
                                        maxScore = topScore.getHashInfo().getGeneratedScore();
                                    }
                                    AppContext.get().getCurrentPlayer().getPlayerWallet().setMaxScore(
                                            maxScore);
                                    cf.complete(null);

                                })
                                .exceptionally(new Function<Throwable, Void>() {
                                    @Override
                                    public Void apply(Throwable throwable) {
                                        cf.completeExceptionally(throwable);
                                        return null;
                                    }
                                });
                    });
                })
                .exceptionally(new Function<Throwable, Void>() {
                    @Override
                    public Void apply(Throwable throwable) {
                        cf.completeExceptionally(throwable);
                        return null;
                    }
                });

        return cf;
    }
}


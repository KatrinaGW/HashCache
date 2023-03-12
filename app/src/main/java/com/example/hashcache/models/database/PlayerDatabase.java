package com.example.hashcache.models.database;

import com.example.hashcache.models.Comment;
import com.example.hashcache.models.ContactInfo;
import com.example.hashcache.models.Player;
import com.example.hashcache.models.PlayerPreferences;
import com.example.hashcache.models.ScannableCode;
import com.example.hashcache.models.database.IPlayerDatabase;
import com.example.hashcache.models.database_connections.PlayerWalletConnectionHandler;
import com.example.hashcache.models.database_connections.PlayersConnectionHandler;
import com.example.hashcache.models.database_connections.ScannableCodesConnectionHandler;
import com.example.hashcache.models.database_connections.callbacks.BooleanCallback;
import com.example.hashcache.models.database_connections.callbacks.GetPlayerCallback;
import com.example.hashcache.models.database_connections.callbacks.GetStringCallback;
import com.example.hashcache.store.AppStore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

/**
 * 
 * The PlayerDatabase class represents a database of players that can be
 * interacted with.
 * It implements the IPlayerDatabase interface which defines the methods that
 * can be performed on the database.
 */
public class PlayerDatabase implements IPlayerDatabase {
    /**
     * Singleton instance of the PlayerDatabase class.
     */
    private static PlayerDatabase instance;
    /**
     * HashMap that contains all the players in the database.
     */
    private HashMap<String, Player> players;
    /**
     * HashMap that maps usernames to userIds.
     */
    private HashMap<String, String> userNameToIdMapper;
    /**
     * HashMap that contains all the scannable codes in the database.
     */
    private HashMap<String, ScannableCode> scannableCodeHashMap;

    /**
     * Checks if a username already exists in the database.
     *
     * @param username the username to check
     * @return a CompletableFuture that will return true if the username already
     *         exists, false otherwise
     */
    @Override
    public CompletableFuture<Boolean> usernameExists(String username) {
        return PlayersConnectionHandler.getInstance().usernameExists(username);

    }

    /**
     * Gets the userId associated with a given username.
     *
     * @param username the username to get the userId for
     * @return a CompletableFuture that will return the userId associated with the
     *         given username
     *         if it exists, or an exception if the username does not exist in the
     *         database
     */
    @Override
    public CompletableFuture<String> getIdByUsername(String username) {

        return PlayersConnectionHandler.getInstance().getPlayerIdByUsername(username);
    }

    @Override
    public CompletableFuture<Void> createPlayer(String username) {

        CompletableFuture<Void> cf = new CompletableFuture<>();

        CompletableFuture.runAsync(() -> {
            usernameExists(username).thenAccept(exists -> {
                if (!exists) {
                    PlayersConnectionHandler.getInstance().createPlayer(username, new GetStringCallback() {
                        @Override
                        public void onCallback(String callbackString) {
                            cf.complete(null);
                        }
                    });
                } else {
                    cf.completeExceptionally(new Exception("Username already exists"));
                }
            });
        });
        return cf;
    }

    /**
     * Get all the Scannable Codes whose ids are in a given list
     * @param scannableCodeIds the list of ids of scannable codes to get
     * @return cf the CompleteableFuture with the list of ScannableCodes
     */
    public CompletableFuture<ArrayList<ScannableCode>> getScannableCodesByIdInList(ArrayList<String> scannableCodeIds){
        CompletableFuture<ArrayList<ScannableCode>> cf = new CompletableFuture<>();
        CompletableFuture.runAsync(() -> {
            ScannableCodesConnectionHandler.getInstance()
                    .getScannableCodesByIdInList(scannableCodeIds).thenAccept(scannableCodes -> {
                cf.complete(scannableCodes);
            }).exceptionally(new Function<Throwable, Void>() {
                @Override
                public Void apply(Throwable throwable) {
                    System.out.println("There was an error getting the scannableCodes.");
                    cf.completeExceptionally(throwable);
                    return null;
                }
            });

        });
        return cf;
    }

    /**
     * Gets the player object associated with a given userId.
     *
     * @param userId the userId to get the player object for
     * @return a CompletableFuture that will return the player object associated
     *         with the given userId
     *         if it exists, or an exception if the userId does not exist in the
     *         database
     */

    @Override
    public CompletableFuture<Player> getPlayer(String userId) {
        CompletableFuture<Player> cf = new CompletableFuture<>();
        CompletableFuture.runAsync(() -> {
            PlayersConnectionHandler.getInstance().getPlayerAsync(userId).thenAccept(playa -> {
                cf.complete(playa);
            }).exceptionally(new Function<Throwable, Void>() {
                @Override
                public Void apply(Throwable throwable) {
                    System.out.println("There was an error getting the player.");
                    cf.completeExceptionally(throwable);
                    return null;
                }
            });

        });
        return cf;
    }

    /**
     * Gets a HashMap of all the players in the database.
     *
     * @return a CompletableFuture that will return a HashMap of all the players in
     *         the database
     */

    @Override
    public CompletableFuture<HashMap<String, String>> getPlayers() {
        return PlayersConnectionHandler.getInstance().getPlayers();
    }

    /**
     * Gets the total score for a given player.
     *
     * @param userId the userId to get the total score for
     * @return a CompletableFuture that will return the total score for the given
     *         player
     */
    @Override
    public CompletableFuture<Integer> getTotalScore(String userId) {
        CompletableFuture<Integer> cf = new CompletableFuture<>();
        CompletableFuture.runAsync(() -> {
            if (players.containsKey(userId)) {
                Player p = players.get(userId);
                PlayerWalletConnectionHandler.getInstance()
                        .getPlayerWalletTotalScore(p.getPlayerWallet().getScannedCodeIds())
                        .thenAccept(totalScore -> {
                            cf.complete(totalScore);
                        })
                        .exceptionally(new Function<Throwable, Void>() {
                            @Override
                            public Void apply(Throwable throwable) {
                                cf.completeExceptionally(throwable);
                                return null;
                            }
                        });
            } else {
                cf.completeExceptionally(new Exception("UserId does not exist."));
            }
        });
        return cf;
    }

    /**
     * Adds a comment to a scannable code.
     *
     * @param scannableCodeId the scannable code to add the comment to
     * @param comment         the comment to add
     * @return a CompletableFuture that will complete successfully if the comment
     *         was added successfully,
     *         or an exception if the scannable code does not exist in the database
     */
    @Override
    public CompletableFuture<Void> addComment(String scannableCodeId, Comment comment) {
        CompletableFuture<Void> cf = new CompletableFuture<>();
        CompletableFuture.runAsync(() -> {
            ScannableCodesConnectionHandler.getInstance().addComment(scannableCodeId, comment, new BooleanCallback() {
                @Override
                public void onCallback(Boolean isTrue) {
                    if (isTrue) {
                        cf.complete(null);
                    } else {
                        cf.completeExceptionally(new Exception("Could not add comment."));
                    }
                }
            });
        });
        return cf;
    }

    /**
     * Updates the player preferences for a given user.
     *
     * @param userId            the userId to update the preferences for
     * @param playerPreferences the updated player preferences
     * @return a CompletableFuture that will complete successfully if the player
     *         preferences were updated successfully,
     *         or an exception if the userId does not exist in the database
     */
    @Override
    public CompletableFuture<Void> updatePlayerPreferences(String userId, PlayerPreferences playerPreferences) {
        CompletableFuture<Void> cf = new CompletableFuture<>();
        CompletableFuture.runAsync(() -> {
            PlayersConnectionHandler.getInstance().updatePlayerPreferences(userId, playerPreferences,
                    new BooleanCallback() {
                        @Override
                        public void onCallback(Boolean isTrue) {
                            if (isTrue) {
                                cf.complete(null);
                            } else {
                                cf.completeExceptionally(new Exception("Could not update player preferences"));
                            }
                        }
                    });
        });
        return cf;
    }

    /**
     * Updates the contact info for a given user.
     *
     * @param userId      the userId to update the contact info for
     * @param contactInfo the updated contact info
     * @return a CompletableFuture that will complete successfully if the contact
     *         info was updated successfully,
     *         or an exception if the userId does not exist in the database
     */
    @Override
    public CompletableFuture<Void> updateContactInfo(String userId, ContactInfo contactInfo) {
        CompletableFuture<Void> cf = new CompletableFuture<>();
        CompletableFuture.runAsync(() -> {
            PlayersConnectionHandler.getInstance().updateContactInfo(userId, contactInfo, new BooleanCallback() {
                @Override
                public void onCallback(Boolean isTrue) {
                    if (isTrue) {
                        cf.complete(null);
                    } else {
                        cf.completeExceptionally(new Exception("Could not update contact info"));
                    }
                }
            });
        });
        return cf;
    }

    @Override
    public CompletableFuture<Void> addScannableCodeToPlayerWallet(String userId, String scannableCodeId) {
        System.out.println("[[ Trying to add to wallet...");
        CompletableFuture<Void> cf = new CompletableFuture<>();
        CompletableFuture.runAsync(() -> {
           PlayersConnectionHandler.getInstance().playerScannedCodeAdded(userId, scannableCodeId, null, new BooleanCallback() {
               @Override
               public void onCallback(Boolean isTrue) {
                   System.out.println("[[ Got response!!!");
                   if(isTrue){
                       cf.complete(null);
                   }
                   else{
                       cf.completeExceptionally(new Exception("Could not add scannable to player wallet, userId: " + userId + " codeId " + scannableCodeId));
                   }
               }
           });
        }).exceptionally(new Function<Throwable, Void>() {
            @Override
            public Void apply(Throwable throwable) {
                cf.completeExceptionally(throwable);
                return null;
            }
        });
        return cf;
    }

    @Override
    public CompletableFuture<Boolean> scannableCodeExists(String scannableCodeId) {
        return ScannableCodesConnectionHandler.getInstance().scannableCodeIdExists(scannableCodeId);
    }

    /**
     * Adds a scannable code to database
     * 
     * @param scannableCode the scannable code to add
     * @return a CompletableFuture that will complete successfully if the scannable
     *         code was added successfully,
     */
    @Override
    public CompletableFuture<String> addScannableCode(ScannableCode scannableCode) {
        CompletableFuture<String> cf = new CompletableFuture<>();
        CompletableFuture.runAsync(() -> {
            ScannableCodesConnectionHandler.getInstance().addScannableCode(scannableCode, new BooleanCallback() {
                @Override
                public void onCallback(Boolean isTrue) {
                    if(isTrue){
                        cf.complete(scannableCode.getScannableCodeId());
                    }
                    else{
                        cf.completeExceptionally(new Exception("Could not add ScannableCode ID: " + scannableCode.getScannableCodeId()));
                    }
                }
            });
        });
        return cf;
    }

    /**
     * Removes a scannable code from a given user's wallet.
     *
     * @param userId          the userId to remove the scannable code from
     * @param scannableCodeId the scannable code to remove
     * @return a CompletableFuture that will complete successfully if the scannable
     *         code was removed successfully,
     *         or an exception if the userId does not exist in the database
     */
    @Override
    public CompletableFuture<Void> removeScannableCode(String userId, String scannableCodeId) {
        CompletableFuture<Void> cf = new CompletableFuture<>();
        CompletableFuture.runAsync(() -> {
            if (players.containsKey(userId)) {
                Player p = players.get(userId);
                String scanId = scannableCodeId;
                ScannableCode scannableCode = scannableCodeHashMap.get(scanId);
                scannableCodeHashMap.remove(scanId);
                p.getPlayerWallet().deleteScannableCode(scannableCode.getScannableCodeId(),
                        scannableCode.getHashInfo().getGeneratedScore());
                cf.complete(null);
            } else {
                cf.completeExceptionally(new Exception("UserId does not exist."));
            }
        });
        return cf;
    }

    /**
     * Changes the username for a given user.
     *
     * @param userId      the userId to change the username for
     * @param newUsername the new username to set
     * @return a CompletableFuture that will complete successfully if the username
     *         was changed successfully,
     *         or an exception if the userId does not exist in the database
     */
    @Override
    public CompletableFuture<Void> changeUserName(String userId, String newUsername) {
        CompletableFuture<Void> cf = new CompletableFuture<>();
        CompletableFuture.runAsync(() -> {
            if (players.containsKey(userId)) {
                Player p = players.get(userId);
                String oldUserName = p.getUsername();
                userNameToIdMapper.remove(oldUserName);
                userNameToIdMapper.put(newUsername, p.getUserId());
                p.updateUserName(newUsername);
                cf.complete(null);
            } else {
                cf.completeExceptionally(new Exception("UserId does not exist."));
            }
        });
        return cf;
    }

    /**
     * Get the player's highest scoring QR code
     * @param scannableCodeIds the scannableIds to find the highest scoring scannableId
     * @return cf the CompletableFuture with the QR Stats
     */
    @Override
    public CompletableFuture<ScannableCode> getPlayerWalletTopScore(ArrayList<String> scannableCodeIds){
        CompletableFuture<ScannableCode> cf = new CompletableFuture<>();
        CompletableFuture.runAsync(() -> {
        PlayerWalletConnectionHandler.getInstance()
                .getPlayerWalletTopScore(scannableCodeIds)
                .thenAccept(topScore -> {
                    cf.complete(topScore);
                })
                .exceptionally(new Function<Throwable, Void>() {
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
     * Get the player's lowest scoring QR code
     * @param scannableCodeIds the scannableIds to find the lowest scoring scannableId
     * @return cf the CompletableFuture with the QR Stats
     */
    @Override
    public CompletableFuture<ScannableCode> getPlayerWalletLowScore(ArrayList<String> scannableCodeIds){
        CompletableFuture<ScannableCode> cf = new CompletableFuture<>();
        CompletableFuture.runAsync(() -> {
            PlayerWalletConnectionHandler.getInstance()
                    .getPlayerWalletLowScore(scannableCodeIds)
                    .thenAccept(lowScore -> {
                        cf.complete(lowScore);
                    })
                    .exceptionally(new Function<Throwable, Void>() {
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

package com.example.hashcache.models.database;

import java.util.Observable;

import com.example.hashcache.models.Comment;
import com.example.hashcache.models.ContactInfo;
import com.example.hashcache.models.Player;
import com.example.hashcache.models.PlayerPreferences;
import com.example.hashcache.models.ScannableCode;
import com.example.hashcache.models.database.DatabaseAdapters.PlayerWalletConnectionHandler;
import com.example.hashcache.models.database.DatabaseAdapters.ScannableCodesDatabaseAdapter;
import com.example.hashcache.models.database.DatabaseAdapters.callbacks.BooleanCallback;
import com.example.hashcache.models.database.DatabaseAdapters.PlayersDatabaseAdapter;
import com.example.hashcache.models.database.DatabaseAdapters.callbacks.GetPlayerCallback;
import com.google.firebase.firestore.ListenerRegistration;

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
public class DatabaseAdapter extends Observable implements DatabasePort {
    /**
     * Singleton instance of the PlayerDatabase class.
     */
    private static DatabaseAdapter instance;
    /**
     * HashMap that contains all the players in the database.
     */
    private HashMap<String, Player> players;
    /**
     * HashMap that maps usernames to userIds.
     */
    private HashMap<String, String> userNameToIdMapper;

    private ListenerRegistration playerListener;
    private ListenerRegistration walletListener;

    /**
     * Checks if a username already exists in the database.
     *
     * @param username the username to check
     * @return a CompletableFuture that will return true if the username already
     *         exists, false otherwise
     */
    @Override
    public CompletableFuture<Boolean> usernameExists(String username) {
        return PlayersDatabaseAdapter.getInstance().usernameExists(username);

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

        return PlayersDatabaseAdapter.getInstance().getPlayerIdByUsername(username);
    }

    /**
     * Create a new player with the given username
     * @param username the username to use for the new player
     * @return cf the CompletableFuture that completes once the user is created
     */
    @Override
    public CompletableFuture<Void> createPlayer(String username) {

        CompletableFuture<Void> cf = new CompletableFuture<>();

        CompletableFuture.runAsync(() -> {
            usernameExists(username).thenAccept(exists -> {
                if (!exists) {
                    PlayersDatabaseAdapter.getInstance().createPlayer(username)
                                    .thenAccept(userId -> {
                                        cf.complete(null);
                                    }).exceptionally(new Function<Throwable, Void>() {
                                @Override
                                public Void apply(Throwable throwable) {
                                    System.out.println("There was an error getting the scannableCodes.");
                                    cf.completeExceptionally(throwable);
                                    return null;
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
     * 
     * @param scannableCodeIds the list of ids of scannable codes to get
     * @return cf the CompleteableFuture with the list of ScannableCodes
     */
    public CompletableFuture<ArrayList<ScannableCode>> getScannableCodesByIdInList(ArrayList<String> scannableCodeIds) {
        CompletableFuture<ArrayList<ScannableCode>> cf = new CompletableFuture<>();
        CompletableFuture.runAsync(() -> {
            ScannableCodesDatabaseAdapter.getInstance()
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
            PlayersDatabaseAdapter.getInstance().getPlayer(userId).thenAccept(playa -> {
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
        CompletableFuture<HashMap<String, String>> cf = new CompletableFuture<>();

        CompletableFuture.runAsync(() -> {
            PlayersDatabaseAdapter.getInstance().getPlayers().thenAccept(
                    players -> {
                        cf.complete(players);
                    });
        });

        return cf;
    }

    /**
     * Gets the total score for a given player.
     *
     * @param userId the userId to get the total score for
     * @return a CompletableFuture that will return the total score for the given
     *         player
     */
    @Override
    public CompletableFuture<Long> getTotalScore(String userId) {
        CompletableFuture<Long> cf = new CompletableFuture<>();
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
            ScannableCodesDatabaseAdapter.getInstance().addComment(scannableCodeId, comment)
                            .thenAccept(success -> {
                                cf.complete(null);
                            }).exceptionally(new Function<Throwable, Void>() {
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
            PlayersDatabaseAdapter.getInstance().updatePlayerPreferences(userId, playerPreferences)
                            .thenAccept(success -> {
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
        return cf;
    }

    /**
     * Add a scannableCode to the player's wallet
     * @param userId the id of the user whose wallet will have the scannable code added to it
     * @param scannableCodeId the id of the scannable code to add
     * @return cf the CompleteableFuture which returns once the operation has been completed
     */
    @Override
    public CompletableFuture<Void> addScannableCodeToPlayerWallet(String userId, String scannableCodeId) {
        System.out.println("[[ Trying to add to wallet...");
        CompletableFuture<Void> cf = new CompletableFuture<>();
        CompletableFuture.runAsync(() -> {
            PlayersDatabaseAdapter.getInstance().playerScannedCodeAdded(userId, scannableCodeId, null)
                            .thenAccept(success -> {
                                cf.complete(null);
                            }).exceptionally(new Function<Throwable, Void>() {
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
        return cf;
    }

    /**
     * Check if a scannableCode already exists in a player's wallet
     * @param userId the id of the user whose wallet needs to be checked for the scannable code
     * @param scannableCodeId the id of the scannable code to check for
     * @return cf the CompletableFuture with a boolean value indicating if the scannable code already
     *          exists in the wallet or not
     */
    @Override
    public CompletableFuture<Boolean> scannableCodeExistsOnPlayerWallet(String userId, String scannableCodeId) {
        return PlayerWalletConnectionHandler.getInstance().scannableCodeExistsOnPlayerWallet(userId, scannableCodeId);
    }

    /**
     * Check if the scannableCode already exists in the database
     * @param scannableCodeId the scannable code to check for
     * @return cf the CompletableFuture with a boolean value indicating if the scananble
     *          code already exists or not
     */
    @Override
    public CompletableFuture<Boolean> scannableCodeExists(String scannableCodeId) {
        return ScannableCodesDatabaseAdapter.getInstance().scannableCodeIdExists(scannableCodeId);
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
            ScannableCodesDatabaseAdapter.getInstance().addScannableCode(scannableCode)
                    .thenAccept(scannableCodeId -> {
                        cf.complete(scannableCode.getScannableCodeId());
                    }).exceptionally(new Function<Throwable, Void>() {
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
     * Removes a scannable code from a given user's wallet.
     *
     * @param userId          the userId to remove the scannable code from
     * @param scannableCodeId the scannable code to remove
     * @return a CompletableFuture that will complete successfully if the scannable
     *         code was removed successfully,
     *         or an exception if the userId does not exist in the database
     */
    @Override
    public CompletableFuture<Boolean> removeScannableCodeFromWallet(String userId, String scannableCodeId) {
        CompletableFuture<Boolean> cf = new CompletableFuture<>();
        CompletableFuture.runAsync(() -> {
            PlayersDatabaseAdapter.getInstance().playerScannedCodeDeleted(userId, scannableCodeId)
                            .thenAccept(success -> {
                                cf.complete(success);
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
     * Update a user's contact information
     * 
     * @param contactInfo the contact information to set for the user
     * @param userId      the id of the user to update the contact information of
     * @return cf the CompleteableFuture with a boolean value indicating if it was
     *         successful
     */
    @Override
    public CompletableFuture<Boolean> updateContactInfo(ContactInfo contactInfo, String userId) {
        CompletableFuture<Boolean> cf = new CompletableFuture<>();

        CompletableFuture.runAsync(() -> {
            PlayersDatabaseAdapter.getInstance().updateContactInfo(userId, contactInfo)
                            .thenAccept(success -> {
                                cf.complete(true);
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
     * Called when the player's data changes
     * @param playerId the id of the player whose data changed
     * @param callback the callback function which will be called with the player hwose data changed
     */
    @Override
    public void onPlayerDataChanged(String playerId, GetPlayerCallback callback) {
        playerListener = PlayersDatabaseAdapter.getInstance().setupPlayerListener(playerId, callback);
    }

    /**
     * Called when a player's wallet changes
     * @param playerId the id of the player whose wallet changed
     * @param callback the callback to call once the changes have been processed
     */
    @Override
    public void onPlayerWalletChanged(String playerId, BooleanCallback callback) {
        walletListener = PlayerWalletConnectionHandler.getInstance().getPlayerWalletChangeListener(playerId, callback);
    }

    /**
     * Deletes a comment from a scananble code
     * @param scannableCodeId the id of the scannable code to delete the comment from
     * @param commentId the id of the comment to delete
     * @return cf the CompletableFuture with a boolean value indicating if the operation was successful
     * or not
     */
    @Override
    public CompletableFuture<Boolean> deleteComment(String scannableCodeId, String commentId){
        CompletableFuture<Boolean> cf = ScannableCodesDatabaseAdapter.getInstance().deleteComment(
                scannableCodeId, commentId
        );
        return cf;
    }

    ;

    /**
     * Get the player's highest scoring QR code
     * 
     * @param scannableCodeIds the scannableIds to find the highest scoring
     *                         scannableId
     * @return cf the CompletableFuture with the QR Stats
     */
    @Override
    public CompletableFuture<ScannableCode> getPlayerWalletTopScore(ArrayList<String> scannableCodeIds) {
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
     * 
     * @param scannableCodeIds the scannableIds to find the lowest scoring
     *                         scannableId
     * @return cf the CompletableFuture with the QR Stats
     */
    @Override
    public CompletableFuture<ScannableCode> getPlayerWalletLowScore(ArrayList<String> scannableCodeIds) {
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

    /**
     * Gets the total score of all scannableCodeIds in a list
     * 
     * @param scannableCodeIds the ids of codes to sum
     * @return cf the CompletableFuture that contains the total score
     */
    @Override
    public CompletableFuture<Long> getPlayerWalletTotalScore(ArrayList<String> scannableCodeIds) {
        CompletableFuture<Long> cf = new CompletableFuture<>();
        CompletableFuture.runAsync(() -> {
            PlayerWalletConnectionHandler.getInstance()
                    .getPlayerWalletTotalScore(scannableCodeIds)
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
        });
        return cf;
    }

    /**
     * Gets a scannable code from the database with a specific id
     *
     * @param scannableCodeId the id of the scannable code to get
     * @return cf the CompleteableFuture with the found scannableCode
     */
    @Override
    public CompletableFuture<ScannableCode> getScannableCodeById(String scannableCodeId) {
        CompletableFuture<ScannableCode> cf = new CompletableFuture<>();
        CompletableFuture.runAsync(() -> {
            ScannableCodesDatabaseAdapter.getInstance().getScannableCode(scannableCodeId)
                    .thenAccept(scannableCode -> {
                        cf.complete(scannableCode);
                    }).exceptionally(new Function<Throwable, Void>() {
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

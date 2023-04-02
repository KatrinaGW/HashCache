package com.example.hashcache.models.database;

import android.content.pm.ComponentInfo;

import java.util.Comparator;
import java.util.HashSet;
import java.lang.reflect.Array;
import android.util.Log;
import com.example.hashcache.models.CodeMetadata;
import android.location.Location;
import android.util.Pair;

import androidx.annotation.NonNull;

import java.lang.reflect.Field;
import java.util.Observable;

import com.example.hashcache.models.Comment;
import com.example.hashcache.models.ContactInfo;
import com.example.hashcache.models.Player;
import com.example.hashcache.models.PlayerPreferences;
import com.example.hashcache.models.PlayerWallet;
import com.example.hashcache.models.ScannableCode;
import com.example.hashcache.models.database.DatabaseAdapters.CodeLocationDatabaseAdapter;
import com.example.hashcache.models.database.DatabaseAdapters.CodeMetadataDatabaseAdapter;
import com.example.hashcache.models.database.DatabaseAdapters.FireStoreHelper;
import com.example.hashcache.models.database.DatabaseAdapters.LoginsAdapter;
import com.example.hashcache.models.database.DatabaseAdapters.PlayerWalletDatabaseAdapter;
import com.example.hashcache.models.database.DatabaseAdapters.ScannableCodesDatabaseAdapter;
import com.example.hashcache.models.database.DatabaseAdapters.callbacks.BooleanCallback;
import com.example.hashcache.models.database.DatabaseAdapters.PlayersDatabaseAdapter;
import com.example.hashcache.models.database.DatabaseAdapters.callbacks.GetPlayerCallback;
import com.example.hashcache.models.database.DatabaseAdapters.callbacks.GetScannableCodeCallback;

import com.example.hashcache.models.database.values.CollectionNames;
import com.firebase.geofire.GeoLocation;
import com.example.hashcache.models.database.values.FieldNames;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.checkerframework.checker.units.qual.A;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

import kotlin.Triple;

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
    private ListenerRegistration scannableCodeListener;

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
                PlayerWalletDatabaseAdapter.getInstance()
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
        return PlayerWalletDatabaseAdapter.getInstance().scannableCodeExistsOnPlayerWallet(userId, scannableCodeId);
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
        walletListener = PlayerWalletDatabaseAdapter.getInstance().getPlayerWalletChangeListener(playerId, callback);
    }

    /**
     * Called when a scannableCodeComment changes
     * @param scannableCodeId the id of the scannable code that changed
     * @param callback the callback to call once the changes have been processed
     */
    @Override
    public void onScannableCodeCommentsChanged(String scannableCodeId, GetScannableCodeCallback callback){
        scannableCodeListener = ScannableCodesDatabaseAdapter.getInstance().setUpScannableCodeCommentsListener(
                scannableCodeId, callback
        );
    }

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
            PlayerWalletDatabaseAdapter.getInstance()
                    .getPlayerWalletTopScore(scannableCodeIds, this)
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
            PlayerWalletDatabaseAdapter.getInstance()
                    .getPlayerWalletLowScore(scannableCodeIds, this)
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
            PlayerWalletDatabaseAdapter.getInstance()
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

    /**
     * Gets the username of a player with a given userid
     * @param userId the userid of the player whose username is needed
     * @return cf the CompletableFuture with the specified user's username paired with their id
     */
    @Override
    public CompletableFuture<Pair<String, String>> getUsernameById(String userId){
        return PlayersDatabaseAdapter.getInstance().getUsernameById(userId);
    }

    /**
     * Get all the usernames for the users in a given list of ids
     * @param userIds the ids of the users whose usernames are wanted
     * @return the completableFuture with the usernames and userIds of the
     * specified users
     */
    @Override
    public CompletableFuture<ArrayList<Pair<String, String>>> getUsernamesByIds(ArrayList<String> userIds){
        return PlayersDatabaseAdapter.getInstance().getUsernamesByIds(userIds);
    }
    
    /**
     * Gets the number of players with the specified scananble code id in their wallets
     * @param scannableCodeId the id of the scannable code to look for
     * @return a completableFuture with the number of players who have the scannablecode
     */
    @Override
    public CompletableFuture<Integer> getNumPlayersWithScannableCode(String scannableCodeId){
        CompletableFuture<Integer> cf = new CompletableFuture<>();

        CompletableFuture.runAsync(() -> {
            PlayersDatabaseAdapter.getInstance().getNumPlayersWithScannableCode(scannableCodeId)
                    .thenAccept(count -> {
                        cf.complete(count);
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
     * Returns the user id of the top k players with the given filter. Given a list containing user
     * names and the score of the user (determined by filter)
     * @param filter the filter you were to sort by
     * @return A array list contains pairs of strings, long and strings. Which correspond to username,
     * score and user Id
     */
    @Override
    public CompletableFuture<ArrayList<Triple<String, Long, String>>> getTopUsers(String filter) {
        ArrayList<Triple<String, Long, String>> list = new ArrayList<>();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference collectionReference = db.collection(CollectionNames.PLAYERS.collectionName);
        CompletableFuture<ArrayList<Triple<String, Long, String>>> cf = new CompletableFuture<>();

        collectionReference.orderBy(filter, Query.Direction.DESCENDING).get()
                .addOnSuccessListener(result -> {
                    for(QueryDocumentSnapshot document: result) {
                        Triple<String, Long, String> tripple = new Triple<>((String) document.get(FieldNames.USERNAME.fieldName),
                                (Long) document.get(filter), (String) document.getId());
                        list.add(tripple);
                    }
                    cf.complete(list);
                })
                .addOnFailureListener(e -> {
            Log.e("DATABASE", "Error getting top k users");
        });


        return cf;
    }

    /**
     * Sets the userId for the user who has logged in with a specified device. Will overwrite any
     * existing login record for the device
     * @param username the name to use for the login record
     * @return cf the CompletableFuture which completes exceptionally if there was an error in setting
     * up the record
     */
    @Override
    public CompletableFuture<Void> addLoginRecord(String username){
        return LoginsAdapter.getInstance().addLoginRecord(username);
    }

    @Override
    public CompletableFuture<Void> addScannableCodeMetadata(CodeMetadata codeMetadata){
        return CodeMetadataDatabaseAdapter.getInstance().createScannableCodeMetadata(codeMetadata);
    }

    @Override
    public CompletableFuture<ArrayList<CodeMetadata>> getCodeMetadataWithinRadius(GeoLocation location, double radiusMeters) {
        return CodeMetadataDatabaseAdapter.getInstance().getCodeMetadataWithinRadius(location, radiusMeters);
    }

    @Override
    public CompletableFuture<ArrayList<ScannableCode>> getScannableCodesWithinRadius(GeoLocation location, double radiusMeters) {
        CompletableFuture<ArrayList<ScannableCode>> cf = new CompletableFuture<>();
        CompletableFuture.runAsync(new Runnable() {
            @Override
            public void run() {
                CodeMetadataDatabaseAdapter.getInstance().getCodeMetadataWithinRadius(location, radiusMeters).thenAccept(cms -> {
                    HashSet<String> scannableCodeIds = new HashSet<>();
                    cms.forEach(cm -> {
                        scannableCodeIds.add(cm.getScannableCodeId());
                    });
                    ArrayList<String> scannableCodeArray = new ArrayList<>();
                    for(String id: scannableCodeIds){
                        scannableCodeArray.add(id);
                    }
                    getScannableCodesByIdInList(scannableCodeArray).thenAccept(scodes -> {
                        cf.complete(scodes);
                    }).exceptionally(throwable -> {
                        cf.completeExceptionally(throwable);
                        return null;
                    });

                }).exceptionally(throwable -> {
                    cf.completeExceptionally(throwable);
                    return null;
                });
            }
        });
        return cf;
    }

    /**
     * Gets the username to use if the device has had a login before
     * @return cf the CompletableFuture with the username of the associated user. Returns
     * null if there is not a login entry for the specified device
     */
    public CompletableFuture<String> getUsernameForDevice(){
        return LoginsAdapter.getInstance().getUsernameForDevice();
    }

    @Override
    public CompletableFuture<Void> updatePlayerCodeMetadataImage(String userId, String scannableCodeId, String image) {
        return CodeMetadataDatabaseAdapter.getInstance().updatePlayerCodeMetadataImage(userId, scannableCodeId, image);
    }

    @Override
    public CompletableFuture<CodeMetadata> getPlayerCodeMetadataById(String userId, String scannableCodeId) {
        return CodeMetadataDatabaseAdapter.getInstance().getPlayerCodeMetadataById(userId, scannableCodeId);
    }

    /**
     * Remove the login record for the current device
     * @return cf the CompletableFuture that completes exceptionally if the operation caused
     * an error
     */
    public CompletableFuture<Void> deleteLogin(){
        return LoginsAdapter.getInstance().deleteLogin();
    }

    

    /**
     * Resets the static instances of the adapters
     */
    public void resetInstances(){
        LoginsAdapter.resetInstance();
        PlayersDatabaseAdapter.resetInstance();
        ScannableCodesDatabaseAdapter.resetInstance();
        CodeMetadataDatabaseAdapter.resetInstance();
        CodeLocationDatabaseAdapter.resetInstance();
    }

    @Override
    public CompletableFuture<Boolean> updatePlayerScores(String userId, PlayerWallet playerWallet) {


        CompletableFuture<Boolean> cf = new CompletableFuture<>();
        CompletableFuture.runAsync(() -> {
            PlayerWalletDatabaseAdapter.getInstance().updatePlayerScores(userId, playerWallet,
                            new FireStoreHelper())
                    .thenAccept(success -> {
                        if(success) {
                            cf.complete(true);
                        } else {
                            Log.e("DATABASE", "Error adding score");
                        }
                    })
                    .exceptionally(new Function<Throwable, Void>() {
                        @Override
                        public Void apply(Throwable throwable) {
                            Log.e("DATABASE", "Error adding score");
                            cf.completeExceptionally(throwable);
                            return null;
                        }
                    });
        });
        return cf;
    }

    /**
     * Gets the top monster name of a player given a user name
     * @param userId The user Id of the
     * @return
     */
    @Override
    public CompletableFuture<String> getTopMonsterName(String userId) {
        CompletableFuture<String> cf = new CompletableFuture<>();
        PlayersDatabaseAdapter.getInstance().getPlayer(userId).thenAccept(player -> {
            ArrayList<String> scannableCodeIds = player.getPlayerWallet().getScannedCodeIds();
            PlayerWalletDatabaseAdapter.getInstance().getPlayerWalletTopScore(scannableCodeIds, Database.getInstance())
                    .thenAccept(code -> {
                        cf.complete(code.getHashInfo().getGeneratedName());
            });
        });
        return cf;
    }

    /**
     *
     * @return
     */
    @Override
    public CompletableFuture<ArrayList<Pair<String, ScannableCode>>> getScannableCodesWithinRadiusSorted(Location location) {
        CompletableFuture<ArrayList<Pair<String, ScannableCode>>> cf = new CompletableFuture<>();
        Database.getInstance().getCodeMetadataWithinRadius(new GeoLocation(
                        location.getLatitude(), location.getLongitude()), 1000)
                .thenAccept(codes -> {
                    // Get the scanableCode that associate with the scannableCodeId
                    ArrayList<String> scannableCodeIds = new ArrayList<>();
                    ArrayList<String> userIds = new ArrayList<>();
                    for(CodeMetadata metadata: codes) {
                        scannableCodeIds.add(metadata.getScannableCodeId());
                        userIds.add(metadata.getUserId());
                    }

                    Database.getInstance().getScannableCodesByIdInList(scannableCodeIds)
                            .thenAccept(scannableCodes -> {
                                ArrayList<Pair<String, ScannableCode>> user_id_scannable = new ArrayList<>();

                                for(int i = 0; i < scannableCodeIds.size(); i++) {
                                    user_id_scannable.add(new Pair<>(userIds.get(i), scannableCodes.get(i)));
                                }

                                // Sort the scannable codes by score
                                user_id_scannable.sort(new Comparator<Pair<String, ScannableCode>>() {
                                    @Override
                                    public int compare(Pair<String, ScannableCode> o1, Pair<String, ScannableCode> o2) {
                                        return (int)o1.second.getHashInfo().getGeneratedScore() - (int)o2.second.getHashInfo().getGeneratedScore();
                                    }
                                });

                                cf.complete(user_id_scannable);
                            });


                })
                .exceptionally(new Function<Throwable, Void>() {
                    @Override
                    public Void apply(Throwable throwable) {
                        return null;
                    }
                });
        return cf;
    }
}

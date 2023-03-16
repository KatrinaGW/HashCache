package com.example.hashcache.models.data_exchange.database.DatabaseAdapters;

import android.media.Image;
import android.util.Log;

import androidx.annotation.NonNull;

import com.example.hashcache.models.data_exchange.data_adapters.PlayerDataAdapter;
import com.example.hashcache.models.data_exchange.database.DatabaseAdapters.callbacks.BooleanCallback;
import com.example.hashcache.models.data_exchange.database.DatabaseAdapters.callbacks.GetPlayerCallback;
import com.example.hashcache.models.data_exchange.database.DatabaseAdapters.callbacks.GetStringCallback;
import com.example.hashcache.models.data_exchange.database.values.CollectionNames;
import com.example.hashcache.models.data_exchange.database.values.FieldNames;
import com.example.hashcache.models.ContactInfo;
import com.example.hashcache.models.Player;
import com.example.hashcache.models.PlayerPreferences;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

/**
 * Handles all calls to the Firebase Players database
 */
public class PlayersDatabaseAdapter {
    private FirebaseFirestore db;
    private CollectionReference collectionReference;
    private HashMap<String, String> inAppUsernamesIds;
    private HashMap<String, Player> cachedPlayers;
    final String TAG = "Sample";
    private PlayerDataAdapter playerDataAdapter;
    private FireStoreHelper fireStoreHelper;
    private PlayerWalletConnectionHandler playerWalletConnectionHandler;
    private static PlayersDatabaseAdapter INSTANCE;

    /**
     * Creates a new instance of the class and initializes the connection to the
     * database
     * 
     * @param inAppNamesIds                 a mapping of userIds to their usernames
     * @param playerDataAdapter       the instance of the
     *                                      PlayerDocumentConverter class
     *                                      to use to convert documents into Player
     *                                      objects
     * @param fireStoreHelper               the instance of the FireStoreHelper
     *                                      class to use to perform
     *                                      general FireStore actions
     * @param db                            an instance of the Firestore database
     * @param playerWalletConnectionHandler the instance of the
     *                                      PlayerWalletConnectionHandler
     *                                      class to use to interact with a player's
     *                                      wallet collection
     */
    private PlayersDatabaseAdapter(HashMap<String, String> inAppNamesIds,
                                   PlayerDataAdapter playerDataAdapter,
                                   FireStoreHelper fireStoreHelper,
                                   FirebaseFirestore db, PlayerWalletConnectionHandler playerWalletConnectionHandler) {
        this.inAppUsernamesIds = inAppNamesIds;
        this.cachedPlayers = new HashMap<>();
        this.playerDataAdapter = playerDataAdapter;
        this.fireStoreHelper = fireStoreHelper;
        this.playerWalletConnectionHandler = playerWalletConnectionHandler;
        this.db = db;

        collectionReference = db.collection(CollectionNames.PLAYERS.collectionName);
    }

    /**
     * Get the current INSTANCE of the PlayersConnectionHandler class
     * 
     * @return PlayersConnectionHandler.INSTANCE the current instance of the
     *         PlayersConnectionHandler class
     * @throws IllegalArgumentException if the INSTANCE hasn't been initialized yet
     */
    public static PlayersDatabaseAdapter getInstance() {
        if (INSTANCE == null) {
            throw new IllegalArgumentException("No instance of PlayersConnectionHandler currently exists!");
        }

        return INSTANCE;
    }

    /**
     * Create and get the static instance of the PlayersConnectinoHandler class with
     * its dependencies
     * 
     * @param inAppNamesIds                 a mapping of userIds to their usernames
     * @param playerDataAdapter       the instance of the
     *                                      PlayerDocumentConverter class
     *                                      to use to convert documents into Player
     *                                      objects
     * @param fireStoreHelper               the instance of the FireStoreHelper
     *                                      class to use to perform
     *                                      general FireStore actions
     * @param db                            an instance of the Firestore database
     * @param playerWalletConnectionHandler the instance of the
     *                                      PlayerWalletConnectionHandler
     *                                      class to use to interact with a player's
     *                                      wallet collection
     * @return PlayersConnectionHandler.INSTANCE the current instance of the
     *         PlayersConnectionHandler class
     *
     * @throws IllegalArgumentException if the INSTANCE has already been initialized
     */
    public static PlayersDatabaseAdapter makeInstance(HashMap<String, String> inAppNamesIds,
                                                      PlayerDataAdapter playerDataAdapter,
                                                      FireStoreHelper fireStoreHelper,
                                                      FirebaseFirestore db, PlayerWalletConnectionHandler playerWalletConnectionHandler) {
        if (INSTANCE != null) {
            throw new IllegalArgumentException("Instance of PlayersConnectionHandler already exists!");
        }
        INSTANCE = new PlayersDatabaseAdapter(inAppNamesIds, playerDataAdapter,
                fireStoreHelper, db, playerWalletConnectionHandler);
        return INSTANCE;
    }

    /**
     * Resets the static INSTANCE to null.
     * Should only be used for test purposes
     */
    public static void resetInstance() {
        INSTANCE = null;
    }

    /**
     * Gets all of the current usernames across the app
     * 
     * @return inAppPlayerUserNames the usernames of all players in the app
     */
    public ArrayList<String> getInAppPlayerUserNames() {
        ArrayList<String> inAppUserNames = new ArrayList<>();
        for (String username : inAppUsernamesIds.keySet()) {
            inAppUserNames.add(username);
        }
        return inAppUserNames;
    }

    /**
     * Returns a boolean CompletableFuture indicating if the username exists or not.
     *
     * @param username the username to use to pull the player with
     */

    public CompletableFuture<Boolean> usernameExists(String username) {
        CompletableFuture<Boolean> cf = new CompletableFuture<>();
        CompletableFuture.runAsync(() -> {
            Query docRef = collectionReference.whereEqualTo(FieldNames.USERNAME.fieldName, username).limit(1);
            docRef.get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    QuerySnapshot document = task.getResult();
                    cf.complete(!document.isEmpty());
                } else {
                    cf.completeExceptionally(new Exception("[usernameExists] Could not complete query"));
                }
            });
        });
        return cf;
    }

    public CompletableFuture<String> getPlayerIdByUsername(String username) {
        CompletableFuture<String> cf = new CompletableFuture<>();
        CompletableFuture.runAsync(() -> {
            Query docRef = collectionReference.whereEqualTo(FieldNames.USERNAME.fieldName, username).limit(1);
            docRef.get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    QuerySnapshot document = task.getResult();
                    if (!document.isEmpty()) {
                        String userId = "";
                        for (QueryDocumentSnapshot docs : task.getResult())
                            userId = docs.getId();
                        cf.complete(userId);
                    } else {
                        cf.completeExceptionally(new Exception("Username does not exist."));
                    }
                } else {
                    cf.completeExceptionally(new Exception("[usernameExists] Could not complete query"));
                }
            });
        });
        return cf;
    }

    public CompletableFuture<HashMap<String, String>> getPlayers() {
        CompletableFuture<HashMap<String, String>> cf = new CompletableFuture<>();
        HashMap<String, String> usernamesIds = new HashMap<>();

        db.collection(CollectionNames.PLAYERS.collectionName)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                if (document.exists()) {
                                    usernamesIds.put((String) document.getData().get(FieldNames.USERNAME.fieldName),
                                            (String) document.getData().get(FieldNames.USER_ID.fieldName));
                                }
                            }

                            cf.complete(usernamesIds);
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                            cf.completeExceptionally(task.getException());
                        }
                    }
                });
        return cf;
    }

    /**
     * Gets the player asynchronously
     * 
     * @param userId the userid of the player object to get
     * @return cf the CompleteableFuture with the searched for player
     */
    public CompletableFuture<Player> getPlayerAsync(String userId) {
        DocumentReference documentReference = collectionReference.document(userId);
        CompletableFuture<Player> cf = new CompletableFuture<>();
        CompletableFuture.runAsync(() -> {
            documentReference.get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        playerDataAdapter.getPlayerFromDocument(documentReference,
                                player -> {
                                    cf.complete(player);
                                });
                    } else {
                        cf.completeExceptionally(new IllegalArgumentException("Given username does not exist!"));
                    }
                } else {
                    Log.d(TAG, "Failed with: ", task.getException());
                    cf.completeExceptionally(task.getException());
                }
            });
        });
        return cf;
    }

    /**
     * Updates the player preferences of an existing user
     * 
     * @param userId            the id of the user to update the preferences for
     * @param playerPreferences the preferences to set for the user
     * @param booleanCallback   the callback function to call once the operation has
     *                          finished. Calls
     *                          with true if the operation was successful, and false
     *                          otherwise
     */
    public void updatePlayerPreferences(String userId, PlayerPreferences playerPreferences,
            BooleanCallback booleanCallback) {
        DocumentReference documentReference = collectionReference.document(userId);
        setPlayerPreferences(documentReference, playerPreferences, booleanCallback);
    }

    /**
     * Sets the player preferences of a user
     * 
     * @param playerDocument    the document of the player to change the preferences
     *                          on
     * @param playerPreferences the preferences to set for the user
     * @param booleanCallback   the callback function to call once the operation has
     *                          finished. Calls
     *                          with true if the operation was successful, and false
     *                          otherwise
     *
     *
     */
    private void setPlayerPreferences(DocumentReference playerDocument, PlayerPreferences playerPreferences,
            BooleanCallback booleanCallback) {
        fireStoreHelper.addBooleanFieldToDocument(playerDocument, FieldNames.RECORD_GEOLOCATION.fieldName,
                playerPreferences.getRecordGeolocationPreference(),
                new BooleanCallback() {
                    @Override
                    public void onCallback(Boolean isTrue) {
                        if (isTrue) {
                            booleanCallback.onCallback(true);
                        } else {
                            Log.e(TAG, "Something went wrong while setting the player preferences");
                            booleanCallback.onCallback(false);
                        }
                    }
                });
    }

    /**
     * Updates the contact information of an existing user
     * 
     * @param userId          the id of the user to update the preferences for
     * @param contactInfo     the contact information to set for the user
     * @param booleanCallback the callback function to call once the operation has
     *                        finished. Calls
     *                        with true if the operation was successful, and false
     *                        otherwise
     */
    public void updateContactInfo(String userId, ContactInfo contactInfo,
            BooleanCallback booleanCallback) {
        DocumentReference documentReference = collectionReference.document(userId);
        setContactInfo(documentReference, contactInfo, booleanCallback);
    }

    /**
     * Sets the contact information of a user
     * 
     * @param playerDocument  the document of the player to change the preferences
     *                        on
     * @param contactInfo     the contact information to set for the user
     * @param booleanCallback the callback function to call once the operation has
     *                        finished. Calls
     *                        with true if the operation was successful, and false
     *                        otherwise
     *
     *
     */
    private void setContactInfo(DocumentReference playerDocument, ContactInfo contactInfo,
            BooleanCallback booleanCallback) {
        fireStoreHelper.addStringFieldToDocument(playerDocument, FieldNames.EMAIL.fieldName,
                contactInfo.getEmail(),
                new BooleanCallback() {
                    @Override
                    public void onCallback(Boolean isTrue) {
                        if (isTrue) {
                            fireStoreHelper.addStringFieldToDocument(playerDocument, FieldNames.PHONE_NUMBER.fieldName,
                                    contactInfo.getPhoneNumber(), new BooleanCallback() {
                                        @Override
                                        public void onCallback(Boolean isTrue) {
                                            if (isTrue) {
                                                booleanCallback.onCallback(true);
                                            }
                                        }
                                    });
                        } else {
                            Log.e(TAG, "Something went wrong while setting the contact information" +
                                    "of the player document");
                            booleanCallback.onCallback(false);
                        }
                    }
                });
    }

    public ListenerRegistration setupPlayerListener(String userId, GetPlayerCallback callback) {
        final DocumentReference documentReference = collectionReference.document(userId);
        ListenerRegistration registration = documentReference.addSnapshotListener((snapshot, e) -> {
            Log.d("Player Firestore Listener", "PLAYER DATA HAS BEEN UPDATED.");
            if (snapshot != null && snapshot.exists()) {
                getPlayerAsync(userId).thenAccept(playa -> {
                    Log.d("Player Firestore Listener", "PLAYER DATA HAS BEEN FETCHED.");
                    callback.onCallback(playa);
                }).exceptionally(new Function<Throwable, Void>() {
                    @Override
                    public Void apply(Throwable throwable) {
                        Log.d("Player Firestore Listener", "Could not get player");
                        return null;
                    }
                });
            } else {
                callback.onCallback(null);
            }
        });
        return registration;
    }

    /**
     * Updates a user's username
     * 
     * @param oldUsername     the user's current username
     * @param newUsername     the username to set for the user
     * @param booleanCallback the callback function to call once the operation has
     *                        finished. Calls
     *                        with true if the operation was successful, and false
     *                        otherwise
     *
     * @throws IllegalArgumentException if the current username does not exist
     */
    public void updateUserName(String oldUsername, String newUsername, BooleanCallback booleanCallback) {
        this.setUserName(this.collectionReference.document(inAppUsernamesIds.get(oldUsername)),
                newUsername, new BooleanCallback() {
                    @Override
                    public void onCallback(Boolean isTrue) {
                        if (isTrue) {
                            if (cachedPlayers.containsKey(oldUsername)) {
                                cachedPlayers.put(newUsername, cachedPlayers.get(oldUsername));
                                cachedPlayers.remove(oldUsername);
                            }
                            booleanCallback.onCallback(true);
                        } else {
                            Log.e(TAG, "something went wrong while updating the user's username");
                            booleanCallback.onCallback(false);
                        }
                    }
                });
    }

    /**
     * Set the username field on a player's document
     * 
     * @param playerDocument  the document of the player to set the username field
     *                        on
     * @param username        the username to set on the document
     * @param booleanCallback the callback function to call once the operation has
     *                        finished, calls
     *                        with true if successful, and false otherwise
     * @throws IllegalArgumentException if there already is a user with the username
     */
    private void setUserName(DocumentReference playerDocument, String username,
            BooleanCallback booleanCallback) {
        if (this.inAppUsernamesIds.keySet().contains(username)) {
            throw new IllegalArgumentException("Given username already exists!");
        }

        this.fireStoreHelper.addStringFieldToDocument(playerDocument, FieldNames.USERNAME.fieldName,
                username, booleanCallback);
    }

    /**
     * Sets the id of a new document with the userId
     * 
     * @param username          the username of the user
     * @param getStringCallback the callback function to call once the operation has
     *                          finished. Calls
     *                          with the userId if the operation was successful, and
     *                          null otherwise
     * @throws IllegalArgumentException if there already is a document with the
     *                                  given userId
     */
    public void createPlayer(String username, GetStringCallback getStringCallback) {
        HashMap<String, String> data = new HashMap<>();
        data.put(FieldNames.USERNAME.fieldName, username);
        data.put(FieldNames.EMAIL.fieldName, "");
        data.put(FieldNames.PHONE_NUMBER.fieldName, "");
        data.put(FieldNames.RECORD_GEOLOCATION.fieldName, "");
        String userId = UUID.randomUUID().toString();
        fireStoreHelper.setDocumentReference(collectionReference.document(userId),
                data, new BooleanCallback() {
                    @Override
                    public void onCallback(Boolean isTrue) {
                        if (isTrue) {
                            getStringCallback.onCallback(userId);
                        } else {
                            Log.e(TAG, "Something went wrong while setting the userId" +
                                    "on a new Playerdocument");
                            getStringCallback.onCallback(null);
                        }
                    }
                });
    }

    /**
     * Update the PlayerWallet collection by adding a new ScannableCode
     * 
     * @param userId          the id of the user whose PlayerWallet must be updated
     * @param scannableCodeId the id of the scannable code to add
     * @param locationImage   the image of the location where the user scanned the
     *                        code
     * @param booleanCallback the callback function to call once the operation has
     *                        finished. Calls
     *                        with true if it was successful, and false otherwise
     *
     * @throws IllegalArgumentException if there are no users with the given Id
     */
    public void playerScannedCodeAdded(String userId, String scannableCodeId,
            Image locationImage, BooleanCallback booleanCallback) {

        CollectionReference scannedCodeCollection = collectionReference
                .document(userId)
                .collection(CollectionNames.PLAYER_WALLET.collectionName);

        playerWalletConnectionHandler.addScannableCodeDocument(scannedCodeCollection,
                scannableCodeId, locationImage, booleanCallback);
    }

    /**
     * Update the PlayerWallet collection by deleting a ScannableCode
     * 
     * @param userId          the id of the user whose PlayerWallet must be updated
     * @param scannableCodeId the id of the scannable code to delete
     * @param booleanCallback the callback function to call once the operation has
     *                        finished. Calls
     *                        with true if it was successful, and false otherwise
     *
     * @throws IllegalArgumentException if there are no users with the given Id
     */
    public void playerScannedCodeDeleted(String userId, String scannableCodeId,
            BooleanCallback booleanCallback) {

        CollectionReference scannedCodeCollection = collectionReference
                .document(userId)
                .collection(CollectionNames.PLAYER_WALLET.collectionName);

        playerWalletConnectionHandler.deleteScannableCodeFromWallet(scannedCodeCollection,
                scannableCodeId, booleanCallback);
    }

    public void getAppUserNames() {

    }
}

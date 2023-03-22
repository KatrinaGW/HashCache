package com.example.hashcache.models.database.DatabaseAdapters;

import static java.util.stream.Collectors.toList;

import android.media.Image;
import android.util.Log;
import android.util.Pair;

import androidx.annotation.NonNull;

import com.example.hashcache.models.database.DatabaseAdapters.converters.PlayerDocumentConverter;
import com.example.hashcache.models.database.DatabaseAdapters.callbacks.GetPlayerCallback;
import com.example.hashcache.models.database.values.CollectionNames;
import com.example.hashcache.models.database.values.FieldNames;
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
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;

/**
 * Handles all calls to the Firebase Players database
 */
public class PlayersDatabaseAdapter {
    private FirebaseFirestore db;
    private CollectionReference collectionReference;
    final String TAG = "Sample";
    private PlayerDocumentConverter playerDocumentConverter;
    private FireStoreHelper fireStoreHelper;
    private PlayerWalletDatabaseAdapter playerWalletDatabaseAdapter;
    private static PlayersDatabaseAdapter INSTANCE;

    /**
     * Creates a new instance of the class and initializes the connection to the
     * database
     *
     * @param playerDocumentConverter       the instance of the
     *                                      PlayerDocumentConverter class
     *                                      to use to convert documents into Player
     *                                      objects
     * @param fireStoreHelper               the instance of the FireStoreHelper
     *                                      class to use to perform
     *                                      general FireStore actions
     * @param db                            an instance of the Firestore database
     * @param playerWalletDatabaseAdapter the instance of the
     *                                      PlayerWalletConnectionHandler
     *                                      class to use to interact with a player's
     *                                      wallet collection
     */
    private PlayersDatabaseAdapter(PlayerDocumentConverter playerDocumentConverter,
                                   FireStoreHelper fireStoreHelper,
                                   FirebaseFirestore db, PlayerWalletDatabaseAdapter playerWalletDatabaseAdapter) {
        this.playerDocumentConverter = playerDocumentConverter;
        this.fireStoreHelper = fireStoreHelper;
        this.playerWalletDatabaseAdapter = playerWalletDatabaseAdapter;
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
     * @param playerDocumentConverter       the instance of the
     *                                      PlayerDocumentConverter class
     *                                      to use to convert documents into Player
     *                                      objects
     * @param fireStoreHelper               the instance of the FireStoreHelper
     *                                      class to use to perform
     *                                      general FireStore actions
     * @param db                            an instance of the Firestore database
     * @param playerWalletDatabaseAdapter the instance of the
     *                                      PlayerWalletConnectionHandler
     *                                      class to use to interact with a player's
     *                                      wallet collection
     * @return PlayersConnectionHandler.INSTANCE the current instance of the
     *         PlayersConnectionHandler class
     *
     * @throws IllegalArgumentException if the INSTANCE has already been initialized
     */
    public static PlayersDatabaseAdapter makeInstance(PlayerDocumentConverter playerDocumentConverter,
                                                      FireStoreHelper fireStoreHelper,
                                                      FirebaseFirestore db,
                                                      PlayerWalletDatabaseAdapter playerWalletDatabaseAdapter) {
        if (INSTANCE != null) {
            throw new IllegalArgumentException("Instance of PlayersConnectionHandler already exists!");
        }
        INSTANCE = new PlayersDatabaseAdapter(playerDocumentConverter,
                fireStoreHelper, db, playerWalletDatabaseAdapter);
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

    /**
     * Get all the players in the app
     * @return cf the CompletableFuture with a mapping of user's ids to their usernames
     */
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
    public CompletableFuture<Player> getPlayer(String userId) {
        DocumentReference documentReference = collectionReference.document(userId);
        CompletableFuture<Player> cf = new CompletableFuture<>();
        CompletableFuture.runAsync(() -> {
            documentReference.get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        playerDocumentConverter.getPlayerFromDocument(documentReference)
                                        .thenAccept(player -> {
                                            cf.complete(player);
                                        })
                                                .exceptionally(new Function<Throwable, Void>() {
                                                    @Override
                                                    public Void apply(Throwable throwable) {
                                                        System.out.println("There was an error getting the scannableCodes.");
                                                        cf.completeExceptionally(throwable);
                                                        return null;
                                                    }
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
     * @return cf the CompletableFuture indicating if the operation was successful or not
     */
    public CompletableFuture<Boolean> updatePlayerPreferences(String userId, PlayerPreferences playerPreferences) {
        DocumentReference documentReference = collectionReference.document(userId);
        CompletableFuture<Boolean> cf = setPlayerPreferences(documentReference, playerPreferences);
        return cf;
    }

    /**
     * Sets the player preferences of a user
     * 
     * @param playerDocument    the document of the player to change the preferences
     *                          on
     * @param playerPreferences the preferences to set for the user
     * @return cf the CompleteableFuture which indicate that the operation was successful
     *
     *
     */
    private CompletableFuture<Boolean> setPlayerPreferences(DocumentReference playerDocument,
                                                            PlayerPreferences playerPreferences) {
        CompletableFuture<Boolean> cf;
        cf = fireStoreHelper.addBooleanFieldToDocument(playerDocument,
                FieldNames.RECORD_GEOLOCATION.fieldName, playerPreferences.getRecordGeolocationPreference());

        return cf;
    }

    /**
     * Updates the contact information of an existing user
     * 
     * @param userId          the id of the user to update the preferences for
     * @param contactInfo     the contact information to set for the user
     * @return cf the CompletableFuture indicating if the operation was successful or not
     */
    public CompletableFuture<Boolean> updateContactInfo(String userId, ContactInfo contactInfo) {
        DocumentReference documentReference = collectionReference.document(userId);
        CompletableFuture<Boolean> cf = setContactInfo(documentReference, contactInfo);

        return cf;
    }

    /**
     * Sets the contact information of a user
     * 
     * @param playerDocument  the document of the player to change the preferences
     *                        on
     * @param contactInfo     the contact information to set for the user
     * @return cf the CompletableFuture indicating if the operation was successful
     *
     *
     */
    private CompletableFuture<Boolean> setContactInfo(DocumentReference playerDocument, ContactInfo contactInfo) {
        CompletableFuture<Boolean> cf = new CompletableFuture<>();

        fireStoreHelper.addStringFieldToDocument(playerDocument, FieldNames.EMAIL.fieldName,
                contactInfo.getEmail()).thenAccept(success -> {
            if (success) {
                fireStoreHelper.addStringFieldToDocument(playerDocument,
                                FieldNames.PHONE_NUMBER.fieldName, contactInfo.getPhoneNumber())
                        .thenAccept(successful->{
                            if(successful){
                                cf.complete(true);
                            }else{
                                cf.completeExceptionally(new Exception(
                                        "Something went wrong while setting the " +
                                                "phone number!"
                                ));
                            }
                        })
                        .exceptionally(new Function<Throwable, Void>() {
                            @Override
                            public Void apply(Throwable throwable) {
                                Log.d("Update contact info",
                                        "Could not set the phone number");
                                return null;
                            }
                        });

            } else {
                Log.e(TAG, "Something went wrong while setting the contact information" +
                        "of the player document");
                cf.completeExceptionally(new Exception("Something went wrong while setting" +
                        "the contact information"));
            }
        })
                .exceptionally(new Function<Throwable, Void>() {
            @Override
            public Void apply(Throwable throwable) {
                Log.d("Update contact info",
                        "Could not set the email");
                return null;
            }
        });
        return cf;
    }

    /**
     * Setup a listener that notifies observers when the player document is changed in the database
     * @param userId the id of the player to be listening to
     * @param callback called once the player's data has been updated
     * @return registration the ListenerRegistration of the now registered listener
     */
    public ListenerRegistration setupPlayerListener(String userId, GetPlayerCallback callback) {
        final DocumentReference documentReference = collectionReference.document(userId);
        ListenerRegistration registration = documentReference.addSnapshotListener((snapshot, e) -> {
            Log.d("Player Firestore Listener", "PLAYER DATA HAS BEEN UPDATED.");
            if (snapshot != null && snapshot.exists()) {
                getPlayer(userId).thenAccept(playa -> {
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
     * Sets the id of a new document with the userId
     * 
     * @param username          the username of the user
     * @return cf the CompletableFuture with the new userId
     */
    public CompletableFuture<String> createPlayer(String username) {
        CompletableFuture<String> cf = new CompletableFuture<>();
        HashMap<String, String> data = new HashMap<>();
        data.put(FieldNames.USERNAME.fieldName, username);
        data.put(FieldNames.EMAIL.fieldName, "");
        data.put(FieldNames.PHONE_NUMBER.fieldName, "");
        data.put(FieldNames.RECORD_GEOLOCATION.fieldName, "");
        String userId = UUID.randomUUID().toString();

        fireStoreHelper.setDocumentReference(collectionReference.document(userId), data)
                        .thenAccept(successful -> {
                            if (successful) {
                                cf.complete(userId);
                            } else {
                                Log.e(TAG, "Something went wrong while setting the userId" +
                                        "on a new Playerdocument");
                                cf.completeExceptionally(new Exception("Something went wrong while " +
                                        "setting the userId on the new PlayerDocument"));
                            }
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

    /**
     * Update the PlayerWallet collection by adding a new ScannableCode
     * 
     * @param userId          the id of the user whose PlayerWallet must be updated
     * @param scannableCodeId the id of the scannable code to add
     * @param locationImage   the image of the location where the user scanned the
     *                        code
     * @return cf the CompletableFuture indicating if the operation was a success or not
     */
    public CompletableFuture<Void> playerScannedCodeAdded(String userId, String scannableCodeId,
            Image locationImage) {
        CompletableFuture<Void> cf;

        CollectionReference scannedCodeCollection = collectionReference
                .document(userId)
                .collection(CollectionNames.PLAYER_WALLET.collectionName);

        cf = playerWalletDatabaseAdapter.addScannableCodeDocument(scannedCodeCollection,
                scannableCodeId, locationImage);

        return cf;
    }

    /**
     * Update the PlayerWallet collection by deleting a ScannableCode
     * 
     * @param userId          the id of the user whose PlayerWallet must be updated
     * @param scannableCodeId the id of the scannable code to delete
     * @return cf the CompletableFuture indicating if the operation was successful or not
     */
    public CompletableFuture<Boolean> playerScannedCodeDeleted(String userId, String scannableCodeId) {
        CompletableFuture<Boolean> cf;

        CollectionReference scannedCodeCollection = collectionReference
                .document(userId)
                .collection(CollectionNames.PLAYER_WALLET.collectionName);

        cf = playerWalletDatabaseAdapter.deleteScannableCodeFromWallet(scannedCodeCollection,
                scannableCodeId);

        return cf;
    }

    /**
     * Get all the usernames for the users in a given list of ids
     * @param userIds the ids of the users whose usernames are wanted
     * @return userIdsNamesCF the completableFuture with the usernames and userIds of the
     * specified users
     */
    public CompletableFuture<ArrayList<Pair<String, String>>> getUsernamesByIds(ArrayList<String> userIds){
        CompletableFuture<ArrayList<Pair<String, String>>> userIdsNamesCF = new CompletableFuture<>();
        ArrayList<Pair<String, String>> userIdsNames = new ArrayList<>();

        ArrayList<CompletableFuture<Pair<String, String>>> futureCfs = new ArrayList<>();

        for(String userId : userIds){
            futureCfs.add(getUsernameById(userId));
        }

        if(futureCfs.size()>0){
            CompletableFuture.allOf(futureCfs.toArray(new CompletableFuture[futureCfs.size()]))
                    .thenAccept(voidValue -> {
                        futureCfs.stream()
                                .forEach(cf -> {
                                    userIdsNames.add(cf.join());
                                });
                        userIdsNamesCF.complete(userIdsNames);
                    })
                    .exceptionally(new Function<Throwable, Void>() {
                        @Override
                        public Void apply(Throwable throwable) {
                            userIdsNamesCF.completeExceptionally(throwable);
                            return null;
                        }
                    });
        }else{
            userIdsNamesCF.complete(userIdsNames);
        }

        return userIdsNamesCF;
    }

    /**
     * Gets the username of a player with a given userid
     * @param userId the userid of the player whose username is needed
     * @return cf the CompletableFuture with the specified user's username paired with their id
     */
    public CompletableFuture<Pair<String, String>> getUsernameById(String userId){
        CompletableFuture<Pair<String, String>>  cf = new CompletableFuture<>();

        fireStoreHelper.documentWithIDExists(collectionReference, userId)
                .thenAccept(exists -> {
                    if(exists){
                        collectionReference.document(userId).get()
                                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                        if (task.isSuccessful()) {
                                            cf.complete(new Pair<String, String>(userId,
                                                    task.getResult().get(FieldNames.USERNAME.fieldName).toString()));
                                        } else {
                                            cf.completeExceptionally(new Exception("Something went wrong" +
                                                    "in getUsernameById"));
                                        }
                                    }
                                });
                    }else{
                        cf.completeExceptionally(new Exception("No player exists with given userId!"));
                    }
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
     * Gets the number of players who have scanned a specific QR code
     * @param scannableCodeId the id of the scannable code to look for in players' wallets
     * @return cf the CompletableFuture with the number of players who have scanned a specific QR code
     */
    public CompletableFuture<Integer> getNumPlayersWithScannableCode(String scannableCodeId){
        CompletableFuture<Integer> cf = new CompletableFuture<>();

        collectionReference.get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    /**
                     * Get the number of players with the scananbleCodeId in their wallet
                     * @param task the task that should have fetched all player documents in the database
                     */
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        //Note that count is initialized to 0
                        AtomicInteger count = new AtomicInteger();
                        if(task.isSuccessful()){
                            int size = task.getResult().size();
                            if(size>0){
                                List<DocumentSnapshot> docs = task.getResult().getDocuments();
                                ArrayList<CompletableFuture<Boolean>> futureCfs = new ArrayList<>();
                                DocumentReference docRef;
                                for(DocumentSnapshot doc : docs){
                                    docRef = doc.getReference();
                                    DocumentReference finalDocRef = docRef;
                                        futureCfs.add(fireStoreHelper.documentWithIDExists(
                                                finalDocRef.collection(CollectionNames.PLAYER_WALLET.collectionName),
                                                scannableCodeId));

                                }
                                CompletableFuture.allOf(futureCfs.toArray(new CompletableFuture[futureCfs.size()]))
                                        .thenAccept(voidValue -> {
                                            futureCfs.stream()
                                                    .forEach(cf -> {
                                                        if(cf.join()){
                                                            count.getAndIncrement();
                                                        }
                                                    });
                                            cf.complete(count.intValue());
                                        });
                            }else{
                                cf.complete(count.intValue());
                            }
                        }else{
                            cf.completeExceptionally(new Exception("Something went wrong while " +
                                    "getting all players"));
                        }
                    }
                });
                return cf;
    }
}

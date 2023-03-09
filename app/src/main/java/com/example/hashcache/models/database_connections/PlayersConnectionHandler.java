package com.example.hashcache.models.database_connections;

import android.media.Image;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.hashcache.models.database_connections.callbacks.BooleanCallback;
import com.example.hashcache.models.database_connections.callbacks.GetPlayerCallback;
import com.example.hashcache.models.database_connections.converters.PlayerDocumentConverter;
import com.example.hashcache.models.database_connections.values.CollectionNames;
import com.example.hashcache.models.database_connections.values.FieldNames;
import com.example.hashcache.models.ContactInfo;
import com.example.hashcache.models.Player;
import com.example.hashcache.models.PlayerPreferences;
import com.example.hashcache.models.PlayerWallet;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.checkerframework.checker.units.qual.A;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Handles all calls to the Firebase Players database
 */
public class PlayersConnectionHandler {
    private FirebaseFirestore db;
    private CollectionReference collectionReference;
    private HashMap<String, String> inAppUsernamesIds;
    private HashMap<String, Player> cachedPlayers;
    final String TAG = "Sample";
    private PlayerDocumentConverter playerDocumentConverter;
    private FireStoreHelper fireStoreHelper;
    private PlayerWalletConnectionHandler playerWalletConnectionHandler;
    private static PlayersConnectionHandler INSTANCE;

    /**
     * Creates a new instance of the class and initializes the connection to the database
     * @param userNamesIds used to keep the app up to date on the current usernames
     *                             and their ids
     */
    private PlayersConnectionHandler(HashMap<String, String> inAppNamesIds,
                                     PlayerDocumentConverter playerDocumentConverter,
                                     FireStoreHelper fireStoreHelper,
                                     FirebaseFirestore db, PlayerWalletConnectionHandler
                                     playerWalletConnectionHandler){
        this.inAppUsernamesIds = inAppNamesIds;
        this.cachedPlayers = new HashMap<>();
        this.playerDocumentConverter = playerDocumentConverter;
        this.fireStoreHelper = fireStoreHelper;
        this.playerWalletConnectionHandler = playerWalletConnectionHandler;
        this.db = db;

        collectionReference = db.collection(CollectionNames.PLAYERS.collectionName);

        collectionReference.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable
            FirebaseFirestoreException error) {
                String username;
                inAppUsernamesIds.clear();

                for(QueryDocumentSnapshot doc: queryDocumentSnapshots)
                {
                    if(doc.getData().get(FieldNames.USERNAME.fieldName) != null){
                        username = doc.getData().get(FieldNames.USERNAME.fieldName).toString();
                        Log.d(TAG, username);
                        inAppUsernamesIds.put(username, doc.getId());
                    }
                }
            }
        });
    }
    
    public static PlayersConnectionHandler getInstance(){
        if(INSTANCE == null){
            throw new IllegalArgumentException("No instance of PlayersConnectionHandler currently exists!");
        }
        
        return INSTANCE;
    }

    public static PlayersConnectionHandler makeInstance(HashMap<String, String> inAppNamesIds,
                                                        PlayerDocumentConverter playerDocumentConverter,
                                                        FireStoreHelper fireStoreHelper,
                                                        FirebaseFirestore db, PlayerWalletConnectionHandler
                                                        playerWalletConnectionHandler){
        if(INSTANCE != null){
            throw new IllegalArgumentException("Instance of PlayersConnectionHandler already exists!");
        }
        INSTANCE = new PlayersConnectionHandler(inAppNamesIds, playerDocumentConverter,
                fireStoreHelper, db, playerWalletConnectionHandler);
        return INSTANCE;
    }

    /**
     * Gets the in app player usernames
     * @return inAppPlayerUserNames gets the usernames of all players in the app
     */
    public ArrayList<String> getInAppPlayerUserNames(){
        ArrayList<String> inAppUserNames = new ArrayList<>();
        for(String username : inAppUsernamesIds.keySet()){
            inAppUserNames.add(username);
        }
        return inAppUserNames;
    }

    /**
     * Gets a Player from the Players database, if the given username belongs to a player
     *
     * @param userName the username to use to pull the player with
     * @param getPlayerCallback the callback function to call with the player once it has
     *                          been found
     * @throws IllegalArgumentException if the given username does not belong to a player
     */
    public void getPlayer(String userName, GetPlayerCallback getPlayerCallback){
        final Player[] player = new Player[1];

        if(cachedPlayers.keySet().contains(userName)){
            player[0] = cachedPlayers.get(userName);
        }else {
            DocumentReference documentReference = collectionReference.document(
                    inAppUsernamesIds.get(userName)
            );
            documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            Log.d(TAG, "Document exists!");

                            playerDocumentConverter.getPlayerFromDocument(documentReference,
                                    new GetPlayerCallback() {
                                        @Override
                                        public void onCallback(Player player) {
                                            cachedPlayers.put(userName, player);
                                            Log.d(TAG, "FIND DONE");
                                            getPlayerCallback.onCallback(player);
                                        }
                                    });
                        } else {
                            Log.d(TAG, "Document does not exist!");
                             throw new IllegalArgumentException("Given username does not exist!");
                        }
                    } else {
                        Log.d(TAG, "Failed with: ", task.getException());
                    }
                }
            });
        }
    }

    /**
     * Adds a player to the database
     *
     * @param player the player to add to the database
     * @throws IllegalArgumentException if the username is empty, too long, or already belongs
     * to a player
     */
    public void addPlayer(Player player, BooleanCallback booleanCallback){
        String userId = player.getUserId();
        String username = player.getUsername();
        ContactInfo contactInfo = player.getContactInfo();
        PlayerPreferences playerPreferences = player.getPlayerPreferences();

        if(username == null || username.equals("")|| username.length()>=50){
            throw new IllegalArgumentException("Username null, empty, or too long");
        }

        if(inAppUsernamesIds.keySet().contains(username)){
            throw new IllegalArgumentException("Username taken!");
        }

        playerWalletConnectionHandler.setPlayerWallet(player.getPlayerWallet(),
                collectionReference.document(userId), new BooleanCallback() {
            @Override
            public void onCallback(Boolean isTrue) {
                setUserId(userId, new BooleanCallback() {
                    @Override
                    public void onCallback(Boolean isTrue)
                    {
                        setUserName(collectionReference.document(userId),username, new BooleanCallback() {
                            @Override
                            public void onCallback(Boolean isTrue) {
                                if (isTrue) {
                                    DocumentReference playerDocument = collectionReference.document(userId);
                                    setContactInfo(playerDocument, contactInfo, new BooleanCallback() {
                                        @Override
                                        public void onCallback(Boolean isTrue) {
                                            if (isTrue) {
                                                setPlayerPreferences(playerDocument, playerPreferences, new BooleanCallback() {
                                                    @Override
                                                    public void onCallback(Boolean isTrue) {
                                                        cachedPlayers.put(player.getUsername(), player);
                                                        booleanCallback.onCallback(true);
                                                    }
                                                });
                                            }
                                        }
                                    });
                                }
                            }
                        });
                    }
                });

            }
        });


    }

    public void updatePlayerPreferences(String userId, PlayerPreferences playerPreferences,
                                        BooleanCallback booleanCallback){
        DocumentReference documentReference = collectionReference.document(userId);
        setPlayerPreferences(documentReference, playerPreferences, booleanCallback);
    }

    private void setPlayerPreferences(DocumentReference playerDocument, PlayerPreferences playerPreferences,
                                      BooleanCallback booleanCallback){
        fireStoreHelper.addBooleanFieldToDocument(playerDocument, FieldNames.RECORD_GEOLOCATION.fieldName,
                playerPreferences.getRecordGeolocationPreference(),
                new BooleanCallback() {
                    @Override
                    public void onCallback(Boolean isTrue) {
                        if(isTrue){
                            booleanCallback.onCallback(true);
                        }else{
                            throw new RuntimeException("Something went wrong");
                        }
                    }
                });
    }

    public void updateContactInfo(String userId, ContactInfo contactInfo,
                                        BooleanCallback booleanCallback){
        DocumentReference documentReference = collectionReference.document(userId);
        setContactInfo(documentReference, contactInfo, booleanCallback);
    }

    private void setContactInfo(DocumentReference playerDocument, ContactInfo contactInfo,
                                BooleanCallback booleanCallback){
        fireStoreHelper.addStringFieldToDocument(playerDocument, FieldNames.EMAIL.fieldName,
                contactInfo.getEmail(),
                new BooleanCallback() {
                    @Override
                    public void onCallback(Boolean isTrue) {
                        if(isTrue){
                            fireStoreHelper.addStringFieldToDocument(playerDocument, FieldNames.PHONE_NUMBER.fieldName,
                                    contactInfo.getPhoneNumber(), new BooleanCallback() {
                                        @Override
                                        public void onCallback(Boolean isTrue) {
                                            if(isTrue){
                                                booleanCallback.onCallback(true);
                                            }
                                        }
                                    });
                        }else{
                            throw new RuntimeException("Something went wrong");
                        }
                    }
                });
    }

    public void updateUserName(String oldUsername, String newUsername, BooleanCallback booleanCallback){
        if(!this.inAppUsernamesIds.containsKey(oldUsername)){
            throw new IllegalArgumentException("Old username does not exist!");
        }

        this.setUserName(this.collectionReference.document(inAppUsernamesIds.get(oldUsername)),
                newUsername, new BooleanCallback() {
                    @Override
                    public void onCallback(Boolean isTrue) {
                        if(isTrue){
                            if(cachedPlayers.containsKey(oldUsername)){
                                cachedPlayers.put(newUsername, cachedPlayers.get(oldUsername));
                                cachedPlayers.remove(oldUsername);
                            }
                            booleanCallback.onCallback(true);
                        }else{
                            booleanCallback.onCallback(false);
                        }
                    }
                });
    }

    private void setUserId(DocumentReference playerDocument, String userId, BooleanCallback booleanCallback){
        HashMap<String, String> data = new HashMap<>();
        data.put(FieldNames.USER_ID.fieldName, userId);
        fireStoreHelper.setDocumentReference(playerDocument, data, booleanCallback);
    }

    private void setUserName(DocumentReference playerDocument, String username,
                             BooleanCallback booleanCallback){
        if(this.inAppUsernamesIds.keySet().contains(username)){
            throw new IllegalArgumentException("Given username already exists!");
        }

        this.fireStoreHelper.addStringFieldToDocument(playerDocument, FieldNames.USERNAME.fieldName,
                username, booleanCallback);
    }

    private void setUserId(String userId, BooleanCallback booleanCallback){
        HashMap<String, String> userIdData = new HashMap<>();
        userIdData.put("userId", userId);
        if(this.inAppUsernamesIds.containsValue(userId)){
            throw new IllegalArgumentException("There already exists a user with the given id!");
        } else{
            fireStoreHelper.setDocumentReference(collectionReference.document(userId),
                    userIdData, new BooleanCallback() {
                        @Override
                        public void onCallback(Boolean isTrue) {
                            if(isTrue){
                                booleanCallback.onCallback(true);
                            }else{
                                throw new RuntimeException("Something went wrong while creating" +
                                        "the new user document");
                            }
                        }
                    });
        }
    }

    public void playerScannedCodeAdded(String userId, String scannableCodeId,
                                       Image locationImage, BooleanCallback booleanCallback){
        if(!this.inAppUsernamesIds.keySet().contains(userId)){
            throw new IllegalArgumentException("Given username does not exist!");
        }

        CollectionReference scannedCodeCollection = collectionReference
                .document(userId)
                .collection(CollectionNames.PLAYER_WALLET.collectionName);

        playerWalletConnectionHandler.addScannableCodeDocument(scannedCodeCollection,
                scannableCodeId, locationImage, booleanCallback);
    }

    public void playerScannedCodeDeleted(String userId, String scannableCodeId,
                                         BooleanCallback booleanCallback){
        if(!this.inAppUsernamesIds.keySet().contains(userId)){
            throw new IllegalArgumentException("Given username does not exist!");
        }

        CollectionReference scannedCodeCollection = collectionReference
                .document(userId)
                .collection(CollectionNames.PLAYER_WALLET.collectionName);

        playerWalletConnectionHandler.deleteScannableCodeFromWallet(scannedCodeCollection,
                scannableCodeId, booleanCallback);
    }
}

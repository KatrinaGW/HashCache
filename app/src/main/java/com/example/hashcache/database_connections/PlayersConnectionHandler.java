package com.example.hashcache.database_connections;

import android.media.Image;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.hashcache.database_connections.callbacks.BooleanCallback;
import com.example.hashcache.database_connections.callbacks.GetPlayerCallback;
import com.example.hashcache.database_connections.converters.FireStoreHelper;
import com.example.hashcache.database_connections.converters.PlayerDocumentConverter;
import com.example.hashcache.database_connections.values.CollectionNames;
import com.example.hashcache.database_connections.values.FieldNames;
import com.example.hashcache.models.ContactInfo;
import com.example.hashcache.models.Player;
import com.example.hashcache.models.PlayerPreferences;
import com.example.hashcache.models.PlayerWallet;
import com.example.hashcache.models.ScannableCode;
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

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Handles all calls to the Firebase Players database
 */
public class PlayersConnectionHandler {
    private FirebaseFirestore db;
    private CollectionReference collectionReference;
    private ArrayList<String> inAppPlayerUserNames;
    private HashMap<String, Player> cachedPlayers;
    final String TAG = "Sample";
    private PlayerDocumentConverter playerDocumentConverter;
    private FireStoreHelper fireStoreHelper;

    /**
     * Creates a new instance of the class and initializes the connection to the database
     * @param inAppPlayerUserNames used to keep the app up to date on the current usernames
     *                             in the database
     */
    public PlayersConnectionHandler(ArrayList<String> inAppPlayerUserNames){
        this.inAppPlayerUserNames = inAppPlayerUserNames;
        this.cachedPlayers = new HashMap<>();
        this.playerDocumentConverter = new PlayerDocumentConverter();
        this.fireStoreHelper = new FireStoreHelper();

        db = FirebaseFirestore.getInstance();

        collectionReference = db.collection(CollectionNames.PLAYERS.collectionName);

        collectionReference.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable
            FirebaseFirestoreException error) {
                inAppPlayerUserNames.clear();
                for(QueryDocumentSnapshot doc: queryDocumentSnapshots)
                {
                    Log.d(TAG, String.valueOf(doc.getData().get("username")));
                    String username = doc.getId();
                    inAppPlayerUserNames.add(username);
                }
            }
        });
    }

    /**
     * Gets the in app player usernames
     * @return inAppPlayerUserNames gets the usernames of all players in the app
     */
    public ArrayList<String> getInAppPlayerUserNames(){
        return this.inAppPlayerUserNames;
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
            DocumentReference documentReference = collectionReference.document(userName);
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
        String username = player.getUsername();
        ContactInfo contactInfo = player.getContactInfo();
        PlayerPreferences playerPreferences = player.getPlayerPreferences();

        if(username == null || username.equals("")|| username.length()>=50){
            throw new IllegalArgumentException("Username null, empty, or too long");
        }

        if(inAppPlayerUserNames.contains(username)){
            throw new IllegalArgumentException("Username taken!");
        }

        HashMap<String, String> usernameData = new HashMap<>();
        usernameData.put("username", username);

        HashMap<String, String> contactInfoData = new HashMap<>();
        contactInfoData.put("email", contactInfo.getEmail());
        contactInfoData.put("phoneNumber", contactInfo.getPhoneNumber());

        HashMap<String, String> recordGeoLocationdData = new HashMap<>();
        recordGeoLocationdData.put(FieldNames.RECORD_GEOLOCATION.fieldName, String.valueOf(playerPreferences
                .getRecordGeolocationPreference())
        );

        setPlayerWallet(player, collectionReference.document(username), new BooleanCallback() {
            @Override
            public void onCallback(Boolean isTrue) {
                DocumentReference contactInfoReference = collectionReference
                        .document(username)
                        .collection(CollectionNames.CONTACT_INFO.collectionName)
                        .document("contactInfo");
                DocumentReference playerPreferenceReference = collectionReference
                        .document(username)
                        .collection(CollectionNames.PLAYER_PREFERENCES.collectionName)
                        .document("playerPreferences");

                fireStoreHelper.setDocumentReference(collectionReference.document(username), usernameData);
                fireStoreHelper.setDocumentReference(contactInfoReference, contactInfoData);
                fireStoreHelper.setDocumentReference(playerPreferenceReference, recordGeoLocationdData);

                booleanCallback.onCallback(true);
            }
        });


    }

    public void playerScannedCodeAdded(String username, ScannableCode scannableCode,
                                         BooleanCallback booleanCallback){
        if(!this.cachedPlayers.containsKey(username)){
            throw new IllegalArgumentException("Given username does not exist!");
        }

        CollectionReference scannedCodeCollection = collectionReference
                .document(username)
                .collection(CollectionNames.PLAYER_WALLET.collectionName);

        fireStoreHelper.documentWithIDExists(scannedCodeCollection, scannableCode.getScannableCodeId(),
                new BooleanCallback() {
                    @Override
                    public void onCallback(Boolean isTrue) {
                        if(!isTrue){
                            scannedCodeCollection.document(scannableCodeId).delete()
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            Log.d(TAG, "DocumentSnapshot successfully deleted!");
                                            booleanCallback.onCallback(true);
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Log.w(TAG, "Error deleting document", e);
                                            booleanCallback.onCallback(false);
                                        }
                                    });
                        }else{
                            throw new IllegalArgumentException("Scannable code already exists!");
                        }
                    }
                });
    }

    public void playerScannedCodeDeleted(String username, String scannableCodeId,
                                         BooleanCallback booleanCallback){
        if(!this.cachedPlayers.containsKey(username)){
            throw new IllegalArgumentException("Given username does not exist!");
        }

        CollectionReference scannedCodeCollection = collectionReference
                .document(username)
                .collection(CollectionNames.PLAYER_WALLET.collectionName);

        fireStoreHelper.documentWithIDExists(scannedCodeCollection, scannableCodeId,
                new BooleanCallback() {
                    @Override
                    public void onCallback(Boolean isTrue) {
                        if(isTrue){
                            scannedCodeCollection.document(scannableCodeId).delete()
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            Log.d(TAG, "DocumentSnapshot successfully deleted!");
                                            booleanCallback.onCallback(true);
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Log.w(TAG, "Error deleting document", e);
                                            booleanCallback.onCallback(false);
                                        }
                                    });
                        }
                    }
                });
    }

    private void addScannableCode(CollectionReference playerWalletCollection, String scannableCodeId,
                                  Image locationImage){
        HashMap<String, String> scannableCodeIdData = new HashMap<>();
        scannableCodeIdData.put(FieldNames.SCANNABLE_CODE_ID.fieldName, scannableCodeId);
        if(locationImage != null){
            //TODO: insert the image
        }
        DocumentReference playerWalletReference = playerWalletCollection.document(scannableCodeId);

        fireStoreHelper.setDocumentReference(playerWalletReference, scannableCodeIdData);
    }

    private void setPlayerWallet(Player player, DocumentReference playerDocumentReference,
                                 BooleanCallback booleanCallback){
        PlayerWallet playerWallet = player.getPlayerWallet();
        ArrayList<String> scannableCodeIds = playerWallet.getScannedCodeIds();

        HashMap<String, String> scannableCodeIdData = new HashMap<>();

        if(scannableCodeIds.size()>0){
            Image scannableCodeImage;

            for(String scannableCodeId : scannableCodeIds){
                addScannableCode(playerDocumentReference.collection(CollectionNames.PLAYER_WALLET.collectionName),
                        scannableCodeId, scannableCodeImage);
//                scannableCodeIdData.clear();
//                scannableCodeIdData.put(FieldNames.SCANNABLE_CODE_ID.fieldName, scannableCodeId);
//                if(playerWallet.getScannableCodeLocationImage(scannableCodeId) != null){
//                    //TODO: insert the image
//                }
//                DocumentReference playerWalletReference = collectionReference.document(player.getUsername())
//                        .collection(CollectionNames.PLAYER_WALLET.collectionName).document(scannableCodeId);
//
//                fireStoreHelper.setDocumentReference(playerWalletReference, scannableCodeIdData);
            }
        }

        booleanCallback.onCallback(true);
    }

}

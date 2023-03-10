package com.example.hashcache.models.database_connections.converters;

import android.media.Image;
import android.util.Log;

import androidx.annotation.NonNull;

import com.example.hashcache.models.database_connections.callbacks.GetContactInfoCallback;
import com.example.hashcache.models.database_connections.callbacks.GetPlayerCallback;
import com.example.hashcache.models.database_connections.callbacks.GetPlayerPreferencesCallback;
import com.example.hashcache.models.database_connections.callbacks.GetPlayerWalletCallback;
import com.example.hashcache.models.database_connections.callbacks.GetStringCallback;
import com.example.hashcache.models.database_connections.values.CollectionNames;
import com.example.hashcache.models.database_connections.values.FieldNames;
import com.example.hashcache.models.ContactInfo;
import com.example.hashcache.models.Player;
import com.example.hashcache.models.PlayerPreferences;
import com.example.hashcache.models.PlayerWallet;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

/**
 * Handles the conversion between a DocumentReference and a PlayerObject
 */
public class PlayerDocumentConverter {
    final String TAG = "Sample";

    /**
     * Converts a DocumentReference into a PlayerObject
     * @param documentReference
     * @param getPlayerCallback
     */
    public void getPlayerFromDocument(DocumentReference documentReference, GetPlayerCallback getPlayerCallback){
        String userId = documentReference.getId();
        /**
         * Get the ContactInfo object from the DocumentReference
         */
        getContactInfo(documentReference, new GetContactInfoCallback() {
            @Override
            public void onGetContactInfoCallback(ContactInfo contactInfo) {
                /**
                 * Get the PlayerPreferences object from the DocumentReference after the previous
                 * callback has finished
                 */
                getPlayerPreferences(documentReference, new GetPlayerPreferencesCallback() {
                    @Override
                    public void onCallback(PlayerPreferences playerPreferences) {
                        /**
                         * Get the PlayerWallet object from the Document reference, after the
                         * previous callback has finished
                         */
                        getPlayerWallet(documentReference.collection(CollectionNames.PLAYER_WALLET.collectionName),
                                new GetPlayerWalletCallback() {
                                    @Override
                                    public void onCallback(PlayerWallet playerWallet) {
                                        /**
                                         * Get the username from the DocumentReference after the
                                         * previous callback has finished, and then call the
                                         * given callback function with the completed
                                         * Player object
                                         */
                                        getUserName(documentReference, new GetStringCallback() {
                                            @Override
                                            public void onCallback(String username) {
                                                getPlayerCallback.onCallback(
                                                        new Player(userId, username, contactInfo,
                                                                playerPreferences, playerWallet));
                                            }
                                        });


                                    }
                                });
                    }
                });
            }
        });

    }

    /**
     * Get the username for a Player from a DocumentReference
     * @param documentReference the DocumentReference to pull the username value from
     * @param getStringCallback the callback function to call with the username. Calls with
     *                          null if the username couldn't be found
     */
    private void getUserName(DocumentReference documentReference, GetStringCallback
                             getStringCallback){
        String[] username = new String[1];

        documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                        try{
                            username[0] = (String) document.getData().get(FieldNames.USERNAME.fieldName);

                            getStringCallback.onCallback(username[0]);
                        }catch (NullPointerException e){
                            Log.e(TAG, "User does not have a username!");
                            getStringCallback.onCallback(username[0]);
                        }
                    } else {
                        getStringCallback.onCallback(null);
                        Log.d(TAG, "No such document");
                    }
                } else {
                    getStringCallback.onCallback(null);
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });
    }

    /**
     * Get the PlayerWallet object from the player's PlayerWallet collection
     * @param collectionReference the PlayerWallet collection with the scannable code ids and their
     *                            images in it
     * @param getPlayerWalletCallback the callback function to call with the PlayerWallet object. Calls
     *                                with null if the extraction is not successful
     */
    private void getPlayerWallet(CollectionReference collectionReference,
                                 GetPlayerWalletCallback getPlayerWalletCallback){
        PlayerWallet playerWallet = new PlayerWallet();
        collectionReference
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            String scannableCodeId;
                            Image scannableCodeImage;

                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d(TAG, document.getId() + " => " + document.getData());
                                scannableCodeId = (String) document.getData()
                                        .get(FieldNames.SCANNABLE_CODE_ID.fieldName);
                                //TODO: check for empty image and then add if it exists
                                playerWallet.addScannableCode(scannableCodeId);
                            }
                            getPlayerWalletCallback.onCallback(playerWallet);
                        } else {
                            getPlayerWalletCallback.onCallback(null);
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });
    }

    /**
     * Get the PlayerPreferences object from the Player's DocumentReference
     * @param documentReference the DocumentReference to get the PlayerPreferences from
     * @param getPlayerPreferencesCallback the callback function to call with the created
     *                                     PlayerPreferences object once it's been extracted. Calls
     *                                     with null if the extraction is not successful
     */
    private void getPlayerPreferences(DocumentReference documentReference,
                                      GetPlayerPreferencesCallback getPlayerPreferencesCallback){
        PlayerPreferences playerPreferences = new PlayerPreferences();

        documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                        try{
                            boolean recordGeolocation = Boolean.parseBoolean(document.getData()
                                    .get(FieldNames.RECORD_GEOLOCATION.fieldName).toString()
                            );
                            playerPreferences.setGeoLocationRecording(recordGeolocation);

                            getPlayerPreferencesCallback.onCallback(playerPreferences);
                        }catch (NullPointerException e){
                            getPlayerPreferencesCallback.onCallback(null);
                            Log.e(TAG, "User does not have a recordGeolocation preference!");
                        }
                    } else {
                        getPlayerPreferencesCallback.onCallback(null);
                        Log.d(TAG, "No such document");
                    }
                } else {
                    getPlayerPreferencesCallback.onCallback(null);
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });
    }

    /**
     * Gets the ContactInfo object from the Player's DocumentReference
     * @param playerDocument the DocumentReference of the player to extract the ContactInfo
     *                       from
     * @param getContactInfoCallback the callback function to call with the extracted ContactInfo
     *                               object. Calls with null if the extraction is not successful
     */
    private void getContactInfo(DocumentReference playerDocument,
                                GetContactInfoCallback getContactInfoCallback){
        ContactInfo contactInfo = new ContactInfo();
        playerDocument.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                        try{
                            String email = (String) document.getData().get(FieldNames.EMAIL.fieldName);
                            contactInfo.setEmail(email);
                        }catch(NullPointerException e){
                            Log.d(TAG, "The contact info does not have an email");
                        }

                        try{
                            String phoneNumber = (String) document.getData().get(FieldNames.PHONE_NUMBER.fieldName);
                            contactInfo.setPhoneNumber(phoneNumber);
                        }catch(NullPointerException e){
                            Log.d(TAG, "The contact info does not have an email");
                        }

                        getContactInfoCallback.onGetContactInfoCallback(contactInfo);
                    } else {
                        getContactInfoCallback.onGetContactInfoCallback(null);
                        Log.d(TAG, "No such document");
                    }
                } else {
                    getContactInfoCallback.onGetContactInfoCallback(null);
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });
    }
}

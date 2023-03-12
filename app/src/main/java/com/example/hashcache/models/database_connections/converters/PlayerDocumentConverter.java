package com.example.hashcache.models.database_connections.converters;

import android.media.Image;
import android.util.Log;

import androidx.annotation.NonNull;

import com.example.hashcache.models.ScannableCode;
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

import java.util.Map;

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
        /**
         * Gets a player with everything but their PlayerWallet object
         */
        getPersonalDetails(documentReference, new GetPlayerCallback() {
            @Override
            public void onCallback(Player player) {
                /**
                 * Get the PlayerWallet object from the Document reference, after the
                 * previous callback has finished
                 */
                getPlayerWallet(documentReference.collection(CollectionNames.PLAYER_WALLET.collectionName),
                    new GetPlayerWalletCallback() {
                        @Override
                        public void onCallback(PlayerWallet playerWallet) {
                            if(playerWallet != null){
                                getPlayerCallback.onCallback(new Player(player.getUserId(), player.getUsername(),
                                        player.getContactInfo(), player.getPlayerPreferences(),
                                        playerWallet));
                            }else{
                                getPlayerCallback.onCallback(player);
                            }

                        }
                    });
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
        collectionReference.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            String scannableCodeId;
                            Image scannableCodeImage;

                            if(task.getResult().size()>0){
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    Map<String, Object> temp = document.getData();
                                    scannableCodeId = (String) document.getData()
                                            .get(FieldNames.SCANNABLE_CODE_ID.fieldName);
                                    //TODO: check for empty image and then add if it exists
                                    playerWallet.addScannableCode(scannableCodeId);
                                }
                            }

                            getPlayerWalletCallback.onCallback(playerWallet);
                        } else {
                            getPlayerWalletCallback.onCallback(null);
                        }
                    }
                });
    }

    /**
     * Gets everything but the PlayerWallet from the player's Document Reference
     * @param playerDocument the document with the Player's personal details
     * @param getPlayerCallback the callback function to call with the new Player object
     */
    private void getPersonalDetails(DocumentReference playerDocument,
                                GetPlayerCallback getPlayerCallback){
        ContactInfo contactInfo = new ContactInfo();
        PlayerPreferences playerPreferences = new PlayerPreferences();
        String[] username = new String[1];
        playerDocument.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
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

                        try{
                            boolean recordGeolocation = Boolean.parseBoolean(document.getData()
                                    .get(FieldNames.RECORD_GEOLOCATION.fieldName).toString()
                            );
                            playerPreferences.setGeoLocationRecording(recordGeolocation);
                        }catch (NullPointerException e){
                            Log.e(TAG, "User does not have a recordGeolocation preference!");
                        }

                        try{
                            username[0] = (String) document.getData().get(FieldNames.USERNAME.fieldName);
                        }catch (NullPointerException e){
                            Log.e(TAG, "User does not have a username!");
                        }

                        getPlayerCallback.onCallback(new Player(document.getId(), username[0],
                                contactInfo, playerPreferences, null));

                    } else {
                        getPlayerCallback.onCallback(null);
                        Log.d(TAG, "No such document");
                    }
                } else {
                    getPlayerCallback.onCallback(null);
                }
            }
        });
    }
}

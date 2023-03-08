package com.example.hashcache.database_connections.converters;

import android.media.Image;
import android.util.Log;

import androidx.annotation.NonNull;

import com.example.hashcache.database_connections.callbacks.GetContactInfoCallback;
import com.example.hashcache.database_connections.callbacks.GetPlayerCallback;
import com.example.hashcache.database_connections.callbacks.GetPlayerPreferencesCallback;
import com.example.hashcache.database_connections.callbacks.GetPlayerWalletCallback;
import com.example.hashcache.database_connections.values.CollectionNames;
import com.example.hashcache.database_connections.values.FieldNames;
import com.example.hashcache.models.Comment;
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

import java.util.ArrayList;

public class PlayerDocumentConverter {
    final String TAG = "Sample";

    public void getPlayerFromDocument(DocumentReference documentReference, GetPlayerCallback getPlayerCallback){
        String username = documentReference.getId();
        CollectionReference contactInfoReference = documentReference.collection(CollectionNames.CONTACT_INFO.collectionName);
        getContactInfo(contactInfoReference, new GetContactInfoCallback() {
            @Override
            public void onGetContactInfoCallback(ContactInfo contactInfo) {
                getPlayerPreferences(documentReference
                        .collection(CollectionNames.PLAYER_PREFERENCES.collectionName), new GetPlayerPreferencesCallback() {
                    @Override
                    public void onCallback(PlayerPreferences playerPreferences) {
                        getPlayerWallet(documentReference.collection(CollectionNames.PLAYER_WALLET.collectionName),
                                new GetPlayerWalletCallback() {
                                    @Override
                                    public void onCallback(PlayerWallet playerWallet) {
                                        getPlayerCallback.onCallback(
                                                new Player(username, contactInfo, playerPreferences,
                                                        playerWallet));
                                    }
                                });
                    }
                });
            }
        });

    }

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
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });
    }

    private void getPlayerPreferences(CollectionReference collectionReference,
                                      GetPlayerPreferencesCallback getPlayerPreferencesCallback){
        PlayerPreferences playerPreferences = new PlayerPreferences();

        DocumentReference docRef = collectionReference.document(
                CollectionNames.PLAYER_PREFERENCES.collectionName
        );
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
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
                            Log.e(TAG, "User does not have a recordGeolocation preference!");
                        }
                    } else {
                        Log.d(TAG, "No such document");
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });
    }

    private void getContactInfo(CollectionReference collectionReference,
                                GetContactInfoCallback getContactInfoCallback){
        ContactInfo contactInfo = new ContactInfo();

        DocumentReference docRef = collectionReference.document("contactInfo");
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                        try{
                            String email = (String) document.getData().get("email");
                            contactInfo.setEmail(email);
                        }catch(NullPointerException e){
                            Log.d(TAG, "The contact info does not have an email");
                        }

                        try{
                            String phoneNumber = (String) document.getData().get("phoneNumber");
                            contactInfo.setPhoneNumber(phoneNumber);
                        }catch(NullPointerException e){
                            Log.d(TAG, "The contact info does not have an email");
                        }

                        getContactInfoCallback.onGetContactInfoCallback(contactInfo);
                    } else {
                        Log.d(TAG, "No such document");
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });
    }
}

package com.example.hashcache.database_connections.converters;

import android.util.Log;

import androidx.annotation.NonNull;

import com.example.hashcache.models.ContactInfo;
import com.example.hashcache.models.Player;
import com.example.hashcache.models.PlayerPreferences;
import com.example.hashcache.models.PlayerWallet;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;

public class PlayerDocumentConverter {
    final String TAG = "Sample";

    public Player getPlayerFromDocument(DocumentReference documentReference){
        String username = documentReference.getId();
        CollectionReference contactInfoReference = documentReference.collection("contactInfo");
        ContactInfo contactInfo = getContactInfo(contactInfoReference);
        PlayerPreferences playerPreferences = getPlayerPreferences(documentReference.collection("playerPreferences"));
        PlayerWallet playerWallet = new PlayerWallet();

        return new Player(username, contactInfo, playerPreferences, playerWallet);
    }

    private PlayerPreferences getPlayerPreferences(CollectionReference collectionReference){
        PlayerPreferences playerPreferences = new PlayerPreferences();

        DocumentReference docRef = collectionReference.document("playerPreferences");
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                        try{
                            boolean recordGeolocation = Boolean.parseBoolean(document.getData()
                                    .get("recordGeolocationPreference").toString()
                            );
                            playerPreferences.setGeoLocationRecording(recordGeolocation);
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

        return playerPreferences;
    }

    private ContactInfo getContactInfo(CollectionReference collectionReference){
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
                    } else {
                        Log.d(TAG, "No such document");
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });

        return contactInfo;
    }
}

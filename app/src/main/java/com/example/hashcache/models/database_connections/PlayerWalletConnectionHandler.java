package com.example.hashcache.models.database_connections;

import android.media.Image;
import android.util.Log;

import androidx.annotation.NonNull;

import com.example.hashcache.models.Player;
import com.example.hashcache.models.PlayerWallet;
import com.example.hashcache.models.database_connections.callbacks.BooleanCallback;
import com.example.hashcache.models.database_connections.values.CollectionNames;
import com.example.hashcache.models.database_connections.values.FieldNames;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Handles the database operations on a player's PlayerWallet collection
 */
public class PlayerWalletConnectionHandler {
    final String TAG = "Sample";
    private FireStoreHelper fireStoreHelper;

    public PlayerWalletConnectionHandler(FireStoreHelper fireStoreHelper){
        this.fireStoreHelper = fireStoreHelper;
    }

    /**
     * Adds a scannableCode to an existing PlayerWallet collection
     * @param playerWalletCollection the collection which contains a player's scananble codes
     * @param scannableCodeId the id of the scannable code to add to the PlayerWallet collection
     * @param locationImage the image of where the user scanned the code
     * @param booleanCallback the callback function to call once the operation has finished. Calls
     *                        with true if the operation was successful, and false otherwise
     * @throws IllegalArgumentException if the PlayerWallet already has a scananbleCode with the given id
     */
    public void addScannableCodeDocument(CollectionReference playerWalletCollection,
                                         String scannableCodeId, Image locationImage,
                                         BooleanCallback booleanCallback){

        fireStoreHelper.documentWithIDExists(playerWalletCollection, scannableCodeId,
                new BooleanCallback() {
                    @Override
                    public void onCallback(Boolean isTrue) {
                        if(isTrue){
                            throw new IllegalArgumentException("A document already exists in the " +
                                    "PlayerWallet with the given scananbleCodeId!");
                        }
                        HashMap<String, String> scannableCodeIdData = new HashMap<>();
                        scannableCodeIdData.put(FieldNames.SCANNABLE_CODE_ID.fieldName, scannableCodeId);
                        if(locationImage != null){
                            //TODO: insert the image
                        }
                        DocumentReference playerWalletReference = playerWalletCollection.document(scannableCodeId);

                        fireStoreHelper.setDocumentReference(playerWalletReference, scannableCodeIdData,
                                booleanCallback);

                    }
                });
    }

    /**
     * Deletes a scannableCode from an existing PlayerWallet collection
     * @param playerWalletCollection the collection which contains a player's scananble codes
     * @param scannableCodeId the id of the scannable code to delete from the PlayerWallet collection
     * @param booleanCallback the callback function to call once the operation has finished. Calls
     *                        with true if the operation was successful, and false otherwise
     */
    public void deleteScannableCodeFromWallet(CollectionReference playerWalletCollection,
                                              String scannableCodeId, BooleanCallback booleanCallback){
        fireStoreHelper.documentWithIDExists(playerWalletCollection, scannableCodeId,
                new BooleanCallback() {
                    @Override
                    public void onCallback(Boolean isTrue) {
                        if(isTrue){
                            playerWalletCollection.document(scannableCodeId).delete()
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
                            throw new IllegalArgumentException("No scannable code exists with the given id!");
                        }
                    }
                });
    }
}
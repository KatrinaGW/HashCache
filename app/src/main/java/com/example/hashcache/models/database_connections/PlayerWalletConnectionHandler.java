package com.example.hashcache.models.database_connections;

import android.media.Image;

import com.example.hashcache.models.Player;
import com.example.hashcache.models.PlayerWallet;
import com.example.hashcache.models.database_connections.callbacks.BooleanCallback;
import com.example.hashcache.models.database_connections.values.CollectionNames;
import com.example.hashcache.models.database_connections.values.FieldNames;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;

import java.util.ArrayList;
import java.util.HashMap;

public class PlayerWalletConnectionHandler {
    final String TAG = "Sample";
    private FireStoreHelper fireStoreHelper;

    protected PlayerWalletConnectionHandler(){
        this.fireStoreHelper = new FireStoreHelper();
    }

    protected void addScannableCodeDocument(CollectionReference playerWalletCollection,
                                            String scannableCodeId, Image locationImage,
                                            BooleanCallback booleanCallback){
        HashMap<String, String> scannableCodeIdData = new HashMap<>();
        scannableCodeIdData.put(FieldNames.SCANNABLE_CODE_ID.fieldName, scannableCodeId);
        if(locationImage != null){
            //TODO: insert the image
        }
        DocumentReference playerWalletReference = playerWalletCollection.document(scannableCodeId);

        fireStoreHelper.setDocumentReference(playerWalletReference, scannableCodeIdData,
                booleanCallback);
    }

    private void addCodeToWallet(DocumentReference playerDocumentReference,
                                 PlayerWallet playerWallet, int index, BooleanCallback
                                         booleanCallback){
        if(index == playerWallet.getSize()){
            booleanCallback.onCallback(true);
        }else{
            String scannableCodeId = playerWallet.getScannedCodeIds().get(index);
            Image scannableCodeImage = playerWallet.getScannableCodeLocationImage(scannableCodeId);

            addScannableCodeDocument(playerDocumentReference.collection(CollectionNames
                            .PLAYER_WALLET
                            .collectionName
                    ),
                    scannableCodeId, scannableCodeImage, new BooleanCallback() {
                        @Override
                        public void onCallback(Boolean isTrue) {
                            addCodeToWallet(playerDocumentReference, playerWallet,
                                    index+1, booleanCallback);
                        }
                    });
        }
    }

    protected void setPlayerWallet(PlayerWallet playerWallet, DocumentReference playerDocumentReference,
                                 BooleanCallback booleanCallback){
        ArrayList<String> scannableCodeIds = playerWallet.getScannedCodeIds();

        if(scannableCodeIds.size()>0){
            addCodeToWallet(playerDocumentReference, playerWallet, 0, new BooleanCallback() {
                @Override
                public void onCallback(Boolean isTrue) {
                    if(isTrue){
                        booleanCallback.onCallback(true);
                    }
                }
            });
        }else{
            booleanCallback.onCallback(true);
        }
    }




}

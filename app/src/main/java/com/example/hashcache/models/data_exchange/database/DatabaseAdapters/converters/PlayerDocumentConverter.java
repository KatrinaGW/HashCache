package com.example.hashcache.models.data_exchange.database.DatabaseAdapters.converters;

import android.media.Image;
import android.util.Log;

import androidx.annotation.NonNull;

import com.example.hashcache.models.data_exchange.database.DatabaseAdapters.callbacks.GetPlayerCallback;
import com.example.hashcache.models.data_exchange.database.DatabaseAdapters.callbacks.GetPlayerWalletCallback;
import com.example.hashcache.models.data_exchange.database.values.CollectionNames;
import com.example.hashcache.models.data_exchange.database.values.FieldNames;
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
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

/**
 * Handles the conversion between a DocumentReference and a PlayerObject
 */
public class PlayerDocumentConverter {
    final String TAG = "Sample";

    /**
     * Converts a DocumentReference into a PlayerObject
     * @param documentReference
     * @return cf the CompletableFuture with the PlayerObject
     */
    public CompletableFuture<Player> getPlayerFromDocument(DocumentReference documentReference){
        CompletableFuture<Player> cf = new CompletableFuture<>();
        /**
         * Gets a player with everything but their PlayerWallet object
         */
        getPersonalDetails(documentReference)
                .thenAccept(playerDetails -> {
                    getPlayerWallet(documentReference.collection(CollectionNames.PLAYER_WALLET.collectionName))
                            .thenAccept(playerWallet -> {
                                cf.complete(new Player(playerDetails.getUserId(),
                                        playerDetails.getUsername(), playerDetails.getContactInfo(),
                                        playerDetails.getPlayerPreferences(), playerWallet));
                            })
                            .exceptionally(new Function<Throwable, Void>() {
                                @Override
                                public Void apply(Throwable throwable) {
                                    System.out.println("There was an error getting the scannableCodes.");
                                    cf.completeExceptionally(throwable);
                                    return null;
                                }
                            });
                })
                        .exceptionally(new Function<Throwable, Void>() {
                            @Override
                            public Void apply(Throwable throwable) {
                                System.out.println("There was an error getting the scannableCodes.");
                                cf.completeExceptionally(throwable);
                                return null;
                            }
                        });

        return cf;
    }

    /**
     * Get the PlayerWallet object from the player's PlayerWallet collection
     * @param collectionReference the PlayerWallet collection with the scannable code ids and their
     *                            images in it
     * @return cf the CompletableFuture with the PlayerWallet
     */
    private CompletableFuture<PlayerWallet> getPlayerWallet(CollectionReference collectionReference){
        CompletableFuture<PlayerWallet> cf = new CompletableFuture<>();
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

                            cf.complete(playerWallet);
                        } else {
                            cf.completeExceptionally(new Exception("Something went wrong " +
                                    "while getting the player wallet"));
                        }
                    }
                });
        return cf;
    }

    /**
     * Gets everything but the PlayerWallet from the player's Document Reference
     * @param playerDocument the document with the Player's personal details
     * @return cf the CompleteableFuture with the Player object without its wallet
     */
    private CompletableFuture<Player> getPersonalDetails(DocumentReference playerDocument){
        CompletableFuture<Player> cf = new CompletableFuture<>();
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

                        cf.complete(new Player(document.getId(), username[0],
                                contactInfo, playerPreferences, null));

                    } else {
                        cf.completeExceptionally(new Exception("No player document exists"));
                        Log.d(TAG, "No such document");
                    }
                } else {
                    cf.completeExceptionally(new Exception(task.getException()));
                }
            }
        });
        return cf;
    }
}

package com.example.hashcache.models.database.DatabaseAdapters.converters;

import android.media.Image;
import android.util.Log;

import androidx.annotation.NonNull;

import com.example.hashcache.models.database.values.CollectionNames;
import com.example.hashcache.models.database.values.FieldNames;
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
import com.google.protobuf.FieldMask;

import java.lang.reflect.Field;
import java.net.FileNameMap;
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
        CompletableFuture.runAsync(()->{
            CompletableFuture<Player> thing = getPersonalDetails(documentReference);
                    thing.thenAccept(playerDetails -> {
                        getPlayerWallet(documentReference.collection(CollectionNames.PLAYER_WALLET.collectionName), documentReference)
                                .thenAccept(playerWallet -> {
                                    getPlayerScores(documentReference, playerWallet).thenAccept(pWallet -> {
                                        cf.complete(new Player(playerDetails.getUserId(),
                                                playerDetails.getUsername(), playerDetails.getContactInfo(),
                                                playerDetails.getPlayerPreferences(), pWallet));
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
                    })
                    .exceptionally(new Function<Throwable, Void>() {
                        @Override
                        public Void apply(Throwable throwable) {
                            System.out.println("There was an error getting the scannableCodes.");
                            cf.completeExceptionally(throwable);
                            return null;
                        }
                    });
        });

        return cf;
    }

    /**
     * Get the PlayerWallet object from the player's PlayerWallet collection
     * @param collectionReference the PlayerWallet collection with the scannable code ids and their
     *                            images in it
     * @return cf the CompletableFuture with the PlayerWallet
     */
    private CompletableFuture<PlayerWallet> getPlayerWallet(CollectionReference collectionReference, DocumentReference documentReference){
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

    /**
     * Take in a playerWallet and fills the score area with those in the player document in the database.
     * Used when first fetching the player information from the database.
     * @param playerDocument a access point to get player document information
     * @param playerWallet the wallet you want to update with the new player scores
     * @return A Completable futre of the Player wallet
     */
    private CompletableFuture<PlayerWallet> getPlayerScores(DocumentReference playerDocument, PlayerWallet playerWallet) {
        CompletableFuture<PlayerWallet> cf = new CompletableFuture<>();
        playerDocument.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();

                    String strTotalScore = (String)
                            document.getData().get(FieldNames.TOTAL_SCORE.fieldName);
                    if(strTotalScore!=null){
                        playerWallet.setTotalScore(Long.parseLong(strTotalScore));
                    }

                    String strQRCount = (String) document.getData().get(FieldNames.QR_COUNT.fieldName);
                    if(strQRCount!=null){
                        playerWallet.setQRCount(Long.parseLong(strQRCount));
                    }

                    String strMaxScore = (String) document.getData().get(FieldNames.QR_COUNT.fieldName);

                    if(strMaxScore!=null){
                        playerWallet.updateMaxScore(Long.parseLong(strMaxScore));
                    }

                    cf.complete(playerWallet);
                } else {
                    cf.completeExceptionally(new Exception(task.getException()));
                }
            }
        });
        return cf;
    }

}

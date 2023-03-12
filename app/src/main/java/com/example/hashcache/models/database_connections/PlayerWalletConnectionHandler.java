package com.example.hashcache.models.database_connections;

import android.media.Image;
import android.util.Log;

import androidx.annotation.NonNull;

import com.example.hashcache.models.ScannableCode;
import com.example.hashcache.models.database_connections.callbacks.BooleanCallback;
import com.example.hashcache.models.database_connections.callbacks.GetScannableCodeCallback;
import com.example.hashcache.models.database_connections.converters.ScannableCodeDocumentConverter;
import com.example.hashcache.models.database_connections.values.CollectionNames;
import com.example.hashcache.models.database_connections.values.FieldNames;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import org.checkerframework.checker.units.qual.A;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Handles the database operations on a player's PlayerWallet collection
 */
public class PlayerWalletConnectionHandler {
    final String TAG = "Sample";
    private FirebaseFirestore db;
    private FireStoreHelper fireStoreHelper;
    private static PlayerWalletConnectionHandler INSTANCE;

    public PlayerWalletConnectionHandler(FireStoreHelper fireStoreHelper){
        this.fireStoreHelper = fireStoreHelper;
    }

    public PlayerWalletConnectionHandler(FirebaseFirestore db){
        this.db = db;
    }

    public static PlayerWalletConnectionHandler getInstance(FireStoreHelper fireStoreHelper){
        if(INSTANCE == null){
            INSTANCE = new PlayerWalletConnectionHandler(fireStoreHelper);
        }
        return INSTANCE;
    }

    public static PlayerWalletConnectionHandler getInstance(FirebaseFirestore db){
        if(INSTANCE == null){
            INSTANCE = new PlayerWalletConnectionHandler(db);
        }
        return INSTANCE;
    }

    public static PlayerWalletConnectionHandler getInstance(){
        if(INSTANCE == null){
            INSTANCE = new PlayerWalletConnectionHandler(FirebaseFirestore.getInstance());
        }
        return INSTANCE;
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

    /**
     * Gets the total score of all scannableCodeIds in a list
     * @param scannableCodeIds the ids of codes to sum
     * @return cf the CompletableFuture that contains the total score
     */
    public CompletableFuture<Integer> getPlayerWalletTotalScore(ArrayList<String> scannableCodeIds){
        CompletableFuture<Integer> cf = new CompletableFuture<>();
        AtomicInteger totalScore = new AtomicInteger(0);

        CompletableFuture.runAsync(() -> {
            Query docRef = db.collection(CollectionNames.SCANNABLE_CODES.collectionName);
            docRef.get().addOnCompleteListener(task -> {
                if(task.isSuccessful()){
                    for(QueryDocumentSnapshot document : task.getResult()){
                        if(scannableCodeIds.contains(document.getId())){
                            totalScore.addAndGet(Integer.parseInt(document.getData().get(FieldNames.GENERATED_SCORE.fieldName)
                                    .toString()));
                        }
                    }
                    cf.complete(totalScore.intValue());
                }
                else{
                    cf.completeExceptionally(new Exception("[usernameExists] Could not complete query"));
                }
            });
        });
        return cf;
    }

    /**
     * Gets the the highest and lowest scores from a list of scannableCodes
     *
     * @param scannableCodeIds the list of scannableIds to get the highest and lowest scores from
     * @return a CompletableFuture that will return the score stats for the given player
     */
    public CompletableFuture<HashMap<String, Integer>> getPlayerWalletTopLowScores(ArrayList<String> scannableCodeIds){
        CompletableFuture<HashMap<String, Integer>> cf = new CompletableFuture<>();
        HashMap<String, Integer> scoreStats = new HashMap<>();

        CompletableFuture.runAsync(() -> {
            Query docRef = db.collection(CollectionNames.SCANNABLE_CODES.collectionName);
            docRef.get().addOnCompleteListener(task -> {
                if(task.isSuccessful()){
                    int highestScore = 0;
                    int lowestScore = Integer.MAX_VALUE;
                    int currentScore;

                    for(QueryDocumentSnapshot document : task.getResult()){
                        if(scannableCodeIds.contains(document.getId())){
                            currentScore = Integer.parseInt(document.getData().get(FieldNames.GENERATED_SCORE.fieldName).toString());
                            if(currentScore<lowestScore){
                                lowestScore = currentScore;
                            }
                            if(currentScore>highestScore){
                                highestScore = currentScore;
                            }
                        }
                    }

                    scoreStats.put("highestScore", highestScore);
                    scoreStats.put("lowestScore", lowestScore);
                    cf.complete(scoreStats);
                }
                else{
                    cf.completeExceptionally(new Exception("[usernameExists] Could not complete query"));
                }
            });
        });
        return cf;
    }
}
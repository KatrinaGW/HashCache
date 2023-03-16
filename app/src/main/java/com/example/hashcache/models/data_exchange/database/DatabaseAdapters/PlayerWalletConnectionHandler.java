package com.example.hashcache.models.data_exchange.database.DatabaseAdapters;

import android.media.Image;
import android.util.Log;

import androidx.annotation.NonNull;

import com.example.hashcache.models.ScannableCode;
import com.example.hashcache.models.data_exchange.database.DatabaseAdapters.callbacks.BooleanCallback;
import com.example.hashcache.models.data_exchange.database.Database;
import com.example.hashcache.models.data_exchange.database.values.CollectionNames;
import com.example.hashcache.models.data_exchange.database.values.FieldNames;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import org.checkerframework.checker.units.qual.C;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Handles the database operations on a player's PlayerWallet collection
 */
public class PlayerWalletConnectionHandler {
    final String TAG = "Sample";
    private FirebaseFirestore db;
    private FireStoreHelper fireStoreHelper;
    private static PlayerWalletConnectionHandler INSTANCE;

    public PlayerWalletConnectionHandler(FireStoreHelper fireStoreHelper) {
        this.fireStoreHelper = fireStoreHelper;
    }

    public ListenerRegistration getPlayerWalletChangeListener(String userId, BooleanCallback callback) {
        CompletableFuture<ArrayList<String>> cf = new CompletableFuture<>();
        CollectionReference scannedCodeCollection = db.collection(CollectionNames.PLAYERS.collectionName)
                .document(userId)
                .collection(CollectionNames.PLAYER_WALLET.collectionName);

        ListenerRegistration reg = scannedCodeCollection.addSnapshotListener((snapshot, e) -> {
            Log.d("FIRESTORE WALLET LISTENER", "Wallet has been changed");
            callback.onCallback(true);
        });
        return reg;
    }

    public PlayerWalletConnectionHandler(FirebaseFirestore db) {
        this.db = db;
    }

    public static PlayerWalletConnectionHandler getInstance(FireStoreHelper fireStoreHelper) {
        if (INSTANCE == null) {
            INSTANCE = new PlayerWalletConnectionHandler(fireStoreHelper);
        }
        return INSTANCE;
    }

    public static PlayerWalletConnectionHandler getInstance(FirebaseFirestore db) {
        if (INSTANCE == null) {
            INSTANCE = new PlayerWalletConnectionHandler(db);
        }
        return INSTANCE;
    }

    public static PlayerWalletConnectionHandler getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new PlayerWalletConnectionHandler(FirebaseFirestore.getInstance());
        }
        return INSTANCE;
    }

    /**
     * Adds a scannableCode to an existing PlayerWallet collection
     * 
     * @param playerWalletCollection the collection which contains a player's
     *                               scananble codes
     * @param scannableCodeId        the id of the scannable code to add to the
     *                               PlayerWallet collection
     * @param locationImage          the image of where the user scanned the code
     * @return cf the CompletableFuture indicating if the operation was successful or not
     * @throws IllegalArgumentException if the PlayerWallet already has a
     *                                  scananbleCode with the given id
     */
    public CompletableFuture<Void> addScannableCodeDocument(CollectionReference playerWalletCollection,
            String scannableCodeId, Image locationImage) {
        CompletableFuture<Void> cf = new CompletableFuture<>();
        fireStoreHelper.documentWithIDExists(playerWalletCollection, scannableCodeId,
                new BooleanCallback() {
                    @Override
                    public void onCallback(Boolean isTrue) {
                        if (isTrue) {
                            throw new IllegalArgumentException("A document already exists in the " +
                                    "PlayerWallet with the given scananbleCodeId!");
                        }
                        HashMap<String, String> scannableCodeIdData = new HashMap<>();
                        scannableCodeIdData.put(FieldNames.SCANNABLE_CODE_ID.fieldName, scannableCodeId);
                        if (locationImage != null) {
                            // TODO: insert the image
                        }
                        DocumentReference playerWalletReference = playerWalletCollection.document(scannableCodeId);

                        fireStoreHelper.setDocumentReference(playerWalletReference, scannableCodeIdData,
                                new BooleanCallback() {
                                    @Override
                                    public void onCallback(Boolean isTrue) {
                                        if(isTrue){
                                            cf.complete(null);
                                        }else{
                                            cf.completeExceptionally(new Exception(
                                                    "Something went wrong while adding the scananble" +
                                                            "code document"
                                            ));
                                        }
                                    }
                                });

                    }
                });
        return cf;
    }

    public CompletableFuture<Boolean> scannableCodeExistsOnPlayerWallet(String userId, String scannableCodeId) {
        DocumentReference documentReference = db.collection(CollectionNames.PLAYERS.collectionName).document(userId)
                .collection(CollectionNames.PLAYER_WALLET.collectionName).document(scannableCodeId);
        CompletableFuture<Boolean> cf = new CompletableFuture<>();
        CompletableFuture.runAsync(() -> {
            documentReference.get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    cf.complete(document.exists());

                } else {
                    Log.d(TAG, "Failed with: ", task.getException());
                    cf.completeExceptionally(task.getException());
                }
            });
        });
        return cf;
    }

    /**
     * Deletes a scannableCode from an existing PlayerWallet collection
     * 
     * @param playerWalletCollection the collection which contains a player's
     *                               scananble codes
     * @param scannableCodeId        the id of the scannable code to delete from the
     *                               PlayerWallet collection
     * @return cf the CompletableFuture indicating if the operation was a success or not
     */
    public CompletableFuture<Boolean> deleteScannableCodeFromWallet(CollectionReference playerWalletCollection,
            String scannableCodeId) {
        CompletableFuture<Boolean> cf = new CompletableFuture<>();

        fireStoreHelper.documentWithIDExists(playerWalletCollection, scannableCodeId,
                new BooleanCallback() {
                    @Override
                    public void onCallback(Boolean isTrue) {
                        if (isTrue) {
                            playerWalletCollection.document(scannableCodeId).delete()
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            Log.d(TAG, "DocumentSnapshot successfully deleted!");
                                            cf.complete(true);
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Log.w(TAG, "Error deleting document", e);
                                            cf.completeExceptionally(new Exception("Error deleting" +
                                                    "document"));
                                        }
                                    });
                        } else {
                            throw new IllegalArgumentException("No scannable code exists with the given id!");
                        }
                    }
                });
        return cf;
    }

    /**
     * Gets the total score of all scannableCodeIds in a list
     * 
     * @param scannableCodeIds the ids of codes to sum
     * @return cf the CompletableFuture that contains the total score
     */
    public CompletableFuture<Long> getPlayerWalletTotalScore(ArrayList<String> scannableCodeIds) {
        CompletableFuture<Long> cf = new CompletableFuture<>();
        AtomicLong totalScore = new AtomicLong(0);

        CompletableFuture.runAsync(() -> {
            Query docRef = db.collection(CollectionNames.SCANNABLE_CODES.collectionName);
            docRef.get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        if (scannableCodeIds.contains(document.getId())) {
                            totalScore.addAndGet(Long
                                    .parseLong((String) document.getData().get(FieldNames.GENERATED_SCORE.fieldName)));
                        }
                    }
                    cf.complete(totalScore.longValue());
                } else {
                    cf.completeExceptionally(new Exception("[usernameExists] Could not complete query"));
                }
            });
        });
        return cf;
    }

    /**
     * Gets the the highest score from a list of scannableCodes
     *
     * @param scannableCodeIds the list of scannableIds to get the highest score
     *                         from
     * @return a CompletableFuture that will return the highest scoring
     *         ScannableCode
     */
    public CompletableFuture<ScannableCode> getPlayerWalletTopScore(ArrayList<String> scannableCodeIds) {
        CompletableFuture<ScannableCode> cf = new CompletableFuture<>();
        HashMap<String, Integer> scoreStats = new HashMap<>();

        CompletableFuture.runAsync(() -> {
            Database.getInstance().getScannableCodesByIdInList(scannableCodeIds).thenAccept(
                    scannableCodes -> {
                        if (scannableCodes.size() > 0) {
                            long highestScore = 0;
                            ScannableCode highestScoring = scannableCodes.get(0);

                            for (int i = 0; i < scannableCodes.size(); i++) {
                                ScannableCode scannableCode = scannableCodes.get(i);
                                if (scannableCode.getHashInfo().getGeneratedScore() > highestScore) {
                                    highestScore = scannableCode.getHashInfo().getGeneratedScore();
                                    highestScoring = scannableCode;
                                }
                            }
                            cf.complete(highestScoring);
                        } else {
                            cf.completeExceptionally(new Exception("No scannablecodes could be found" +
                                    "for the given IDs!"));
                        }

                    });
        });
        return cf;
    }

    /**
     * Gets the the lowest score from a list of scannableCodes
     *
     * @param scannableCodeIds the list of scannableIds to get the lowest score from
     * @return a CompletableFuture that will return the lowest scoring ScannableCode
     */
    public CompletableFuture<ScannableCode> getPlayerWalletLowScore(ArrayList<String> scannableCodeIds) {
        CompletableFuture<ScannableCode> cf = new CompletableFuture<>();
        HashMap<String, Integer> scoreStats = new HashMap<>();

        CompletableFuture.runAsync(() -> {
            Database.getInstance().getScannableCodesByIdInList(scannableCodeIds).thenAccept(
                    scannableCodes -> {
                        if (scannableCodes.size() > 0) {
                            long lowestScore = Long.MAX_VALUE;
                            ScannableCode lowestScoring = scannableCodes.get(0);

                            for (int i = 1; i < scannableCodes.size(); i++) {
                                ScannableCode scannableCode = scannableCodes.get(i);
                                if (scannableCode.getHashInfo().getGeneratedScore() < lowestScore) {
                                    lowestScore = scannableCode.getHashInfo().getGeneratedScore();
                                    lowestScoring = scannableCode;
                                }
                            }
                            cf.complete(lowestScoring);
                        } else {
                            cf.completeExceptionally(new Exception("No scannablecodes could be found" +
                                    "for the given IDs!"));
                        }

                    });
        });
        return cf;
    }
}
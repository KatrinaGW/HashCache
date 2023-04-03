package com.example.hashcache.models.database.DatabaseAdapters;

import android.media.Image;
import android.util.Log;

import androidx.annotation.NonNull;

import com.example.hashcache.models.Comment;
import com.example.hashcache.models.PlayerWallet;
import com.example.hashcache.models.ScannableCode;
import com.example.hashcache.models.database.DatabaseAdapters.callbacks.BooleanCallback;
import com.example.hashcache.models.database.Database;
import com.example.hashcache.models.database.DatabasePort;
import com.example.hashcache.models.database.values.CollectionNames;
import com.example.hashcache.models.database.values.FieldNames;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Function;

/**
 * Handles the database operations on a player's PlayerWallet collection
 */
public class PlayerWalletDatabaseAdapter {
    final String TAG = "Sample";
    private FirebaseFirestore db;
    private FireStoreHelper fireStoreHelper;
    private static PlayerWalletDatabaseAdapter INSTANCE;

    /**
     * Create a new PlayerWalletDatabaseAdapter
     * @param fireStoreHelper the firestore helper to use with the new PlayerWalletDatabaseAdapter
     */
    public PlayerWalletDatabaseAdapter(FireStoreHelper fireStoreHelper) {
        this.fireStoreHelper = fireStoreHelper;
    }

    /**
     * Get the listener for the Player Wallet
     * @param userId the userId to get the wallet listener for
     * @param callback the callback function to call once the listener has been added
     * @return the listener now attached to the user's wallet
     */
    public ListenerRegistration getPlayerWalletChangeListener(String userId, BooleanCallback callback) {
        CollectionReference scannedCodeCollection = db.collection(CollectionNames.PLAYERS.collectionName)
                .document(userId)
                .collection(CollectionNames.PLAYER_WALLET.collectionName);

        ListenerRegistration reg = scannedCodeCollection.addSnapshotListener((snapshot, e) -> {
            Log.d("FIRESTORE WALLET LISTENER", "Wallet has been changed");
            callback.onCallback(true);
        });
        return reg;
    }

    /**
     * Creates an instance of this class with the injected database
     * @param db
     */
    public PlayerWalletDatabaseAdapter(FirebaseFirestore db) {
        this.db = db;
    }

    /**
     * Resets the static instance to null
     */
    public void resetInstance(){
        INSTANCE = null;
    }

    /**
     * Gets the current static instance of this class or creates a new one with the injected
     * FireStoreHelper
     * @param fireStoreHelper the instance of the FireStoreHelper to use
     * @return INSTANCE the static instance of this class
     */
    public static PlayerWalletDatabaseAdapter getInstance(FireStoreHelper fireStoreHelper) {
        if (INSTANCE == null) {
            INSTANCE = new PlayerWalletDatabaseAdapter(fireStoreHelper);
        }
        return INSTANCE;
    }

    /**
     * Gets the current static instance of this class or creates a new one with the injected
     * FirebaseFirestore
     * @param db the instance of the FirebaseFirestore to use
     * @return INSTANCE the static instance of this class
     */
    public static PlayerWalletDatabaseAdapter getInstance(FirebaseFirestore db) {
        if (INSTANCE == null) {
            INSTANCE = new PlayerWalletDatabaseAdapter(db);
        }
        return INSTANCE;
    }

    /**
     * Gets the current static instance of this class or creates a new one with a FireStoreHelper
     * @return INSTANCE the static instance of this class
     */
    public static PlayerWalletDatabaseAdapter getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new PlayerWalletDatabaseAdapter(FirebaseFirestore.getInstance());
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

        fireStoreHelper.documentWithIDExists(playerWalletCollection, scannableCodeId)
                        .thenAccept(exists -> {
                            if (exists) {
                                cf.completeExceptionally(new Exception("The scannable code with" +
                                        "the given id already exists!"));
                            }
                            else{
                                HashMap<String, String> scannableCodeIdData = new HashMap<>();
                                scannableCodeIdData.put(FieldNames.SCANNABLE_CODE_ID.fieldName, scannableCodeId);
                                if (locationImage != null) {
                                    // TODO: insert the image
                                }
                                DocumentReference playerWalletReference = playerWalletCollection.document(scannableCodeId);

                                CompletableFuture.runAsync(() -> {
                                    fireStoreHelper.setDocumentReference(playerWalletReference,
                                                    scannableCodeIdData)
                                            .thenAccept(successful -> {
                                                if (successful) {
                                                    cf.complete(null);
                                                } else {
                                                    cf.completeExceptionally(new Exception(
                                                            "Something went wrong while adding the scananble" +
                                                                    "code document"
                                                    ));
                                                }
                                            })
                                            .exceptionally(new Function<Throwable, Void>() {
                                                @Override
                                                public Void apply(Throwable throwable) {
                                                    cf.completeExceptionally(throwable);
                                                    return null;
                                                }
                                            });
                                });
                            }

                        })
                .exceptionally(new Function<Throwable, Void>() {
                    @Override
                    public Void apply(Throwable throwable) {
                        cf.completeExceptionally(throwable);
                        return null;
                    }
                });
        return cf;
    }

    /**
     * Checks if a player has a scananbleCode
     * @param userId the player to check the scannable code on
     * @param scannableCodeId the scannable code to check for on the player
     * @return cf the CompletableFuture with a boolean value indicating if the player has the
     * scannableCode or not
     */
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

        CompletableFuture.runAsync(() -> {
           fireStoreHelper.documentWithIDExists(playerWalletCollection, scannableCodeId)
                   .thenAccept(exists -> {
                       if (exists) {
                           playerWalletCollection.document(scannableCodeId).delete()
                                   .addOnSuccessListener(new OnSuccessListener<Void>() {
                                       @Override
                                       public void onSuccess(Void aVoid) {
                                           cf.complete(true);
                                       }
                                   })
                                   .addOnFailureListener(new OnFailureListener() {
                                       @Override
                                       public void onFailure(@NonNull Exception e) {
                                           Log.w(TAG, "Error deleting document", e);
                                           cf.completeExceptionally(e);
                                       }
                                   });
                       } else {
                           throw new IllegalArgumentException("No scannable code exists with the given id!");
                       }
                   })
                   .exceptionally(new Function<Throwable, Void>() {
                       @Override
                       public Void apply(Throwable throwable) {
                           cf.completeExceptionally(throwable);
                           return null;
                       }
                   });
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
     * @param databasePort the instance of the databasePort to use
     * @return a CompletableFuture that will return the highest scoring
     *         ScannableCode
     */
    public CompletableFuture<ScannableCode> getPlayerWalletTopScore(ArrayList<String> scannableCodeIds,
                                                                    DatabasePort databasePort) {
        CompletableFuture<ScannableCode> cf = new CompletableFuture<>();

        CompletableFuture.runAsync(() -> {
            databasePort.getScannableCodesByIdInList(scannableCodeIds).thenAccept(
                    scannableCodes -> {
                        if (scannableCodes.size() > 0) {
                            ScannableCode highestScoring = scannableCodes.get(0);
                            long highestScore = highestScoring.getHashInfo().getGeneratedScore();

                            for (int i = 0; i < scannableCodes.size(); i++) {
                                ScannableCode scannableCode = scannableCodes.get(i);
                                if (scannableCode.getHashInfo().getGeneratedScore() > highestScore) {
                                    highestScore = scannableCode.getHashInfo().getGeneratedScore();
                                    highestScoring = scannableCode;
                                }
                            }
                            cf.complete(highestScoring);
                        } else {
                            cf.complete(null);
                        }

                    }).exceptionally(new Function<Throwable, Void>() {
                @Override
                public Void apply(Throwable throwable) {
                    cf.completeExceptionally(throwable);
                    return null;
                }
            });
        });
        return cf;
    }

    /**
     * Gets the the lowest score from a list of scannableCodes
     *
     * @param scannableCodeIds the list of scannableIds to get the lowest score from
     * @param databasePort the instance of the databaseport to use
     * @return a CompletableFuture that will return the lowest scoring ScannableCode
     */
    public CompletableFuture<ScannableCode> getPlayerWalletLowScore(ArrayList<String> scannableCodeIds,
                                                                    DatabasePort databasePort) {
        CompletableFuture<ScannableCode> cf = new CompletableFuture<>();

        CompletableFuture.runAsync(() -> {
            databasePort.getScannableCodesByIdInList(scannableCodeIds).thenAccept(
                    scannableCodes -> {
                        if (scannableCodes.size() > 0) {
                            ScannableCode lowestScoring = scannableCodes.get(0);
                            long lowestScore = lowestScoring.getHashInfo().getGeneratedScore();

                            for (int i = 1; i < scannableCodes.size(); i++) {
                                ScannableCode scannableCode = scannableCodes.get(i);
                                if (scannableCode.getHashInfo().getGeneratedScore() < lowestScore) {
                                    lowestScore = scannableCode.getHashInfo().getGeneratedScore();
                                    lowestScoring = scannableCode;
                                }
                            }
                            cf.complete(lowestScoring);
                        } else {
                            cf.complete(null);
                        }

                    }).exceptionally(new Function<Throwable, Void>() {
                @Override
                public Void apply(Throwable throwable) {
                    cf.completeExceptionally(throwable);
                    return null;
                }
            });
        });
        return cf;
    }

    private CompletableFuture<Void> setPlayerScores(DocumentReference playerDocument,
                                               PlayerWallet playerWallet) {
        CompletableFuture<Void> cf = new CompletableFuture<>();

        HashMap<String, Object> data = new HashMap<>();
        data.put(FieldNames.TOTAL_SCORE.fieldName, playerWallet.getTotalScore());
        data.put(FieldNames.MAX_SCORE.fieldName, playerWallet.getMaxScore());
        data.put(FieldNames.QR_COUNT.fieldName, playerWallet.getQrCount());

        CompletableFuture.runAsync(() -> {
            fireStoreHelper.addUpdateManyFieldsIntoDocument(playerDocument, data)
                    .thenAccept(success -> {
                        if(success){
                            cf.complete(null);
                        }else{
                            cf.completeExceptionally(
                                    new Exception("Something went wrong while setting the" +
                                            "user scores!")
                            );
                        }
                    })
                    .exceptionally(new Function<Throwable, Void>() {
                        @Override
                        public Void apply(Throwable throwable) {
                            cf.completeExceptionally(throwable);
                            return null;
                        }
                    });
        });

        return  cf;
    }

    /**
     * Updates the player score values on the player document
     * @param userId the id of the user whose score values need to be updated
     * @param playerWallet the wallet of the player
     * @return cf the CompletableFuture that completes with true if the operation was successful
     */
    public CompletableFuture<Boolean> updatePlayerScores(String userId, PlayerWallet playerWallet,
                                                         FireStoreHelper fireStoreHelper) {
        CompletableFuture<Boolean> cf = new CompletableFuture<>();
        CollectionReference collectionReference = db.collection(CollectionNames.PLAYERS.collectionName);
        this.fireStoreHelper = fireStoreHelper;

        CompletableFuture.runAsync(() -> {
            fireStoreHelper.documentWithIDExists(collectionReference, userId)
                    .thenAccept(exists -> {
                        if(exists){
                            DocumentReference playerDocument = collectionReference.document(userId);
                            setPlayerScores(playerDocument, playerWallet)
                                    .thenAccept(nullValue -> {
                                        cf.complete(true);
                                    })
                                    .exceptionally(new Function<Throwable, Void>() {
                                        @Override
                                        public Void apply(Throwable throwable) {
                                            cf.completeExceptionally(throwable);
                                            return null;
                                        }
                                    });
                        }else{
                            cf.completeExceptionally(new IllegalArgumentException("The player" +
                                    "id doesn't exist!"));
                        }
                    })
                    .exceptionally(new Function<Throwable, Void>() {
                        @Override
                        public Void apply(Throwable throwable) {
                            cf.completeExceptionally(throwable);
                            return null;
                        }
                    });
        });

        return cf;
    }

}
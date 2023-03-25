package com.example.hashcache.models.database.DatabaseAdapters;

import android.util.Log;
import android.util.Pair;

import androidx.annotation.NonNull;

import com.example.hashcache.models.database.DatabaseAdapters.callbacks.GetPlayerCallback;
import com.example.hashcache.models.database.DatabaseAdapters.callbacks.GetScannableCodeCallback;
import com.example.hashcache.models.database.DatabaseAdapters.converters.ScannableCodeDocumentConverter;
import com.example.hashcache.models.database.values.CollectionNames;
import com.example.hashcache.models.Comment;
import com.example.hashcache.models.ScannableCode;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

/**
 * Handles all calls to the Firebase ScannableCodes database
 */
public class ScannableCodesDatabaseAdapter {
    private FirebaseFirestore db;
    private CollectionReference collectionReference;
    final String TAG = "Sample";
    private ScannableCodeDocumentConverter scannableCodeDocumentConverter;
    private FireStoreHelper fireStoreHelper;
    private static ScannableCodesDatabaseAdapter INSTANCE;

    /**
     * Constructor for the ScannableCodeConnectionHandler class which takes in
     * dependencies
     *
     * @param scannableCodeDocumentConverter the instance of the
     *                                       ScannableCodeDocumentConverter
     *                                       to use to convert documents to
     *                                       ScannableCode objects
     * @param fireStoreHelper                the instance of the FireStoreHelper
     *                                       class to use to perform
     *                                       common FireStore actions
     * @param db                             the instance of the database to use to
     *                                       connect to the ScannableCodes
     *                                       collection
     * @return ScannableCodesConnectionHandler.INSTANCE the newly created static
     * instance of the
     * ScannableCodesConnectionHandler class
     */
    private ScannableCodesDatabaseAdapter(ScannableCodeDocumentConverter scannableCodeDocumentConverter,
                                          FireStoreHelper fireStoreHelper, FirebaseFirestore db) {
        this.scannableCodeDocumentConverter = scannableCodeDocumentConverter;
        this.fireStoreHelper = fireStoreHelper;
        this.db = db;
        collectionReference = db.collection(CollectionNames.SCANNABLE_CODES.collectionName);
    }

    /**
     * \
     * Makes and gets the static instance of the ScannableCodesConnectionHandler
     * class with specific
     * dependencies
     *
     * @param scannableCodeDocumentConverter the instance of the
     *                                       ScannableCodeDocumentConverter
     *                                       to use to convert documents to
     *                                       ScannableCode objects
     * @param fireStoreHelper                the instance of the FireStoreHelper
     *                                       class to use to perform
     *                                       common FireStore actions
     * @param db                             the instance of the database to use to
     *                                       connect to the ScannableCodes
     *                                       collection
     * @return ScannableCodesConnectionHandler.INSTANCE the newly created static
     * instance of the
     * ScannableCodesConnectionHandler class
     * @throws IllegalArgumentException if the static instance of the
     *                                  ScannableCodesConnectionHandler
     *                                  class has already been initialized
     */
    public static ScannableCodesDatabaseAdapter makeInstance(
            ScannableCodeDocumentConverter scannableCodeDocumentConverter,
            FireStoreHelper fireStoreHelper,
            FirebaseFirestore db) {
        if (INSTANCE != null) {
            throw new IllegalArgumentException("ScannableCodesConnectionHandler INSTANCE" +
                    "already exists!");
        }

        INSTANCE = new ScannableCodesDatabaseAdapter(scannableCodeDocumentConverter,
                fireStoreHelper, db);
        return INSTANCE;
    }

    /**
     * Gets the current instance of the ScannableCodesConnectionHandler
     *
     * @return INSTANCE the current instance of the ScannableCodesConnectionHandler
     * @throws IllegalArgumentException if the current INSTANCE hasn't been
     *                                  initialized
     */
    public static ScannableCodesDatabaseAdapter getInstance() {
        if (INSTANCE == null) {
            throw new IllegalArgumentException("ScannableCodesConnectionHandler INSTANCE does" +
                    "not exist!");
        }
        return INSTANCE;
    }

    /**
     * Check if a scannable code with a certain id exists
     *
     * @param scannablecodeId the id to check for
     * @return cf the CompletableFuture with a boolean return value indicating if the document exists
     */
    public CompletableFuture<Boolean> scannableCodeIdExists(String scannablecodeId) {
        DocumentReference documentReference = collectionReference.document(scannablecodeId);
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
     * Resets the static instance - should only be used for testing purposes
     */
    public static void resetInstance() {
        INSTANCE = null;
    }

    /**
     * Gets a scannable code from the database with a specific id
     *
     * @param scannableCodeId the id of the scannable code to get
     * @return cf the CompleteableFuture with the ScannableCode
     */
    public CompletableFuture<ScannableCode> getScannableCode(String scannableCodeId) {
        CompletableFuture<ScannableCode> cf;
        DocumentReference documentReference = this.collectionReference.document(scannableCodeId);
        cf = scannableCodeDocumentConverter.getScannableCodeFromDocument(documentReference);

        return cf;
    }

    /**
     * Get all the Scannable Codes whose ids are in a given list
     *
     * @param scannableCodeIds the list of ids of scannable codes to get
     * @return cf the CompleteableFuture with the list of ScannableCodes
     */
    public CompletableFuture<ArrayList<ScannableCode>> getScannableCodesByIdInList(ArrayList<String> scannableCodeIds) {
        CompletableFuture<ArrayList<ScannableCode>> cf = new CompletableFuture<>();
        ArrayList<ScannableCode> scannableCodes = new ArrayList<>();

        if (scannableCodeIds.size() == 0) {
            cf.complete(scannableCodes);
        } else {
            CompletableFuture.runAsync(() -> {
                Query docRef = this.collectionReference;
                docRef.get().addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        int matches = 0;
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            if (scannableCodeIds.contains(document.getId())) {
                                matches++;

                                scannableCodeDocumentConverter.getScannableCodeFromDocument(
                                        document.getReference()
                                ).thenAccept(scannableCode -> {
                                    scannableCodes.add(scannableCode);
                                    if (scannableCodes.size() == scannableCodeIds.size()) {
                                        cf.complete(scannableCodes);
                                    }
                                }).exceptionally(new Function<Throwable, Void>() {
                                    @Override
                                    public Void apply(Throwable throwable) {
                                        cf.completeExceptionally(throwable);
                                        return null;
                                    }
                                });
                            }
                        }

                        if (matches != scannableCodeIds.size()) {
                            cf.completeExceptionally(new Exception("One or more player wallet code ids" +
                                    "could not be mapped to actual codes!"));
                        }

                    } else {
                        cf.completeExceptionally(new Exception("[usernameExists] Could not complete query"));
                    }
                });
            });
        }

        return cf;
    }

    /**
     * Add a scannable code to the scannableCodes collection in the Firestore
     * database
     *
     * @param scannableCode the scannable code to add to the database
     * @return cf the CompleteableFuture once the operation has finished
     */
    public CompletableFuture<String> addScannableCode(ScannableCode scannableCode) {
        CompletableFuture<String> cf = new CompletableFuture<>();
        /**
         * If a document with the id doesn't already exist, add it to the collection.
         * Otherwise,
         * throw an error
         */
        CompletableFuture.runAsync(() -> {
            fireStoreHelper.documentWithIDExists(collectionReference, scannableCode.getScannableCodeId())
                    .thenAccept(exists -> {
                        if (!exists) {
                            ScannableCodeDocumentConverter.addScannableCodeToCollection(scannableCode,
                                            collectionReference, fireStoreHelper)
                                    .thenAccept(scannableCodeId -> {
                                        cf.complete(scannableCodeId);
                                    })
                                    .exceptionally(new Function<Throwable, Void>() {
                                        @Override
                                        public Void apply(Throwable throwable) {
                                            cf.completeExceptionally(throwable);
                                            return null;
                                        }
                                    });
                        } else {
                            cf.completeExceptionally(new Exception("Scannable code with id already exists!"));
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
     * Add a comment to a scannable code, if it exists
     *
     * @param scannableCodeId the id of the scannable code to add the comment to
     * @param newComment      the comment to add to the scannable code
     * @return cf the CompleteableFuture that completes with True if the operation was successful
     */
    public CompletableFuture<Boolean> addComment(String scannableCodeId, Comment newComment) {
        CompletableFuture<Boolean> cf = new CompletableFuture<>();
        CompletableFuture.runAsync(()->{
            fireStoreHelper.documentWithIDExists(this.collectionReference, scannableCodeId)
                    .thenAccept(exists -> {
                        if(exists){
                            ScannableCodeDocumentConverter.addCommentToScannableCodeDocument(newComment,
                                    collectionReference.document(scannableCodeId));
                            cf.complete(true);
                        }else{
                            cf.completeExceptionally(new Exception("ScannableCode does not exist!"));
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
     * Adds a listener to a given scananble code in the DB
     * @param scannableCodeId the id of the scannable code to be listening to
     * @param callback the callback function to call with the changed scannable code
     * @return registration a confirmation that the listener was registered
     */
    public ListenerRegistration setUpScannableCodeCommentsListener(String scannableCodeId,
                                                           GetScannableCodeCallback callback) {
        final CollectionReference collection = collectionReference.document(scannableCodeId)
                .collection(CollectionNames.COMMENTS.collectionName);
        ListenerRegistration registration = collection.addSnapshotListener((snapshot, e) -> {
            Log.d("ScannableCodeComments Firestore Listener", "COMMENTS DATA HAS BEEN UPDATED.");

            if (snapshot != null && (snapshot.getDocumentChanges().size()>0)) {
                getScannableCode(scannableCodeId).thenAccept(scannableCode -> {
                    Log.d("ScannableCode Firestore Listener", "SCANNABLE CODE  DATA  AFTER COMMENT" +
                            "HAS BEEN FETCHED");
                    Log.d("Number of Scannable Code Comments",
                            Integer.toString(scannableCode.getComments().size()));
                    callback.onCallback(scannableCode);
                }).exceptionally(new Function<Throwable, Void>() {
                    @Override
                    public Void apply(Throwable throwable) {
                        Log.d("ScannableCode Firestore Listener", "Could not get player");
                        return null;
                    }
                });
            } else {
                callback.onCallback(null);
            }
        });
        return registration;
    }

    /**
     * Delete a comment from the database
     *
     * @param scannableCodeId the id of the scannable code that the comment belongs
     *                        to
     * @param commentId       the id of the comment to delete
     * @return cf the CompleteableFuture indicating if the operation was successful or not
     */
    public CompletableFuture<Boolean> deleteComment(String scannableCodeId, String commentId) {
        CompletableFuture<Boolean> cf = new CompletableFuture<>();

        /**
         * If the scananbleCode exists, then try to delete the comment from the
         * collection
         */
        CompletableFuture.runAsync(()->{
            fireStoreHelper.documentWithIDExists(collectionReference, scannableCodeId)
                    .thenAccept(exists -> {
                        if (exists) {
                            CollectionReference commentCollection = collectionReference
                                    .document(scannableCodeId)
                                    .collection(CollectionNames.COMMENTS.collectionName);

                            /**
                             * If a comment with the commentId exists, delete it from the collection
                             */
                            fireStoreHelper.documentWithIDExists(commentCollection, commentId)
                                    .thenAccept(commentExists -> {
                                        if (commentExists) {
                                            commentCollection.document(commentId)
                                                    .delete()
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
                                                            cf.completeExceptionally(e);
                                                        }
                                                    });
                                        } else {
                                            cf.completeExceptionally(new Exception("No such comment with the" +
                                                    "given id exists!"));
                                        }
                                    })
                                    .exceptionally(new Function<Throwable, Void>() {
                                        @Override
                                        public Void apply(Throwable throwable) {
                                            cf.completeExceptionally(throwable);
                                            return null;
                                        }
                                    });
                        } else {
                            cf.completeExceptionally(new Exception("No such document with the given scannableCodeId exists!"));
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
}

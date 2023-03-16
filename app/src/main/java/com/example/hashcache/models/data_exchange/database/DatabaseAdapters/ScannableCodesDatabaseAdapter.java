package com.example.hashcache.models.data_exchange.database.DatabaseAdapters;

import android.util.Log;

import androidx.annotation.NonNull;

import com.example.hashcache.models.data_exchange.database.DatabaseAdapters.callbacks.BooleanCallback;
import com.example.hashcache.models.data_exchange.data_adapters.ScannableCodeDataAdapter;
import com.example.hashcache.models.data_exchange.database.DatabaseAdapters.callbacks.GetScannableCodeCallback;
import com.example.hashcache.models.data_exchange.database.DatabaseAdapters.converters.ScannableCodeDocumentConverter;
import com.example.hashcache.models.data_exchange.database.values.CollectionNames;
import com.example.hashcache.models.data_exchange.database.values.FieldNames;
import com.example.hashcache.models.Comment;
import com.example.hashcache.models.HashInfo;
import com.example.hashcache.models.ScannableCode;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import org.checkerframework.checker.units.qual.C;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

/**
 * Handles all calls to the Firebase ScannableCodes database
 */
public class ScannableCodesDatabaseAdapter {
    private FirebaseFirestore db;
    private CollectionReference collectionReference;
    private HashMap<String, ScannableCode> cachedScannableCodes;
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
        this.cachedScannableCodes = new HashMap<>();
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

                                ScannableCodeDocumentConverter.getScannableCodeFromDocument(
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
        fireStoreHelper.documentWithIDExists(collectionReference, scannableCode.getScannableCodeId(),
                new BooleanCallback() {
                    @Override
                    public void onCallback(Boolean documentExists) {
                        if (!documentExists) {
                            HashInfo hashInfo = scannableCode.getHashInfo();
                            ArrayList<Comment> comments = scannableCode.getComments();

                            HashMap<String, String> data = new HashMap<>();
                            data.put(FieldNames.SCANNABLE_CODE_ID.fieldName, scannableCode.getScannableCodeId());
                            data.put(FieldNames.CODE_LOCATION_ID.fieldName, scannableCode.getCodeLocationId());
                            data.put(FieldNames.GENERATED_NAME.fieldName, hashInfo.getGeneratedName());
                            data.put(FieldNames.GENERATED_SCORE.fieldName, Long.toString(hashInfo.getGeneratedScore()));

                            /**
                             * Create a new document with the ScannableCode data and whose id is the
                             * scannableCodeId, and put the document into the scannableCodes collection
                             */
                            fireStoreHelper.setDocumentReference(collectionReference
                                    .document(scannableCode.getScannableCodeId()), data, new BooleanCallback() {
                                @Override
                                public void onCallback(Boolean isTrue) {
                                    if (isTrue) {
                                        if (comments.size() > 0) {

                                            addComment(scannableCode.getScannableCodeId(),
                                                    comments.get(0))
                                                    .thenAccept(success -> {
                                                        cf.complete(scannableCode.getScannableCodeId());
                                                    })
                                                    .exceptionally(new Function<Throwable, Void>() {
                                                        @Override
                                                        public Void apply(Throwable throwable) {
                                                            cf.completeExceptionally(throwable);
                                                            return null;
                                                        }
                                                    });
                                        } else {
                                            cf.complete(scannableCode.getScannableCodeId());
                                        }
                                    }
                                }
                            });
                        } else {
                            cf.completeExceptionally(new Exception("Scannable code with id already exists!"));
                        }
                    }

                    ;
                });
        return cf;
    }

    /**
     * Creates the data map to put onto a scannableCode document
     *
     * @param comment the comment to convert into fields for a document
     * @return commentData a HashMap which maps the comment values to field names
     * that
     * match the variable names
     */
    private HashMap<String, String> getCommentData(Comment comment) {
        HashMap<String, String> commentData = new HashMap<>();
        commentData.put(FieldNames.COMMENTATOR_ID.fieldName, comment.getCommentatorId());
        commentData.put(FieldNames.COMMENT_BODY.fieldName, comment.getBody());

        return commentData;
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
        fireStoreHelper.documentWithIDExists(this.collectionReference, scannableCodeId, new BooleanCallback() {
            @Override
            public void onCallback(Boolean isTrue) {
                if (isTrue) {
                    HashMap<String, String> commentData;
                    collectionReference
                            .document(scannableCodeId)
                            .collection(CollectionNames.COMMENTS.collectionName)
                            .document(newComment.getCommentId())
                            .set(getCommentData(newComment));
                    cf.complete(true);
                } else {
                    cf.completeExceptionally(new Exception("ScannableCode does not exist!"));
                }
            }
        });
        return cf;
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
        fireStoreHelper.documentWithIDExists(collectionReference, scannableCodeId, new BooleanCallback() {

            @Override
            public void onCallback(Boolean isTrue) {
                if (isTrue) {
                    CollectionReference commentCollection = collectionReference
                            .document(scannableCodeId)
                            .collection(CollectionNames.COMMENTS.collectionName);

                    /**
                     * If a comment with the commentId exists, delete it from the collection
                     */
                    fireStoreHelper.documentWithIDExists(commentCollection, commentId,
                            new BooleanCallback() {
                                @Override
                                public void onCallback(Boolean isTrue) {
                                    if (isTrue) {
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
                                                        cf.completeExceptionally(new Exception("" +
                                                                "An error occurred while deleting" +
                                                                "the comment"));
                                                    }
                                                });
                                    } else {
                                        cf.completeExceptionally(new Exception("No such comment with the" +
                                                "given id exists!"));
                                    }
                                }
                            });
                } else {
                    cf.completeExceptionally(new Exception("No such document with the given scannableCodeId exists!"));
                }
            }
        });
        return cf;
    }

}

package com.example.hashcache.models.database_connections;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.hashcache.models.Player;
import com.example.hashcache.models.database_connections.callbacks.BooleanCallback;
import com.example.hashcache.models.database_connections.callbacks.GetScannableCodeCallback;
import com.example.hashcache.models.database_connections.converters.ScannableCodeDocumentConverter;
import com.example.hashcache.models.database_connections.values.CollectionNames;
import com.example.hashcache.models.database_connections.values.FieldNames;
import com.example.hashcache.models.Comment;
import com.example.hashcache.models.HashInfo;
import com.example.hashcache.models.ScannableCode;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.CompletableFuture;

/**
 * Handles all calls to the Firebase ScannableCodes database
 */
public class ScannableCodesConnectionHandler {
    private FirebaseFirestore db;
    private CollectionReference collectionReference;
    private HashMap<String, ScannableCode> cachedScannableCodes;
    final String TAG = "Sample";
    private ScannableCodeDocumentConverter scannableCodeDocumentConverter;
    private FireStoreHelper fireStoreHelper;
    private static ScannableCodesConnectionHandler INSTANCE;

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
     *         instance of the
     *         ScannableCodesConnectionHandler class
     */
    private ScannableCodesConnectionHandler(ScannableCodeDocumentConverter scannableCodeDocumentConverter,
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
     *         instance of the
     *         ScannableCodesConnectionHandler class
     *
     * @throws IllegalArgumentException if the static instance of the
     *                                  ScannableCodesConnectionHandler
     *                                  class has already been initialized
     */
    public static ScannableCodesConnectionHandler makeInstance(
            ScannableCodeDocumentConverter scannableCodeDocumentConverter,
            FireStoreHelper fireStoreHelper,
            FirebaseFirestore db) {
        if (INSTANCE != null) {
            throw new IllegalArgumentException("ScannableCodesConnectionHandler INSTANCE" +
                    "already exists!");
        }

        INSTANCE = new ScannableCodesConnectionHandler(scannableCodeDocumentConverter,
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
    public static ScannableCodesConnectionHandler getInstance() {
        if (INSTANCE == null) {
            throw new IllegalArgumentException("ScannableCodesConnectionHandler INSTANCE does" +
                    "not exist!");
        }
        return INSTANCE;
    }

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
     * @param scannableCodeId          the id of the scannable code to get
     * @param getScannableCodeCallback the callback function to be called with the
     *                                 found scnnablecode
     */
    public void getScannableCode(String scannableCodeId, GetScannableCodeCallback getScannableCodeCallback) {
        if (this.cachedScannableCodes.containsKey(scannableCodeId)) {
            getScannableCodeCallback.onCallback(cachedScannableCodes.get(scannableCodeId));
        } else {
            DocumentReference documentReference = this.collectionReference.document(scannableCodeId);
            this.scannableCodeDocumentConverter.getScannableCodeFromDocument(documentReference,
                    getScannableCodeCallback);
        }
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
                                scannableCodeDocumentConverter.getScannableCodeFromDocument(document.getReference(),
                                        new GetScannableCodeCallback() {
                                            @Override
                                            public void onCallback(ScannableCode scannableCode) {
                                                scannableCodes.add(scannableCode);
                                                if (scannableCodes.size() == scannableCodeIds.size()) {
                                                    cf.complete(scannableCodes);
                                                }
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
     * @param scannableCode   the scannable code to add to the database
     * @param booleanCallback the function to call back with once the addition has
     *                        succeeded
     * @throws IllegalArgumentException when there already exists a scannable code
     *                                  with the given id
     */
    public void addScannableCode(ScannableCode scannableCode, BooleanCallback booleanCallback) {
        if (this.cachedScannableCodes.containsKey(scannableCode.getScannableCodeId())) {
            Log.d(TAG, "scannable code already exists with given id!");
            booleanCallback.onCallback(false);
            return;
        }

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
                                                    // Assume that scannable codes only have up to 1 comment
                                                    // when being initialized
                                                    addComment(scannableCode.getScannableCodeId(), comments.get(0),
                                                            new BooleanCallback() {
                                                                @Override
                                                                public void onCallback(Boolean isTrue) {
                                                                    if (isTrue) {
                                                                        booleanCallback.onCallback(true);
                                                                    } else {
                                                                        booleanCallback.onCallback(false);
                                                                    }
                                                                }
                                                            });
                                                } else {
                                                    booleanCallback.onCallback(true);
                                                }
                                            }
                                        }
                                    });
                        } else {
                            throw new IllegalArgumentException("Scannable code with id already exists!");
                        }
                    };
                });
    }

    /**
     * Creates the data map to put onto a scannableCode document
     * 
     * @param comment the comment to convert into fields for a document
     * @return commentData a HashMap which maps the comment values to field names
     *         that
     *         match the variable names
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
     * @param booleanCallback the callback function to call if the addition is
     *                        successful
     * @throws IllegalArgumentException when the scannable code id is not valid
     */
    public void addComment(String scannableCodeId, Comment newComment, BooleanCallback booleanCallback) {
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
                    booleanCallback.onCallback(true);
                } else {
                    throw new IllegalArgumentException("ScannableCode does not exist!");
                }
            }
        });
    }

    /**
     * Delete a comment from the database
     * 
     * @param scannableCodeId the id of the scannable code that the comment belongs
     *                        to
     * @param commentId       the id of the comment to delete
     * @param booleanCallback the callback function to call once the operation is
     *                        finished. Calls with
     *                        true if the operation was successful, and false
     *                        otherwise
     * @throws IllegalArgumentException if no scannableCode exists with the given id
     */
    public void deleteComment(String scannableCodeId, String commentId, BooleanCallback booleanCallback) {
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
                                    } else {
                                        throw new IllegalArgumentException("No such comment with the" +
                                                "given id exists!");
                                    }
                                }
                            });
                } else {
                    throw new IllegalArgumentException("No such document with the given scannableCodeId exists!");
                }
            }
        });
    }
}

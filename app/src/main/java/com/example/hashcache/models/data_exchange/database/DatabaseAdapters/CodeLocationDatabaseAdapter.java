package com.example.hashcache.models.data_exchange.database.DatabaseAdapters;

import android.util.Log;

import com.example.hashcache.models.data_exchange.database.DatabaseAdapters.callbacks.BooleanCallback;
import com.example.hashcache.models.data_exchange.database.DatabaseAdapters.converters.CodeLocationDocumentConverter;
import com.example.hashcache.models.data_exchange.database.values.CollectionNames;
import com.example.hashcache.models.CodeLocation;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

/**
 * Handles all calls to the Firebase CodeLocations database
 */
public class CodeLocationDatabaseAdapter {
    private FirebaseFirestore db;
    private CollectionReference collectionReference;
    final String TAG = "Sample";
    private FireStoreHelper fireStoreHelper;
    private static CodeLocationDatabaseAdapter INSTANCE;
    private CodeLocationDocumentConverter codeLocationDocumentConverter;

    /**
     * Creates a connection to the CodeLocation collection in the database and keeps a cache
     * of the recently accessed CodeLocations
     *
     * @param fireStoreHelper the instance of the FireStoreHelper class to call upon to perform
     *                        common firestore actions
     * @param db the instance of the database to use to get connections to the CodeLocation collection
     *           and its documents
     */
    private CodeLocationDatabaseAdapter(FireStoreHelper fireStoreHelper,
                                        FirebaseFirestore db, CodeLocationDocumentConverter codeLocationDocumentConverter){
        this.fireStoreHelper = fireStoreHelper;
        this.db = db;
        this.codeLocationDocumentConverter = codeLocationDocumentConverter;

        collectionReference = db.collection(CollectionNames.CODE_LOCATIONS.collectionName);

    }

    /**
     * Create the static instance of the CodeLocationConnectionHandler class
     * @param fireStoreHelper the instance of the FireStoreHelper class to call upon to perform
     *                        common firestore actions
     * @param db the instance of the database to use to get connections to the CodeLocation collection
     *           and its documents
     * @return CodeLocationConnectionHandler.INSTANCE the static instance of the CodeLocationConnectionHandler
     * class to use for all actions concerning the CodeLocation database collection
     *
     * @throws IllegalArgumentException if the INSTANCE has already been initialized
     */
    public static CodeLocationDatabaseAdapter makeInstance(FireStoreHelper fireStoreHelper,
                                                           FirebaseFirestore db, CodeLocationDocumentConverter codeLocationDocumentConverter){
        if(INSTANCE != null){
            throw new IllegalArgumentException("CodeLocationConnectionHandler INSTANCE already " +
                    "exists!");
        }

        INSTANCE = new CodeLocationDatabaseAdapter(fireStoreHelper, db, codeLocationDocumentConverter);

        return INSTANCE;
    }

    /**
     * Resets the static instance
     * Should only be used for testing purposes
     */
    public static void resetInstance(){
        INSTANCE = null;
    }

    /**
     * Get the singleton instance of the CodeLocationConnectionHandler
     * @return INSTANCE the singleton instance of the CodeLocationConnectionHandler
     * @throws IllegalArgumentException if the CodeLocationConnectionHandler instance hasn't
     * been initialized yet
     */
    public static CodeLocationDatabaseAdapter getInstance(){
        if(INSTANCE == null){
            throw new IllegalArgumentException("CodeLocationConnectionHandler INSTANCE does" +
                    "not exist!");
        }

        return INSTANCE;
    }

    /**
     * Adds a code location to the database
     * @param codeLocation the codelocation to add to the database
     *
     * @return cf the CompleteableFuture with a True value if the operation was successful
     */
    public CompletableFuture<Boolean> addCodeLocation(CodeLocation codeLocation){
        String id = codeLocation.getId();
        CompletableFuture<Boolean> cf = new CompletableFuture<>();

        final boolean[] codeLocationExists = new boolean[1];

        /**
         * Check if the a codeLocation document already exists with the given id, and add it
         * to the collection if it doesn't
         */
        fireStoreHelper.documentWithIDExists(collectionReference, id, new BooleanCallback() {
            @Override
            public void onCallback(Boolean isTrue) {
                codeLocationExists[0] = isTrue;

                if(!codeLocationExists[0]){
                    codeLocationDocumentConverter.addCodeLocationToCollection(codeLocation,
                            collectionReference, fireStoreHelper).thenAccept(success -> {
                                cf.complete(true);
                    }).exceptionally(new Function<Throwable, Void>() {
                        @Override
                        public Void apply(Throwable throwable) {
                            cf.completeExceptionally(throwable);
                            return null;
                        }
                    });
                }else{
                    Log.e(TAG, "Code Location already exists!");
                    cf.completeExceptionally(new Exception("Code location already exists!"));
                }
            }
        });

        return cf;
    }

    /**
     * Gets a code location from the database
     * @param id the id of the code location to get
     * @return cf the CompleteableFuture with the CodeLocation
     */
    public CompletableFuture<CodeLocation> getCodeLocation(String id){
        DocumentReference documentReference = collectionReference.document(id);

        CompletableFuture<CodeLocation> cf = new CompletableFuture<>();

        CompletableFuture.runAsync(() -> {
            CodeLocationDocumentConverter.convertDocumentReferenceToCodeLocation(documentReference);
        }).exceptionally(new Function<Throwable, Void>() {
            @Override
            public Void apply(Throwable throwable) {
                cf.completeExceptionally(throwable);
                return null;
            }
        });

        return cf;
    }
}

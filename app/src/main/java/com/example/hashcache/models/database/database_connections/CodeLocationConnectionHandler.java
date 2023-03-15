package com.example.hashcache.models.database.database_connections;

import android.util.Log;

import com.example.hashcache.models.database.data_adapters.CodeLocationDataAdapter;
import com.example.hashcache.models.database.database_connections.callbacks.BooleanCallback;
import com.example.hashcache.models.database.database_connections.callbacks.GetCodeLocationCallback;
import com.example.hashcache.models.database.values.CollectionNames;
import com.example.hashcache.models.CodeLocation;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.lang.reflect.Array;
import java.util.HashMap;

/**
 * Handles all calls to the Firebase CodeLocations database
 */
public class CodeLocationConnectionHandler {
    private FirebaseFirestore db;
    private CollectionReference collectionReference;
    private HashMap<String, CodeLocation> cachedCodeLocations;
    final String TAG = "Sample";
    private FireStoreHelper fireStoreHelper;
    private CodeLocationDataAdapter codeLocationDataAdapter;
    private static CodeLocationConnectionHandler INSTANCE;

    /**
     * Creates a connection to the CodeLocation collection in the database and keeps a cache
     * of the recently accessed CodeLocations
     *
     * @param fireStoreHelper the instance of the FireStoreHelper class to call upon to perform
     *                        common firestore actions
     * @param codeLocationDataAdapter the instance of the CodeLocationDocumentConverter to
     *                                      use when converting a document to a CodeLocation object
     * @param db the instance of the database to use to get connections to the CodeLocation collection
     *           and its documents
     */
    private CodeLocationConnectionHandler(FireStoreHelper fireStoreHelper,
                                          CodeLocationDataAdapter codeLocationDataAdapter,
                                          FirebaseFirestore db){
        this.cachedCodeLocations = new HashMap<>();
        this.fireStoreHelper = fireStoreHelper;
        this.codeLocationDataAdapter = codeLocationDataAdapter;
        this.db = db;

        collectionReference = db.collection(CollectionNames.CODE_LOCATIONS.collectionName);

    }

    /**
     * Create the static instance of the CodeLocationConnectionHandler class
     * @param fireStoreHelper the instance of the FireStoreHelper class to call upon to perform
     *                        common firestore actions
     * @param codeLocationDataAdapter the instance of the CodeLocationDocumentConverter to
     *                                      use when converting a document to a CodeLocation object
     * @param db the instance of the database to use to get connections to the CodeLocation collection
     *           and its documents
     * @return CodeLocationConnectionHandler.INSTANCE the static instance of the CodeLocationConnectionHandler
     * class to use for all actions concerning the CodeLocation database collection
     *
     * @throws IllegalArgumentException if the INSTANCE has already been initialized
     */
    public static CodeLocationConnectionHandler makeInstance(FireStoreHelper fireStoreHelper,
                                                      CodeLocationDataAdapter codeLocationDataAdapter,
                                                      FirebaseFirestore db){
        if(INSTANCE != null){
            throw new IllegalArgumentException("CodeLocationConnectionHandler INSTANCE already " +
                    "exists!");
        }

        INSTANCE = new CodeLocationConnectionHandler(fireStoreHelper, codeLocationDataAdapter,
                db);

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
    public CodeLocationConnectionHandler getInstance(){
        if(INSTANCE == null){
            throw new IllegalArgumentException("CodeLocationConnectionHandler INSTANCE does" +
                    "not exist!");
        }

        return INSTANCE;
    }

    /**
     * Adds a code location to the database
     * @param codeLocation the codelocation to add to the database
     * @param booleanCallback the callback function to call once the asynchronous database calls are done
     *
     * @throws IllegalArgumentException when the code location already exists in the database
     */
    public void addCodeLocation(CodeLocation codeLocation, BooleanCallback booleanCallback){
        String name = codeLocation.getLocationName();
        double[] coordinates = codeLocation.getCoordinates().getCoordinates();
        String x = Double.toString((double)Array.get(coordinates, 0));
        String y = Double.toString((double)Array.get(coordinates, 1));
        String z = Double.toString((double)Array.get(coordinates, 2));
        String id = codeLocation.getId();

        final boolean[] codeLocationExists = new boolean[1];

        if(cachedCodeLocations.containsKey(id)){
            booleanCallback.onCallback(false);
            return;
        }

        /**
         * Check if the a codeLocation document already exists with the given id, and add it
         * to the collection if it doesn't
         */
        fireStoreHelper.documentWithIDExists(collectionReference, id, new BooleanCallback() {
            @Override
            public void onCallback(Boolean isTrue) {
                codeLocationExists[0] = isTrue;

                if(!codeLocationExists[0]){

                    HashMap<String, String> data = new HashMap<>();
                    data.put("name", name);
                    data.put("x", x);
                    data.put("y", y);
                    data.put("z", z);

                    DocumentReference documentReference = collectionReference.document(id);

                    /**
                     * Add the document to the collection. If the operation is successful, cache
                     * the codeLocation and call the callback function with a true value,
                     * otherwise call the callback function with false.
                     */
                    fireStoreHelper.setDocumentReference(documentReference, data, new BooleanCallback() {
                        @Override
                        public void onCallback(Boolean isTrue) {
                            if(isTrue){
                                cachedCodeLocations.put(codeLocation.getId(), codeLocation);
                                booleanCallback.onCallback(true);
                            }else{
                                booleanCallback.onCallback(false);
                            }
                        }
                    });
                }else{
                    Log.e(TAG, "Code Location already exists!");
                    throw new IllegalArgumentException("Code location already exists!");
                }
            }
        });
    }

    /**
     * Gets a code location from the database
     * @param id the id of the code location to get
     * @param getCodeLocationCallback the callback function to call with the location once it
     *                                has been found
     */
    public void getCodeLocation(String id, GetCodeLocationCallback getCodeLocationCallback){
        CodeLocation codeLocation;

        if(cachedCodeLocations.containsKey(id)){
            codeLocation = cachedCodeLocations.get(id);
            getCodeLocationCallback.onCallback(codeLocation);
        }else {
            DocumentReference documentReference = collectionReference.document(id);

            /**
             * Get and cache a CodeLocation object from the document, and then call the
             * callback function with it
             */
            codeLocationDataAdapter.getCodeLocationFromDocument(documentReference,
                    new GetCodeLocationCallback() {
                        @Override
                        public void onCallback(CodeLocation codeLocation) {
                            cachedCodeLocations.put(codeLocation.getId(), codeLocation);
                            getCodeLocationCallback.onCallback(codeLocation);
                        }
                    });
        }
    }
}

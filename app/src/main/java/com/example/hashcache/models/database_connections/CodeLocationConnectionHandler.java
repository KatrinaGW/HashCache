package com.example.hashcache.models.database_connections;

import android.util.Log;

import androidx.annotation.Nullable;

import com.example.hashcache.models.database_connections.callbacks.BooleanCallback;
import com.example.hashcache.models.database_connections.callbacks.GetCodeLocationCallback;
import com.example.hashcache.models.database_connections.converters.CodeLocationDocumentConverter;
import com.example.hashcache.models.database_connections.converters.PlayerDocumentConverter;
import com.example.hashcache.models.database_connections.values.CollectionNames;
import com.example.hashcache.models.CodeLocation;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.lang.reflect.Array;
import java.util.HashMap;

/**
 * Handles all calls to the Firebase ScannableCodes database
 */
public class CodeLocationConnectionHandler {
    private FirebaseFirestore db;
    private CollectionReference collectionReference;
    private HashMap<String, CodeLocation> cachedCodeLocations;
    final String TAG = "Sample";
    private PlayerDocumentConverter playerDocumentConverter;
    private FireStoreHelper fireStoreHelper;
    private CodeLocationDocumentConverter codeLocationDocumentConverter;
    private static CodeLocationConnectionHandler INSTANCE;

    /**
     * Creates a connection to the CodeLocation collection in the database and keeps a cache
     * of the locations
     */
    private CodeLocationConnectionHandler(){
        this.cachedCodeLocations = new HashMap<>();
        this.fireStoreHelper = new FireStoreHelper();
        this.codeLocationDocumentConverter = new CodeLocationDocumentConverter();

        db = FirebaseFirestore.getInstance();

        collectionReference = db.collection(CollectionNames.CODE_LOCATIONS.collectionName);

        collectionReference.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable
            FirebaseFirestoreException error) {
                cachedCodeLocations.clear();
                for(QueryDocumentSnapshot doc: queryDocumentSnapshots)
                {
                    Log.d(TAG, "Code Location with id " + doc.getId());
                    double x = Double.parseDouble((String) doc.getData().get("x"));
                    double y = Double.parseDouble((String) doc.getData().get("y"));
                    double z = Double.parseDouble((String) doc.getData().get("z"));
                    String locationName = (String) doc.getData().get("name");

                    cachedCodeLocations.put(doc.getId(), new CodeLocation(locationName, x, y, z));
                }
            }
        });
    }

    /**
     * Get the singleton instance of the CodeLocationConnectionHandler
     * @return INSTANCE the singleton instance of the CodeLocationConnectionHandler
     */
    public CodeLocationConnectionHandler getInstance(){
        if(INSTANCE == null){
            INSTANCE = new CodeLocationConnectionHandler();
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
            return;
        }

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

                    fireStoreHelper.setDocumentReference(documentReference, data, booleanCallback);
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
            codeLocationDocumentConverter.getCodeLocationFromDocument(documentReference,
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

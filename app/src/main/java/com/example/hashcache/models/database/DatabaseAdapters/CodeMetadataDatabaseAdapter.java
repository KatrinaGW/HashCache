package com.example.hashcache.models.database.DatabaseAdapters;

import android.util.Log;

import androidx.annotation.NonNull;

import com.example.hashcache.models.CodeMetadata;
import com.example.hashcache.models.database.values.CollectionNames;
import com.example.hashcache.models.database.values.FieldNames;
import com.firebase.geofire.GeoFireUtils;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQueryBounds;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * A class to handle interfacing with the CodeMetadata collection in the database
 */
public class CodeMetadataDatabaseAdapter {
    private String TAG = "CodeMetadataDatabaseAdapter";
    private FirebaseFirestore db;
    private CollectionReference collectionReference;
    private FireStoreHelper fireStoreHelper;
    private static CodeMetadataDatabaseAdapter INSTANCE;



    /**
     * Creates a connection to the CodeLocation collection in the database and keeps
     * a cache
     * of the recently accessed CodeLocations
     *
     * @param fireStoreHelper the instance of the FireStoreHelper class to call upon
     *                        to perform
     *                        common firestore actions
     * @param db              the instance of the database to use to get connections
     *                        to the CodeLocation collection
     *                        and its documents
     */
    private CodeMetadataDatabaseAdapter(FireStoreHelper fireStoreHelper,
                                        FirebaseFirestore db) {
        this.fireStoreHelper = fireStoreHelper;
        this.db = db;
        collectionReference = db.collection(CollectionNames.CODE_METADATA.collectionName);
    }

    /**
     * Create the static instance of the CodeLocationConnectionHandler class
     *
     * @param fireStoreHelper the instance of the FireStoreHelper class to call upon
     *                        to perform
     *                        common firestore actions
     * @param db              the instance of the database to use to get connections
     *                        to the CodeLocation collection
     *                        and its documents
     * @return CodeLocationConnectionHandler.INSTANCE the static instance of the
     *         CodeLocationConnectionHandler
     *         class to use for all actions concerning the CodeLocation database
     *         collection
     **/
    public static CodeMetadataDatabaseAdapter makeOrGetInstance(FireStoreHelper fireStoreHelper,
                                                                FirebaseFirestore db) {
        if (INSTANCE == null) {
            INSTANCE = new CodeMetadataDatabaseAdapter(fireStoreHelper, db);

        }

        return INSTANCE;
    }

    /**
     * Get the singleton instance of the CodeMetadataDatabaseAdapter
     *
     * @return INSTANCE the singleton instance of the CodeMetadataDatabaseAdapter
     * @throws IllegalArgumentException if the CodeMetadataDatabaseAdapter instance
     *                                  hasn't
     *                                  been initialized yet
     */
    public static CodeMetadataDatabaseAdapter getInstance() {
        if (INSTANCE == null) {
            throw new IllegalArgumentException("CodeMetadataDatabaseAdapter INSTANCE does" +
                    "not exist!");
        }
        return INSTANCE;
    }

    /**
     * Resets the static instance to null
     */
    public static void resetInstance(){
        INSTANCE = null;
    }

    // Based on:
    // https://firebase.google.com/docs/firestore/solutions/geoqueries#java

    /**
     * Adds a CodeMetadata to the database
     * @param codeMetadata the metadata object to add to the database
     * @return cf the CompletableFuture that compeltes with true if the operation was successful
     */
    public CompletableFuture<Void> createScannableCodeMetadata(CodeMetadata codeMetadata) {
        CompletableFuture<Void> cf = new CompletableFuture<>();
        CompletableFuture.runAsync(() -> {
            String documentId = codeMetadata.getDocumentId();

            boolean hasLocation = codeMetadata.hasLocation();
            Map<String, Object> objMap = new HashMap<>();
            objMap.put(FieldNames.ScannableCodeId.name, codeMetadata.getScannableCodeId());
            objMap.put(FieldNames.USER_ID.name, codeMetadata.getUserId());
            if(hasLocation){
                GeoLocation loc = codeMetadata.getLocation();
                objMap.put(FieldNames.Latitude.name, loc.latitude);
                objMap.put(FieldNames.Longitude.name, loc.longitude);
                objMap.put(FieldNames.Geohash.name, codeMetadata.getGeohash());
            }
            objMap.put(FieldNames.HasLocation.name, codeMetadata.hasLocation());
            String image = codeMetadata.getImage();
            objMap.put(FieldNames.ImageBase64.name, image);
            DocumentReference docRef = collectionReference.document(documentId);
            docRef.set(objMap).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    cf.complete(null);
                } else {
                    Log.d(TAG, "Failed with: " + task.getException());
                    cf.completeExceptionally(task.getException());
                }
            });
        });
        return cf;
    }

    /**
     * Removes the metadata for a ScannableCodeId with a specific user
     * @param scannableCodeId the id of the scannable code to delete
     * @param userId the id of the user to remove the scannable code metadata for
     * @return cf the CompletableFuture which completes with True if the operation was successful
     */
    public CompletableFuture<Boolean> removeScannableCodeMetadata(String scannableCodeId, String userId){
        CompletableFuture<Boolean> cf = new CompletableFuture<>();

        Query query = collectionReference.whereEqualTo(FieldNames.ScannableCodeId.name, scannableCodeId);
        query = query.whereEqualTo(FieldNames.USER_ID.fieldName, userId);

        query.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                if(task.getResult().size()==1){
                    DocumentReference doc = task.getResult().getDocuments().get(0).getReference();

                    doc.delete()
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
                }else{
                    Log.d("CodeMetadataDatabaseAdapter", "User did not have any metadata for " +
                            "deleted scannable code");
                    cf.complete(true);
                }
            } else {
                cf.completeExceptionally(new Exception("[usernameExists] Could not complete query"));
            }
        });
        return cf;
    }

    /**
     * Updates the image a player took of a scannable code
     * @param userId the id of the player
     * @param scannableCodeId the id of the scannablecode the player photographed
     * @param image the image to update with
     * @return cf the CompletableFuture that completes successfully if the operation was successful
     */
    public CompletableFuture<Void> updatePlayerCodeMetadataImage(String userId, String scannableCodeId, String image) {
        CompletableFuture<Void> cf = new CompletableFuture<>();
        CompletableFuture.runAsync(() -> {
            CollectionReference colRef = collectionReference;
            Query query = colRef.
                    whereEqualTo(FieldNames.ScannableCodeId.name, scannableCodeId).
                    whereEqualTo(FieldNames.USER_ID.name, userId);
            query.get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    if (task.getResult().isEmpty()) {
                        cf.completeExceptionally(new Exception("Code Metadata entry does not exist!"));
                    } else {
                        List<DocumentSnapshot> sn = task.getResult().getDocuments();
                        DocumentSnapshot ds = sn.get(0);
                        DocumentReference dr = ds.getReference();
                        dr.update(FieldNames.ImageBase64.name, image).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                cf.complete(null);
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                cf.completeExceptionally(e);
                            }
                        });

                    }
                } else {
                    cf.completeExceptionally(new Exception("Could not fet code metadata"));
                }
            });

        });
        return cf;
    }

    /**
     * Gets the metadata for a player's specific scannableCode
     * @param userId the id of the player to get the metadata for
     * @param scannableCodeId the id of the code to get the metadata for
     * @return cf the CompletableFuture with the CodeMetadata
     */
    public CompletableFuture<CodeMetadata> getPlayerCodeMetadataById(String userId, String scannableCodeId) {
        CompletableFuture<CodeMetadata> cf = new CompletableFuture<>();
        CompletableFuture.runAsync(() -> {
            CollectionReference colRef = collectionReference;
            Query query = colRef.whereEqualTo(FieldNames.ScannableCodeId.name, scannableCodeId);
            query = query.whereEqualTo(FieldNames.USER_ID.name, userId);
            query.get().addOnCompleteListener(task -> {
                try {
                    if (task.isSuccessful()) {
                        if (task.getResult().isEmpty()) {
                            cf.completeExceptionally(new Exception(String.format("Code metadata entry does not exist: userId: %s, scannableCodeId: %s", userId, scannableCodeId)));
                        } else {
                            List<DocumentSnapshot> sn = task.getResult().getDocuments();
                            cf.complete(parseCodeMetadataDocument(sn.get(0)));
                        }
                    } else {
                        cf.completeExceptionally(new Exception("Could not fet code metadata"));
                    }
                }
                catch(Exception e){
                    cf.completeExceptionally(e);
                }
            });

        });
        return cf;
    }

    /**
     * Gets all the CodeMetadata objects for a specific ScannableCode
     * @param scannableCodeId the id of the scannableCode
     * @return cf the CompletableFuture with all the CodeMetadatas that had the specific scananbleCodeId
     */
    public CompletableFuture<ArrayList<CodeMetadata>> getCodeMetadataById(String scannableCodeId) {
        CompletableFuture<ArrayList<CodeMetadata>> cf = new CompletableFuture<>();
        CompletableFuture.runAsync(() -> {
            CollectionReference colRef = collectionReference;
            Query query = colRef.whereEqualTo(FieldNames.ScannableCodeId.name, scannableCodeId);
            query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if(task.isSuccessful()){
                        ArrayList<CodeMetadata> cms = new ArrayList<>();
                        QuerySnapshot querySnapshot = task.getResult();
                        List<DocumentSnapshot> documents = querySnapshot.getDocuments();
                        for (DocumentSnapshot document : documents) {
                            CodeMetadata cm = parseCodeMetadataDocument(document);
                            cms.add(cm);
                        }
                        cf.complete(cms);
                    }
                    else{
                        cf.completeExceptionally(new Exception("Could not fet code metadata"));
                    }
                }
            });
        });
        return cf;
    }

    /**
     * Checks if a document exists for a code metadata with a specific userid and scannableCodeId
     * @param userId the id of the user
     * @param scannableCodeId the id of the scannableCode
     * @return cf the CompletableFuture that completes with whether or not the document exists
     */
    public CompletableFuture<Boolean> codeMetadataEntryExists(String userId, String scannableCodeId){
        CompletableFuture<Boolean> cf = new CompletableFuture<>();
        CompletableFuture.runAsync(() -> {
            CollectionReference colRef = collectionReference;
            Query query = colRef.
                    whereEqualTo(FieldNames.ScannableCodeId.name, scannableCodeId).
                    whereEqualTo(FieldNames.USER_ID.name, userId);
            query.get().addOnCompleteListener(task -> {
                try {
                    if (task.isSuccessful()) {
                        if (task.getResult().isEmpty()) {
                            cf.complete(false);
                        } else {
                            List<DocumentSnapshot> sn = task.getResult().getDocuments();
                            if(sn.isEmpty()){
                                cf.complete(false);
                            }
                            else{
                                cf.complete(true);
                            }
                        }
                    } else {
                        cf.completeExceptionally(new Exception("Could not fetch code metadata"));
                    }
                }
                catch(Exception e){
                    cf.completeExceptionally(e);
                }
            });

        });
        return cf;
    }

    /**
     * Gets the metadata for all the scannableCodes within a certain radius of a certain location
     * @param loc the location to use as the centre point
     * @param radiusMeters the radius to find all scananbleCodes in
     * @return cf the CompletableFuture with a list of the codes in the radius
     */
    // Based on:
    // https://firebase.google.com/docs/firestore/solutions/geoqueries#java
    public CompletableFuture<ArrayList<CodeMetadata>> getCodeMetadataWithinRadius(GeoLocation loc,
                                                                                  double radiusMeters) {
        CompletableFuture<ArrayList<CodeMetadata>> cf = new CompletableFuture<>();
        CompletableFuture.runAsync(() -> {
            final GeoLocation center = new GeoLocation(loc.latitude, loc.longitude);
            List<GeoQueryBounds> bounds = GeoFireUtils.getGeoHashQueryBounds(center, radiusMeters);
            final List<Task<QuerySnapshot>> tasks = new ArrayList<>();
            for (GeoQueryBounds b : bounds) {
                Query q = collectionReference
                        .orderBy(FieldNames.Geohash.name)
                        .startAt(b.startHash)
                        .endAt(b.endHash);
                tasks.add(q.get());
            }
            Tasks.whenAllComplete(tasks)
                    .addOnCompleteListener(t -> {
                        ArrayList<CodeMetadata> matchingMetadata = new ArrayList<>();

                        for (Task<QuerySnapshot> task : tasks) {
                            QuerySnapshot snap = task.getResult();
                            for (DocumentSnapshot doc : snap.getDocuments()) {
                                try {
                                    if(doc.getBoolean(FieldNames.HasLocation.name))
                                    {
                                        double lat = doc.getDouble(FieldNames.Latitude.name);
                                        double lng = doc.getDouble(FieldNames.Longitude.name);
                                        GeoLocation docLocation = new GeoLocation(lat, lng);
                                        double distanceInM = GeoFireUtils.getDistanceBetween(docLocation, center);
                                        if (distanceInM <= radiusMeters) {
                                            CodeMetadata cm = parseCodeMetadataDocument(doc);
                                            matchingMetadata.add(cm);

                                        }
                                    }
                                } catch (Exception e) {

                                }
                            }
                        }
                        cf.complete(matchingMetadata);
                    });

        });
        return cf;
    }

    /**
     * Turn a CodeMetadata document into an object
     * @param doc the document to turn into an object
     * @return the object version of the document
     */
    @NonNull
    private CodeMetadata parseCodeMetadataDocument(DocumentSnapshot doc) {
        String scannableCodeId = doc.getString(FieldNames.ScannableCodeId.name);
        String userId = doc.getString(FieldNames.USER_ID.name);
        String image = null;
        if(doc.contains(FieldNames.ImageBase64.name)){
            image = doc.getString(FieldNames.ImageBase64.name);
        }
        if(doc.getBoolean(FieldNames.HasLocation.name)){
            double lat = doc.getDouble(FieldNames.Latitude.name);
            double lng = doc.getDouble(FieldNames.Longitude.name);
            return new CodeMetadata(scannableCodeId, userId, new GeoLocation(lat, lng), image);
        }
        else{
            return new CodeMetadata(scannableCodeId, userId, image);
        }
    }
}
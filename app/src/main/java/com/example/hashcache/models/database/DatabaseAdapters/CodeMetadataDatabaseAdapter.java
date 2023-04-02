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

public class CodeMetadataDatabaseAdapter {
    private String TAG = "CodeMetadataDatabaseAdapter";
    private FirebaseFirestore db;
    private CollectionReference collectionReference;
    private FireStoreHelper fireStoreHelper;
    private static CodeMetadataDatabaseAdapter INSTANCE;


    public enum FieldNames {
        Geohash("geohash"),
        ScannableCodeId("scannableCodeId"),
        ImageBase64("base64Image"),
        Latitude("lat"),
        Longitude("lon"),
        UserId("userId"),
        HasLocation("hasLocation");
        public final String name;
        FieldNames(String fieldName) {
            this.name = fieldName;
        }
    }


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

    public CompletableFuture<Void> updateLocationImage(String codeMetadataId, String base64Image) {
        CompletableFuture<Void> cf = new CompletableFuture<>();
        CompletableFuture.runAsync(() -> {
            Map<String, Object> objMap = new HashMap<>();
            objMap.put(FieldNames.ImageBase64.name, base64Image);
            DocumentReference docRef = collectionReference.document(codeMetadataId);
            docRef.update(objMap).addOnCompleteListener(task -> {
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

    // Based on:
    // https://firebase.google.com/docs/firestore/solutions/geoqueries#java
    public CompletableFuture<Void> createScannableCodeMetadata(CodeMetadata codeMetadata) {
        CompletableFuture<Void> cf = new CompletableFuture<>();
        CompletableFuture.runAsync(() -> {
            String documentId = codeMetadata.getDocumentId();

            boolean hasLocation = codeMetadata.hasLocation();
            Map<String, Object> objMap = new HashMap<>();
            objMap.put(FieldNames.ScannableCodeId.name, codeMetadata.getScannableCodeId());
            objMap.put(FieldNames.UserId.name, codeMetadata.getUserId());
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

        Query docRef = collectionReference.whereEqualTo(FieldNames.ScannableCodeId.name, scannableCodeId)
                .whereEqualTo(FieldNames.UserId.name, userId);

        docRef.get().addOnCompleteListener(task -> {
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

    public CompletableFuture<Void> updatePlayerCodeMetadataImage(String userId, String scannableCodeId, String image) {

        Log.d("updatePlayerCodeMetadataImage", String.format("scannableId: %s, userId: %s", scannableCodeId, userId));
        CompletableFuture<Void> cf = new CompletableFuture<>();
        CompletableFuture.runAsync(() -> {
            CollectionReference colRef = collectionReference;
            Query query = colRef.
                    whereEqualTo(FieldNames.ScannableCodeId.name, scannableCodeId).
                    whereEqualTo(FieldNames.UserId.name, userId);
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

    public CompletableFuture<CodeMetadata> getPlayerCodeMetadataById(String userId, String scannableCodeId) {
        CompletableFuture<CodeMetadata> cf = new CompletableFuture<>();
        CompletableFuture.runAsync(() -> {
            CollectionReference colRef = collectionReference;
            Query query = colRef.
                    whereEqualTo(FieldNames.ScannableCodeId.name, scannableCodeId).
                    whereEqualTo(FieldNames.UserId.name, userId);
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
                        for (QueryDocumentSnapshot document : task.getResult()) {
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

    @NonNull
    private CodeMetadata parseCodeMetadataDocument(DocumentSnapshot doc) {
        String scannableCodeId = doc.getString(FieldNames.ScannableCodeId.name);
        String userId = doc.getString(FieldNames.UserId.name);
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
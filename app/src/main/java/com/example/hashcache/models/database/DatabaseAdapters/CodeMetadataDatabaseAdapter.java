package com.example.hashcache.models.database.DatabaseAdapters;

import android.location.Location;
import android.util.Log;

import androidx.annotation.NonNull;

import com.example.hashcache.models.CodeMetadata;
import com.example.hashcache.models.ScannableCode;
import com.example.hashcache.models.database.DatabaseAdapters.converters.CodeLocationDocumentConverter;
import com.example.hashcache.models.database.values.CollectionNames;
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
import com.google.rpc.Code;

import java.security.NoSuchAlgorithmException;
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
     *
     * @throws IllegalArgumentException if the INSTANCE has already been initialized
     */
    public static CodeMetadataDatabaseAdapter makeInstance(FireStoreHelper fireStoreHelper,
                                                           FirebaseFirestore db) {
        if (INSTANCE != null) {
            throw new IllegalArgumentException("CodeLocationConnectionHandler INSTANCE already " +
                    "exists!");
        }

        INSTANCE = new CodeMetadataDatabaseAdapter(fireStoreHelper, db);
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

    public CompletableFuture<Void> updateLocationImage(String codeMetadataId, String base64Image) {
        CompletableFuture<Void> cf = new CompletableFuture<>();
        CompletableFuture.runAsync(() -> {
            Map<String, Object> objMap = new HashMap<>();
            objMap.put("image", base64Image);
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
            double lat = codeMetadata.getLocation().latitude;
            double lng = codeMetadata.getLocation().longitude;
            Map<String, Object> objMap = new HashMap<>();
            objMap.put("geohash", codeMetadata.getGeohash());
            objMap.put("lat", lat);
            objMap.put("lng", lng);
            objMap.put("base64Image", codeMetadata.getImage());
            objMap.put("scannableCodeId", codeMetadata.getScannableCodeId());
            objMap.put("userId", codeMetadata.getUserId());
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

    public CompletableFuture<Void> updatePlayerCodeMetadataImage(String userId, String scannableCodeId, String image) {

        Log.d("updatePlayerCodeMetadataImage", String.format("scannableId: %s, userId: %s", scannableCodeId, userId));
        CompletableFuture<Void> cf = new CompletableFuture<>();
        CompletableFuture.runAsync(() -> {
            CollectionReference colRef = collectionReference;
            Query query = colRef.
                    whereEqualTo("scannableCodeId", scannableCodeId).
                    whereEqualTo("userId", userId);
            query.get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    if (task.getResult().isEmpty()) {
                        cf.completeExceptionally(new Exception("Code Metadata entry does not exist!"));
                    } else {
                        List<DocumentSnapshot> sn = task.getResult().getDocuments();
                        DocumentSnapshot ds = sn.get(0);
                        DocumentReference dr = ds.getReference();
                        dr.update("base64Image", image).addOnSuccessListener(new OnSuccessListener<Void>() {
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
                    whereEqualTo("scannableCodeId", scannableCodeId).
                    whereEqualTo("userId", userId);
            query.get().addOnCompleteListener(task -> {
                if(task.isSuccessful()){
                    if(task.getResult().isEmpty()){
                        cf.completeExceptionally(new Exception(String.format("Code metadata entry does not exist: userId: %s, scannableCodeId: %s", userId, scannableCodeId)));
                    }else{
                        List<DocumentSnapshot> sn = task.getResult().getDocuments();
                        cf.complete(parseCodeMetadataDocument(sn.get(0)));
                    }
                }
                else{
                    cf.completeExceptionally(new Exception("Could not fet code metadata"));
                }
            });

        });
        return cf;
    }

    public CompletableFuture<ArrayList<CodeMetadata>> getCodeMetadataById(String scannableCodeId) {
        CompletableFuture<ArrayList<CodeMetadata>> cf = new CompletableFuture<>();
        CompletableFuture.runAsync(() -> {
            CollectionReference colRef = collectionReference;
            Query query = colRef.whereEqualTo("scannableCodeId", scannableCodeId);
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
                        .orderBy("geohash")
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

                                    double lat = doc.getDouble("lat");
                                    double lng = doc.getDouble("lng");
                                    GeoLocation docLocation = new GeoLocation(lat, lng);
                                    double distanceInM = GeoFireUtils.getDistanceBetween(docLocation, center);
                                    if (distanceInM <= radiusMeters) {
                                        CodeMetadata cm = parseCodeMetadataDocument(doc);
                                        matchingMetadata.add(cm);

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
        String image = doc.getString("base64Image");
        String scannableCodeId = doc.getString("scannableCodeId");
        double lat = doc.getDouble("lat");
        double lng = doc.getDouble("lng");
        String userId = doc.getString("userId");
        CodeMetadata cm = new CodeMetadata(scannableCodeId, userId, new GeoLocation(lat, lng), image);
        return cm;
    }
}
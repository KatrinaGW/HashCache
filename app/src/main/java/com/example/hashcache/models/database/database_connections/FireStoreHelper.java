package com.example.hashcache.models.database.database_connections;

import android.util.Pair;

import androidx.annotation.NonNull;

import com.example.hashcache.models.database.database_connections.callbacks.BooleanCallback;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * Performs common actions on a Firestore database
 */
public class FireStoreHelper {
    final String TAG = "Sample";

    public static Pair<CompletableFuture<Map<String, Object>>, ListenerRegistration> setupFirebaseDocListener(FirebaseFirestore db, String collectionName, String documentId){

        CompletableFuture<Map<String, Object>> cf = new CompletableFuture<>();
        final DocumentReference documentReference = db.collection(collectionName).document(documentId);
        ListenerRegistration registration = documentReference.addSnapshotListener((snapshot, e) -> {
                    cf.complete(null);
                    if (e != null) {
                        cf.completeExceptionally(e);
                        return;
                    }

                    if (snapshot != null && snapshot.exists()) {
                        cf.complete(snapshot.getData());
                    } else {
                        cf.complete(null);
                    }
                }
        );
        return new Pair(cf, registration);
    }

    /**
     * Adds a field with a boolean value to a given Firestore document
     * @param documentReference the document to add the field to
     * @param key the name of the field to add
     * @param value the value of the field to add
     * @param booleanCallback the callback function to call once the operation has finished. Call with
     *                        true if the operation was successful, and false otherwise
     */
    public void addBooleanFieldToDocument(DocumentReference documentReference, String key, boolean value,
                                   BooleanCallback booleanCallback){
        documentReference
                .update(key, value)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        booleanCallback.onCallback(true);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        booleanCallback.onCallback(false);
                    }
                });
    }

    /**
     * Adds a field with a String value to a given Firestore document
     * @param documentReference the document to add the field to
     * @param key the name of the field to add
     * @param value the value of the field to add
     * @param booleanCallback the callback function to call once the operation has finished. Call with
     *                        true if the operation was successful, and false otherwise
     */
    public void addStringFieldToDocument(DocumentReference documentReference, String key, String value,
                                          BooleanCallback booleanCallback){
        documentReference
                .update(key, value)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        booleanCallback.onCallback(true);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        booleanCallback.onCallback(false);
                    }
                });
    }

    /**
     * Sets the id reference and initial data for a specific document
     * @param documentReference the document to set the id and initial data on
     * @param data the initial fields, with String values, to add to the document
     * @param booleanCallback the callback function to call once the operation has finished. Call
     *                        with true if the operation was successful, and false otherwise
     */
    public void setDocumentReference(DocumentReference documentReference,
                                     HashMap<String, String> data, BooleanCallback booleanCallback){
        documentReference.set(data)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        booleanCallback.onCallback(true);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        booleanCallback.onCallback(false);
                    }
                });
    }


    /**
     * Checks if a document exists in a certain collection
     * @param collectionReference the collection to scan for a document
     * @param id the id of the document that's being searched for
     * @param booleanCallback the callback function to call once the scan has finished. Call
     *                        with true if a document is found, and false otherwise
     */
    public void documentWithIDExists(CollectionReference collectionReference, String id,
                                        BooleanCallback booleanCallback){
        final boolean[] exists = new boolean[1];

        DocumentReference documentReference = collectionReference.document(id);
        documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        exists[0] = true;
                        booleanCallback.onCallback(exists[0]);
                    } else {
                        exists[0] = false;
                        booleanCallback.onCallback(exists[0]);
                    }
                } else {
                    booleanCallback.onCallback(false);
                }
            }
        });
    }
}

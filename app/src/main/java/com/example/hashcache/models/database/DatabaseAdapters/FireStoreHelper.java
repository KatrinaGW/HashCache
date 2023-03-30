package com.example.hashcache.models.database.DatabaseAdapters;

import android.util.Pair;

import androidx.annotation.NonNull;

import com.example.hashcache.models.database.DatabaseAdapters.callbacks.BooleanCallback;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;

import org.checkerframework.checker.units.qual.C;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * Performs common actions on a Firestore database
 */
public class FireStoreHelper {

    /**
     * Adds a field with a boolean value to a given Firestore document
     * @param documentReference the document to add the field to
     * @param key the name of the field to add
     * @param value the value of the field to add
     * @return cf the CompleteableFuture with a boolean value indicating if the operation was
     *         successful or not
     */
    public CompletableFuture<Boolean> addBooleanFieldToDocument(DocumentReference documentReference,
                                                                String key, boolean value){
        CompletableFuture<Boolean> cf = new CompletableFuture<>();

        documentReference
                .update(key, value)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        cf.complete(true);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        cf.completeExceptionally(e);
                    }
                });
        return cf;
    }

    /**
     * Adds a field with a String value to a given Firestore document
     * @param documentReference the document to add the field to
     * @param key the name of the field to add
     * @param value the value of the field to add
     * @return cf the CompletableFuture with a boolean value indicating if the operation was
     *          successful or not
     */
    public CompletableFuture<Boolean> addStringFieldToDocument(DocumentReference documentReference,
                                                               String key, String value){
        CompletableFuture<Boolean> cf = new CompletableFuture<>();

        documentReference
                .update(key, value)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        cf.complete(true);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        cf.completeExceptionally(e);
                    }
                });
        return cf;
    }

    /**
     * Adds a number field to the document
     * @param documentReference document to add the field to
     * @param key key of the field
     * @param value integer value for the field
     * @return returns true on success
     */
    public CompletableFuture<Boolean> addNumberFieldToDocument(DocumentReference documentReference,
                                                               String key, Long value) {
        CompletableFuture<Boolean> cf = new CompletableFuture<>();

        documentReference
                .update(key, value)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        cf.complete(true);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        cf.completeExceptionally(e);
                    }
                });

        return cf;
    }
    /**
     * Sets the id reference and initial data for a specific document
     * @param documentReference the document to set the id and initial data on
     * @param data the initial fields, with String values, to add to the document
     * @return cf the CompletableFuture with a boolean value indicating if the operation was
     *          successful or not
     */
    public CompletableFuture<Boolean> setDocumentReference(DocumentReference documentReference,
                                     HashMap<String, String> data){
        CompletableFuture<Boolean> cf = new CompletableFuture<>();

        documentReference.set(data)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        cf.complete(true);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        cf.completeExceptionally(e);
                    }
                });
        return cf;
    }


    /**
     * Checks if a document exists in a certain collection
     * @param collectionReference the collection to scan for a document
     * @param id the id of the document that's being searched for
     * @return cf the CompletableFuture with a boolean value indicating if the
     *          document exists in the collection or not
     */
    public CompletableFuture<Boolean> documentWithIDExists(CollectionReference collectionReference,
                                                           String id){
        CompletableFuture<Boolean> cf = new CompletableFuture<>();
        final boolean[] exists = new boolean[1];

        DocumentReference documentReference = collectionReference.document(id);
        documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        exists[0] = true;
                        cf.complete(exists[0]);
                    } else {
                        exists[0] = false;
                        cf.complete(exists[0]);
                    }
                } else {
                    cf.complete(false);
                }
            }
        });
        return cf;
    }
}

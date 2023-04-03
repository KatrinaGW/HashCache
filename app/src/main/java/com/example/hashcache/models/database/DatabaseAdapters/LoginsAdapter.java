package com.example.hashcache.models.database.DatabaseAdapters;

import androidx.annotation.NonNull;

import com.example.hashcache.appContext.AppContext;
import com.example.hashcache.models.database.values.CollectionNames;
import com.example.hashcache.models.database.values.FieldNames;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

/**
 * Handles the interactions with the logins collection in the database
 */
public class LoginsAdapter {
    private static LoginsAdapter INSTANCE;
    private FireStoreHelper fireStoreHelper;
    private FirebaseFirestore db;
    private CollectionReference collectionReference;
    final String TAG = "Sample";

    private LoginsAdapter(FireStoreHelper fireStoreHelper, FirebaseFirestore firebaseFirestore){
        this.fireStoreHelper = fireStoreHelper;
        this.db = firebaseFirestore;
        this.collectionReference = firebaseFirestore.collection(CollectionNames.LOGINS.collectionName);
    }

    /**
     * Gets the static instance of the Logins Adapter
     * @return INSTANCE the static instance of the LoginsAdapter
     * @throws IllegalArgumentException when the INSTANCE does not exist
     */
    public static LoginsAdapter getInstance(){
        if(INSTANCE!=null){
            return INSTANCE;
        }else{
            throw new IllegalArgumentException("The LoginsAdapter INSTANCE does not exist!");
        }
    }

    /**
     * Resets the static instance
     */
    public static void resetInstance(){
        INSTANCE = null;
    }

    /**
     * Creates the static INSTANCE of the LoginsAdapter
     * @param fireStoreHelper the instance of the FireStoreHelper for the static INSTANCE to use
     * @param firebaseFirestore the instance of the database for the static INSTANCE to use
     * @return INSTANCE the newly created INSTANCE of the LoginsAdapter
     * @throws IllegalArgumentException when the INSTANCE already exists
     */
    public static LoginsAdapter makeOrGetInstance(FireStoreHelper fireStoreHelper,
                                                  FirebaseFirestore firebaseFirestore){
        if(INSTANCE == null){
            INSTANCE = new LoginsAdapter(fireStoreHelper, firebaseFirestore);
            return INSTANCE;
        }else{
            return INSTANCE;
        }
    }

    /**
     * Sets the userId for the user who has logged in with a specified device. Will overwrite any
     * existing login record for the device
     * @param username the name to use for the login record
     * @return cf the CompletableFuture which completes exceptionally if there was an error in setting
     * up the record
     */
    public CompletableFuture<Void> addLoginRecord(String username){
        CompletableFuture<Void> cf = new CompletableFuture<>();
        String deviceId = AppContext.get().getDeviceId();
        HashMap<String, String> data = new HashMap<>();
        data.put(FieldNames.DEVICE_ID.fieldName, deviceId);
        data.put(FieldNames.USERNAME.fieldName, username);

        CompletableFuture.runAsync(() -> {
            fireStoreHelper.setDocumentReference(collectionReference.document(deviceId), data)
                    .thenAccept(complete -> {
                        if(complete){
                            cf.complete(null);
                        }else{
                            cf.completeExceptionally(new Exception("Something went wrong while adding" +
                                    "a login record"));
                        }
                    })
                    .exceptionally(new Function<Throwable, Void>() {
                        @Override
                        public Void apply(Throwable throwable) {
                            cf.completeExceptionally(throwable);
                            return null;
                        };
                    });
        });
        return cf;
    }

    /**
     * Gets the username to use if the device has had a login before
     * @return cf the CompletableFuture with the username of the associated user. Returns
     * null if there is not a login entry for the specified device
     */
    public CompletableFuture<String> getUsernameForDevice(){
        CompletableFuture<String> cf = new CompletableFuture<>();
        String deviceId = AppContext.get().getDeviceId();

        CompletableFuture.runAsync(()->{
            fireStoreHelper.documentWithIDExists(collectionReference, deviceId)
                    .thenAccept(exists -> {
                        if(exists){
                            collectionReference.document(deviceId).get()
                                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                            if(task.isSuccessful()){
                                                DocumentSnapshot document = task.getResult();
                                                if (document.exists()) {
                                                    cf.complete((String) document.getData()
                                                            .get((FieldNames.USERNAME.fieldName)));
                                                } else {
                                                    cf.completeExceptionally(new Exception(
                                                            "Something went wrong while getting the " +
                                                                    "username from the deviceId record"
                                                    ));
                                                }
                                            }else{
                                                cf.completeExceptionally(new Exception(
                                                        "Something went wrong while getting the" +
                                                                "username from the deviceId record"
                                                ));
                                            }
                                        }
                                    });
                        }else{
                            cf.complete(null);
                        }
                    }).exceptionally(
                            new Function<Throwable, Void>() {
                                @Override
                                public Void apply(Throwable throwable) {
                                    cf.completeExceptionally(throwable);
                                    return null;
                                }
                            }
                    );
                }
        );
        return cf;
    }

    /**
     * Remove the login record for the current device
     * @return cf the CompletableFuture that completes exceptionally if the operation caused
     * an error
     */
    public CompletableFuture<Void> deleteLogin(){
        CompletableFuture<Void> cf = new CompletableFuture<>();

        CompletableFuture.runAsync(() -> {
            fireStoreHelper.documentWithIDExists(collectionReference, AppContext.get().getDeviceId())
                    .thenAccept(
                            exists -> {
                                if(!exists){
                                    cf.completeExceptionally(new Exception(
                                            "There is no login record for the given ID!"
                                    ));
                                }else{
                                    collectionReference.document(AppContext.get().getDeviceId())
                                            .delete()
                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void unused) {
                                                    cf.complete(null);
                                                }
                                            })
                                            .addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    cf.completeExceptionally(e);
                                                }
                                            });
                                }
                            }
                    ).exceptionally(new Function<Throwable, Void>() {
                        @Override
                        public Void apply(Throwable throwable) {
                            cf.completeExceptionally(throwable);
                            return null;
                        }
                    });
        });
        return cf;
    }
}

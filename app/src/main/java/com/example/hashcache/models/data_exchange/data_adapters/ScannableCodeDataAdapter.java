package com.example.hashcache.models.data_exchange.data_adapters;

import android.util.Log;

import androidx.annotation.NonNull;

import com.example.hashcache.models.data_exchange.database.values.CollectionNames;
import com.example.hashcache.models.data_exchange.database.DatabaseAdapters.callbacks.GetCommentsCallback;
import com.example.hashcache.models.data_exchange.database.DatabaseAdapters.callbacks.GetScannableCodeCallback;
import com.example.hashcache.models.Comment;
import com.example.hashcache.models.HashInfo;
import com.example.hashcache.models.ScannableCode;
import com.example.hashcache.models.data_exchange.database.values.FieldNames;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.checkerframework.checker.units.qual.C;

import java.util.ArrayList;
import java.util.concurrent.CompletableFuture;

/**
 * Handles the conversion between a DocumentReference and a ScannableCode object
 */
public class ScannableCodeDataAdapter {
    final String TAG = "Sample";

//    public static CompletableFuture<ScannableCode> getScannableCodeWithId(String id){
//
//    }

//    /**
//     * Gets the scannablecode from the document
//     * @param documentReference the reference to the document with the data
//     * @param getScannableCodeCallback the callback function to call with the scannable code
//     * @throws IllegalArgumentException if the scannable code does not contain all the necessary fields
//     */
//    public CompletableFuture<ScannableCode> getScannableCodeFromDocument(DocumentReference documentReference){
//        String[] scannableCodeId = new String[1];
//        String[] codeLocationId  = new String[1];
//        String[] generatedName = new String[1];
//        int[] generatedScore = new int[1];
//
//        CompletableFuture<ScannableCode> cf = new CompletableFuture<>();
//
//        documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
//            @Override
//            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
//                if (task.isSuccessful()) {
//                    DocumentSnapshot document = task.getResult();
//                    if (document.exists()) {
//                        try{
//                            scannableCodeId[0] = document.getId();
//                            codeLocationId[0] = (String) document.getData().get(FieldNames.CODE_LOCATION_ID.fieldName);
//                            generatedName[0] = (String) document.getData().get(FieldNames.GENERATED_NAME.fieldName);
//                            generatedScore[0] = Integer.parseInt((String) document.getData()
//                                                                            .get(FieldNames.GENERATED_SCORE.fieldName));
//                            getAllComments(documentReference
//                                            .collection(CollectionNames.COMMENTS.collectionName),
//                                    new GetCommentsCallback() {
//                                        @Override
//                                        public void onCallback(ArrayList<Comment> comments) {
//                                            getScannableCodeCallback.onCallback(new ScannableCode(scannableCodeId[0],
//                                                    codeLocationId[0], new HashInfo(null, generatedName[0],
//                                                    generatedScore[0]), comments));
//                                        }
//                                    });
//
//                            //TODO: Store the image once we figure out how to do it
//                        }catch (NullPointerException e){
//                            Log.e(TAG, "Scannable Code missing fields!");
//                            getScannableCodeCallback.onCallback(null);
//                        }
//                    } else {
//                        Log.d(TAG, "No such document");
//                        throw new IllegalArgumentException("Scannable Code does not exist");
//                    }
//                } else {
//                    Log.d(TAG, "get failed with ", task.getException());
//                }
//            }
//        });
//    }

//    /**
//     * Gets all the comments on a scannable code document
//     * @param collectionReference the reference to the comments collection
//     * @param getCommentsCallback the callback to call with the filled comments list
//     */
//    private void getAllComments(CollectionReference collectionReference,
//                                GetCommentsCallback getCommentsCallback){
//        ArrayList<Comment> comments = new ArrayList<>();
//        collectionReference
//                .get()
//                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
//                    @Override
//                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
//                        if (task.isSuccessful()) {
//                            if(task.getResult().size()>0){
//                                for (QueryDocumentSnapshot document : task.getResult()) {
//                                    Log.d(TAG, document.getId() + " => " + document.getData());
//                                    comments.add(new Comment((String) document.getData().get("body"),
//                                            (String) document.getData().get("commentatorId")));
//                                }
//                            }
//
//                            getCommentsCallback.onCallback(comments);
//                        } else {
//                            Log.d(TAG, "Error getting documents: ", task.getException());
//                        }
//                    }
//                });
//    }
}

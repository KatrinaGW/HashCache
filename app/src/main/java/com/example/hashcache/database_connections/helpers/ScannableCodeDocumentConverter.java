package com.example.hashcache.database_connections.helpers;

import android.util.Log;

import androidx.annotation.NonNull;

import com.example.hashcache.database_connections.GetScannableCodeCallback;
import com.example.hashcache.models.Comment;
import com.example.hashcache.models.HashInfo;
import com.example.hashcache.models.ScannableCode;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import java.util.ArrayList;
import java.util.UUID;

public class ScannableCodeDocumentConverter {
    final String TAG = "Sample";

    public void getScannableCodeFromDocument(DocumentReference documentReference,
                                            GetScannableCodeCallback getScannableCodeCallback){
        String[] scannableCodeId = new String[1];
        String[] codeLocationId  = new String[1];
        HashInfo hashInfo;
        final ArrayList<Comment> comments = new ArrayList<>();

        documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                        try{
                            scannableCodeId[0] = document.getId();
                            codeLocationId[0] = (String) document.getData().get("codeLocationId");
                            getAllComments(documentReference.collection("commentIds"),
                                    comments);

                            getScannableCodeCallback.onCallback(new ScannableCode(scannableCodeId[0],
                                    codeLocationId[0], null, comments)); //TODO: change from null once hash info done

                        }catch (NullPointerException e){
                            Log.e(TAG, "Code location missing fields!");
                        }
                    } else {
                        Log.d(TAG, "No such document");
                        throw new IllegalArgumentException("Codelocation does not exist");
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });
    }

    private ArrayList<Comment> getAllComments(CollectionReference collectionReference,
                                               ArrayList<Comment> comments){
        collectionReference
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d(TAG, document.getId() + " => " + document.getData());
                                comments.add(new Comment((String) document.getData().get("body"),
                                        (String) document.getData().get("commentatorId")));
                            }
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });

        return comments;
    }
}

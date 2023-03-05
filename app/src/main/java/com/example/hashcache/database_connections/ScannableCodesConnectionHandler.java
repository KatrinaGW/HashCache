package com.example.hashcache.database_connections;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.hashcache.database_connections.helpers.FireStoreHelper;
import com.example.hashcache.database_connections.helpers.PlayerDocumentConverter;
import com.example.hashcache.database_connections.helpers.ScannableCodeDocumentConverter;
import com.example.hashcache.models.Comment;
import com.example.hashcache.models.ContactInfo;
import com.example.hashcache.models.Player;
import com.example.hashcache.models.PlayerPreferences;
import com.example.hashcache.models.PlayerWallet;
import com.example.hashcache.models.ScannableCode;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.checkerframework.checker.units.qual.A;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

/**
 * Handles all calls to the Firebase ScannableCodes database
 */
public class ScannableCodesConnectionHandler {
    private FirebaseFirestore db;
    private CollectionReference collectionReference;
    private HashMap<String, ScannableCode> cachedScannableCodes;
    final String TAG = "Sample";
    private ScannableCodeDocumentConverter scannableCodeDocumentConverter;
    private FireStoreHelper fireStoreHelper;

    public ScannableCodesConnectionHandler(){
        this.cachedScannableCodes = new HashMap<>();
        this.scannableCodeDocumentConverter = new ScannableCodeDocumentConverter();
        this.fireStoreHelper = new FireStoreHelper();

        db = FirebaseFirestore.getInstance();

        collectionReference = db.collection(CollectionNames.SCANNABLE_CODES.collectionName);

        collectionReference.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable
            FirebaseFirestoreException error) {
                for(QueryDocumentSnapshot doc: queryDocumentSnapshots)
                {
                    //TODO: get scannable code
//                    Log.d(TAG, String.valueOf(doc.getData().get("username")));
//                    String username = doc.getId();
                }
            }
        });
    }

    public void getScannableCode(String scannableCodeId, GetScannableCodeCallback getScannableCodeCallback){
        if(this.cachedScannableCodes.containsKey(scannableCodeId)){
            getScannableCodeCallback.onCallback(cachedScannableCodes.get(scannableCodeId));
        }else{
            DocumentReference documentReference = this.collectionReference.document(scannableCodeId);
            this.scannableCodeDocumentConverter.getScannableCodeFromDocument(documentReference, getScannableCodeCallback);
        }
    }

    public void addScannableCode(ScannableCode scannableCode, BooleanCallback booleanCallback){
        if(this.cachedScannableCodes.containsKey(scannableCode.getScannableCodeId())){
            Log.d(TAG, "scannable code already exists with given id!");
            return;
        }

        fireStoreHelper.documentWithIDExists(collectionReference, scannableCode.getScannableCodeId(),
                new BooleanCallback() {
                    @Override
                    public void onCallback(Boolean isTrue) {
                        HashMap<String, String> data = new HashMap<>();
                        data.put("id", scannableCode.getScannableCodeId());
                        data.put("codeLocationId", scannableCode.getCodeLocationId());
                        data.put("hashInfo", "foo");

                        HashMap<String, String> commentData = new HashMap<>();

                        fireStoreHelper.setDocumentReference(collectionReference
                                .document(scannableCode.getScannableCodeId()), data);

                        for(Comment comment : scannableCode.getComments()){
                            commentData.clear();
                            commentData.put(comment.getCommentatorId(), comment.getBody());
                            fireStoreHelper.setDocumentReference(collectionReference
                                    .document(scannableCode.getScannableCodeId()).collection("comments")
                                    .document(UUID.randomUUID().toString()), commentData);
                        }
                    };
                });
    }
}

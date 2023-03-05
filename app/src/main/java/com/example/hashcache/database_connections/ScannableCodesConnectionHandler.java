package com.example.hashcache.database_connections;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.hashcache.database_connections.helpers.FireStoreHelper;
import com.example.hashcache.database_connections.helpers.PlayerDocumentConverter;
import com.example.hashcache.database_connections.helpers.ScannableCodeDocumentConverter;
import com.example.hashcache.models.Comment;
import com.example.hashcache.models.ContactInfo;
import com.example.hashcache.models.HashInfo;
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

import java.lang.reflect.Field;
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

    /**
     * Gets a scannable code from the database with a specific id
     * @param scannableCodeId the id of the scannable code to get
     * @param getScannableCodeCallback the callback function to be called with the found scnnablecode
     */
    public void getScannableCode(String scannableCodeId, GetScannableCodeCallback getScannableCodeCallback){
        if(this.cachedScannableCodes.containsKey(scannableCodeId)){
            getScannableCodeCallback.onCallback(cachedScannableCodes.get(scannableCodeId));
        }else{
            DocumentReference documentReference = this.collectionReference.document(scannableCodeId);
            this.scannableCodeDocumentConverter.getScannableCodeFromDocument(documentReference, getScannableCodeCallback);
        }
    }

    /**
     * Add a scannable code to the database
     * @param scannableCode the scannable code to add to the database
     * @param booleanCallback the function to call back with once the addition has succeeded
     * @throws IllegalArgumentException when there already exists a scannable code with the given id
     */
    public void addScannableCode(ScannableCode scannableCode, BooleanCallback booleanCallback){
        if(this.cachedScannableCodes.containsKey(scannableCode.getScannableCodeId())){
            Log.d(TAG, "scannable code already exists with given id!");
            booleanCallback.onCallback(false);
            return;
        }

        fireStoreHelper.documentWithIDExists(collectionReference, scannableCode.getScannableCodeId(),
                new BooleanCallback() {
                    @Override
                    public void onCallback(Boolean documentExists) {
                        if(!documentExists){
                            HashInfo hashInfo = scannableCode.getHashInfo();
                            ArrayList<Comment> comments = scannableCode.getComments();

                            HashMap<String, String> data = new HashMap<>();
                            data.put(FieldNames.SCANNABLE_CODE_ID.fieldName, scannableCode.getScannableCodeId());
                            data.put(FieldNames.CODE_LOCATION_ID.fieldName, scannableCode.getCodeLocationId());
                            data.put(FieldNames.GENERATED_NAME.fieldName, hashInfo.getGeneratedName());
                            data.put(FieldNames.GENERATED_SCORE.fieldName, Integer.toString(hashInfo.getGeneratedScore()));

                            fireStoreHelper.setDocumentReference(collectionReference
                                    .document(scannableCode.getScannableCodeId()), data);

                            HashMap<String, String> commentData = new HashMap<>();

                            for (Comment comment : comments) {
                                commentData.clear();
                                commentData.put(FieldNames.COMMENTATOR_ID.fieldName, comment.getCommentatorId());
                                commentData.put(FieldNames.COMMENT_BODY.fieldName, comment.getBody());
                                fireStoreHelper.setDocumentReference(collectionReference
                                        .document(scannableCode.getScannableCodeId())
                                        .collection(CollectionNames.COMMENTS.collectionName)
                                        .document(comment.getCommentId()), commentData);
                            }

                            booleanCallback.onCallback(true);
                        }else{
                            throw new IllegalArgumentException("Scannable code with id already exists!");
                        }
                    };
                });
    }
}

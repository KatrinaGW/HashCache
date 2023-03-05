package com.example.hashcache.database_connections;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.hashcache.database_connections.helpers.FireStoreHelper;
import com.example.hashcache.database_connections.helpers.PlayerDocumentConverter;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Handles all calls to the Firebase ScannableCodes database
 */
public class ScannableCodesConnectionHandler {
    private FirebaseFirestore db;
    private CollectionReference collectionReference;
    private HashMap<String, ScannableCode> cachedScannableCodes;
    final String TAG = "Sample";
    private PlayerDocumentConverter playerDocumentConverter;
    private FireStoreHelper fireStoreHelper;

    public ScannableCodesConnectionHandler(){
        this.cachedScannableCodes = new HashMap<>();
        this.playerDocumentConverter = new PlayerDocumentConverter();
        this.fireStoreHelper = new FireStoreHelper();

        db = FirebaseFirestore.getInstance();

        collectionReference = db.collection("scannableCodes");

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
}

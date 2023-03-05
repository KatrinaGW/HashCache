package com.example.hashcache.database_connections;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.hashcache.database_connections.helpers.FireStoreHelper;
import com.example.hashcache.database_connections.helpers.PlayerDocumentConverter;
import com.example.hashcache.models.CodeLocation;
import com.example.hashcache.models.ContactInfo;
import com.example.hashcache.models.Coordinate;
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

import org.checkerframework.checker.units.qual.C;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Handles all calls to the Firebase ScannableCodes database
 */
public class CodeLocationConnectionHandler {
    private FirebaseFirestore db;
    private CollectionReference collectionReference;
    private HashMap<String, CodeLocation> cachedCodeLocations;
    final String TAG = "Sample";
    private PlayerDocumentConverter playerDocumentConverter;
    private FireStoreHelper fireStoreHelper;

    public CodeLocationConnectionHandler(){
        this.cachedCodeLocations = new HashMap<>();
        this.playerDocumentConverter = new PlayerDocumentConverter();
        this.fireStoreHelper = new FireStoreHelper();

        db = FirebaseFirestore.getInstance();

        collectionReference = db.collection("codeLocations");

        collectionReference.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable
            FirebaseFirestoreException error) {
                cachedCodeLocations.clear();
                for(QueryDocumentSnapshot doc: queryDocumentSnapshots)
                {
                    Log.d(TAG, "Code Location with id " + doc.getId());
                    double x = Double.parseDouble((String) doc.getData().get("x"));
                    double y = Double.parseDouble((String) doc.getData().get("y"));
                    double z = Double.parseDouble((String) doc.getData().get("z"));
                    String locationName = (String) doc.getData().get("name");

                    cachedCodeLocations.put(doc.getId(), new CodeLocation(locationName, x, y, z));
                }
            }
        });
    }

    public void addCodeLocation(CodeLocation codeLocation, BooleanCallback booleanCallback){
        String name = codeLocation.getLocationName();
        double[] coordinates = codeLocation.getCoordinates().getCoordinates();
        String x = Double.toString((double)Array.get(coordinates, 0));
        String y = Double.toString((double)Array.get(coordinates, 1));
        String z = Double.toString((double)Array.get(coordinates, 2));
        String id = x + y + z;

        final boolean[] codeLocationExists = new boolean[1];

        System.out.println("ID IS " + id);

        fireStoreHelper.documentWithIDExists(collectionReference, id, new BooleanCallback() {
            @Override
            public void onCallback(Boolean isTrue) {
                codeLocationExists[0] = isTrue;

                if(!codeLocationExists[0] && !cachedCodeLocations.containsKey(id)){

                    HashMap<String, String> data = new HashMap<>();
                    data.put("name", name);
                    data.put("x", x);
                    data.put("y", y);
                    data.put("z", z);

                    DocumentReference documentReference = collectionReference.document(id);

                    fireStoreHelper.setDocumentReference(documentReference, data);
                }else{
                    Log.e(TAG, "Code Location already exists!");
                    throw new IllegalArgumentException("Code location already exists!");
                }
            }
        });

        booleanCallback.onCallback(true);
    }
}

package com.example.hashcache.database_connections.helpers;

import android.util.Log;

import androidx.annotation.NonNull;

import com.example.hashcache.database_connections.GetCodeLocationCallback;
import com.example.hashcache.models.CodeLocation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;

import java.lang.reflect.Array;

public class CodeLocationDocumentConverter {
    final String TAG = "Sample";

    public void getCodeLocationFromDocument(DocumentReference documentReference,
                                                    GetCodeLocationCallback getCodeLocationCallback){
        double[] coordinates = new double[3];
        String[] locationName = new String[1];

        documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                        try{
                            locationName[0] = (String) document.getData().get("name");
                            System.out.println("C " + locationName[0]);
                            Array.set(coordinates, 0,
                                    Double.parseDouble((String) document.getData().get("x")));
                            Array.set(coordinates, 1,
                                    Double.parseDouble((String) document.getData().get("y")));
                            Array.set(coordinates, 2,
                                    Double.parseDouble((String) document.getData().get("z")));

                            getCodeLocationCallback.onCallback(new CodeLocation(locationName[0],
                                    (Double) Array.get(coordinates, 0),
                                    (Double) Array.get(coordinates, 1),
                                    (Double) Array.get(coordinates, 2)));
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
}

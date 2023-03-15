package com.example.hashcache.models.database.data_adapters;

import androidx.annotation.NonNull;

import com.example.hashcache.models.database.database_connections.callbacks.GetCodeLocationCallback;
import com.example.hashcache.models.CodeLocation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;

import java.lang.reflect.Array;

/**
 * Handles the conversion between a DocumentReference and a CodeLocation
 */
public class CodeLocationDataAdapter {
    final String TAG = "Sample";

    /**
     * Converts a DocumentReference into a CodeLocation object
     * @param documentReference the DocumentReference to convert into a CodeLocation object
     * @param getCodeLocationCallback the callback function to call with the created CodeLocation object
     * @throws IllegalArgumentException if the document does not exist
     */
    public void getCodeLocationFromDocument(DocumentReference documentReference,
                                                    GetCodeLocationCallback getCodeLocationCallback){
        double[] coordinates = new double[3];
        String[] locationName = new String[1];

        /**
         * If the document exists, convert it into a CodeLocation object
         */
        documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        try{
                            locationName[0] = (String) document.getData().get("name");
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
                            getCodeLocationCallback.onCallback(null);
                        }
                    } else {
                        throw new IllegalArgumentException("Codelocation does not exist");
                    }
                } else {
                    getCodeLocationCallback.onCallback(null);
                }
            }
        });
    }
}

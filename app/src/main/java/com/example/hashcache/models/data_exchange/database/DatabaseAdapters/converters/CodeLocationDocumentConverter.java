package com.example.hashcache.models.data_exchange.database.DatabaseAdapters.converters;

import androidx.annotation.NonNull;

import com.example.hashcache.models.CodeLocation;
import com.example.hashcache.models.data_exchange.database.DatabaseAdapters.FireStoreHelper;
import com.example.hashcache.models.data_exchange.database.DatabaseAdapters.callbacks.BooleanCallback;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;

import java.lang.reflect.Array;
import java.util.HashMap;
import java.util.concurrent.CompletableFuture;

public class CodeLocationDocumentConverter {

    public CompletableFuture<Boolean> addCodeLocationToCollection(CodeLocation codeLocation,
                                                                         CollectionReference collectionReference,
                                                                         FireStoreHelper fireStoreHelper){
        CompletableFuture<Boolean> cf = new CompletableFuture<>();

        CompletableFuture.runAsync(() -> {
            String name = codeLocation.getLocationName();
            String id = codeLocation.getId();
            double[] coordinates = codeLocation.getCoordinates().getCoordinates();
            String x = Double.toString((double)Array.get(coordinates, 0));
            String y = Double.toString((double)Array.get(coordinates, 1));
            String z = Double.toString((double)Array.get(coordinates, 2));

            HashMap<String, String> data = new HashMap<>();
            data.put("name", name);
            data.put("x", x);
            data.put("y", y);
            data.put("z", z);

            DocumentReference documentReference = collectionReference.document(id);

            /**
             * Add the document to the collection. If the operation is successful, cache
             * the codeLocation and call the callback function with a true value,
             * otherwise call the callback function with false.
             */
            fireStoreHelper.setDocumentReference(documentReference, data, new BooleanCallback() {
                @Override
                public void onCallback(Boolean isTrue) {
                    if(isTrue){
                        cf.complete(true);
                    }else{
                        cf.completeExceptionally(new Exception("Something went wrong while" +
                                "adding the code location to the Collection"));
                    }
                }
            });
        });

        return cf;
    }

    /**
     * Converts a DocumentReference into a CodeLocation object
     * @param documentReference the DocumentReference to convert into a CodeLocation object
     * @return cf the CompleteableFuture with the CodeLocation
     * @throws IllegalArgumentException if the document does not exist
     */
    public static CompletableFuture<CodeLocation> convertDocumentReferenceToCodeLocation(DocumentReference documentReference){
        CompletableFuture<CodeLocation> cf = new CompletableFuture<>();
        double[] coordinates = new double[3];
        String[] locationName = new String[1];

        CompletableFuture.runAsync(()->{        /**
         * If the document exists, convert it into a CodeLocation object
         */
            documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            try {
                                locationName[0] = (String) document.getData().get("name");
                                Array.set(coordinates, 0,
                                        Double.parseDouble((String) document.getData().get("x")));
                                Array.set(coordinates, 1,
                                        Double.parseDouble((String) document.getData().get("y")));
                                Array.set(coordinates, 2,
                                        Double.parseDouble((String) document.getData().get("z")));

                                cf.complete(new CodeLocation(locationName[0],
                                        (Double) Array.get(coordinates, 0),
                                        (Double) Array.get(coordinates, 1),
                                        (Double) Array.get(coordinates, 2)));
                            } catch (NullPointerException e) {
                                cf.completeExceptionally(e);
                            }
                        } else {
                            cf.completeExceptionally(new Throwable("The code location document does not" +
                                    "exist!"));
                        }
                    } else {
                        cf.completeExceptionally(new Throwable("Something went wrong while getting the " +
                                "code location document!"));
                    }
                }
            });
        });

        return cf;
    }
}

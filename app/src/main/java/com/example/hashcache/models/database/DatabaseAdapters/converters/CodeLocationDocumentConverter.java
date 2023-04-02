package com.example.hashcache.models.database.DatabaseAdapters.converters;

import android.location.Location;

import androidx.annotation.NonNull;

import com.example.hashcache.models.CodeLocation;
import com.example.hashcache.models.database.DatabaseAdapters.FireStoreHelper;
import com.example.hashcache.models.database.DatabaseAdapters.callbacks.BooleanCallback;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;

import java.lang.reflect.Array;
import java.util.HashMap;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

public class CodeLocationDocumentConverter {

    public CompletableFuture<Boolean> addCodeLocationToCollection(CodeLocation codeLocation,
                                                                         CollectionReference collectionReference,
                                                                         FireStoreHelper fireStoreHelper){
        CompletableFuture<Boolean> cf = new CompletableFuture<>();

        CompletableFuture.runAsync(() -> {
            String id = codeLocation.getId();
            String lat = String.valueOf(codeLocation.getLocation().getLatitude());
            String lng = String.valueOf(codeLocation.getLocation().getLongitude());


            HashMap<String, String> data = new HashMap<>();
            data.put("Lat", lat);
            data.put("Lng", lng);

            DocumentReference documentReference = collectionReference.document(id);

            /**
             * Add the document to the collection. If the operation is successful, cache
             * the codeLocation and call the callback function with a true value,
             * otherwise call the callback function with false.
             */
            fireStoreHelper.setDocumentReference(documentReference, data)
                            .thenAccept(successful -> {
                                if(successful){
                                    cf.complete(true);
                                }else{
                                    cf.completeExceptionally(new Exception("Something went wrong while" +
                                            "adding the code location to the Collection"));
                                }
                            })
                                    .exceptionally(new Function<Throwable, Void>() {
                                        @Override
                                        public Void apply(Throwable throwable) {
                                            cf.completeExceptionally(throwable);
                                            return null;
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
    public CompletableFuture<CodeLocation> convertDocumentReferenceToCodeLocation(DocumentReference documentReference){
        CompletableFuture<CodeLocation> cf = new CompletableFuture<>();
        String[] QRcontent = new String[1];
        Location[] location = new Location[1];
        location[0] = new Location("");
        /**
         * If the document exists, convert it into a CodeLocation object
         */
            documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            try {

                                QRcontent[0] = (String) document.getId();
                                location[0].setLatitude((Double) document.getData().get("Lat"));
                                location[0].setLongitude((Double) document.getData().get("Lng"));

                                cf.complete(new CodeLocation(QRcontent[0],location[0]));

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

        return cf;
    }
}

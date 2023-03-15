package com.example.hashcache.models.data_exchange.data_adapters;

import androidx.annotation.NonNull;

import com.example.hashcache.models.data_exchange.database.DatabaseAdapters.CodeLocationDatabaseAdapter;
import com.example.hashcache.models.data_exchange.database.DatabaseAdapters.FireStoreHelper;
import com.example.hashcache.models.data_exchange.database.DatabaseAdapters.callbacks.GetCodeLocationCallback;
import com.example.hashcache.models.CodeLocation;
import com.example.hashcache.models.data_exchange.database.DatabaseAdapters.converters.CodeLocationDocumentConverter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import org.checkerframework.checker.units.qual.C;

import java.lang.reflect.Array;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

/**
 * Handles the conversion between a DocumentReference and a CodeLocation
 */
public class CodeLocationDataAdapter {

    /**
     * Add a CodeLocation to the database
     * @param codeLocation the codeLocation to add to the Database
     * @return cf the CompleteableFuture with True if the operation was successful
     */
    public CompletableFuture<Boolean> addCodeLocation(CodeLocation codeLocation, CodeLocationDatabaseAdapter
                                                             codeLocationDatabaseAdapter){
        CompletableFuture<Boolean> cf = new CompletableFuture<>();

        CompletableFuture.runAsync(() -> {
            codeLocationDatabaseAdapter.addCodeLocation(codeLocation)
                    .thenAccept(success -> {
                        cf.complete(true);
                    }).exceptionally(new Function<Throwable, Void>() {
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
     * Get a CodeLocation object with a specific ID
     * @param codeLocationid the ID of the codeLocation to get
     * @return cf the CompleteableFuture with the CodeLocation
     */
    public static CompletableFuture<CodeLocation> getCodeLocation(String codeLocationid){
        CompletableFuture<CodeLocation> cf = new CompletableFuture<>();

        CompletableFuture.runAsync(() -> {
            CodeLocationDatabaseAdapter instance;
            try{
                instance = CodeLocationDatabaseAdapter.getInstance();
            }catch(IllegalArgumentException e){
                instance = CodeLocationDatabaseAdapter.makeInstance(new FireStoreHelper(),
                        FirebaseFirestore.getInstance(), new CodeLocationDocumentConverter());
            }

            instance.getCodeLocation(codeLocationid).thenAccept(codeLocation -> {
                cf.complete(codeLocation);
            }).exceptionally(new Function<Throwable, Void>() {
                @Override
                public Void apply(Throwable throwable) {
                    cf.completeExceptionally(throwable);
                    return null;
                }
            });
        });

        return cf;

    }
}

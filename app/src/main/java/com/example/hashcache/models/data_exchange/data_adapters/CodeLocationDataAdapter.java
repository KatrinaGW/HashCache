package com.example.hashcache.models.data_exchange.data_adapters;

import androidx.annotation.NonNull;

import com.example.hashcache.models.data_exchange.database.DatabaseAdapters.CodeLocationDatabaseAdapter;
import com.example.hashcache.models.data_exchange.database.DatabaseAdapters.FireStoreHelper;
import com.example.hashcache.models.data_exchange.database.DatabaseAdapters.callbacks.GetCodeLocationCallback;
import com.example.hashcache.models.CodeLocation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.lang.reflect.Array;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

/**
 * Handles the conversion between a DocumentReference and a CodeLocation
 */
public class CodeLocationDataAdapter {

    public static CompletableFuture<Boolean> addCodeLocation(CodeLocation codeLocation){
        CompletableFuture<Boolean> cf = new CompletableFuture<>();

        CompletableFuture.runAsync(() -> {
            CodeLocationDatabaseAdapter.getInstance().addCodeLocation(codeLocation)
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

    public static CompletableFuture<CodeLocation> getCodeLocation(String codeLocationid){
        CompletableFuture<CodeLocation> cf = new CompletableFuture<>();

        CompletableFuture.runAsync(() -> {
            CodeLocationDatabaseAdapter instance;
            try{
                instance = CodeLocationDatabaseAdapter.getInstance();
            }catch(IllegalArgumentException e){
                instance = CodeLocationDatabaseAdapter.makeInstance(new FireStoreHelper(),
                        FirebaseFirestore.getInstance());
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

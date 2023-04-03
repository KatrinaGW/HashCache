package com.example.hashcache.controllers;

import android.location.Location;
import android.util.Log;

import androidx.annotation.NonNull;

import com.example.hashcache.appContext.AppContext;
import com.example.hashcache.models.CodeMetadata;
import com.example.hashcache.models.database.Database;
import com.firebase.geofire.GeoLocation;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

/**
 * Handles the commands for the code location database collection
 */
public class LocationController {
    /**
     * Gets the last location of the user
     * @param fusedLocationProviderClient an instance of FusedLocationProviderClient to use
     * @return cf the CompletableFuture that completes with the last location
     */
    public static CompletableFuture<Location> getLastLocation(FusedLocationProviderClient fusedLocationProviderClient){
        CompletableFuture<Location>  cf = new CompletableFuture<>();
        CompletableFuture.runAsync(() -> {
            try{
                fusedLocationProviderClient.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        cf.complete(location);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        cf.completeExceptionally(e);
                    }
                });
            }
            catch(SecurityException e){
                cf.completeExceptionally(e);
            }
        });
        return cf;
    }
}

package com.example.hashcache.controllers.hashInfo;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.os.Build;

import androidx.annotation.RequiresApi;


import com.example.hashcache.models.HashInfo;
import com.example.hashcache.models.ScannableCode;
import com.example.hashcache.models.database.Database;
import com.example.hashcache.store.AppStore;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

import javax.net.ssl.HttpsURLConnection;

public class HashController {

    public static CompletableFuture<Void> addScannableCode(String qrContent){
        CompletableFuture<Void> cf = new CompletableFuture<>();
        CompletableFuture.runAsync(new Runnable() {
            @Override
            public void run() {

                try {
                    MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
                    messageDigest.update(qrContent.getBytes());
                    byte[] byteArray = messageDigest.digest();
                    String hash = new BigInteger(1, byteArray).toString(16);
                    HashInfoGenerator.generateHashInfo(byteArray).thenAccept(hashInfo -> {
                        Database.getInstance().scannableCodeExists(hash).thenAccept(exists -> {
                            String userId = AppStore.get().getCurrentPlayer().getUserId();
                            ScannableCode sc = new ScannableCode(hash, hashInfo);
                            if(exists){

                                addScannableCodeToPlayer(hash, userId, cf, sc);
                            }
                            else{
                                Database.getInstance().addScannableCode(sc).thenAccept(id -> {
                                    addScannableCodeToPlayer(hash, userId, cf, sc);
                                }).exceptionally(throwable -> {
                                    cf.completeExceptionally(throwable);
                                    return null;
                                });;
                            }
                        }).exceptionally(throwable -> {
                            cf.completeExceptionally(throwable);
                            return null;
                        });;

                    }).exceptionally(throwable -> {
                        cf.completeExceptionally(throwable);
                        return null;
                    });


                } catch (NoSuchAlgorithmException e) {
                    e.printStackTrace();
                    cf.completeExceptionally(e);
                }
            }
        });
        return cf;
    }

    private static void addScannableCodeToPlayer(String hash, String userId, CompletableFuture<Void> cf, ScannableCode sc) {
        Database.getInstance().addScannableCodeToPlayerWallet(userId, hash).thenAccept(created->{
            AppStore.get().setCurrentScannableCode(sc);
            cf.complete(null);
        }).exceptionally(new Function<Throwable, Void>() {
            @Override
            public Void apply(Throwable throwable) {
                cf.completeExceptionally(throwable);
                return null;
            }
        });
    }
}


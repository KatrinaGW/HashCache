package com.example.hashcache.controllers.hashInfo;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;

import java.io.InputStream;
import java.net.URL;
import java.util.concurrent.CompletableFuture;

import javax.net.ssl.HttpsURLConnection;

public class ImageGenerator {
    private static final String TAG = ImageGenerator.class.toString();
    public static CompletableFuture<Drawable> getImageFromHash(String hash) {
        CompletableFuture<Drawable> cf = new CompletableFuture<>();
        CompletableFuture.runAsync(new Runnable() {
            @Override
            public void run() {
                //https://stackoverflow.com/questions/10292792/getting-image-from-url-java
                //https://stackoverflow.com/questions/6407324/how-to-display-image-from-url-on-android
                String urlString = String.format("https://robohash.org/%s?set=set2", hash);
                Log.d(TAG, String.format("URL is: %s", urlString));
                try {
                        InputStream inputStream = (InputStream) new URL(urlString).getContent();
                    cf.complete(Drawable.createFromStream(inputStream, "src name"));

                } catch (Exception e) {
                    e.printStackTrace();
                    cf.completeExceptionally(e);
                }
            }
        });
        return cf;
    }

}

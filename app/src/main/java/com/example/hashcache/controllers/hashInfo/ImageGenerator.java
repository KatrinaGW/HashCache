package com.example.hashcache.controllers.hashInfo;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;

import androidx.annotation.RequiresApi;

import java.io.InputStream;
import java.net.URL;
import java.util.concurrent.CompletableFuture;

import javax.net.ssl.HttpsURLConnection;

public class ImageGenerator {
    @RequiresApi(api = Build.VERSION_CODES.TIRAMISU)
    public static CompletableFuture<Bitmap> getImageFromHash(String hash) {
        CompletableFuture<Bitmap> cf = new CompletableFuture<>();
        CompletableFuture.runAsync(new Runnable() {
            @Override
            public void run() {
                //https://stackoverflow.com/questions/10292792/getting-image-from-url-java
                String urlString = String.format("https://robohash.org/%s?set=set2", hash);
                try {
                    URL url = new URL(urlString);
                    HttpsURLConnection handle = (HttpsURLConnection) url.openConnection();
                    handle.setRequestMethod("GET");
                    handle.setRequestProperty("content-type", "image/png");
                    InputStream inputStream = handle.getInputStream();
                    inputStream.read();
                    final int bufSize = 4096;
                    byte[] bytes = new byte[bufSize];
                    int bytesRead = 0;
                    while (bytesRead != -1) {
                        bytesRead = inputStream.read(bytes);

                    }
                    ;
                    Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                    handle.disconnect();
                    cf.complete(bitmap);

                } catch (Exception e) {
                    e.printStackTrace();
                    cf.completeExceptionally(e);
                }
            }
        });
        return cf;
    }

}

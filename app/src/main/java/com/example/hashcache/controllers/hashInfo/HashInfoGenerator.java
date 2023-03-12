package com.example.hashcache.controllers.hashInfo;

import static com.example.hashcache.controllers.hashInfo.Constants.consecutiveOnesProbs;
import static com.example.hashcache.controllers.hashInfo.Constants.totalOnesProbs;

import android.content.res.Resources;
import android.graphics.Bitmap;

import com.example.hashcache.models.HashInfo;
import com.google.android.material.color.utilities.Score;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.concurrent.CompletableFuture;
import java.util.jar.Attributes;

public class HashInfoGenerator {
    public static CompletableFuture<HashInfo> generateHashInfo(String sha256Hash){
        CompletableFuture<HashInfo> cf = new CompletableFuture<>();
        String generatedName = NameGenerator.generateName(getIntegerIdFromHash(sha256Hash));
        try {
            long generatedScore = ScoreGenerator.generateScore(sha256Hash.getBytes(StandardCharsets.UTF_8));
            // Image will be added on part 4
            HashInfo hashInfo = new HashInfo(null, generatedName, generatedScore);
            cf.complete(hashInfo);

        } catch (Exception e) {
            cf.completeExceptionally(e);
            e.printStackTrace();
        }
        return cf;
    }

    public static long getIntegerIdFromHash(String sha256Hash){
        byte[] bytes = sha256Hash.getBytes(StandardCharsets.UTF_8);
        long number = 0;
        for(int i = 0; i < 8; i++){
            number |= bytes[0] << i;
        }
        return number;
    }
}

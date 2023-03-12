package com.example.hashcache.controllers.hashInfo;

import static com.example.hashcache.controllers.hashInfo.Constants.consecutiveOnesProbs;
import static com.example.hashcache.controllers.hashInfo.Constants.totalOnesProbs;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.util.Log;

import com.example.hashcache.models.HashInfo;
import com.google.android.material.color.utilities.Score;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;
import java.util.concurrent.CompletableFuture;
import java.util.jar.Attributes;

public class HashInfoGenerator {
    public static CompletableFuture<HashInfo> generateHashInfo(byte[] byteArray){
        CompletableFuture<HashInfo> cf = new CompletableFuture<>();
        CompletableFuture.runAsync(new Runnable() {
            @Override
            public void run() {
                long num = getIntegerIdFromHash(Arrays.copyOfRange(byteArray, 0, 7));
                String generatedName = NameGenerator.generateName(num);
                try {
                    long generatedScore = ScoreGenerator.generateScore(Arrays.copyOfRange(byteArray, 0, 8));
                    // Image will be added on part 4
                    HashInfo hashInfo = new HashInfo(null, generatedName, generatedScore);
                    cf.complete(hashInfo);

                } catch (Exception e) {
                    cf.completeExceptionally(e);
                    e.printStackTrace();
                }
            }
        });
        return cf;
    }

    public static long getIntegerIdFromHash(byte[] byteArray){
        return new BigInteger(byteArray).longValue();
    }
}

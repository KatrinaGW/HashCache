package com.example.hashcache.controllers.hashInfo;

import static com.example.hashcache.controllers.hashInfo.Constants.consecutiveOnesProbs;
import static com.example.hashcache.controllers.hashInfo.Constants.totalOnesProbs;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.util.Log;
import android.util.Pair;

import com.example.hashcache.models.HashInfo;
import com.google.android.material.color.utilities.Score;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;
import java.util.concurrent.CompletableFuture;
import java.util.jar.Attributes;
/**
 * The HashInfoGenerator class provides methods for generating hash information for scannable codes.
 */
public class HashInfoGenerator {
    /**
     * Generates hash information for a given byte array.
     *
     * @param byteArray the byte array to generate hash information for
     * @return a CompletableFuture that completes with a HashInfo object containing the generated hash information
     */
    public static CompletableFuture<HashInfo> generateHashInfo(byte[] byteArray){
        CompletableFuture<HashInfo> cf = new CompletableFuture<>();
        CompletableFuture.runAsync(new Runnable() {
            @Override
            public void run() {
                // Generate an integer ID from the first 7 bytes of the hash
                long num = getIntegerIdFromHash(Arrays.copyOfRange(byteArray, 0, 7));
                // Generate a name for the scannable code using the generated ID
                String generatedName = NameGenerator.generateName(num);
                try {
                    // Generate a score for the scannable code using the first 8 bytes of the hash
                    long generatedScore = ScoreGenerator.generateScore(Arrays.copyOfRange(byteArray, 0, 8));
                    // Create a new HashInfo object with the generated name and score (the image field will be added in part 4)
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
    /**
     * Converts a byte array to a long integer ID.
     *
     * @param byteArray the byte array to convert
     * @return a long integer ID generated from the byte array
     */
    public static long getIntegerIdFromHash(byte[] byteArray){
        return new BigInteger(byteArray).longValue();
    }


    /**
     * Hashes the QR contents to a unique identifier using SHA-256.
     *
     * @param QRContents the byte array to convert
     * @return a String with the hash.
     */

    public static Pair<String, byte[]> getHashFromQRContents(String QRContents) throws NoSuchAlgorithmException {
        MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
        messageDigest.update(QRContents.getBytes());
        byte[] byteArray = messageDigest.digest();
        String hash = new BigInteger(1, byteArray).toString(16);
        return new Pair<>(hash, byteArray);
    }
}

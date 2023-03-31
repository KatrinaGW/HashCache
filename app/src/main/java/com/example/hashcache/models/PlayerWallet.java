package com.example.hashcache.models;

import android.media.Image;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Represents a list of the player's current scanned codes
 */
public class PlayerWallet{
    private HashMap<String, Image> scannableCodes;
    private int size;
    private long totalScore;
    private long maxScore;
    private long qrCount;

    public PlayerWallet(){
        this.size = 0;
        this.totalScore = 0;
        this.maxScore = 0;
        this.qrCount = 0;
        this.scannableCodes = new HashMap<String, Image>();
    }

    /**
     * Adds a scannable code to the player's collection without an image
     * @param scannableCodeId The id of the scanned code
     */
    public void addScannableCode(String scannableCodeId){
        this.scannableCodes.put(scannableCodeId, null);
        this.size++;
    }

    /**
     * Adds a scannable code and its image to the player's collection
     * @param scannableCodeId The id of the scannable code
     * @param locationImage The image of the location where the user scanned the code
     */
    public void addScannableCode(String scannableCodeId, Image locationImage){
        this.scannableCodes.put(scannableCodeId, locationImage);
        this.size++;
    }

    /**
     * Get the list of the code ids the user has scanned
     * @return scannedCodeIds The ids of all the codes the user has scanned
     */
    public ArrayList<String> getScannedCodeIds(){
        ArrayList<String> scannedCodeIds = new ArrayList<>(this.scannableCodes.keySet());

        return scannedCodeIds;
    }

    /**
     * Gets the total score of all of the scannable codes in the wallet
     * @return totalScore the total score of all scannable codes in the wallet
     */
    public long getTotalScore(){
        return totalScore;
    }

    /**
     * Sets the total score in the wallet
     * @param newTotalScore the new total score in the wallet
     */
    public void setTotalScore(long newTotalScore){
        this.totalScore = newTotalScore;
    }

    /**
     * Gets the image that the user took of the scannable code whose id is specified. Could return
     * null if there was no image taken
     * @param scannableCodeId The id of the scannable code to get its image of
     * @return The image that the user took of the location where they scanned the code
     * @throws IllegalArgumentException If the user has not scanned a code with the specified id
     */
    public Image getScannableCodeLocationImage(String scannableCodeId){
        if(this.scannableCodes.keySet().contains(scannableCodeId)){
            return this.scannableCodes.get(scannableCodeId);
        }else{
            throw new IllegalArgumentException("User has not scanned a code with this id!");
        }

    }

    /**
     * Gets the number of scanned codes
     * @return the number of scanned codes
     */
    public int getSize(){
        return this.size;
    }

    /**
     * Deletes a scannable code from the Player Wallet
     * @param scannableCodeId the id of the scannable code to delete
     * @throws IllegalArgumentException when the id does not exist in the player wallet
     */
    public void deleteScannableCode(String scannableCodeId){
        if(this.scannableCodes.containsKey(scannableCodeId)){
            this.scannableCodes.remove(scannableCodeId);
            this.size--;

        }else{
            throw new IllegalArgumentException("Player wallet does not contain scannable" +
                    "code with given id");
        }
    }

    public long getMaxScore() {
        return maxScore;
    }

    public long getQrCount() {
        return qrCount;
    }

    public void updateMaxScore(long candidateScore) {
        if(candidateScore>maxScore){
            this.maxScore = candidateScore;
        }
    }

    public void setQRCount(long qrCount){
        this.qrCount = qrCount;
    }

    public void incrementQRCount(){
        this.qrCount++;
    }
}
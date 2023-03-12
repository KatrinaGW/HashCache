package com.example.hashcache.models;

import android.media.Image;

import com.example.hashcache.controllers.DependencyInjector;
import com.example.hashcache.models.database_connections.ScannableCodesConnectionHandler;
import com.example.hashcache.models.database_connections.callbacks.GetIntegerCallback;
import com.example.hashcache.models.database_connections.callbacks.GetScannableCodeCallback;

import org.checkerframework.checker.units.qual.A;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

/**
 * Represents a list of the player's current scanned codes
 */
public class PlayerWallet{
    private HashMap<String, Image> scannableCodes;
    private int size;

    public PlayerWallet(){
        this.size = 0;
        this.scannableCodes = new HashMap<String, Image>();
    }

    /**
     * Adds a scannable code to the player's collection without an image
     * @param scannableCodeId The id of the scanned code
     */
    public void addScannableCode(String scannableCodeId){
        this.scannableCodes.put(scannableCodeId, null);
    }



    /**
     * Adds a scannable code and its image to the player's collection
     * @param scannableCodeId The id of the scannable code
     * @param locationImage The image of the location where the user scanned the code
     */
    public void addScannableCode(String scannableCodeId, Image locationImage){
        this.size++;
        this.scannableCodes.put(scannableCodeId, locationImage);
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
        }else{
            throw new IllegalArgumentException("Player wallet does not contain scannable" +
                    "code with given id");
        }

    }

    //Do we need to get the scannablecode from here? Since I'm assuming we're just connecting to
    //DB and can get it somewhere else. Assume id is already known if the user gets here - doesn't
    //seem to be a point in adding that method
}
package com.example.hashcache.store;

import com.example.hashcache.models.Player;
import com.example.hashcache.models.ScannableCode;

/**
 * Represents a store for holding global state information in the app
 */
public class AppStore {
    private static AppStore instance;
    private AppStore(){}

    boolean isLoggedIn;
    private Player currentPlayer;
    private ScannableCode currentScannableCode;
    /**
     * Gets the singleton instance of the AppStore
     *
     * @return instance The singleton instance of the AppStore
     */
    public static AppStore get(){
        if(instance == null){
            synchronized(AppStore.class) {
                if(instance == null){
                    instance = new AppStore();
                }

            }
        }
        return instance;
    }
    /**
     * Sets the current player in the AppStore
     *
     * @param player The player to set as the current player
     */
    public void setCurrentPlayer(Player player){
        currentPlayer = player;
    }
    /**
     * Gets the current player in the AppStore
     *
     * @return currentPlayer The current player in the AppStore
     */
    public Player getCurrentPlayer(){
        return currentPlayer;
    }

    /**
     * Sets the selected scannable code
     * @param currentScannableCode the scananbleCode to set as selected
     */
    public void setCurrentScannableCode(ScannableCode currentScannableCode) {
        this.currentScannableCode = currentScannableCode;
    }

    /**
     * Gets the selectedScannableCode
     * @return currentScannableCode the currently selectedScannableCode
     */
    public ScannableCode getCurrentScannableCode() {
        return currentScannableCode;
    }
}

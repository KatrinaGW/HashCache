package com.example.hashcache.appContext;

import android.util.Log;

import com.example.hashcache.models.Comment;
import com.example.hashcache.models.Player;
import com.example.hashcache.models.ScannableCode;
import com.example.hashcache.models.database.Database;
import com.example.hashcache.models.database.DatabaseAdapters.LoginsAdapter;
import com.example.hashcache.models.database.DatabaseAdapters.PlayersDatabaseAdapter;
import com.example.hashcache.models.database.DatabaseAdapters.callbacks.BooleanCallback;
import com.example.hashcache.models.database.DatabaseAdapters.callbacks.GetPlayerCallback;
import com.example.hashcache.models.database.DatabaseAdapters.callbacks.GetScannableCodeCallback;

import org.checkerframework.checker.units.qual.A;
import org.checkerframework.checker.units.qual.C;

import java.util.ArrayList;
import java.util.Observable;
import java.util.concurrent.CompletableFuture;

/**
 * Holds the global state information for the app
 */
public class AppContext extends Observable {
    private static AppContext instance;

    private AppContext() {}

    boolean isLoggedIn;
    private Player currentPlayer;
    private String deviceId;
    private Player selectedPlayer;
    private ScannableCode currentScannableCode = new ScannableCode();
    private ScannableCode lowestScannableCode = new ScannableCode();
    private ScannableCode highestScannableCode = new ScannableCode();

    /**
     * Gets the singleton instance of the AppStore
     *
     * @return instance The singleton instance of the AppStore
     */
    public static AppContext get() {
        if (instance == null) {
            synchronized (AppContext.class) {
                if (instance == null) {
                    instance = new AppContext();
                }

            }
        }
        return instance;
    }

    /**
     * Resets the context after a logout
     */
    public void resetContext(){
        deleteObservers();
        this.currentScannableCode = null;
        this.currentPlayer = null;
        this.deviceId = null;
    }


    public void setSelectedPlayer(Player player){
        selectedPlayer = player;
    }

    public Player getSelectedPlayer(){
        return selectedPlayer;
    }
    /**
     * Sets the current player in the AppStore
     *
     * @param player The player to set as the current player
     */
    public void setCurrentPlayer(Player player) {
        currentPlayer = player;
    }

    /**
     * Gets the current player in the AppStore
     *
     * @return currentPlayer The current player in the AppStore
     */
    public Player getCurrentPlayer() {
        return currentPlayer;
    }

    /**
     * Sets the selected scannable code
     * 
     * @param newCurrentScannableCode the scananbleCode to set as selected
     */
    public void setCurrentScannableCode(ScannableCode newCurrentScannableCode) {
        if(newCurrentScannableCode!=null&&(this.currentScannableCode==null||
                !this.currentScannableCode.getScannableCodeId()
                        .equals(newCurrentScannableCode.getScannableCodeId()))){
            this.currentScannableCode = newCurrentScannableCode;
            this.setUpScannableCodeCommentListener();
        }else{
            this.currentScannableCode = newCurrentScannableCode;
        }

    }

    private void setHighestScannableCode(ScannableCode scanCode) {
        this.highestScannableCode = scanCode;
    }

    public ScannableCode getHighestScannableCode() {
        return this.highestScannableCode;
    }

    private void setLowestScannableCode(ScannableCode scanCode) {
        this.lowestScannableCode = scanCode;
    }

    public ScannableCode getLowestScannableCode() {
        return this.lowestScannableCode;
    }

    public ScannableCode getCurrentScannableCode() {
        return currentScannableCode;
    }

    /**
     * Sets the device Id for this app session
     * @param deviceId the id of the device being used
     */
    public void setDeviceId(String deviceId){
        this.deviceId = deviceId;
    }

    /**
     * Gets the device Id for this app session
     * @return the id of the deivce being used
     */
    public String getDeviceId(){
        return this.deviceId;
    }

    private void setUpScannableCodeCommentListener(){
        if(currentScannableCode!=null){
            String scananbleCodeId = getCurrentScannableCode().getScannableCodeId();
            Database.getInstance().onScannableCodeCommentsChanged(scananbleCodeId, new GetScannableCodeCallback() {
                @Override
                public void onCallback(ScannableCode scannableCode) {
                    if(scannableCode != null &&
                            scannableCode.getScannableCodeId().equals(currentScannableCode.getScannableCodeId())){
                        Log.d("Context.onScannableCodeCommentsChanged", "notifying observers");
                        setCurrentScannableCode(scannableCode);
                        setChanged();
                        notifyObservers();
                    }
                }
            });
        }
    }

    public void setupListeners() {
        String userId = getCurrentPlayer().getUserId();
        Database.getInstance().onPlayerDataChanged(userId, new GetPlayerCallback() {
            @Override
            public void onCallback(Player player) {
                System.out.println(String.format("Player data for %s has changed", currentPlayer.getUsername()));
                if(currentPlayer!=null && player!=null &&
                        player.getUserId() == currentPlayer.getUserId()){
                    setCurrentPlayer(player);
                    setChanged();
                    notifyObservers();
                }
            }
        });

        Database.getInstance().onPlayerWalletChanged(userId, new BooleanCallback() {
            @Override
            public void onCallback(Boolean isTrue) {
                System.out.println(String.format("Player wallet for %s has changed", currentPlayer.getUsername()));
                Database.getInstance().getPlayer(userId).thenAccept(playa -> {
                    if (playa != null) {
                        setCurrentPlayer(playa);
                        setChanged();
                        notifyObservers();

                        Database.getInstance()
                                .getPlayerWalletTotalScore(playa.getPlayerWallet().getScannedCodeIds())
                                .thenAccept(totalScore -> {
                                    setChanged();
                                    notifyObservers();
                                });
                        Database.getInstance().getPlayerWalletLowScore(playa.getPlayerWallet().getScannedCodeIds())
                                .thenAccept(scanCode -> {
                                    setLowestScannableCode(scanCode);
                                    setChanged();
                                    notifyObservers();
                                });
                        Database.getInstance().getPlayerWalletTopScore(playa.getPlayerWallet().getScannedCodeIds())
                                .thenAccept(scanCode -> {
                                    setHighestScannableCode(scanCode);
                                    setChanged();
                                    notifyObservers();
                                });

                    }
                });
            }
        });

    }

}

package com.example.hashcache.store;

import com.example.hashcache.models.Player;

public class AppStore {
    private static AppStore instance;
    private AppStore(){}

    boolean isLoggedIn;
    private Player currentPlayer;
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

    public void setCurrentPlayer(Player player){
        instance.currentPlayer = player;
    }
    public Player getCurrentPlayer(){
        return instance.currentPlayer;
    }
}

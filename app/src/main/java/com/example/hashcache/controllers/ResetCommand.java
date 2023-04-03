package com.example.hashcache.controllers;

import com.example.hashcache.appContext.AppContext;
import com.example.hashcache.models.PlayerList;
import com.example.hashcache.models.database.Database;

/**
 * A controller for resetting the app state
 */
public class ResetCommand {
    /**
     * Resets the app context and any static instances
     */
    public static void reset(){
        PlayerList.resetInstance();
        AppContext.get().resetContext();
        Database.getInstance().resetInstances();
    }
}

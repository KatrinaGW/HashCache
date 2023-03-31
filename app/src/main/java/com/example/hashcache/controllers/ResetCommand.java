package com.example.hashcache.controllers;

import com.example.hashcache.appContext.AppContext;
import com.example.hashcache.models.PlayerList;
import com.example.hashcache.models.database.Database;

public class ResetCommand {
    public static void reset(){
        PlayerList.resetInstance();
        AppContext.get().resetContext();
        Database.getInstance().resetInstances();
    }
}

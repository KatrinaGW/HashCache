package com.example.hashcache.models.database;

import java.util.HashMap;

public class Database {
    private static IPlayerDatabase instance;
    public static IPlayerDatabase getInstance(){
        if(instance == null){
            synchronized(PlayerDatabase.class) {
                if(instance == null){
                    instance = new TestPlayerDatabase();
                }
            }
        }
        return instance;
    }

}

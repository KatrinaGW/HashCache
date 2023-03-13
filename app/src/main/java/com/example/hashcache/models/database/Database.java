package com.example.hashcache.models.database;

import java.util.HashMap;
/**

 This class represents a singleton object that returns an instance of a player database.
 */
public class Database {
    private static IPlayerDatabase instance;
    /**
     Returns an instance of a player database. If no instance has been created, it will create an instance of TestPlayerDatabase.
     @return The instance of the player database.
     */
    public static IPlayerDatabase getInstance(){
        if(instance == null){
            synchronized(PlayerDatabase.class) {
                if(instance == null){
                    instance = new PlayerDatabase();
                }
            }
        }
        return instance;
    }

}

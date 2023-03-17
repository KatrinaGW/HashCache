package com.example.hashcache.models.database;

/**

 This class represents a singleton object that returns an instance of a player database.
 */
public class Database {
    private static DatabasePort instance;
    /**
     Returns an instance of a player database. If no instance has been created, it will create an instance of TestPlayerDatabase.
     @return The instance of the player database.
     */
    public static DatabasePort getInstance(){
        if(instance == null){
            synchronized(DatabaseMapper.class) {
                if(instance == null){
                    instance = new DatabaseMapper();
                }
            }
        }
        return instance;
    }

}

package com.example.hashcache.models;

import com.example.hashcache.database_connections.GetPlayerCallback;
import com.example.hashcache.database_connections.PlayersConnectionHandler;

import java.util.ArrayList;

/**
 * Represents a container for all the players in game right now
 */
public class PlayerList {
    private ArrayList<String> playerUserNames;
    private PlayersConnectionHandler playersConnectionHandler;

    public PlayerList(){
        playerUserNames = new ArrayList<>();
        playersConnectionHandler = new PlayersConnectionHandler(playerUserNames);
    }

    /**
     * Adds a player to the database
     * @param username the username of the player to add
     * @return success indicates if the user was successfully added or not
     */
    public boolean addPlayer(String username){
        boolean success = true;

        try{
            this.playersConnectionHandler.addPlayer(username);
        }catch (IllegalArgumentException e){
            success = false;
        }

        return success;
    }

    /**
     * Gets a player with a given username
     * @param username the username of the player to find
     * @param getPlayerCallback callback function to get username after asynchronous firestore call
     * @return player The player with the given username
     */
    public Player getPlayer(String username, GetPlayerCallback getPlayerCallback){
        return this.playersConnectionHandler.getPlayer(username, getPlayerCallback);
    }

    /**
     * Returns a list of the game-wide players, sorted in a specified manner
     * @param filter The way to sort the players
     * @return players The playerlist sorted in the specified manner
     */
    public ArrayList<String> getPlayersSortedBy(Object filter){
        //return the players sorted by a specific method
        return this.playerUserNames;
    }

    /**
     * Gets the first n players in the playerlist sorted by a specified manner
     * @param filter The way to sort the players
     * @param n The top number of players to get
     * @return this.players sublist The top n players sorted in a specified manner
     */
    public ArrayList<String> getFirstNPlayersSortedBy(Object filter, int n){
        return (ArrayList<String>) (this.getPlayersSortedBy(filter)).subList(0, n);
    }
}
